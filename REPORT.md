# JSONassertify: Upstream Issues Report

This report analyzes the open issues and pull requests from the original [skyscreamer/JSONassert](https://github.com/skyscreamer/JSONassert) project and provides recommendations on which ones should be incorporated into JSONassertify. Issues are categorized by priority and type.

> **Note:** JSONassertify is a fork of JSONassert version 1.5.1. The upstream project has 68 open issues and several open PRs. This report focuses on the most relevant and impactful issues.

---

## Critical Priority: Bugs

### 1. Long value comparison loses precision by converting to double — [#203](https://github.com/skyscreamer/JSONassert/issues/203)

**Type:** Bug  
**Impact:** High — Incorrect assertion results (false positives)

When comparing large `long` values, the `DefaultComparator.areNotSameDoubles()` method converts both values to `double` before comparison. This causes precision loss for long values that differ only in the last few digits, as `double` cannot represent all `long` values exactly.

**Example:** `3965376318358833921` and `3965376318358833920` are treated as equal because they map to the same `double` value.

**Affected code:** `DefaultComparator.areNotSameDoubles()` at line 127 in JSONassertify.

**Recommendation:** Modify `compareValues()` in `DefaultComparator` to compare numbers using their native types when possible. If both values are integral types (`Long`, `Integer`), compare them as `BigDecimal` or using `longValue()` instead of `doubleValue()`. Fall back to `double` comparison only for floating-point numbers. This is a correctness bug that can lead to tests passing when they should fail.

---

### 2. JSONParser doesn't support `null`, `false`, `true` — [#205](https://github.com/skyscreamer/JSONassert/issues/205)

**Type:** Bug  
**Impact:** High — Prevents parsing valid JSON primitive values

The `JSONParser.parseJSON()` method only handles JSON objects (`{}`), arrays (`[]`), strings (`""`), and numbers. It throws a `JSONException` for valid JSON values `null`, `true`, and `false`. This prevents use cases like comparing primitive JSON responses (e.g., Spring's `WebTestClient.expectBody().json("null")`).

**Affected code:** `JSONParser.parseJSON()` in JSONassertify.

**Open PR:** [#207](https://github.com/skyscreamer/JSONassert/pull/207) — Adds boolean support to the parser (but not `null`).

**Recommendation:** Extend `JSONParser.parseJSON()` to handle `true`, `false`, and `null` as valid JSON values. PR #207 provides a partial fix for booleans that can be referenced. Full support for `null` should also be added.

---

### 3. Customization wildcard paths don't match trailing wildcards — [#204](https://github.com/skyscreamer/JSONassert/issues/204)

**Type:** Bug  
**Impact:** Medium — Customizations with trailing wildcards silently don't work

A `Customization` with path `foo.*` does not match `foo.bar` as expected. The wildcard pattern building in `Customization.buildPattern()` fails when the wildcard is at the end of the path because `String.split()` discards trailing empty strings.

**Affected code:** `Customization.buildPattern()` method in JSONassertify.

**Recommendation:** Fix the pattern building logic to handle trailing wildcards correctly. The `split()` call with the wildcard regex needs a `-1` limit parameter to preserve trailing empty strings, or the pattern needs to be adjusted to correctly handle wildcards at the end of a path.

---

### 4. NullPointerException with invalid JSON containing trailing commas — [#130](https://github.com/skyscreamer/JSONassert/issues/130)

**Type:** Bug  
**Impact:** Medium — Unexpected exception type

Comparing invalid JSON like `[{id:1},]` with `assertEquals` in strict mode throws a `NullPointerException` instead of a proper `JSONException`. This is because the underlying JSON parser creates a `null` array element for the trailing comma, and the comparison code doesn't handle `null` elements properly.

**Affected code:** `AbstractComparator.compareJSONArrayWithStrictOrder()` and related array comparison methods.

**Recommendation:** Add null-safety checks in array comparison methods, or catch and wrap `NullPointerException` into a more informative `JSONException`. While the input JSON is invalid, the error message should be helpful rather than a raw NPE. This may already be partially mitigated by the switch to the `org.json` library in JSONassertify since `org.json` handles trailing commas differently.

---

### 5. Trailing characters after JSON closing tags are silently ignored — [#113](https://github.com/skyscreamer/JSONassert/issues/113)

**Type:** Bug  
**Impact:** Medium — False positive comparisons

Comparing `{}` with `{}a` returns success because the underlying JSON parser ignores trailing characters. This means malformed JSON can pass assertion checks.

**Recommendation:** Investigate whether the current `org.json` library (used by JSONassertify) exhibits this same behavior. If so, consider adding a validation step in `JSONParser.parseJSON()` to verify that the entire input string was consumed during parsing.

---

### 6. Array comparison in LENIENT mode doesn't report field-level changes — [#200](https://github.com/skyscreamer/JSONassert/issues/200)

**Type:** Bug  
**Impact:** Medium — Missing failure details

When comparing arrays of simple values in LENIENT/NON_EXTENSIBLE mode, changes within array elements are not reported in `getFieldFailures()`. The array comparison uses set-based matching which doesn't map old values to new values for reporting purposes.

**Recommendation:** This is a design limitation of the set-based array comparison. Document this behavior or consider enhancing `compareJSONArrayOfSimpleValues()` to produce more detailed failure information. This is not a simple fix and could be deferred.

---

### 7. Exception with ArrayValueMatcher comparing non-empty actual to empty expected array — [#206](https://github.com/skyscreamer/JSONassert/issues/206)

**Type:** Bug  
**Impact:** Low-Medium — Exception in edge case with custom comparators

When using `ArrayValueMatcher` with a `CustomComparator`, comparing an actual JSON with a non-empty array to an expected JSON with an empty array causes an exception.

**Recommendation:** Add bounds checking in the `ArrayValueMatcher` to handle the case where the expected array is empty but the actual array is not.

---

### 8. `getFieldMissing()` and `getFieldUnexpected()` return empty lists — [#102](https://github.com/skyscreamer/JSONassert/issues/102)

**Type:** Bug  
**Impact:** Medium — API does not work as expected

The `JSONCompareResult.getFieldMissing()` and `getFieldUnexpected()` methods return empty lists even when fields are missing or unexpected. This is because the `checkJsonObjectKeysExpectedInActual()` and `checkJsonObjectKeysActualInExpected()` methods in `AbstractComparator` pass the field name as the `expected`/`actual` parameter to `missing()`/`unexpected()` rather than the field value.

**Affected code:** `AbstractComparator.checkJsonObjectKeysExpectedInActual()` and `checkJsonObjectKeysActualInExpected()` in JSONassertify.

**Recommendation:** Review the current implementation. Looking at the code, `missing()` and `unexpected()` are called with the key name (a String) rather than the actual value object. The `FieldComparisonFailure` objects are being created, so the lists should not be empty. This may have already been fixed or may need further investigation to reproduce. Verify by writing a test case.

---

### 9. Long/Integer type mismatch in array unique key lookup — [#85](https://github.com/skyscreamer/JSONassert/issues/85)

**Type:** Bug  
**Impact:** Medium — Array comparison fails when integer types differ

When comparing JSON arrays of objects, the unique key identification uses `Map<Object, JSONObject>`. If the expected JSON has `Integer` keys and the actual has `Long` keys (or vice versa), the map lookups fail because `Integer(1)` does not equal `Long(1)`.

**Affected code:** `AbstractComparator.compareJSONArrayOfJsonObjects()` and `JSONCompareUtil.arrayOfJsonObjectToMap()`.

**Recommendation:** Normalize numeric keys to a common type (e.g., `Long` or `BigDecimal`) when building the value maps for array comparison. This is related to issue #203 (number comparison) and could be addressed together.

---

### 10. No detailed message when JSONString comparison fails — [#101](https://github.com/skyscreamer/JSONassert/issues/101)

**Type:** Bug  
**Impact:** Low-Medium — Poor error messages

When two `JSONString` objects fail comparison, the `JSONCompare.compareJson()` method calls `result.fail("")` without including the expected and actual values, making it difficult to diagnose test failures.

**Affected code:** `JSONCompare.compareJson()` at line 121 in JSONassertify.

**Recommendation:** Change `result.fail("")` to `result.fail("", expectedJson, actualJson)` to include the expected and actual JSON string values in the failure message. This is a simple one-line fix with high value.

---

## Medium Priority: Bugs & Improvements

### 11. Customizations not applied during array unique key selection — [#109](https://github.com/skyscreamer/JSONassert/issues/109)

**Type:** Bug  
**Impact:** Medium — Custom comparators ignored during array matching

When using `CustomComparator` with customizations to ignore certain fields (like timestamps), the `AbstractComparator.compareJSONArrayOfJsonObjects()` method may select one of those ignored fields as the "unique key" for matching array elements. This causes the comparison to fail because the ignored field values differ.

**Open PR:** [#177](https://github.com/skyscreamer/JSONassert/pull/177) — Proposes allowing users to specify unique keys.

**Recommendation:** This is a significant design issue. Consider either: (a) allowing users to specify which field to use as the unique key for array element matching, or (b) excluding customized (ignored) fields from the unique key selection process. Option (b) is more transparent but requires the `AbstractComparator` to be aware of customizations.

---

### 12. Duplicate JSON fields should cause assertion failure — [#95](https://github.com/skyscreamer/JSONassert/issues/95)

**Type:** Bug  
**Impact:** Low — Already partially addressed

JSON with duplicate fields like `{"field":"val2","field":"val1"}` was being silently accepted. The last value would win, potentially masking bugs in JSON generation.

**Recommendation:** JSONassertify's migration to `org.json` already addresses this — the `org.json` library throws an error on duplicate keys. This should be verified with a test case and documented. No further action needed if confirmed.

---

### 13. `JSONObject.getLong` broken for very large values — [#25](https://github.com/skyscreamer/JSONassert/issues/25)

**Type:** Bug  
**Impact:** Low — Upstream JSON library issue

Very large long values don't parse correctly through `getLong()`. This was a problem in the old `android-json` library.

**Recommendation:** Verify that the current `org.json` library used by JSONassertify handles this correctly. If so, no action needed. This is primarily a JSON library issue rather than a JSONassertify issue.

---

## Low Priority: Enhancements

### 14. Allow field values to be ignored by JSON path — [#15](https://github.com/skyscreamer/JSONassert/issues/15)

**Type:** Enhancement  
**Impact:** High demand (67 comments, oldest open issue)

Users want the ability to ignore specific fields during comparison by specifying a JSON path. This is by far the most requested feature.

**Recommendation:** The `CustomComparator` with `Customization` already provides some of this functionality. However, the API could be more user-friendly. Consider adding convenience methods like `JSONAssert.assertEquals(expected, actual, mode, ignorePaths("timestamp", "id"))`. This is a significant feature addition that should be planned carefully.

---

### 15. Precision control for float/double comparison — [#47](https://github.com/skyscreamer/JSONassert/issues/47)

**Type:** Enhancement  
**Impact:** Medium demand

Users want the ability to specify an epsilon/tolerance when comparing floating point numbers.

**Recommendation:** Could be implemented as an option on `DefaultComparator` or as a new `NumericComparator`. This is a useful enhancement but not critical.

---

### 16. Add support for TestNG — [#3](https://github.com/skyscreamer/JSONassert/issues/3)

**Type:** Enhancement  
**Impact:** Low

Request for TestNG support alongside JUnit.

**Recommendation:** The core comparison logic (`JSONCompare`) is already framework-independent. Only `JSONAssert` has JUnit dependencies. A `JSONAssertTestNG` class could be added, but this is low priority. Users can already use `JSONCompare` directly with any framework.

---

### 17. Hamcrest Matchers — [#97](https://github.com/skyscreamer/JSONassert/issues/97)

**Type:** Enhancement  
**Impact:** Low

Request for Hamcrest-style matchers for more fluent assertions.

**Recommendation:** Nice-to-have but low priority. Users who want Hamcrest can write thin wrapper matchers around the existing API.

---

### 18. More verbose diff output — [#91](https://github.com/skyscreamer/JSONassert/issues/91), [#183](https://github.com/skyscreamer/JSONassert/issues/183)

**Type:** Enhancement  
**Impact:** Low-Medium

Multiple requests for better diff output when assertions fail, such as side-by-side comparison or unified diff format.

**Recommendation:** Improving error messages is valuable but is a significant effort. Could be considered for a future version. The current error messages are functional.

---

### 19. Lazily-constructed assertion messages — [#185](https://github.com/skyscreamer/JSONassert/issues/185)

**Type:** Enhancement  
**Impact:** Low

Request to support JUnit 5 style lazy message construction for better performance.

**Recommendation:** Low priority. The performance impact of eager message construction is negligible in test contexts.

---

### 20. Better error messages when JSON object count is high — [#94](https://github.com/skyscreamer/JSONassert/issues/94)

**Type:** Enhancement  
**Impact:** Low

When comparing large arrays (>1500 objects), error messages lack detail about why elements don't match.

**Recommendation:** This is related to the expensive `recursivelyCompareJSONArray()` method. Consider providing an option to enable more detailed (but slower) comparison for debugging. Low priority.

---

## Already Addressed in JSONassertify

The following upstream issues have already been addressed by JSONassertify's existing changes:

| Issue | Description | How Addressed |
|-------|-------------|---------------|
| [#99](https://github.com/skyscreamer/JSONassert/issues/99) | Dependency on `android-json` instead of `org.json` | JSONassertify uses `org.json:json` |
| [#89](https://github.com/skyscreamer/JSONassert/issues/89) | Ability to choose JSON library | Resolved by switching to standard `org.json` |
| [#93](https://github.com/skyscreamer/JSONassert/issues/93) | Duplicate `org.json.JSONString` class | No longer ships custom `org.json` classes |
| [#190](https://github.com/skyscreamer/JSONassert/issues/190) | Version 1.5.2 requires Java 21 | JSONassertify targets Java 8 |
| [#166](https://github.com/skyscreamer/JSONassert/pull/166) | GitHub Actions CI pipeline | JSONassertify already uses GitHub Actions |
| [#95](https://github.com/skyscreamer/JSONassert/issues/95) | Duplicate JSON fields not rejected | The `org.json` library rejects duplicates |

---

## Summary of Recommended Actions

### High Priority (Should Fix)

1. **Fix long/number comparison precision loss** ([#203](https://github.com/skyscreamer/JSONassert/issues/203)) — Correctness bug causing false positives
2. **Add `null`, `true`, `false` support to JSONParser** ([#205](https://github.com/skyscreamer/JSONassert/issues/205)) — Prevents valid JSON from being parsed
3. **Fix trailing wildcard matching in Customization** ([#204](https://github.com/skyscreamer/JSONassert/issues/204)) — Customizations silently fail
4. **Improve JSONString comparison failure messages** ([#101](https://github.com/skyscreamer/JSONassert/issues/101)) — Simple fix, high value

### Medium Priority (Should Consider)

5. **Fix Integer/Long mismatch in array key lookup** ([#85](https://github.com/skyscreamer/JSONassert/issues/85)) — Correctness issue
6. **Handle customizations during array unique key selection** ([#109](https://github.com/skyscreamer/JSONassert/issues/109)) — Common use case fails
7. **Handle null elements in array comparison** ([#130](https://github.com/skyscreamer/JSONassert/issues/130)) — Better error handling
8. **Validate trailing characters after JSON** ([#113](https://github.com/skyscreamer/JSONassert/issues/113)) — Validate against `org.json` behavior

### Low Priority (Nice to Have)

9. Convenience API for ignoring fields ([#15](https://github.com/skyscreamer/JSONassert/issues/15))
10. Float/double comparison precision control ([#47](https://github.com/skyscreamer/JSONassert/issues/47))
11. Improved diff output ([#91](https://github.com/skyscreamer/JSONassert/issues/91), [#183](https://github.com/skyscreamer/JSONassert/issues/183))
12. ArrayValueMatcher empty array handling ([#206](https://github.com/skyscreamer/JSONassert/issues/206))
