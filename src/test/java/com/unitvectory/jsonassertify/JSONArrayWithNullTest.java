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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@code JSONArray}.
 * 
 * @author Carter Page
 * @author Corby Page
 * @author Solomon Duskis
 */
public class JSONArrayWithNullTest {

    @Test
    public void unorderedMixedArraysConsumeEachMatchingElementOnce() {
        for (JSONCompareMode mode : new JSONCompareMode[] { JSONCompareMode.LENIENT, JSONCompareMode.NON_EXTENSIBLE }) {
            assertFalse(JSONCompare.compareJSON("[null,null,{}]", "[null,{},{}]", mode).passed());
            assertFalse(JSONCompare.compareJSON("[null,{},{}]", "[null,null,{}]", mode).passed());
            assertTrue(JSONCompare.compareJSON("[null,null,{}]", "[null,{},null]", mode).passed());
            assertFalse(JSONCompare.compareJSON("[[null,null,{}],1]", "[1,[null,{},{}]]", mode).passed());
        }
    }

    @Test
    public void unorderedMixedArraysDoNotReuseIdenticalObjectReferences() {
        JSONObject shared = new JSONObject();
        JSONArray expected = new JSONArray().put(shared).put(shared).put(1);
        JSONArray actual = new JSONArray().put(shared).put(new JSONObject().put("extra", true)).put(1);
        assertFalse(JSONCompare.compareJSON(expected, actual, JSONCompareMode.NON_EXTENSIBLE).passed());
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
