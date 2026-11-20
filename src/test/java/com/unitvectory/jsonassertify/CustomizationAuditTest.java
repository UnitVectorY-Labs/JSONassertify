package com.unitvectory.jsonassertify;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import com.unitvectory.jsonassertify.comparator.CustomComparator;

class CustomizationAuditTest {
    private CustomComparator comparator(JSONCompareMode mode) {
        return new CustomComparator(mode, new Customization("**.id", (actual, expected) -> true));
    }

    @Test void unorderedUniqueKey() {
        assertDoesNotThrow(() -> JSONAssert.assertEquals("[{\"id\":\"a\"}]", "[{\"id\":\"b\"}]", comparator(JSONCompareMode.LENIENT)));
    }

    @Test void orderedControl() {
        assertDoesNotThrow(() -> JSONAssert.assertEquals("[{\"id\":\"a\"}]", "[{\"id\":\"b\"}]", comparator(JSONCompareMode.STRICT_ORDER)));
    }

    @Test void nestedUnorderedObjectsAreMatchedOneToOne() {
        String expected = "{\"items\":[{\"id\":\"a\",\"value\":1},{\"id\":\"b\",\"value\":2}]}";
        String actual = "{\"items\":[{\"id\":\"x\",\"value\":2},{\"id\":\"y\",\"value\":1}]}";
        assertDoesNotThrow(() -> JSONAssert.assertEquals(expected, actual, comparator(JSONCompareMode.LENIENT)));
        assertThrows(AssertionError.class, () -> JSONAssert.assertEquals(expected,
                "{\"items\":[{\"id\":\"x\",\"value\":1},{\"id\":\"y\",\"value\":1}]}", comparator(JSONCompareMode.LENIENT)));
    }

    @Test void rejectedKeyAndOtherFieldDifferencesStillFail() {
        CustomComparator rejecting = new CustomComparator(JSONCompareMode.LENIENT,
                new Customization("**.id", (actual, expected) -> false));
        assertThrows(AssertionError.class, () -> JSONAssert.assertEquals("[{\"id\":\"a\"}]", "[{\"id\":\"a\"}]", rejecting));
        assertThrows(AssertionError.class, () -> JSONAssert.assertEquals("[{\"id\":\"a\",\"value\":1}]", "[{\"id\":\"b\",\"value\":2}]", comparator(JSONCompareMode.LENIENT)));
    }

    @Test void ambiguousMatchesCanBeReassigned() {
        CustomComparator custom = new CustomComparator(JSONCompareMode.LENIENT,
                new Customization("**.id", (actual, expected) -> "a".equals(expected) || "x".equals(actual)));
        assertDoesNotThrow(() -> JSONAssert.assertEquals("[{\"id\":\"a\"},{\"id\":\"b\"}]", "[{\"id\":\"x\"},{\"id\":\"y\"}]", custom));
    }

    @Test void duplicateActualKeysAndSpecificPaths() {
        CustomComparator custom = new CustomComparator(JSONCompareMode.LENIENT,
                new Customization("items[id=a].id", (actual, expected) -> true),
                new Customization("items[id=b].id", (actual, expected) -> true));
        assertDoesNotThrow(() -> JSONAssert.assertEquals(
                "{\"items\":[{\"id\":\"a\",\"value\":1},{\"id\":\"b\",\"value\":2}]}",
                "{\"items\":[{\"id\":\"x\",\"value\":2},{\"id\":\"x\",\"value\":1}]}", custom));
    }

    @Test void unrelatedCustomizationKeepsNormalKeyMatching() {
        CustomComparator custom = new CustomComparator(JSONCompareMode.LENIENT,
                new Customization("**.other", (actual, expected) -> true));
        assertDoesNotThrow(() -> JSONAssert.assertEquals(
                "[{\"id\":\"a\"},{\"id\":\"b\"}]", "[{\"id\":\"b\"},{\"id\":\"a\"}]", custom));
        assertThrows(AssertionError.class, () -> JSONAssert.assertEquals(
                "[{\"id\":\"a\"}]", "[{\"id\":\"b\"}]", custom));
    }

    @Test void fallsBackWhenNoUniqueKeyOrActualContainsNonObject() {
        assertDoesNotThrow(() -> JSONAssert.assertEquals(
                "[{\"id\":\"a\"},{\"id\":\"a\"}]", "[{\"id\":\"a\"},{\"id\":\"a\"}]", comparator(JSONCompareMode.LENIENT)));
        assertThrows(AssertionError.class, () -> JSONAssert.assertEquals(
                "[{\"id\":\"a\"}]", "[1]", comparator(JSONCompareMode.LENIENT)));
    }

    @Test void pathSpecificCustomizationAppliesOnlyToSelectedObject() {
        CustomComparator custom = new CustomComparator(JSONCompareMode.LENIENT,
                new Customization("items[id=b].id", (actual, expected) -> true));
        assertDoesNotThrow(() -> JSONAssert.assertEquals(
                "{\"items\":[{\"id\":\"a\"},{\"id\":\"b\"}]}",
                "{\"items\":[{\"id\":\"a\"},{\"id\":\"x\"}]}", custom));
        assertThrows(AssertionError.class, () -> JSONAssert.assertEquals(
                "{\"items\":[{\"id\":\"a\"},{\"id\":\"b\"}]}",
                "{\"items\":[{\"id\":\"x\"},{\"id\":\"y\"}]}", custom));
    }

    @Test void overlappingCandidatesCannotBeReused() {
        CustomComparator custom = new CustomComparator(JSONCompareMode.LENIENT,
                new Customization("**.id", (actual, expected) -> "x".equals(actual)));
        assertThrows(AssertionError.class, () -> JSONAssert.assertEquals(
                "[{\"id\":\"a\"},{\"id\":\"b\"}]", "[{\"id\":\"x\"},{\"id\":\"y\"}]", custom));
    }
    @Test void unmatchedObjectsRetainStructuredUniqueKeyDiagnostics() {
        JSONCompareResult result = JSONCompare.compareJSON(
                "[{\"id\":\"a\",\"value\":1},{\"id\":\"b\",\"value\":2}]",
                "[{\"id\":\"x\",\"value\":1},{\"id\":\"y\",\"value\":3}]",
                comparator(JSONCompareMode.LENIENT));
        org.junit.jupiter.api.Assertions.assertEquals(1, result.getFieldMissing().size());
        org.junit.jupiter.api.Assertions.assertEquals("[id=b]", result.getFieldMissing().get(0).getField());
        org.junit.jupiter.api.Assertions.assertEquals(1, result.getFieldUnexpected().size());
        org.junit.jupiter.api.Assertions.assertEquals("[id=y]", result.getFieldUnexpected().get(0).getField());
    }
}
