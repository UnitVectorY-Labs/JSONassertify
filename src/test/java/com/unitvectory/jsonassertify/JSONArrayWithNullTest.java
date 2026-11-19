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
package com.unitvectory.jsonassertify;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@code JSONArray}.
 * 
 * @author Carter Page
 * @author Corby Page
 * @author Solomon Duskis
 */
public class JSONArrayWithNullTest {

    @Test
    public void strictNullMismatchesReportFailures() {
        JSONCompareResult expectedNull = JSONCompare.compareJSON("[null]", "[1]", JSONCompareMode.STRICT);
        JSONCompareResult actualNull = JSONCompare.compareJSON("[1]", "[null]", JSONCompareMode.STRICT);

        assertTrue(expectedNull.failed());
        assertEquals("[0]\nExpected: null\n     got: 1\n", expectedNull.getMessage());
        assertEquals(1, expectedNull.getFieldFailures().size());
        assertEquals("[0]", expectedNull.getFieldFailures().get(0).getField());
        assertTrue(actualNull.failed());
        assertEquals("[0]\nExpected: 1\n     got: null\n", actualNull.getMessage());
        assertEquals(1, actualNull.getFieldFailures().size());
        assertTrue(JSONCompare.compareJSON("[null]", "[null]", JSONCompareMode.STRICT).passed());
        assertTrue(JSONCompare.compareJSON("[1]", "[2]", JSONCompareMode.STRICT).failed());
    }

    @Test
    public void testJSONArrayWithNullValue() throws JSONException {
        JSONArray jsonArray1 = getJSONArray1();
        JSONArray jsonArray2 = getJSONArray2();

        JSONAssert.assertEquals(jsonArray1, jsonArray2, true);
        JSONAssert.assertEquals(jsonArray1, jsonArray2, false);
    }

    @Test
    public void testJSONArrayWithNullValueAndJsonObject() throws JSONException {
        JSONArray jsonArray1 = getJSONArray1();
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("hey", "value");

        JSONArray jsonArray2 = getJSONArray2();
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("hey", "value");

        JSONAssert.assertEquals(jsonArray1, jsonArray2, true);
        JSONAssert.assertEquals(jsonArray1, jsonArray2, false);
    }

    private JSONArray getJSONArray1() {
        JSONArray jsonArray1 = new JSONArray();
        jsonArray1.put(1);
        jsonArray1.put(JSONObject.NULL);
        jsonArray1.put(3);
        jsonArray1.put(2);
        return jsonArray1;
    }

    private JSONArray getJSONArray2() {
        JSONArray jsonArray1 = new JSONArray();
        jsonArray1.put(1);
        jsonArray1.put(JSONObject.NULL);
        jsonArray1.put(3);
        jsonArray1.put(2);
        return jsonArray1;
    }
}
