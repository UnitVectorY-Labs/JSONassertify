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
import com.unitvectory.jsonassertify.JSONCompareMode;
import com.unitvectory.jsonassertify.JSONCompareResult;

import static com.unitvectory.jsonassertify.comparator.JSONCompareUtil.allJSONObjects;
import static com.unitvectory.jsonassertify.comparator.JSONCompareUtil.allSimpleValues;

/**
 * This class is the default json comparator implementation.
 * Comparison is performed according to {@link JSONCompareMode} that is passed
 * as constructor's argument.
 * 
 * @author Carter Page
 * @author Corby Page
 * @author Solomon Duskis
 */
public class DefaultComparator extends AbstractComparator {

    JSONCompareMode mode;

    /**
     * Constructs a new DefaultComparator with the provided {@link JSONCompareMode}.
     * 
     * @param mode the comparison mode
     */
    public DefaultComparator(JSONCompareMode mode) {
        this.mode = mode;
    }

    @Override
    public void compareJSON(String prefix, JSONObject expected, JSONObject actual, JSONCompareResult result)
            throws JSONException {
        // Check that actual contains all the expected values
        checkJsonObjectKeysExpectedInActual(prefix, expected, actual, result);

        // If strict, check for vice-versa
        if (!mode.isExtensible()) {
            checkJsonObjectKeysActualInExpected(prefix, expected, actual, result);
        }
    }

    @Override
    public void compareValues(String prefix, Object expectedValue, Object actualValue, JSONCompareResult result)
            throws JSONException {
        if (expectedValue == actualValue) {
            return;
        }
        if ((expectedValue == null && actualValue != null) || (expectedValue != null && actualValue == null)) {
            result.fail(prefix, expectedValue, actualValue);
        }
        if (areNumbers(expectedValue, actualValue)) {
            if (areNotSameDoubles(expectedValue, actualValue)) {
                result.fail(prefix, expectedValue, actualValue);
            }
        } else if (expectedValue.getClass().isAssignableFrom(actualValue.getClass())) {
            if (expectedValue instanceof JSONArray) {
                compareJSONArray(prefix, (JSONArray) expectedValue, (JSONArray) actualValue, result);
            } else if (expectedValue instanceof JSONObject) {
                compareJSON(prefix, (JSONObject) expectedValue, (JSONObject) actualValue, result);
            } else if (!expectedValue.equals(actualValue)) {
                result.fail(prefix, expectedValue, actualValue);
            }
        } else {
            result.fail(prefix, expectedValue, actualValue);
        }
    }

    @Override
    public void compareJSONArray(String prefix, JSONArray expected, JSONArray actual, JSONCompareResult result)
            throws JSONException {
        if (expected.length() != actual.length()) {
            result.fail(prefix + "[]: Expected " + expected.length() + " values but got " + actual.length());
            return;
        } else if (expected.length() == 0) {
            return; // Nothing to compare
        }

        if (mode.hasStrictOrder()) {
            compareJSONArrayWithStrictOrder(prefix, expected, actual, result);
        } else if (allSimpleValues(expected)) {
            compareJSONArrayOfSimpleValues(prefix, expected, actual, result);
        } else if (allJSONObjects(expected)) {
            compareJSONArrayOfJsonObjects(prefix, expected, actual, result);
        } else {
            // An expensive last resort
            recursivelyCompareJSONArray(prefix, expected, actual, result);
        }
    }

    /**
     * Checks if the provided objects are of type Number.
     * 
     * @param expectedValue the expected value
     * @param actualValue   the actual value
     * @return true if both objects are of type Number; false otherwise
     */
    protected boolean areNumbers(Object expectedValue, Object actualValue) {
        return expectedValue instanceof Number && actualValue instanceof Number;
    }

    /**
     * Checks if the provided objects are of type Number and are not equal.
     * 
     * @param expectedValue the expected value
     * @param actualValue   the actual value
     * @return true if both objects are of type Number and are not equal; false
     *         otherwise
     */
    protected boolean areNotSameDoubles(Object expectedValue, Object actualValue) {
        return ((Number) expectedValue).doubleValue() != ((Number) actualValue).doubleValue();
    }
}
