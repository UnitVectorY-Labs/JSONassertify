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

/**
 * Models a failure when comparing two fields.
 */
public class FieldComparisonFailure {

    private final String _field;
    private final Object _expected;
    private final Object _actual;

    /**
     * Constructs a new FieldComparisonFailure.
     * 
     * @param field    the field that failed
     * @param expected the expected value
     * @param actual   the actual value
     */
    public FieldComparisonFailure(String field, Object expected, Object actual) {
        this._field = field;
        this._expected = expected;
        this._actual = actual;
    }

    /**
     * Gets the field that failed.
     * 
     * @return the field
     */
    public String getField() {
        return _field;
    }

    /**
     * Gets the expected value.
     * 
     * @return the expected value
     */
    public Object getExpected() {
        return _expected;
    }

    /**
     * Gets the actual value.
     * 
     * @return the actual value
     */
    public Object getActual() {
        return _actual;
    }
}
