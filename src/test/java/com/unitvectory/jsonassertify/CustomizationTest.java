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
 * Unit tests for {@link Customization}
 */
public class CustomizationTest {

    @Test
    public void testStaticFactoryMethod() {
        ValueMatcher<Object> matcher = (o1, o2) -> true;
        Customization customization = Customization.customization("path.to.field", matcher);
        assertNotNull(customization);
        assertTrue(customization.appliesToPath("path.to.field"));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testDeprecatedMatchesMethod() {
        ValueMatcher<Object> matcher = (o1, o2) -> o1.equals(o2);
        Customization customization = new Customization("test", matcher);
        // Test deprecated matches method
        assertTrue(customization.matches("value", "value"));
        assertFalse(customization.matches("value1", "value2"));
    }

    @Test
    public void testAppliesToPathExactMatch() {
        ValueMatcher<Object> matcher = (o1, o2) -> true;
        Customization customization = new Customization("user.name", matcher);
        assertTrue(customization.appliesToPath("user.name"));
        assertFalse(customization.appliesToPath("user.id"));
    }

    @Test
    public void testAppliesToPathWithSingleWildcard() {
        ValueMatcher<Object> matcher = (o1, o2) -> true;
        Customization customization = new Customization("user.*.name", matcher);
        assertTrue(customization.appliesToPath("user.profile.name"));
        assertTrue(customization.appliesToPath("user.account.name"));
        assertFalse(customization.appliesToPath("user.profile.id"));
    }

    @Test
    public void testAppliesToPathWithDoubleWildcard() {
        ValueMatcher<Object> matcher = (o1, o2) -> true;
        Customization customization = new Customization("user.**.name", matcher);
        assertTrue(customization.appliesToPath("user.name"));
        assertTrue(customization.appliesToPath("user.profile.name"));
        assertTrue(customization.appliesToPath("user.profile.details.name"));
    }

    @Test
    public void testAppliesToPathWithRootDoubleWildcard() {
        ValueMatcher<Object> matcher = (o1, o2) -> true;
        Customization customization = new Customization("**.name", matcher);
        assertTrue(customization.appliesToPath("name"));
        assertTrue(customization.appliesToPath("user.name"));
        assertTrue(customization.appliesToPath("user.profile.name"));
    }

    @Test
    public void testMatchesWithLocationAwareValueMatcher() throws JSONException {
        LocationAwareValueMatcher<Object> matcher = new LocationAwareValueMatcher<Object>() {
            @Override
            public boolean equal(Object o1, Object o2) {
                return o1.equals(o2);
            }

            @Override
            public boolean equal(String prefix, Object actual, Object expected, JSONCompareResult result) {
                return actual.equals(expected);
            }
        };
        
        Customization customization = new Customization("test", matcher);
        JSONCompareResult result = new JSONCompareResult();
        assertTrue(customization.matches("test", "value", "value", result));
        assertFalse(customization.matches("test", "value1", "value2", result));
    }

    @Test
    public void testMatchesWithRegularValueMatcher() throws JSONException {
        ValueMatcher<Object> matcher = (o1, o2) -> o1.equals(o2);
        Customization customization = new Customization("test", matcher);
        JSONCompareResult result = new JSONCompareResult();
        assertTrue(customization.matches("test", "value", "value", result));
        assertFalse(customization.matches("test", "value1", "value2", result));
    }
}
