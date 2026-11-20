package com.unitvectory.jsonassertify;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import com.unitvectory.jsonassertify.comparator.CustomComparator;

class RecursiveCustomizationPathTest {
    private CustomComparator comparator(JSONCompareMode mode, String path) {
        return new CustomComparator(mode, new Customization(path, (actual, expected) -> true));
    }

    @Test
    void mixedArrayPreservesObjectPath() {
        String expected = "{\"items\":[{\"id\":\"a\"},null]}";
        String actual = "{\"items\":[{\"id\":\"b\"},null]}";
        assertDoesNotThrow(() -> JSONAssert.assertEquals(expected, actual, comparator(JSONCompareMode.LENIENT, "items[0].id")));
        assertDoesNotThrow(() -> JSONAssert.assertEquals(expected, actual, comparator(JSONCompareMode.STRICT_ORDER, "items[0].id")));
        assertThrows(AssertionError.class, () -> JSONAssert.assertEquals(expected, actual, comparator(JSONCompareMode.LENIENT, "id")));
    }

    @Test
    void nestedArrayPreservesPathAndUsesExpectedIndex() {
        String expected = "{\"items\":[[{\"id\":\"a\"},null],42]}";
        String actual = "{\"items\":[42,[null,{\"id\":\"b\"}]]}";
        assertDoesNotThrow(() -> JSONAssert.assertEquals(expected, actual, comparator(JSONCompareMode.LENIENT, "items[0][0].id")));
        assertThrows(AssertionError.class, () -> JSONAssert.assertEquals(expected, actual, comparator(JSONCompareMode.LENIENT, "items[1][1].id")));
    }

    @Test
    void failedCandidateDoesNotPolluteSuccessfulMatch() {
        String expected = "{\"items\":[{\"kind\":\"x\",\"id\":\"a\"},{\"kind\":\"y\",\"id\":\"same\"},null]}";
        String actual = "{\"items\":[{\"kind\":\"y\",\"id\":\"same\"},{\"kind\":\"x\",\"id\":\"b\"},null]}";
        assertDoesNotThrow(() -> JSONAssert.assertEquals(expected, actual, comparator(JSONCompareMode.LENIENT, "items[0].id")));
    }
}
