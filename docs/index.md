---
layout: default
title: Home
nav_order: 1
permalink: /
---

# Introduction

[JSONassertify](https://github.com/UnitVectorY-Labs/JSONassertify) is a fork of [JSONassert](https://github.com/skyscreamer/JSONassert) that aims to update and enhance the codebase, introducing some necessary breaking changes while continuing to support older Java versions.

Supported test frameworks:
- [JUnit](http://junit.org)

# Quick Start

JSONAssertify is avaialble on Maven Central and can be used by including the following in your project's `pom.xml`:

```xml
<dependency>
    <groupId>com.unitvectory</groupId>
    <artifactId>jsonassertify</artifactId>
    <version>0.0.2</version>
    <scope>test</scope>
</dependency>
```

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
