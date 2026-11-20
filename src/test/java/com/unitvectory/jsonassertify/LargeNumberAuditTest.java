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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.unitvectory.jsonassertify.comparator.DefaultComparator;

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

    @Test
    void nonFiniteNumbersRetainDoubleComparisonBehavior() {
        DefaultComparator comparator = new DefaultComparator(JSONCompareMode.STRICT);
        JSONCompareResult equal = new JSONCompareResult();
        comparator.compareValues("n", Double.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, equal);
        assertTrue(equal.passed());

        JSONCompareResult different = new JSONCompareResult();
        comparator.compareValues("n", Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, different);
        assertTrue(different.failed());

        JSONCompareResult nan = new JSONCompareResult();
        comparator.compareValues("n", Double.NaN, Float.NaN, nan);
        assertTrue(nan.failed());
    }
}
