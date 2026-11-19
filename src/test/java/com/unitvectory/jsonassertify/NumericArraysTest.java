package com.unitvectory.jsonassertify;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class NumericArraysTest {
    @Test
    void unorderedNumbersUseNumericEqualityAndPreserveCounts() {
        assertDoesNotThrow(() -> JSONAssert.assertEquals("[1,1.0,1e2,-0.0]", "[100,1.00,0,1]", JSONCompareMode.LENIENT));
        assertThrows(AssertionError.class, () -> JSONAssert.assertEquals("[1,1.0,2]", "[1,2,2.0]", JSONCompareMode.LENIENT));
        assertThrows(AssertionError.class, () -> JSONAssert.assertEquals("[1]", "[\"1\"]", JSONCompareMode.LENIENT));
    }

    @Test
    void unorderedObjectKeysUseNumericEquality() {
        assertDoesNotThrow(() -> JSONAssert.assertEquals("[{\"id\":1},{\"id\":1e2}]", "[{\"id\":100},{\"id\":1.0}]", JSONCompareMode.LENIENT));
        assertThrows(AssertionError.class, () -> JSONAssert.assertEquals("[{\"id\":1}]", "[{\"id\":2.0}]", JSONCompareMode.LENIENT));
    }

    @Test
    void equivalentObjectKeysAreNotConsideredUnique() {
        assertDoesNotThrow(() -> JSONAssert.assertEquals("[{\"id\":1,\"v\":\"a\"},{\"id\":1.0,\"v\":\"b\"}]", "[{\"id\":1.00,\"v\":\"b\"},{\"id\":1,\"v\":\"a\"}]", JSONCompareMode.LENIENT));
    }

    @Test
    void mixedAndNestedArraysUseNumericEquality() {
        assertDoesNotThrow(() -> JSONAssert.assertEquals("[1,{\"x\":2},[1e2]]", "[[100],1.0,{\"x\":2.0}]", JSONCompareMode.LENIENT));
    }

    @Test
    void orderedArraysAndFieldsStillCompareNumbers() {
        assertDoesNotThrow(() -> JSONAssert.assertEquals("[1,1e2,{\"id\":1}]", "[1.0,100,{\"id\":1.0}]", JSONCompareMode.STRICT_ORDER));
        assertDoesNotThrow(() -> JSONAssert.assertEquals("{\"id\":1,\"n\":1e2}", "{\"id\":1.0,\"n\":100}", JSONCompareMode.LENIENT));
    }
}
