/*
 * Copyright (c) 2011-2014 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 *     The Eclipse Public License is available at
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 *     The Apache License v2.0 is available at
 *     http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package io.vertx.test.core;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class JsonArrayTest {

  private JsonArray jsonArray;

  @Before
  public void setUp() {
    jsonArray = new JsonArray();
  }

  @Test
  public void testGetInteger() {
    jsonArray.add(123);
    assertEquals(Integer.valueOf(123), jsonArray.getInteger(0));
    try {
      jsonArray.getInteger(-1);
      fail();
    } catch (IndexOutOfBoundsException e) {
      // OK
    }
    try {
      jsonArray.getInteger(1);
      fail();
    } catch (IndexOutOfBoundsException e) {
      // OK
    }
    // Different number types
    jsonArray.add(123l);
    assertEquals(Integer.valueOf(123), jsonArray.getInteger(1));
    jsonArray.add(123f);
    assertEquals(Integer.valueOf(123), jsonArray.getInteger(2));
    jsonArray.add(123d);
    assertEquals(Integer.valueOf(123), jsonArray.getInteger(3));
    jsonArray.add("foo");
    try {
      jsonArray.getInteger(4);
      fail();
    } catch (ClassCastException e) {
      // OK
    }
    jsonArray.addNull();
    assertNull(jsonArray.getInteger(5));
  }

  @Test
  public void testGetLong() {
    jsonArray.add(123l);
    assertEquals(Long.valueOf(123l), jsonArray.getLong(0));
    try {
      jsonArray.getLong(-1);
      fail();
    } catch (IndexOutOfBoundsException e) {
      // OK
    }
    try {
      jsonArray.getLong(1);
      fail();
    } catch (IndexOutOfBoundsException e) {
      // OK
    }
    // Different number types
    jsonArray.add(123);
    assertEquals(Long.valueOf(123l), jsonArray.getLong(1));
    jsonArray.add(123f);
    assertEquals(Long.valueOf(123l), jsonArray.getLong(2));
    jsonArray.add(123d);
    assertEquals(Long.valueOf(123l), jsonArray.getLong(3));
    jsonArray.add("foo");
    try {
      jsonArray.getLong(4);
      fail();
    } catch (ClassCastException e) {
      // OK
    }
    jsonArray.addNull();
    assertNull(jsonArray.getLong(5));
  }

  @Test
  public void testGetFloat() {
    jsonArray.add(123f);
    assertEquals(Float.valueOf(123f), jsonArray.getFloat(0));
    try {
      jsonArray.getFloat(-1);
      fail();
    } catch (IndexOutOfBoundsException e) {
      // OK
    }
    try {
      jsonArray.getFloat(1);
      fail();
    } catch (IndexOutOfBoundsException e) {
      // OK
    }
    // Different number types
    jsonArray.add(123);
    assertEquals(Float.valueOf(123f), jsonArray.getFloat(1));
    jsonArray.add(123);
    assertEquals(Float.valueOf(123f), jsonArray.getFloat(2));
    jsonArray.add(123d);
    assertEquals(Float.valueOf(123f), jsonArray.getFloat(3));
    jsonArray.add("foo");
    try {
      jsonArray.getFloat(4);
      fail();
    } catch (ClassCastException e) {
      // OK
    }
    jsonArray.addNull();
    assertNull(jsonArray.getFloat(5));
  }

  @Test
  public void testGetDouble() {
    jsonArray.add(123d);
    assertEquals(Double.valueOf(123d), jsonArray.getDouble(0));
    try {
      jsonArray.getDouble(-1);
      fail();
    } catch (IndexOutOfBoundsException e) {
      // OK
    }
    try {
      jsonArray.getDouble(1);
      fail();
    } catch (IndexOutOfBoundsException e) {
      // OK
    }
    // Different number types
    jsonArray.add(123);
    assertEquals(Double.valueOf(123d), jsonArray.getDouble(1));
    jsonArray.add(123);
    assertEquals(Double.valueOf(123d), jsonArray.getDouble(2));
    jsonArray.add(123d);
    assertEquals(Double.valueOf(123d), jsonArray.getDouble(3));
    jsonArray.add("foo");
    try {
      jsonArray.getDouble(4);
      fail();
    } catch (ClassCastException e) {
      // OK
    }
    jsonArray.addNull();
    assertNull(jsonArray.getDouble(5));
  }

  @Test
  public void testGetString() {
    jsonArray.add("foo");
    assertEquals("foo", jsonArray.getString(0));
    try {
      jsonArray.getString(-1);
      fail();
    } catch (IndexOutOfBoundsException e) {
      // OK
    }
    try {
      jsonArray.getString(1);
      fail();
    } catch (IndexOutOfBoundsException e) {
      // OK
    }
    jsonArray.add(123);
    try {
      jsonArray.getString(1);
      fail();
    } catch (ClassCastException e) {
      // OK
    }
    jsonArray.addNull();
    assertNull(jsonArray.getString(2));
  }

  @Test
  public void testGetBoolean() {
    jsonArray.add(true);
    assertEquals(true, jsonArray.getBoolean(0));
    jsonArray.add(false);
    assertEquals(false, jsonArray.getBoolean(1));
    try {
      jsonArray.getBoolean(-1);
      fail();
    } catch (IndexOutOfBoundsException e) {
      // OK
    }
    try {
      jsonArray.getBoolean(2);
      fail();
    } catch (IndexOutOfBoundsException e) {
      // OK
    }
    jsonArray.add(123);
    try {
      jsonArray.getBoolean(2);
      fail();
    } catch (ClassCastException e) {
      // OK
    }
    jsonArray.addNull();
    assertNull(jsonArray.getBoolean(3));
  }

  @Test
  public void testGetBinary() {
    byte[] bytes = TestUtils.randomByteArray(10);
    jsonArray.add(bytes);
    assertTrue(TestUtils.byteArraysEqual(bytes, jsonArray.getBinary(0)));
    assertTrue(TestUtils.byteArraysEqual(bytes, Base64.getDecoder().decode(jsonArray.getString(0))));
    try {
      jsonArray.getBinary(-1);
      fail();
    } catch (IndexOutOfBoundsException e) {
      // OK
    }
    try {
      jsonArray.getBinary(1);
      fail();
    } catch (IndexOutOfBoundsException e) {
      // OK
    }
    jsonArray.add(123);
    try {
      jsonArray.getBinary(1);
      fail();
    } catch (ClassCastException e) {
      // OK
    }
    jsonArray.addNull();
    assertNull(jsonArray.getBinary(2));
  }

  @Test
  public void testGetJsonObject() {
    JsonObject obj = new JsonObject().put("foo", "bar");    
    jsonArray.add(obj);
    assertEquals(obj, jsonArray.getJsonObject(0));
    try {
      jsonArray.getJsonObject(-1);
      fail();
    } catch (IndexOutOfBoundsException e) {
      // OK
    }
    try {
      jsonArray.getJsonObject(1);
      fail();
    } catch (IndexOutOfBoundsException e) {
      // OK
    }
    jsonArray.add(123);
    try {
      jsonArray.getJsonObject(1);
      fail();
    } catch (ClassCastException e) {
      // OK
    }
    jsonArray.addNull();
    assertNull(jsonArray.getJsonObject(2));
  }

  @Test
  public void testGetJsonArray() {
    JsonArray arr = new JsonArray().add("foo");
    jsonArray.add(arr);
    assertEquals(arr, jsonArray.getJsonArray(0));
    try {
      jsonArray.getJsonArray(-1);
      fail();
    } catch (IndexOutOfBoundsException e) {
      // OK
    }
    try {
      jsonArray.getJsonArray(1);
      fail();
    } catch (IndexOutOfBoundsException e) {
      // OK
    }
    jsonArray.add(123);
    try {
      jsonArray.getJsonArray(1);
      fail();
    } catch (ClassCastException e) {
      // OK
    }
    jsonArray.addNull();
    assertNull(jsonArray.getJsonArray(2));
  }

  @Test
  public void testGetValue() {
    jsonArray.add(123);
    assertEquals(123, jsonArray.getValue(0));
    jsonArray.add(123l);
    assertEquals(123l, jsonArray.getValue(1));
    jsonArray.add(123f);
    assertEquals(123f, jsonArray.getValue(2));
    jsonArray.add(123d);
    assertEquals(123d, jsonArray.getValue(3));
    jsonArray.add(false);
    assertEquals(false, jsonArray.getValue(4));
    jsonArray.add(true);
    assertEquals(true, jsonArray.getValue(5));
    jsonArray.add("bar");
    assertEquals("bar", jsonArray.getValue(6));
    JsonObject obj = new JsonObject().put("blah", "wibble");
    jsonArray.add(obj);
    assertEquals(obj, jsonArray.getValue(7));
    JsonArray arr = new JsonArray().add("blah").add("wibble");
    jsonArray.add(arr);
    assertEquals(arr, jsonArray.getValue(8));
    byte[] bytes = TestUtils.randomByteArray(100);
    jsonArray.add(bytes);
    assertTrue(TestUtils.byteArraysEqual(bytes, Base64.getDecoder().decode((String) jsonArray.getValue(9))));
    jsonArray.addNull();
    assertNull(jsonArray.getValue(10));
    try {
      jsonArray.getValue(-1);
      fail();
    } catch (IndexOutOfBoundsException e) {
      // OK
    }
    try {
      jsonArray.getValue(11);
      fail();
    } catch (IndexOutOfBoundsException e) {
      // OK
    }
  }

  @Test
  public void testAddString() {
    assertSame(jsonArray, jsonArray.add("foo"));
    assertEquals("foo", jsonArray.getString(0));
    try {
      jsonArray.add((String)null);
      fail();
    } catch (NullPointerException e) {
      // OK
    }
  }

  @Test
  public void testAddInteger() {
    assertSame(jsonArray, jsonArray.add(123));
    assertEquals(Integer.valueOf(123), jsonArray.getInteger(0));
    try {
      jsonArray.add((Integer)null);
      fail();
    } catch (NullPointerException e) {
      // OK
    }
  }

  @Test
  public void testAddLong() {
    assertSame(jsonArray, jsonArray.add(123l));
    assertEquals(Long.valueOf(123l), jsonArray.getLong(0));
    try {
      jsonArray.add((Long)null);
      fail();
    } catch (NullPointerException e) {
      // OK
    }
  }

  @Test
  public void testAddFloat() {
    assertSame(jsonArray, jsonArray.add(123f));
    assertEquals(Float.valueOf(123f), jsonArray.getFloat(0));
    try {
      jsonArray.add((Float)null);
      fail();
    } catch (NullPointerException e) {
      // OK
    }
  }

  @Test
  public void testAddDouble() {
    assertSame(jsonArray, jsonArray.add(123d));
    assertEquals(Double.valueOf(123d), jsonArray.getDouble(0));
    try {
      jsonArray.add((Double)null);
      fail();
    } catch (NullPointerException e) {
      // OK
    }
  }

  @Test
  public void testAddBoolean() {
    assertSame(jsonArray, jsonArray.add(true));
    assertEquals(true, jsonArray.getBoolean(0));
    jsonArray.add(false);
    assertEquals(false, jsonArray.getBoolean(1));
    try {
      jsonArray.add((Boolean)null);
      fail();
    } catch (NullPointerException e) {
      // OK
    }
  }

  @Test
  public void testAddJsonObject() {
    JsonObject obj = new JsonObject().put("foo", "bar");
    assertSame(jsonArray, jsonArray.add(obj));
    assertEquals(obj, jsonArray.getJsonObject(0));
    try {
      jsonArray.add((JsonObject)null);
      fail();
    } catch (NullPointerException e) {
      // OK
    }
  }

  @Test
  public void testAddJsonArray() {
    JsonArray arr = new JsonArray().add("foo");
    assertSame(jsonArray, jsonArray.add(arr));
    assertEquals(arr, jsonArray.getJsonArray(0));
    try {
      jsonArray.add((JsonArray)null);
      fail();
    } catch (NullPointerException e) {
      // OK
    }
  }

  @Test
  public void testAddBinary() {
    byte[] bytes = TestUtils.randomByteArray(10);
    assertSame(jsonArray, jsonArray.add(bytes));
    assertTrue(TestUtils.byteArraysEqual(bytes, jsonArray.getBinary(0)));
    try {
      jsonArray.add((byte[])null);
      fail();
    } catch (NullPointerException e) {
      // OK
    }
  }

  @Test
  public void testAddNull() {
    assertSame(jsonArray, jsonArray.addNull());
    assertEquals(null, jsonArray.getString(0));
    assertTrue(jsonArray.hasNull(0));
  }

  @Test
  public void testHasNull() {
    jsonArray.addNull();
    jsonArray.add("foo");
    assertEquals(null, jsonArray.getString(0));
    assertTrue(jsonArray.hasNull(0));
    assertFalse(jsonArray.hasNull(1));
  }

  @Test
  public void testContains() {
    jsonArray.add("wibble");
    jsonArray.add(true);
    jsonArray.add(123);
    JsonObject obj = new JsonObject();
    JsonArray arr = new JsonArray();
    jsonArray.add(obj);
    jsonArray.add(arr);
    assertFalse(jsonArray.contains("eek"));
    assertFalse(jsonArray.contains(false));
    assertFalse(jsonArray.contains(321));
    assertFalse(jsonArray.contains(new JsonObject()));
    assertFalse(jsonArray.contains(new JsonArray()));
    assertTrue(jsonArray.contains("wibble"));
    assertTrue(jsonArray.contains(true));
    assertTrue(jsonArray.contains(123));
    assertTrue(jsonArray.contains(obj));
    assertTrue(jsonArray.contains(arr));
  }

  @Test
  public void testRemoveByObject() {
    jsonArray.add("wibble");
    jsonArray.add(true);
    jsonArray.add(123);
    assertEquals(3, jsonArray.size());
    assertTrue(jsonArray.remove("wibble"));
    assertEquals(2, jsonArray.size());
    assertFalse(jsonArray.remove("notthere"));
    assertTrue(jsonArray.remove(true));
    assertTrue(jsonArray.remove(Integer.valueOf(123)));
    assertTrue(jsonArray.isEmpty());
  }

  @Test
  public void testRemoveByPos() {
    jsonArray.add("wibble");
    jsonArray.add(true);
    jsonArray.add(123);
    assertEquals(3, jsonArray.size());
    assertEquals("wibble", jsonArray.remove(0));
    assertEquals(2, jsonArray.size());
    assertEquals(123, jsonArray.remove(1));
    assertEquals(1, jsonArray.size());
    assertEquals(true, jsonArray.remove(0));
    assertTrue(jsonArray.isEmpty());
  }

  @Test
  public void testSize() {
    jsonArray.add("wibble");
    jsonArray.add(true);
    jsonArray.add(123);
    assertEquals(3, jsonArray.size());
  }

  @Test
  public void testClear() {
    jsonArray.add("wibble");
    jsonArray.add(true);
    jsonArray.add(123);
    assertEquals(3, jsonArray.size());
    assertEquals(jsonArray, jsonArray.clear());
    assertEquals(0, jsonArray.size());
    assertTrue(jsonArray.isEmpty());
  }

  @Test
  public void testIterator() {
    jsonArray.add("foo");
    jsonArray.add(123);
    JsonObject obj = new JsonObject().put("foo", "bar");
    jsonArray.add(obj);
    Iterator<Object> iter = jsonArray.iterator();
    assertTrue(iter.hasNext());
    Object entry = iter.next();
    assertEquals("foo", entry);
    assertTrue(iter.hasNext());
    entry = iter.next();
    assertEquals(123, entry);
    assertTrue(iter.hasNext());
    entry = iter.next();
    assertEquals(obj, entry);
    assertFalse(iter.hasNext());
  }

  @Test
  public void testStream() {
    jsonArray.add("foo");
    jsonArray.add(123);
    JsonObject obj = new JsonObject().put("foo", "bar");
    jsonArray.add(obj);
    List<Object> list = jsonArray.stream().collect(Collectors.toList());
    Iterator<Object> iter = list.iterator();
    assertTrue(iter.hasNext());
    Object entry = iter.next();
    assertEquals("foo", entry);
    assertTrue(iter.hasNext());
    entry = iter.next();
    assertEquals(123, entry);
    assertTrue(iter.hasNext());
    entry = iter.next();
    assertEquals(obj, entry);
    assertFalse(iter.hasNext());
  }

  @Test
  public void testCopy() {
    jsonArray.add("foo");
    jsonArray.add(123);
    JsonObject obj = new JsonObject().put("foo", "bar");
    jsonArray.add(obj);
    JsonArray copy = jsonArray.copy();
    assertNotSame(jsonArray, copy);
    assertEquals(jsonArray, copy);
    assertEquals(3, copy.size());
    assertEquals("foo", copy.getString(0));
    assertEquals(Integer.valueOf(123), copy.getInteger(1));
    assertEquals(obj, copy.getJsonObject(2));
    assertNotSame(obj, copy.getJsonObject(2));

    copy.add("foo");
    assertEquals(3, jsonArray.size());
    jsonArray.add("bar");
    assertEquals(3, copy.size());
  }

  // TODO
  // test encode and encodePrettily
  // test create from list
  // test copy checks types
  // test created from list with invalid objects
  // test create from list with mixed lists and jsonarray
  //




}
