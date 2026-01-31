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
import org.junit.jupiter.api.Test;
import com.unitvectory.jsonassertify.comparator.DefaultComparator;
import com.unitvectory.jsonassertify.comparator.JSONComparator;

/**
 * Additional tests to improve code coverage.
 */
public class AdditionalCoverageTest {

    // ===== JSONAssert tests for missing coverage =====

    @Test
    public void testAssertEqualsExpectingArrayButPassingObject() throws JSONException {
        JSONObject actual = new JSONObject();
        actual.put("id", 1);
        // Passing an array expected string but a JSONObject actual should throw
        assertThrows(AssertionError.class, () -> 
            JSONAssert.assertEquals("[1,2,3]", actual, JSONCompareMode.LENIENT));
    }

    @Test
    public void testAssertEqualsExpectingArrayButPassingObjectWithMessage() throws JSONException {
        JSONObject actual = new JSONObject();
        actual.put("id", 1);
        // Passing an array expected string but a JSONObject actual should throw
        assertThrows(AssertionError.class, () -> 
            JSONAssert.assertEquals("Custom message", "[1,2,3]", actual, JSONCompareMode.LENIENT));
    }

    @Test
    public void testAssertNotEqualsExpectingArrayButPassingObject() throws JSONException {
        JSONObject actual = new JSONObject();
        actual.put("id", 1);
        assertThrows(AssertionError.class, () -> 
            JSONAssert.assertNotEquals("[1,2,3]", actual, JSONCompareMode.LENIENT));
    }

    @Test
    public void testAssertNotEqualsExpectingArrayButPassingObjectWithMessage() throws JSONException {
        JSONObject actual = new JSONObject();
        actual.put("id", 1);
        assertThrows(AssertionError.class, () -> 
            JSONAssert.assertNotEquals("Custom message", "[1,2,3]", actual, JSONCompareMode.LENIENT));
    }

    @Test
    public void testAssertEqualsExpectingObjectButPassingArray() throws JSONException {
        JSONArray actual = new JSONArray();
        actual.put(1);
        actual.put(2);
        // Passing an object expected string but a JSONArray actual should throw
        assertThrows(AssertionError.class, () -> 
            JSONAssert.assertEquals("{id:1}", actual, JSONCompareMode.LENIENT));
    }

    @Test
    public void testAssertNotEqualsExpectingObjectButPassingArray() throws JSONException {
        JSONArray actual = new JSONArray();
        actual.put(1);
        actual.put(2);
        assertThrows(AssertionError.class, () -> 
            JSONAssert.assertNotEquals("{id:1}", actual, JSONCompareMode.LENIENT));
    }

    @Test
    public void testAssertNotEqualsExpectingObjectButPassingArrayWithMessage() throws JSONException {
        JSONArray actual = new JSONArray();
        actual.put(1);
        actual.put(2);
        assertThrows(AssertionError.class, () -> 
            JSONAssert.assertNotEquals("Message", "{id:1}", actual, JSONCompareMode.LENIENT));
    }

    @Test
    public void testAssertEqualsStringNullExpected() throws JSONException {
        assertThrows(AssertionError.class, () -> 
            JSONAssert.assertEquals(null, "{id:1}", JSONCompareMode.LENIENT));
    }

    @Test
    public void testAssertEqualsStringNullActual() throws JSONException {
        assertThrows(AssertionError.class, () -> 
            JSONAssert.assertEquals("{id:1}", (String) null, JSONCompareMode.LENIENT));
    }

    @Test
    public void testAssertNotEqualsWithComparator() throws JSONException {
        JSONComparator comparator = new DefaultComparator(JSONCompareMode.LENIENT);
        // Different JSON should pass assertNotEquals
        JSONAssert.assertNotEquals("{id:1}", "{id:2}", comparator);
    }

    @Test
    public void testAssertNotEqualsWithComparatorAndMessage() throws JSONException {
        JSONComparator comparator = new DefaultComparator(JSONCompareMode.LENIENT);
        // Different JSON should pass assertNotEquals
        JSONAssert.assertNotEquals("Message", "{id:1}", "{id:2}", comparator);
    }

    @Test
    public void testAssertNotEqualsWithComparatorFailsWhenEqual() throws JSONException {
        JSONComparator comparator = new DefaultComparator(JSONCompareMode.LENIENT);
        // Same JSON should fail assertNotEquals
        assertThrows(AssertionError.class, () -> 
            JSONAssert.assertNotEquals("{id:1}", "{id:1}", comparator));
    }

    @Test
    public void testAssertNotEqualsWithComparatorAndMessageFailsWhenEqual() throws JSONException {
        JSONComparator comparator = new DefaultComparator(JSONCompareMode.LENIENT);
        // Same JSON should fail assertNotEquals
        assertThrows(AssertionError.class, () -> 
            JSONAssert.assertNotEquals("Message", "{id:1}", "{id:1}", comparator));
    }

