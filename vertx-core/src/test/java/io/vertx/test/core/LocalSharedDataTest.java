/*
 * Copyright 2014 Red Hat, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package io.vertx.test.core;

import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.SharedData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class LocalSharedDataTest extends VertxTestBase {

  private SharedData sharedData;

  @Before
  public void setUp() {
    sharedData = vertx.sharedData();
  }

  @After
  public void teardown() {
  }

  @Test
  public void testMapPutGet() throws Exception {
    sharedData.<String, String>getClusterWideMap("map1", ar -> {
      assertTrue(ar.succeeded());
      AsyncMap<String, String> map = ar.result();
      map.put("foo", "bar", ar2 -> {
        assertTrue(ar2.succeeded());
        map.get("foo", ar3 -> {
          assertTrue(ar3.succeeded());
          assertEquals("bar", ar3.result());
          testComplete();
        });
      });
    });
    await();
  }

  @Test
  public void testRemove() throws Exception {
    sharedData.<String, String>getClusterWideMap("map1", ar -> {
      assertTrue(ar.succeeded());
      AsyncMap<String, String> map = ar.result();
      map.put("foo", "bar", ar2 -> {
        assertTrue(ar2.succeeded());
        map.remove("foo", ar3 -> {
          assertTrue(ar3.succeeded());
          assertTrue(ar3.result());
          testComplete();
        });
      });
    });
    await();
  }

  @Test
  public void testRemoveNotThere() throws Exception {
    sharedData.<String, String>getClusterWideMap("map1", ar -> {
      assertTrue(ar.succeeded());
      AsyncMap<String, String> map = ar.result();
      map.remove("foo", ar2 -> {
        assertTrue(ar2.succeeded());
        assertFalse(ar2.result());
        testComplete();
      });
    });
    await();
  }

  @Test
  public void testMapsIsolated() throws Exception {
    sharedData.<String, String>getClusterWideMap("map1", ar -> {
      assertTrue(ar.succeeded());
      AsyncMap<String, String> map1 = ar.result();
      sharedData.<String, String>getClusterWideMap("map2", a2 -> {
        assertTrue(ar.succeeded());
        AsyncMap<String, String> map2 = ar.result();
        map1.put("foo", "bar", null);
      });
    });
    await();
  }

//  @Test
//  public void testMapTypes() throws Exception {
//
//    Map<String, Object> map = sharedData.getMap("foo");
//
//    String key = "key";
//
//    double d = new Random().nextDouble();
//    map.put(key, d);
//    assertEquals(d, map.get(key));
//
//    float f = new Random().nextFloat();
//    map.put(key, f);
//    assertEquals(f, map.get(key));
//
//    byte b = (byte)new Random().nextInt();
//    map.put(key, b);
//    assertEquals(b, map.get(key));
//
//    short s = (short)new Random().nextInt();
//    map.put(key, s);
//    assertEquals(s, map.get(key));
//
//    int i = new Random().nextInt();
//    map.put(key, i);
//    assertEquals(i, map.get(key));
//
//    long l = new Random().nextLong();
//    map.put(key, l);
//    assertEquals(l, map.get(key));
//
//    map.put(key, true);
//    assertTrue((Boolean)map.get(key));
//
//    map.put(key, false);
//    assertFalse((Boolean) map.get(key));
//
//    char c = (char)new Random().nextLong();
//    map.put(key, c);
//    assertEquals(c, map.get(key));
//
//    Buffer buff = TestUtils.randomBuffer(100);
//    map.put(key, buff);
//    Buffer got1 = (Buffer)map.get(key);
//    assertTrue(got1 != buff); // Make sure it's copied
//    assertEquals(buff, map.get(key));
//    Buffer got2 = (Buffer)map.get(key);
//    assertTrue(got1 != got2); // Should be copied each time
//    assertTrue(got2 != buff);
//    assertEquals(buff, map.get(key));
//
//
//    byte[] bytes = TestUtils.randomByteArray(100);
//    map.put(key, bytes);
//    byte[] bgot1 = (byte[]) map.get(key);
//    assertTrue(bgot1 != bytes);
//    assertTrue(TestUtils.byteArraysEqual(bytes, bgot1));
//    byte[] bgot2 = (byte[]) map.get(key);
//    assertTrue(bgot2 != bytes);
//    assertTrue(bgot1 != bgot2);
//    assertTrue(TestUtils.byteArraysEqual(bytes, bgot2));
//
//    try {
//      map.put(key, new SomeOtherClass());
//      fail("Should throw exception");
//    } catch (IllegalArgumentException e) {
//      //OK
//    }
//  }
//
//  @Test
//  public void testSetTypes() throws Exception {
//
//    Set<Object> set = sharedData.getSet("foo");
//
//    double d = new Random().nextDouble();
//    set.add(d);
//    assertEquals(d, set.iterator().next());
//    set.clear();
//
//    float f = new Random().nextFloat();
//    set.add(f);
//    assertEquals(f, set.iterator().next());
//    set.clear();
//
//    byte b = (byte)new Random().nextInt();
//    set.add(b);
//    assertEquals(b, set.iterator().next());
//    set.clear();
//
//    short s = (short)new Random().nextInt();
//    set.add(s);
//    assertEquals(s, set.iterator().next());
//    set.clear();
//
//    int i = new Random().nextInt();
//    set.add(i);
//    assertEquals(i, set.iterator().next());
//    set.clear();
//
//    long l = new Random().nextLong();
//    set.add(l);
//    assertEquals(l, set.iterator().next());
//    set.clear();
//
//    set.add(true);
//    assertTrue((Boolean)set.iterator().next());
//    set.clear();
//
//    set.add(false);
//    assertFalse((Boolean) set.iterator().next());
//    set.clear();
//
//    char c = (char)new Random().nextLong();
//    set.add(c);
//    assertEquals(c, set.iterator().next());
//    set.clear();
//
//    Buffer buff = TestUtils.randomBuffer(100);
//    set.add(buff);
//    Buffer got1 = (Buffer)set.iterator().next();
//    assertTrue(got1 != buff); // Make sure it's copied
//    assertEquals(buff, set.iterator().next());
//    Buffer got2 = (Buffer)set.iterator().next();
//    assertTrue(got1 != got2); // Should be copied on each get
//    assertTrue(got2 != buff);
//    assertEquals(buff, set.iterator().next());
//    set.clear();
//
//
//    byte[] bytes = TestUtils.randomByteArray(100);
//    set.add(bytes);
//    byte[] bgot1 = (byte[]) set.iterator().next();
//    assertTrue(bgot1 != bytes);
//    assertTrue(TestUtils.byteArraysEqual(bytes, bgot1));
//    byte[] bgot2 = (byte[]) set.iterator().next();
//    assertTrue(bgot2 != bytes);
//    assertTrue(bgot1 != bgot2);
//    assertTrue(TestUtils.byteArraysEqual(bytes, bgot2));
//    set.clear();
//
//    try {
//      set.add(new SomeOtherClass());
//      fail("Should throw exception");
//    } catch (IllegalArgumentException e) {
//      //OK
//    }
//  }



  class SomeOtherClass {
  }
}
