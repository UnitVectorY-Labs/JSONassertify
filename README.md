[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Active](https://img.shields.io/badge/Status-Active-green)](https://unitvectory-labs.github.io/uvy-labs-guide/bestpractices/status/#active) [![Maven Central](https://img.shields.io/maven-central/v/com.unitvectory/jsonassertify)](https://central.sonatype.com/artifact/com.unitvectory/jsonassertify) [![javadoc](https://javadoc.io/badge2/com.unitvectory/jsonassertify/javadoc.svg)](https://javadoc.io/doc/com.unitvectory/jsonassertify) [![codecov](https://codecov.io/gh/UnitVectorY-Labs/JSONassertify/graph/badge.svg?token=AkErQGHlR1)](https://codecov.io/gh/UnitVectorY-Labs/JSONassertify)

JSONassertify
=============

Write JSON unit tests in less code.  Great for testing REST interfaces.

JSONassertify: A Fork of JSONassert
-----------------------------------

JSONassert has been a valuable tool for simplifying JSON comparisons in unit tests for over a decade. While we deeply appreciate the original developers' contributions, the project has seen limited updates in recent years. To address current needs and modernize the tool, we have created [JSONassertify](https://github.com/UnitVectorY-Labs/JSONassertify), a fork of [JSONassert](https://github.com/skyscreamer/JSONassert) version 1.5.1.

This fork aims to update and enhance the codebase, introducing some necessary breaking changes while continuing to support older Java versions. Notably, this follows the recent release of [JSONassert 1.5.2](https://github.com/skyscreamer/JSONassert/pull/188), which unexpectedly required Java 21, as discussed [here](https://github.com/skyscreamer/JSONassert/issues/190). Our goal with JSONassertify is not to maintain perfect compatibility with the original but to improve and modernize the project.

Key changes include:

- New project name and package moving from `org.skyscreamer.jsonassert` to `com.unitvectory.jsonassertify`
- Updating dependencies to the latest versions to address security issues
- Utilizing Dependabot to keep dependencies up-to-date on an ongoing basis
- Upgrade from JUnit4 to JUnit 5 in the unit tests used by the project
- Transitioning to the `org.json` library from the less common and not maintained `com.vaadin.external.google: android-json` (this has the possibility of introducing breaking changes)
- Implementing GitHub Actions for automated builds
- Move from targeting Java 6 to targeting Java 8 with the intent of maintaining this backwards compatibility until Java 8 is fully deprecated as a build target
- Overall formatting improvements of the code base including addition of missing Java Docs.

Things that will stay the same:

- The overwhelming majority of the codebase remains unchanged, this is the same library with the same functionality as JSONassert and changes and fixes will be made with care to maintain compatibility
- Same classes and same methods (just under a different package)
- Same Apache 2.0 License as the original project

Migration Guide and Usage
-------------------------

The intent is for JSONassertify to be a drop in replacement for JSONassert. Migrating is intended to be straightforward but there are a few things to keep in mind.

1. Update the dependency to use JSONassertify

```xml
<dependency>
    <groupId>com.unitvectory</groupId>
    <artifactId>jsonassertify</artifactId>
    <version>0.0.2</version>
    <scope>test</scope>
</dependency>
```

2. Update the package imports

For most projects this is as simple as replacing the original import...

```java
import org.skyscreamer.jsonassert.JSONAssert;
```

with the import to reference the new package...

```java
import com.unitvectory.jsonassertify.JSONAssert;
```

3. Check for org.json compatibility issues.

The change of the underlying JSON library to use org.json is one of the modernization and security considerations that is a potential breaking change.  The way this library parses JSON is different from the over decade old [com.vaadin.external.google:android-json](https://mvnrepository.com/artifact/com.vaadin.external.google/android-json/0.0.20131108.vaadin1) library used by JSONassert.

One notable change is how duplicate JSON keys are handled, the original library would allow those to be parsed where the most recent version of org.json will throw an error as part of the parsing before JSONassertify is able to perform a comparison of the objects.

Summary
-------

Write JSON tests as if you are comparing a string.  Under the covers, JSONassertify converts your string into a JSON object and compares the logical structure and data with the actual JSON.  When _strict_ is set to false (recommended), it forgives reordering data and extending results (as long as all the expected elements are there), making tests less brittle.

Supported test frameworks:

- [JUnit](http://junit.org)

Examples
--------

In JSONassertify you write and maintain something like this:

```java
JSONObject data = getRESTData("/friends/367.json");
String expected = "{friends:[{id:123,name:\"Corby Page\"},{id:456,name:\"Carter Page\"}]}";
JSONAssert.assertEquals(expected, data, false);
```

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

```java
String expected = "{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}";
String actual = "{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"cat\",\"fish\"]}],pets:[]}"
JSONAssert.assertEquals(expected, actual, false);
```

returns the following:

```
friends[id=3].pets[]: Expected bird, but not found ; friends[id=3].pets[]: Contains cat, but not expected
```

Which tells you that the pets array under the friend where id=3 was supposed to contain "bird", but had "cat" instead.  (Maybe the cat ate the bird?)
