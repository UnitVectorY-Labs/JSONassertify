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

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import com.unitvectory.jsonassertify.JSONCompare;
import com.unitvectory.jsonassertify.JSONCompareMode;
import com.unitvectory.jsonassertify.JSONCompareResult;

/**
 * @author Ivan Zaytsev
 * @author Carter Page
 * @author Corby Page
 * @author Solomon Duskis
 */
public class CustomComparatorTest {

    private static class ArrayOfJsonObjectsComparator extends DefaultComparator {
        public ArrayOfJsonObjectsComparator(JSONCompareMode mode) {
            super(mode);
        }

        @Override
        public void compareJSONArray(String prefix, JSONArray expected, JSONArray actual, JSONCompareResult result)
                throws JSONException {
            compareJSONArrayOfJsonObjects(prefix, expected, actual, result);
        }
    }

    @Test
    public void testFullArrayComparison() throws Exception {
        JSONCompareResult compareResult = JSONCompare.compareJSON(
                "[{id:1}, {id:3}, {id:5}]",
                "[{id:1}, {id:3}, {id:6}, {id:7}]", new ArrayOfJsonObjectsComparator(JSONCompareMode.LENIENT));

        assertTrue(compareResult.failed());
        String message = compareResult.getMessage().replaceAll("\n", "");
        assertTrue(message.matches(".*id=5.*Expected.*id=6.*Unexpected.*id=7.*Unexpected.*"), message);
    }
}
