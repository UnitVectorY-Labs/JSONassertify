package com.unitvectory.jsonassertify;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class LargeNumberAuditTest {
    @Test
    void distinctLargeIntegersMustDiffer() {
        assertThrows(AssertionError.class, () -> JSONAssert.assertEquals(
                "{\"n\":9007199254740992}", "{\"n\":9007199254740993}", true));
    }

    @Test
    void distinctDecimalsMustDiffer() {
        assertThrows(AssertionError.class, () -> JSONAssert.assertEquals(
                "{\"n\":1.0000000000000001}", "{\"n\":1.0000000000000000}", true));
    }

    @Test
    void distinctNumbersInOrderedArraysMustDiffer() {
        assertThrows(AssertionError.class, () -> JSONAssert.assertEquals(
                "[1e400]", "[2e400]", true));
    }

    @Test
    void equivalentNumbersWithDifferentRepresentationsCompareEqual() {
        JSONAssert.assertEquals("{\"n\":1}", "{\"n\":1.00}", true);
        JSONAssert.assertEquals("[100000000000000000000]", "[1e20]", true);
    }
}
