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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.unitvectory.jsonassertify.JSONCompareResult;

import java.util.*;

import static com.unitvectory.jsonassertify.comparator.JSONCompareUtil.*;

/**
 * This class provides a skeletal implementation of the {@link JSONComparator}
 * interface, to minimize the effort required to implement this interface.
 * 
 * @author Carter Page
 * @author Corby Page
 * @author Solomon Duskis
 */
public abstract class AbstractComparator implements JSONComparator {

    /**
     * Constructs a new AbstractComparator.
     */
    public AbstractComparator() {
    }

    /**
     * Compares JSONObject provided to the expected JSONObject, and returns the
     * results of the comparison.
     *
     * @param expected Expected JSONObject
     * @param actual   JSONObject to compare
     * @throws JSONException JSON parsing error
     */
    @Override
    public final JSONCompareResult compareJSON(JSONObject expected, JSONObject actual) throws JSONException {
        JSONCompareResult result = new JSONCompareResult();
        compareJSON("", expected, actual, result);
        return result;
    }

    /**
     * Compares JSONArray provided to the expected JSONArray, and returns the
     * results of the comparison.
     *
     * @param expected Expected JSONArray
     * @param actual   JSONArray to compare
     * @throws JSONException JSON parsing error
     */
    @Override
    public final JSONCompareResult compareJSON(JSONArray expected, JSONArray actual) throws JSONException {
        JSONCompareResult result = new JSONCompareResult();
        compareJSONArray("", expected, actual, result);
        return result;
    }

    /**
     * Compares JSON string provided to the expected JSON string, and returns the
     * results of the comparison.
     * 
     * @param prefix   Prefix to use
     * @param expected Expected JSON string
     * @param actual   JSON string to compare
     * @param result   Result of the comparison
     */
    protected void checkJsonObjectKeysActualInExpected(String prefix, JSONObject expected, JSONObject actual,
            JSONCompareResult result) {
        Set<String> actualKeys = getKeys(actual);
        for (String key : actualKeys) {
            if (!expected.has(key)) {
                result.unexpected(prefix, key);
            }
        }
    }

    /**
     * Compares JSON object provided to the expected JSON object using provided
     * comparator, and returns the results of the comparison.
     * 
     * @param prefix   Prefix to use
     * @param expected Expected JSON object
     * @param actual   Actual JSON object
     * @param result   Result of the comparison
     * @throws JSONException JSON parsing error
     */
    protected void checkJsonObjectKeysExpectedInActual(String prefix, JSONObject expected, JSONObject actual,
            JSONCompareResult result) throws JSONException {
        Set<String> expectedKeys = getKeys(expected);
        for (String key : expectedKeys) {
            Object expectedValue = expected.get(key);
            if (actual.has(key)) {
                Object actualValue = actual.get(key);
                compareValues(qualify(prefix, key), expectedValue, actualValue, result);
            } else {
                result.missing(prefix, key);
            }
        }
    }

    /**
     * Compares JSON object provided to the expected JSON object using provided
     * comparator, and returns the results of the comparison.
     * 
     * @param key      Key to use
     * @param expected Expected JSON object
     * @param actual   Actual JSON object
     * @param result   Result of the comparison
     * @throws JSONException JSON parsing error
     */
    protected void compareJSONArrayOfJsonObjects(String key, JSONArray expected, JSONArray actual,
            JSONCompareResult result) throws JSONException {
        String uniqueKey = findUniqueKey(expected);
        if (uniqueKey == null || !isUsableAsUniqueKey(uniqueKey, actual)) {
            // An expensive last resort
            recursivelyCompareJSONArray(key, expected, actual, result);
            return;
        }
        Map<Object, JSONObject> expectedValueMap = numericKeyMap(expected, uniqueKey);
        Map<Object, JSONObject> actualValueMap = numericKeyMap(actual, uniqueKey);
        for (Object id : expectedValueMap.keySet()) {
            if (!actualValueMap.containsKey(id)) {
                result.missing(formatUniqueKey(key, uniqueKey, id), expectedValueMap.get(id));
                continue;
            }
            JSONObject expectedValue = expectedValueMap.get(id);
            JSONObject actualValue = actualValueMap.get(id);
            compareValues(formatUniqueKey(key, uniqueKey, id), expectedValue, actualValue, result);
        }
        for (Object id : actualValueMap.keySet()) {
            if (!expectedValueMap.containsKey(id)) {
                result.unexpected(formatUniqueKey(key, uniqueKey, id), actualValueMap.get(id));
            }
        }
    }

