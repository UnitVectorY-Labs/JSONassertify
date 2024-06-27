---
layout: default
title: Cookbook
nav_order: 3
---

# Cookbook

Assertion parameters can be a java.lang.String with JSON data, an org.json.JSONObject, or an org.json.JSONArray. For readability, we'll use strings in the following examples.

A really simple example. Get a JSON object and test its ID:

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