    @Test
    public void testAssertEqualsJSONObjectWithComparator() throws JSONException {
        JSONComparator comparator = new DefaultComparator(JSONCompareMode.LENIENT);
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", 1);
        actual.put("id", 1);
        JSONAssert.assertEquals(expected, actual, comparator);
    }

    @Test
    public void testAssertEqualsJSONObjectWithComparatorAndMessage() throws JSONException {
        JSONComparator comparator = new DefaultComparator(JSONCompareMode.LENIENT);
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", 1);
        actual.put("id", 1);
        JSONAssert.assertEquals("Message", expected, actual, comparator);
    }

    @Test
    public void testAssertEqualsJSONObjectWithComparatorFails() throws JSONException {
        JSONComparator comparator = new DefaultComparator(JSONCompareMode.LENIENT);
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", 1);
        actual.put("id", 2);
        assertThrows(AssertionError.class, () -> 
            JSONAssert.assertEquals(expected, actual, comparator));
    }

    @Test
    public void testAssertNotEqualsJSONObjectWithComparator() throws JSONException {
        JSONComparator comparator = new DefaultComparator(JSONCompareMode.LENIENT);
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", 1);
        actual.put("id", 2);
        JSONAssert.assertNotEquals(expected, actual, comparator);
    }

    @Test
    public void testAssertNotEqualsJSONObjectWithComparatorAndMessage() throws JSONException {
        JSONComparator comparator = new DefaultComparator(JSONCompareMode.LENIENT);
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", 1);
        actual.put("id", 2);
        JSONAssert.assertNotEquals("Message", expected, actual, comparator);
    }

    @Test
    public void testAssertNotEqualsJSONObjectWithComparatorFails() throws JSONException {
        JSONComparator comparator = new DefaultComparator(JSONCompareMode.LENIENT);
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", 1);
        actual.put("id", 1);
        assertThrows(AssertionError.class, () -> 
            JSONAssert.assertNotEquals(expected, actual, comparator));
    }

    @Test
    public void testAssertEqualsJSONArrayWithCompareMode() throws JSONException {
        JSONArray expected = new JSONArray();
        JSONArray actual = new JSONArray();
        expected.put(1);
        expected.put(2);
        actual.put(1);
        actual.put(2);
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
    }

    @Test
    public void testAssertEqualsSameStringReference() throws JSONException {
        String json = "{id:1}";
        // Same reference should pass immediately
        JSONAssert.assertEquals(json, json, JSONCompareMode.LENIENT);
    }

