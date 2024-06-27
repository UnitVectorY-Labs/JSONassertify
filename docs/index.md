# JSONAssertify

Write JSON unit tests in less code.  Great for testing REST interfaces.

Supported test frameworks:
- [JUnit](http://junit.org)

## Quick Start

JSONAssertify is avaialble on Maven Central and can be used by including the following in your project's `pom.xml`:

ADD ONCE RELEASED TO MAVEN CENTRAL

Syntax is simple, and similar to JUnit Assert:

```java
JSONAssert.assertEquals(expectedJSON, actualJSON, strictMode);
```

Add JSONassert tests within existing JUnit tests, just like you would add a standard Assert:

```java
@Test
public void testGetUser() {
    Assert.assertTrue(_restService.isEnabled());
    String result = _restService.get("/user/123.json");
    JSONAssert.assertEquals("{id:123,name:\"Joe\"}", result, false);
}
```

It is recommended that you leave `strictMode` off, so your tests will be less brittle. Turn it on if you need to enforce a particular order for arrays, or if you want to ensure that the actual JSON does not have any fields beyond what's expected.

## Examples

With JSONAssertify, you write and maintain something like this:

```java
JSONObject data = getRESTData("/friends/367.json");
String expected = "{friends:[{id:123,name:\"Corby Page\"},{id:456,name:\"Carter Page\"}]}";
JSONAssert.assertEquals(expected, data, false);
```

... instead of all this:

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

## Error Messages

We tried to make error messages easy to understand. This is really important, since it gets hard for the eye to pick out the difference, particularly in long JSON strings. For example:

```java
String expected = "{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}";
String actual = "{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"cat\",\"fish\"]}],pets:[]}"
JSONAssert.assertEquals(expected, actual, false);
```

... returns the following:

```
friends[id=3].pets[]: Expected bird, but not found ; friends[id=3].pets[]: Contains cat, but not expected
```

which tells you that the pets array under the friend where id=3 was supposed to contain "bird", but had "cat" instead. 
(Maybe the cat ate the bird?)

## Cookbook

Assertion parameters can be a java.lang.String with JSON data, an org.json.JSONObject, or an org.json.JSONArray. For readability, we'll use strings in the following examples.

### A really simple example. Get a JSON object and test its ID:

```java
@Test
public void testSimple() {
    String result = getJSONResult("/user/1.json");
    JSONAssert.assertEquals("{id:1}", result, false); // Pass
}
```

If you enable *strictMode*, then extended fields fail:

```java
String result = "{id:1,name:\"Juergen\"}";
JSONAssert.assertEquals("{id:1}", result, false); // Pass
JSONAssert.assertEquals("{id:1}", result, true); // Fail
```

Strict or not, field order does not matter:

```java
String result = "{id:1,name:\"Juergen\"}";
JSONAssert.assertEquals("{name:\"Juergen\",id:1}", result, true); // Pass
```

Because application interfaces are naturally extended as they mature, it is recommended that you default to leaving strict mode off, except in particular cases.

Arrays rules are different. If sequence is important, you can enable strict mode:

```java
String result = "[1,2,3,4,5]";
JSONAssert.assertEquals("[1,2,3,4,5]", result, true); // Pass
JSONAssert.assertEquals("[5,3,2,1,4]", result, true); // Fail
```

When strict mode is off, arrays can be in any order:

```java
String result = "[1,2,3,4,5]";
JSONAssert.assertEquals("[5,3,2,1,4]", result, false); // Pass
```

Strict or not, arrays must match. They can't be "extended" like object fields can:

```java
String result = "[1,2,3,4,5]";
JSONAssert.assertEquals("[1,2,3,4,5]", result, false); // Pass
JSONAssert.assertEquals("[1,2,3]", result, false); // Fail
JSONAssert.assertEquals("[1,2,3,4,5,6]", result, false); // Fail
```

The examples so far are simple, but this will work for JSON objects of any size (per VM memory limits), depth, or complexity.

You can test arrays of arrays, loose/strict ordering constraints apply at all levels:

```java
String result = "{id:1,stuff:[[1,2],[2,3],[],[3,4]]}";
JSONAssert.assertEquals("{id:1,stuff:[[1,2],[2,3],[],[3,4]]}", result, true); // Pass
JSONAssert.assertEquals("{id:1,stuff:[[4,3],[3,2],[],[1,2]]}", result, false); // Pass
```

You can test arrays of arrays, loose/strict ordering constraints apply at all levels:

```java
String result = "{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}";
JSONAssert.assertEquals("{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}", result, true); // Pass
JSONAssert.assertEquals("{name:\"Joe\",friends:[{id:3,name:\"Sue\",pets:[\"fish\",\"bird\"]},{id:2,name:\"Pat\",pets:[\"dog\"]}],pets:[],id:1}", result, false); // Pass
```

As you can see, tests work against any level of depth:

```java
String result = "{a:{b:{c:{d:{e:{f:{g:{h:{i:{j:{k:{l:{m:{n:{o:{p:\"blah\"}}}}}}}}}}}}}}}";
JSONAssert.assertEquals("{a:{b:{c:{d:{e:{f:{g:{h:{i:{j:{k:{l:{m:{n:{o:{p:\"blah\"}}}}}}}}}}}}}}}", result, true); // Pass
```
