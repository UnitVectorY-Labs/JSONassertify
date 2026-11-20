package com.unitvectory.jsonassertify;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONException;
import org.junit.jupiter.api.Test;

class ScalarLiteralTest {
    @Test
    void comparesTopLevelLiteralsInEveryMode() {
        for (JSONCompareMode mode : JSONCompareMode.values()) {
            for (String literal : new String[] {"true", "false", "null"}) {
                assertTrue(JSONCompare.compareJSON(literal, literal, mode).passed());
                assertTrue(JSONCompare.compareJSON(" \t" + literal + "\n", literal, mode).passed());
            }
            assertTrue(JSONCompare.compareJSON("{\"t\":true,\"f\":false,\"n\":null}",
                    "{\"t\":true,\"f\":false,\"n\":null}", mode).passed());
        }
    }

    @Test
    void reportsDifferentLiteralsAndTypes() {
        for (String[] pair : new String[][] {{"true", "false"}, {"null", "false"},
                {"true", "\"true\""}, {"null", "{}"}, {"[]", "false"}}) {
            assertTrue(JSONCompare.compareJSON(pair[0], pair[1], JSONCompareMode.LENIENT).failed());
            assertTrue(JSONCompare.compareJSON(pair[1], pair[0], JSONCompareMode.LENIENT).failed());
        }
    }

    @Test
    void rejectsInvalidLiteralTokens() {
        for (String value : new String[] {"True", "FALSE", "NULL", "truex", "null false"}) {
            assertThrows(JSONException.class, () -> JSONParser.parseJSON(value));
        }
    }

    @Test
    void ignoresSurroundingWhitespaceForOtherScalars() {
        for (String scalar : new String[] {"123", "-1.5e2", "\"hello world\"", "\" padded \""}) {
            assertTrue(JSONCompare.compareJSON(" \t" + scalar + "\n", scalar, JSONCompareMode.LENIENT).passed());
            assertTrue(JSONCompare.compareJSON(scalar, "\n" + scalar + " ", JSONCompareMode.LENIENT).passed());
        }
        assertTrue(JSONCompare.compareJSON("\" padded \"", "\"padded\"", JSONCompareMode.LENIENT).failed());
    }
}