    // ===== JSONParser tests =====

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
        assertThrows(JSONException.class, () -> 
            JSONParser.parseJSON("   "));
    }

    // ===== JSONCompareResult tests =====

    @Test
    @SuppressWarnings("deprecation")
    public void testJSONCompareResultDeprecatedGetters() throws JSONException {
        JSONCompareResult result = JSONCompare.compareJSON("{name:\"Pat\"}", "{name:\"Sue\"}", JSONCompareMode.STRICT);
        assertTrue(result.failed());
        
        // Test deprecated getters
        assertEquals("name", result.getField());
        assertEquals("Pat", result.getExpected());
        assertEquals("Sue", result.getActual());
    }

    @Test
    public void testJSONCompareResultToString() throws JSONException {
        JSONCompareResult result = JSONCompare.compareJSON("{id:1}", "{id:2}", JSONCompareMode.STRICT);
        assertTrue(result.failed());
        String message = result.toString();
        assertTrue(message.contains("Expected"));
        assertTrue(message.contains("got"));
    }

    @Test
    public void testJSONCompareResultPassedToString() throws JSONException {
        JSONCompareResult result = JSONCompare.compareJSON("{id:1}", "{id:1}", JSONCompareMode.STRICT);
        assertTrue(result.passed());
        // toString on passed result should return empty message
        String message = result.toString();
        assertEquals("", message);
    }

    // ===== Customization tests =====

    @Test
    public void testCustomizationStaticFactoryMethod() {
        ValueMatcher<Object> matcher = (o1, o2) -> true;
        Customization customization = Customization.customization("path.to.field", matcher);
        assertNotNull(customization);
        assertTrue(customization.appliesToPath("path.to.field"));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testCustomizationDeprecatedMatches() {
        ValueMatcher<Object> matcher = (o1, o2) -> o1.equals(o2);
        Customization customization = new Customization("test", matcher);
        // Test deprecated matches method
        assertTrue(customization.matches("value", "value"));
        assertFalse(customization.matches("value1", "value2"));
    }

    // ===== DefaultComparator tests for null values =====

    @Test
    public void testCompareValuesNullExpectedNonNullActual() throws JSONException {
        JSONCompareResult result = JSONCompare.compareJSON("{id:null}", "{id:1}", JSONCompareMode.STRICT);
        assertTrue(result.failed());
    }

    @Test
    public void testCompareValuesNonNullExpectedNullActual() throws JSONException {
        JSONCompareResult result = JSONCompare.compareJSON("{id:1}", "{id:null}", JSONCompareMode.STRICT);
        assertTrue(result.failed());
    }

    // ===== CustomComparator test for failed match =====

    @Test
    public void testCustomComparatorWithFailingMatcher() throws JSONException {
        ValueMatcher<Object> matcher = (o1, o2) -> false;
        Customization customization = new Customization("id", matcher);
        com.unitvectory.jsonassertify.comparator.CustomComparator comparator = 
            new com.unitvectory.jsonassertify.comparator.CustomComparator(JSONCompareMode.STRICT, customization);
        
        JSONCompareResult result = JSONCompare.compareJSON("{id:1}", "{id:1}", comparator);
        assertTrue(result.failed());
    }

    // ===== JSONCompareUtil tests =====

    @Test
    public void testAllJSONArraysTrue() throws JSONException {
        JSONArray array = new JSONArray();
        array.put(new JSONArray("[1,2]"));
        array.put(new JSONArray("[3,4]"));
        assertTrue(com.unitvectory.jsonassertify.comparator.JSONCompareUtil.allJSONArrays(array));
    }

    @Test
    public void testAllJSONArraysFalse() throws JSONException {
        JSONArray array = new JSONArray();
        array.put(new JSONArray("[1,2]"));
        array.put("not an array");
        assertFalse(com.unitvectory.jsonassertify.comparator.JSONCompareUtil.allJSONArrays(array));
    }

    // ===== AbstractComparator tests for nested arrays =====

    @Test
    public void testRecursiveCompareJSONArrayWithNestedArrays() throws JSONException {
        // Test case with nested arrays where order doesn't matter (LENIENT)
        JSONCompareResult result = JSONCompare.compareJSON(
            "{arr:[[1,2],[3,4]]}",
            "{arr:[[3,4],[1,2]]}",
            JSONCompareMode.LENIENT);
        assertTrue(result.passed());
    }

    // ===== JSONCompareResult additional tests =====

    @Test
    public void testJSONCompareResultIsFailureOnField() throws JSONException {
        // Test with a field mismatch
        JSONCompareResult result = JSONCompare.compareJSON("{id:1}", "{id:2}", JSONCompareMode.STRICT);
        assertTrue(result.failed());
        assertTrue(result.isFailureOnField());
    }

    @Test
    public void testJSONCompareResultIsFailureOnFieldFalse() throws JSONException {
        // Test with no failures
        JSONCompareResult result = JSONCompare.compareJSON("{id:1}", "{id:1}", JSONCompareMode.STRICT);
        assertTrue(result.passed());
        assertFalse(result.isFailureOnField());
    }

    @Test
    public void testJSONCompareResultIsMissingOnField() throws JSONException {
        // Test with a missing field
        JSONCompareResult result = JSONCompare.compareJSON("{id:1, name:\"Joe\"}", "{id:1}", JSONCompareMode.STRICT);
        assertTrue(result.failed());
        assertTrue(result.isMissingOnField());
    }

    @Test
    public void testJSONCompareResultIsMissingOnFieldFalse() throws JSONException {
        // Test with no missing fields
        JSONCompareResult result = JSONCompare.compareJSON("{id:1}", "{id:1}", JSONCompareMode.STRICT);
        assertTrue(result.passed());
        assertFalse(result.isMissingOnField());
    }

    @Test
    public void testJSONCompareResultIsUnexpectedOnField() throws JSONException {
        // Test with an unexpected field in NON_EXTENSIBLE mode
        JSONCompareResult result = JSONCompare.compareJSON("{id:1}", "{id:1, name:\"Joe\"}", JSONCompareMode.NON_EXTENSIBLE);
        assertTrue(result.failed());
        assertTrue(result.isUnexpectedOnField());
    }

    @Test
    public void testJSONCompareResultIsUnexpectedOnFieldFalse() throws JSONException {
        // Test with no unexpected fields
        JSONCompareResult result = JSONCompare.compareJSON("{id:1}", "{id:1}", JSONCompareMode.STRICT);
        assertTrue(result.passed());
        assertFalse(result.isUnexpectedOnField());
    }

    // ===== JSONAssert tests for JSONArray with CompareMode =====

    @Test
    public void testAssertEqualsJSONArrayWithCompareModeFails() throws JSONException {
        JSONArray expected = new JSONArray();
        JSONArray actual = new JSONArray();
        expected.put(1);
        expected.put(2);
        actual.put(1);
        actual.put(3);
        assertThrows(AssertionError.class, () -> 
            JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT));
    }

    // ===== ArrayValueMatcher tests =====

    @Test
    public void testArrayValueMatcherEqualMethod() {
        ArrayValueMatcher<Object> matcher = new ArrayValueMatcher<>(
            new DefaultComparator(JSONCompareMode.LENIENT));
        // The equal(T o1, T o2) method just returns false as it's not meant to be called
        assertFalse(matcher.equal("value1", "value2"));
    }
}