    private Map<Object, JSONObject> numericKeyMap(JSONArray array, String uniqueKey) {
        Map<Object, JSONObject> result = new HashMap<Object, JSONObject>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            result.put(numericKey(object.get(uniqueKey)), object);
        }
        return result;
    }

    private Map<Object, Integer> numericCardinalityMap(JSONArray array) {
        List<Object> keys = new ArrayList<Object>();
        for (Object value : jsonArrayToList(array)) {
            keys.add(numericKey(value));
        }
        return getCardinalityMap(keys);
    }

    /**
     * Compares JSON object provided to the expected JSON object using provided
     * comparator, and returns the results of the comparison.
     *
     * @param key Key to use
     * @param expected Expected JSON object
     * @param actual Actual JSON object
     * @param result Result of the comparison
     * @throws JSONException JSON parsing error
     */
    protected void compareJSONArrayOfSimpleValues(String key, JSONArray expected, JSONArray actual,
            JSONCompareResult result) throws JSONException {
        Map<Object, Integer> expectedCount = numericCardinalityMap(expected);
        Map<Object, Integer> actualCount = numericCardinalityMap(actual);
        for (Object o : expectedCount.keySet()) {
            if (!actualCount.containsKey(o)) {
                result.missing(key + "[]", originalValue(o));
            } else if (!actualCount.get(o).equals(expectedCount.get(o))) {
                result.fail(key + "[]: Expected " + expectedCount.get(o) + " occurrence(s) of " + o
                        + " but got " + actualCount.get(o) + " occurrence(s)");
            }
        }
        for (Object o : actualCount.keySet()) {
            if (!expectedCount.containsKey(o)) {
                result.unexpected(key + "[]", originalValue(o));
            }
        }
    }

    /**
     * Compares JSON object provided to the expected JSON object using provided
     * comparator, and returns the results of the comparison.
     * 
     * @param key      Key to use
     * @param expected Expected JSON object
     * @param actual   Actual JSON object
     * @param result   Result of the comparison
     * @throws JSONException JSON parsing error
     */
    protected void compareJSONArrayWithStrictOrder(String key, JSONArray expected, JSONArray actual,
            JSONCompareResult result) throws JSONException {
        for (int i = 0; i < expected.length(); ++i) {
            Object expectedValue = JSONCompareUtil.getObjectOrNull(expected, i);
            Object actualValue = JSONCompareUtil.getObjectOrNull(actual, i);
            compareValues(key + "[" + i + "]", expectedValue, actualValue, result);
        }
    }

    /**
     * Compares JSON object provided to the expected JSON object using provided
     * comparator, and returns the results of the comparison.
     * 
     * @param key      Key to use
     * @param expected Expected JSON object
     * @param actual   Actual JSON object
     * @param result   Result of the comparison
     * @throws JSONException JSON parsing error
     */
    protected void recursivelyCompareJSONArray(String key, JSONArray expected, JSONArray actual,
            JSONCompareResult result) throws JSONException {
        // This is expensive (O(n^2) -- yuck), but may be the only resort for some cases
        // with loose array ordering, and no easy way to uniquely identify each element.
        Set<Integer> matched = new HashSet<Integer>();
        for (int i = 0; i < expected.length(); ++i) {
            Object expectedElement = JSONCompareUtil.getObjectOrNull(expected, i);
            boolean matchFound = false;
            for (int j = 0; j < actual.length(); ++j) {
                Object actualElement = JSONCompareUtil.getObjectOrNull(actual, j);
                if (matched.contains(j)) {
                    continue;
                }
                if (expectedElement == actualElement) {
                    matched.add(j);
                    matchFound = true;
                    break;
                }
                if ((expectedElement == null && actualElement != null)
                        || (expectedElement != null && actualElement == null)) {
                    continue;
                }
                if (expectedElement instanceof Number && actualElement instanceof Number) {
                    if (((Number) expectedElement).doubleValue() == ((Number) actualElement).doubleValue()) {
                        matched.add(j);
                        matchFound = true;
                        break;
                    }
                    continue;
                }
                if (!actualElement.getClass().equals(expectedElement.getClass())) {
                    continue;
                }
                if (expectedElement instanceof JSONObject) {
                    JSONCompareResult candidateResult = new JSONCompareResult();
                    compareJSON(key + "[" + i + "]", (JSONObject) expectedElement, (JSONObject) actualElement, candidateResult);
                    if (candidateResult.passed()) {
                        matched.add(j);
                        matchFound = true;
                        break;
                    }
                } else if (expectedElement instanceof JSONArray) {
                    JSONCompareResult candidateResult = new JSONCompareResult();
                    compareJSONArray(key + "[" + i + "]", (JSONArray) expectedElement, (JSONArray) actualElement, candidateResult);
                    if (candidateResult.passed()) {
                        matched.add(j);
                        matchFound = true;
                        break;
                    }
                } else if (expectedElement.equals(actualElement)) {
                    matched.add(j);
                    matchFound = true;
                    break;
                }
            }
            if (!matchFound) {
                result.fail(key + "[" + i + "] Could not find match for element " + expectedElement);
                return;
            }
        }
    }
}
