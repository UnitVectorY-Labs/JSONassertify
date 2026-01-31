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

import org.json.JSONException;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link JSONCompareResult}
 * 
 * @author Carter Page
 * @author Corby Page
 * @author Solomon Duskis
 */
public class JSONCompareResultTest {

    @Test
    @SuppressWarnings("deprecation")
    public void testDeprecatedGetters() throws JSONException {
        JSONCompareResult result = JSONCompare.compareJSON("{name:\"Pat\"}", "{name:\"Sue\"}", JSONCompareMode.STRICT);
        assertTrue(result.failed());
        
        // Test deprecated getters
        assertEquals("name", result.getField());
        assertEquals("Pat", result.getExpected());
        assertEquals("Sue", result.getActual());
    }

    @Test
    public void testToStringWithFailure() throws JSONException {
        JSONCompareResult result = JSONCompare.compareJSON("{id:1}", "{id:2}", JSONCompareMode.STRICT);
        assertTrue(result.failed());
        String message = result.toString();
        assertTrue(message.contains("Expected"));
        assertTrue(message.contains("got"));
    }

    @Test
    public void testToStringWhenPassed() throws JSONException {
        JSONCompareResult result = JSONCompare.compareJSON("{id:1}", "{id:1}", JSONCompareMode.STRICT);
        assertTrue(result.passed());
        // toString on passed result should return empty message
        String message = result.toString();
        assertEquals("", message);
    }

    @Test
    public void testIsFailureOnFieldTrue() throws JSONException {
        JSONCompareResult result = JSONCompare.compareJSON("{id:1}", "{id:2}", JSONCompareMode.STRICT);
        assertTrue(result.failed());
        assertTrue(result.isFailureOnField());
    }

    @Test
    public void testIsFailureOnFieldFalse() throws JSONException {
        JSONCompareResult result = JSONCompare.compareJSON("{id:1}", "{id:1}", JSONCompareMode.STRICT);
        assertTrue(result.passed());
        assertFalse(result.isFailureOnField());
    }

    @Test
    public void testIsMissingOnFieldTrue() throws JSONException {
        JSONCompareResult result = JSONCompare.compareJSON("{id:1, name:\"Joe\"}", "{id:1}", JSONCompareMode.STRICT);
        assertTrue(result.failed());
        assertTrue(result.isMissingOnField());
    }

    @Test
    public void testIsMissingOnFieldFalse() throws JSONException {
        JSONCompareResult result = JSONCompare.compareJSON("{id:1}", "{id:1}", JSONCompareMode.STRICT);
        assertTrue(result.passed());
        assertFalse(result.isMissingOnField());
    }

    @Test
    public void testIsUnexpectedOnFieldTrue() throws JSONException {
        JSONCompareResult result = JSONCompare.compareJSON("{id:1}", "{id:1, name:\"Joe\"}", JSONCompareMode.NON_EXTENSIBLE);
        assertTrue(result.failed());
        assertTrue(result.isUnexpectedOnField());
    }

    @Test
    public void testIsUnexpectedOnFieldFalse() throws JSONException {
        JSONCompareResult result = JSONCompare.compareJSON("{id:1}", "{id:1}", JSONCompareMode.STRICT);
        assertTrue(result.passed());
        assertFalse(result.isUnexpectedOnField());
    }

    @Test
    public void testGetFieldFailures() throws JSONException {
        JSONCompareResult result = JSONCompare.compareJSON("{id:1}", "{id:2}", JSONCompareMode.STRICT);
        assertTrue(result.failed());
        assertEquals(1, result.getFieldFailures().size());
        assertEquals("id", result.getFieldFailures().get(0).getField());
    }

    @Test
    public void testGetFieldMissing() throws JSONException {
        JSONCompareResult result = JSONCompare.compareJSON("{id:1, name:\"Joe\"}", "{id:1}", JSONCompareMode.STRICT);
        assertTrue(result.failed());
        assertEquals(1, result.getFieldMissing().size());
    }

    @Test
    public void testGetFieldUnexpected() throws JSONException {
        JSONCompareResult result = JSONCompare.compareJSON("{id:1}", "{id:1, name:\"Joe\"}", JSONCompareMode.NON_EXTENSIBLE);
        assertTrue(result.failed());
        assertEquals(1, result.getFieldUnexpected().size());
    }

    @Test
    public void testGetMessage() throws JSONException {
        JSONCompareResult result = JSONCompare.compareJSON("{id:1}", "{id:2}", JSONCompareMode.STRICT);
        assertTrue(result.failed());
        String message = result.getMessage();
        assertNotNull(message);
        assertTrue(message.length() > 0);
    }

    @Test
    public void testPassedAndFailed() throws JSONException {
        JSONCompareResult passedResult = JSONCompare.compareJSON("{id:1}", "{id:1}", JSONCompareMode.STRICT);
        assertTrue(passedResult.passed());
        assertFalse(passedResult.failed());

        JSONCompareResult failedResult = JSONCompare.compareJSON("{id:1}", "{id:2}", JSONCompareMode.STRICT);
        assertFalse(failedResult.passed());
        assertTrue(failedResult.failed());
    }
}
