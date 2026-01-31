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

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link JSONParser}
 * 
 * @author Carter Page
 * @author Corby Page
 * @author Solomon Duskis
 */
public class JSONParserTest {

    @Test
    public void testParseJSONObject() throws JSONException {
        Object result = JSONParser.parseJSON("{id:1}");
        assertTrue(result instanceof JSONObject);
    }

    @Test
    public void testParseJSONArray() throws JSONException {
        Object result = JSONParser.parseJSON("[1,2,3]");
        assertTrue(result instanceof JSONArray);
    }

    @Test
    public void testParseJSONString() throws JSONException {
        Object result = JSONParser.parseJSON("\"hello\"");
        assertTrue(result instanceof JSONString);
    }

    @Test
    public void testParseJSONNumber() throws JSONException {
        Object result = JSONParser.parseJSON("123");
        assertTrue(result instanceof JSONString);
    }

    @Test
    public void testParseJSONNegativeNumber() throws JSONException {
        Object result = JSONParser.parseJSON("-123");
        assertTrue(result instanceof JSONString);
    }

    @Test
    public void testParseJSONDecimalNumber() throws JSONException {
        Object result = JSONParser.parseJSON("123.456");
        assertTrue(result instanceof JSONString);
    }

    @Test
    public void testParseJSONScientificNotation() throws JSONException {
        Object result = JSONParser.parseJSON("1.5e10");
        assertTrue(result instanceof JSONString);
    }

    @Test
    public void testParseJSONUnparsableString() {
        assertThrows(JSONException.class, () -> 
            JSONParser.parseJSON("invalid json"));
    }

    @Test
    public void testParseJSONEmptyString() {
        assertThrows(JSONException.class, () -> 
            JSONParser.parseJSON(""));
    }

    @Test
    public void testParseJSONWhitespaceOnly() {
        // Test whitespace-only input, which is different from empty string 
        // as it tests the trim() behavior in the parser
        assertThrows(JSONException.class, () -> 
            JSONParser.parseJSON("   "));
    }

    @Test
    public void testParseJSONWithLeadingWhitespace() throws JSONException {
        Object result = JSONParser.parseJSON("   {id:1}");
        assertTrue(result instanceof JSONObject);
    }

    @Test
    public void testParseJSONWithTrailingWhitespace() throws JSONException {
        Object result = JSONParser.parseJSON("{id:1}   ");
        assertTrue(result instanceof JSONObject);
    }
}
