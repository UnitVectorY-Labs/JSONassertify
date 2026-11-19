/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package com.unitvectory.jsonassertify.comparator;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
import com.unitvectory.jsonassertify.Customization;
import com.unitvectory.jsonassertify.JSONCompareMode;
import com.unitvectory.jsonassertify.JSONCompareResult;
import com.unitvectory.jsonassertify.ValueMatcherException;

import java.util.Arrays;
import java.util.Collection;

import static com.unitvectory.jsonassertify.comparator.JSONCompareUtil.*;

/**
 * A JSONAssert custom comparator.
 * 
 * @author Carter Page
 * @author Corby Page
 * @author Solomon Duskis
 */
public class CustomComparator extends DefaultComparator {

    private final Collection<Customization> customizations;

    /**
     * Constructs a new CustomComparator with the provided {@link JSONCompareMode} and {@link Customization}s.
     * @param mode the comparison mode
     * @param customizations the customizations to apply
     */
    public CustomComparator(JSONCompareMode mode, Customization... customizations) {
        super(mode);
        this.customizations = Arrays.asList(customizations);
    }

    @Override
    public void compareValues(String prefix, Object expectedValue, Object actualValue, JSONCompareResult result)
            throws JSONException {
        Customization customization = getCustomization(prefix);
        if (customization != null) {
            try {
                if (!customization.matches(prefix, actualValue, expectedValue, result)) {
                    result.fail(prefix, expectedValue, actualValue);
                }
            } catch (ValueMatcherException e) {
                result.fail(prefix, e);
            }
        } else {
            super.compareValues(prefix, expectedValue, actualValue, result);
        }
    }

    @Override
    protected void compareJSONArrayOfJsonObjects(String key, JSONArray expected, JSONArray actual,
            JSONCompareResult result) throws JSONException {
        String uniqueKey = findUniqueKey(expected);
        if (uniqueKey != null && allJSONObjects(actual)) {
            for (int i = 0; i < expected.length(); i++) {
                JSONObject object = expected.getJSONObject(i);
                String objectPath = formatUniqueKey(key, uniqueKey, object.get(uniqueKey));
                if (getCustomization(qualify(objectPath, uniqueKey)) != null) {
                    compareCustomizedObjectArray(key, uniqueKey, expected, actual, result);
                    return;
                }
            }
        }
        super.compareJSONArrayOfJsonObjects(key, expected, actual, result);
    }

    private void compareCustomizedObjectArray(String key, String uniqueKey, JSONArray expected, JSONArray actual,
            JSONCompareResult result) {
        int size = expected.length();
        boolean[][] matches = new boolean[size][size];
        for (int i = 0; i < size; i++) {
            JSONObject expectedObject = expected.getJSONObject(i);
            String path = formatUniqueKey(key, uniqueKey, expectedObject.get(uniqueKey));
            for (int j = 0; j < size; j++) {
                JSONCompareResult candidate = new JSONCompareResult();
                compareValues(path, expectedObject, actual.getJSONObject(j), candidate);
                matches[i][j] = candidate.passed();
            }
        }
        int[] assigned = new int[size];
        Arrays.fill(assigned, -1);
        for (int i = 0; i < size; i++) {
            if (!assignMatch(i, matches, assigned, new boolean[size])) {
                result.fail(key + "[" + i + "] Could not find match for element " + expected.get(i));
                return;
            }
        }
    }

    private boolean assignMatch(int expectedIndex, boolean[][] matches, int[] assigned, boolean[] visited) {
        for (int j = 0; j < assigned.length; j++) {
            if (matches[expectedIndex][j] && !visited[j]) {
                visited[j] = true;
                if (assigned[j] == -1 || assignMatch(assigned[j], matches, assigned, visited)) {
                    assigned[j] = expectedIndex;
                    return true;
                }
            }
        }
        return false;
    }

    private Customization getCustomization(String path) {
        for (Customization c : customizations)
            if (c.appliesToPath(path))
                return c;
        return null;
    }
}
