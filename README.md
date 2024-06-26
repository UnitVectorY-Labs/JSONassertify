[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![codecov](https://codecov.io/gh/UnitVectorY-Labs/JSONassertify/graph/badge.svg?token=AkErQGHlR1)](https://codecov.io/gh/UnitVectorY-Labs/JSONassertify)

JSONassertify
=============

Write JSON unit tests in less code.  Great for testing REST interfaces.

Fork
----

[JSONassertify](https://github.com/UnitVectorY-Labs/JSONassertify) is a fork of [JSONassert](https://github.com/skyscreamer/JSONassert) version 1.5.1 that is actively under development.  This fork intends to modernize the codebase including some breaking changes.  This is following the release of the [1.5.2](https://github.com/skyscreamer/JSONassert/pull/188) which changed the minimum version to Java 21 which has been discussed [here](https://github.com/skyscreamer/JSONassert/issues/190). The goal of this fork is to not maintain perfect compatibility, but rather upgrade and modernize the components.

 - New project name and new package for the project (part of the fork)
 - Update dependencies to latest version to address security issues
 - Use Dependabot to ensure dependencies remain up-to-date
 - Upgrade to JUnit 5
 - Move to use org.json library
 - Utilize GitHub actions for building
 - Move to a newer version of Java (specific version TBD)

THIS IS A WORK IN PROGRESS AND HAS NOT REACHED THE INITIAL RELEASE

Summary
-------

Write JSON tests as if you are comparing a string.  Under the covers, JSONassert converts your string into a JSON object and compares the logical structure and data with the actual JSON.  When _strict_ is set to false (recommended), it forgives reordering data and extending results (as long as all the expected elements are there), making tests less brittle.

Supported test frameworks:

 * [JUnit](http://junit.org)

Examples
--------

In JSONassert you write and maintain something like this:

    JSONObject data = getRESTData("/friends/367.json");
    String expected = "{friends:[{id:123,name:\"Corby Page\"},{id:456,name:\"Carter Page\"}]}";
    JSONAssert.assertEquals(expected, data, false);

instead of all this:

<pre><code><del>
JSONObject data = getRESTData("/friends/367.json");
Assert.assertTrue(data.has("friends"));
Object friendsObject = data.get("friends");
Assert.assertTrue(friendsObject instanceof JSONArray);
JSONArray friends = (JSONArray) friendsObject;
Assert.assertEquals(2, data.length());
JSONObject friend1Obj = friends.getJSONObject(data.get(0));
Assert.true(friend1Obj.has("id"));
Assert.true(friend1Obj.has("name"));
JSONObject friend2Obj = friends.getJSONObject(data.get(1));
Assert.true(friend2Obj.has("id"));
Assert.true(friend2Obj.has("name"));
if ("Carter Page".equals(friend1Obj.getString("name"))) {
    Assert.assertEquals(123, friend1Obj.getInt("id"));
    Assert.assertEquals("Corby Page", friend2Obj.getString("name"));
    Assert.assertEquals(456, friend2Obj.getInt("id"));
}
else if ("Corby Page".equals(friend1Obj.getString("name"))) {
    Assert.assertEquals(456, friend1Obj.getInt("id"));
    Assert.assertEquals("Carter Page", friend2Obj.getString("name"));
    Assert.assertEquals(123, friend2Obj.getInt("id"));
}
else {
    Assert.fail("Expected either Carter or Corby, Got: " + friend1Obj.getString("name"));
}
</del></code></pre>

Error Messages
--------------

We tried to make error messages easy to understand.  This is really important, since it gets hard for the eye to pick out the difference, particularly in long JSON strings.  For example:

    String expected = "{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}";
    String actual = "{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"cat\",\"fish\"]}],pets:[]}"
    JSONAssert.assertEquals(expected, actual, false);

returns the following:

    friends[id=3].pets[]: Expected bird, but not found ; friends[id=3].pets[]: Contains cat, but not expected

Which tells you that the pets array under the friend where id=3 was supposed to contain "bird", but had "cat" instead.  (Maybe the cat ate the bird?)

