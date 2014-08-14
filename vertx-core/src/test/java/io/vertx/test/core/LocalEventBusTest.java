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

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.eventbus.Registration;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.eventbus.ReplyFailure;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.impl.ContextImpl;
import io.vertx.core.impl.EventLoopContext;
import io.vertx.core.impl.MultiThreadedWorkerContext;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.impl.WorkerContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class LocalEventBusTest extends EventBusTestBase {

  private Vertx vertx;
  private EventBus eb;

  @Before
  public void before() throws Exception {
    vertx = Vertx.vertx();
    eb = vertx.eventBus();
  }

  @After
  public void after() throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    vertx.close(ar -> {
      assertTrue(ar.succeeded());
      latch.countDown();
    });
    assertTrue(latch.await(30, TimeUnit.SECONDS));
  }

  @Test
  public void testRegisterUnregister() {
    String str = TestUtils.randomUnicodeString(100);
    Handler<Message<String>> handler = msg -> fail("Should not receive message");
    Registration reg = eb.registerHandler(ADDRESS1, handler);
    reg.unregister();
    eb.send(ADDRESS1, str);
    vertx.setTimer(1000, id -> testComplete());
    await();
  }

  @Test
  public void testUnregisterTwice() {
    Handler<Message<String>> handler = msg -> {};
    Registration reg = eb.registerHandler(ADDRESS1, handler);
    reg.unregister();
    reg.unregister(); // Ok to unregister twice
    testComplete();
  }

  @Test
  public void testRegisterLocal() {
    String str = TestUtils.randomUnicodeString(100);
    eb.registerLocalHandler(ADDRESS1, (Message<String> msg) -> {
      assertEquals(str, msg.body());
      testComplete();
    });
    eb.send(ADDRESS1, str);
    await();
  }

  @Test
  public void testRegisterWithCompletionHandler() {
    String str = TestUtils.randomUnicodeString(100);
    Registration reg = eb.registerHandler(ADDRESS1, (Message<String> msg) -> {
      assertEquals(str, msg.body());
      testComplete();
    });
    reg.completionHandler(ar -> {
      assertTrue(ar.succeeded());
      eb.send(ADDRESS1, str);
    });
    await();
  }

  @Test
  public void testSendRoundRobin() {
    String str = TestUtils.randomUnicodeString(100);
    int numHandlers = 10;
    int numMessages = 100;
    Handler<Message<String>>[] handlers = new Handler[numHandlers];
    Map<Handler, Integer> countMap = new ConcurrentHashMap<>();
    AtomicInteger totalCount = new AtomicInteger();
    for (int i = 0; i < numHandlers; i++) {
      int index = i;
      handlers[i] = (Message<String> msg) -> {
        assertEquals(str, msg.body());
        Integer cnt = countMap.get(handlers[index]);
        int icnt = cnt == null ? 0 : cnt;
        icnt++;
        countMap.put(handlers[index], icnt);
        if (totalCount.incrementAndGet() == numMessages) {
          assertEquals(numHandlers, countMap.size());
          for (Integer ind: countMap.values()) {
            assertEquals(numMessages / numHandlers, ind.intValue());
          }
          testComplete();
        }
      };
      eb.registerHandler(ADDRESS1, handlers[i]);
    }

    for (int i = 0; i < numMessages; i++) {
      eb.send(ADDRESS1, str);
    }

    await();
  }

  @Test
  public void testSendRegisterSomeUnregisterOne() {
    String str = TestUtils.randomUnicodeString(100);
    AtomicInteger totalCount = new AtomicInteger();
    Handler<Message<String>> handler1 = msg -> fail("Should not receive message");
    Handler<Message<String>> handler2 = msg -> {
      assertEquals(str, msg.body());
      if (totalCount.incrementAndGet() == 2) {
        testComplete();
      }
    };
    Handler<Message<String>> handler3 = msg -> {
      assertEquals(str, msg.body());
      if (totalCount.incrementAndGet() == 2) {
        testComplete();
      }
    };

    Registration reg = eb.registerHandler(ADDRESS1, handler1);
    eb.registerHandler(ADDRESS1, handler2);
    eb.registerHandler(ADDRESS1, handler3);
    reg.unregister();
    eb.send(ADDRESS1, str);
    eb.send(ADDRESS1, str);

    await();
  }

  @Test
  public void testSendRegisterSameHandlerMultipleTimes() {
    String str = TestUtils.randomUnicodeString(100);
    AtomicInteger totalCount = new AtomicInteger();
    Handler<Message<String>> handler = (Message<String> msg) -> {
      assertEquals(str, msg.body());
      if (totalCount.incrementAndGet() == 3) {
        testComplete();
      }
    };
    eb.registerHandler(ADDRESS1, handler);
    eb.registerHandler(ADDRESS1, handler);
    eb.registerHandler(ADDRESS1, handler);

    eb.send(ADDRESS1, str);
    eb.send(ADDRESS1, str);
    eb.send(ADDRESS1, str);
    await();
  }

  @Test
  public void testSendWithNoHandler() {
    eb.send(ADDRESS1, TestUtils.randomUnicodeString(100));
    vertx.setTimer(1000, id -> testComplete());
    await();
  }

  @Test
  public void testSendMultipleAddresses() {
    String str = TestUtils.randomUnicodeString(100);
    AtomicInteger cnt = new AtomicInteger();
    eb.registerHandler(ADDRESS1, (Message<String> msg) -> {
      fail("Should not receive message");
    });
    eb.registerHandler(ADDRESS2, (Message<String> msg) -> {
      assertEquals(str, msg.body());
      if (cnt.incrementAndGet() == 2) {
        testComplete();
      }
    });
    eb.send(ADDRESS2, str);
    eb.send(ADDRESS2, str);
    await();
  }

  @Test
  public void testSendWithTimeoutNoTimeoutNoReply() {
    String str = TestUtils.randomUnicodeString(1000);
    eb.registerHandler(ADDRESS1, (Message<String> msg) -> {
      assertEquals(str, msg.body());
      testComplete();
    });
    long timeout = 1000;
    eb.sendWithOptions(ADDRESS1, str, DeliveryOptions.options().setTimeout(timeout), ar -> {
    });
    await();
  }

  @Test
  public void testSendWithReply() {
    String str = TestUtils.randomUnicodeString(1000);
    String reply = TestUtils.randomUnicodeString(1000);
    eb.registerHandler(ADDRESS1, (Message<String> msg) -> {
      assertEquals(str, msg.body());
      msg.reply(reply);
    });
    eb.send(ADDRESS1, str, onSuccess((Message<String> msg) -> {
      assertEquals(reply, msg.body());
      testComplete();
    }));
    await();
  }

  @Test
  public void testReplyToReply() {
    String str = TestUtils.randomUnicodeString(1000);
    String reply = TestUtils.randomUnicodeString(1000);
    String replyReply = TestUtils.randomUnicodeString(1000);
    eb.registerHandler(ADDRESS1, (Message<String> msg) -> {
      assertEquals(str, msg.body());
      msg.reply(reply, onSuccess((Message<String> rep) -> {
        assertEquals(replyReply, rep.body());
        testComplete();
      }));
    });
    eb.send(ADDRESS1, str, onSuccess((Message<String>msg) -> {
      assertEquals(reply, msg.body());
      msg.reply(replyReply);
    }));
    await();
  }

  @Test
  public void testSendReplyWithTimeout() {
    String str = TestUtils.randomUnicodeString(1000);
    String reply = TestUtils.randomUnicodeString(1000);
    eb.registerHandler(ADDRESS1, (Message<String> msg) -> {
      assertEquals(str, msg.body());
      long start = System.currentTimeMillis();
      long timeout = 1000;
      msg.replyWithOptions(reply, DeliveryOptions.options().setTimeout(timeout), ar -> {
        long now = System.currentTimeMillis();
        assertFalse(ar.succeeded());
        Throwable cause = ar.cause();
        assertTrue(cause instanceof ReplyException);
        ReplyException re = (ReplyException) cause;
        assertEquals(-1, re.failureCode());
        assertEquals(ReplyFailure.TIMEOUT, re.failureType());
        assertTrue(now - start >= timeout);
        testComplete();
      });
    });
    eb.send(ADDRESS1, str, onSuccess((Message<String>msg) -> {
      assertEquals(reply, msg.body());
      // Now don't reply
    }));
    await();
  }

  @Test
  public void testSendReplyWithTimeoutNoTimeout() {
    String str = TestUtils.randomUnicodeString(1000);
    String reply = TestUtils.randomUnicodeString(1000);
    String replyReply = TestUtils.randomUnicodeString(1000);
    eb.registerHandler(ADDRESS1, (Message<String> msg) -> {
      assertEquals(str, msg.body());
      long timeout = 1000;
      msg.replyWithOptions(reply, DeliveryOptions.options().setTimeout(timeout), ar -> {
        assertTrue(ar.succeeded());
        assertEquals(replyReply, ar.result().body());
        testComplete();
      });
    });
    eb.send(ADDRESS1, str, onSuccess((Message<String>msg) -> {
      assertEquals(reply, msg.body());
      msg.reply(replyReply);
    }));
    await();
  }

  @Test
  public void testSendWithTimeoutNoTimeoutReply() {
    String str = TestUtils.randomUnicodeString(1000);
    eb.registerHandler(ADDRESS1, (Message<String> msg) -> {
      assertEquals(str, msg.body());
      msg.reply(23);
    });
    long timeout = 1000;
    eb.sendWithOptions(ADDRESS1, str, DeliveryOptions.options().setTimeout(timeout), (AsyncResult<Message<Integer>> ar) -> {
      assertTrue(ar.succeeded());
      assertEquals(23, (int) ar.result().body());
      testComplete();
    });
    await();
  }

  @Test
  public void testSendWithTimeoutNoReply() {
    String str = TestUtils.randomUnicodeString(1000);
    eb.registerHandler(ADDRESS1, (Message<String> msg) -> {
      assertEquals(str, msg.body());
    });
    long timeout = 1000;
    long start = System.currentTimeMillis();
    eb.sendWithOptions(ADDRESS1, str, DeliveryOptions.options().setTimeout(timeout), (AsyncResult<Message<Integer>> ar) -> {
      long now = System.currentTimeMillis();
      assertFalse(ar.succeeded());
      Throwable cause = ar.cause();
      assertTrue(cause instanceof ReplyException);
      ReplyException re = (ReplyException)cause;
      assertEquals(-1, re.failureCode());
      assertEquals(ReplyFailure.TIMEOUT, re.failureType());
      assertTrue(now - start >= timeout);
      testComplete();
    });
    await();
  }

  @Test
  public void testSendWithTimeoutNoHandlers() {
    String str = TestUtils.randomUnicodeString(1000);
    long timeout = 1000;
    eb.sendWithOptions(ADDRESS1, str, DeliveryOptions.options().setTimeout(timeout), (AsyncResult<Message<Integer>> ar) -> {
      assertFalse(ar.succeeded());
      Throwable cause = ar.cause();
      assertTrue(cause instanceof ReplyException);
      ReplyException re = (ReplyException)cause;
      assertEquals(-1, re.failureCode());
      assertEquals(ReplyFailure.NO_HANDLERS, re.failureType());
      testComplete();
    });
    await();
  }

  @Test
  public void testSendWithTimeoutRecipientFailure() {
    String str = TestUtils.randomUnicodeString(1000);
    String failureMsg = TestUtils.randomUnicodeString(1000);
    int failureCode = 123;
    eb.registerHandler(ADDRESS1, (Message<String> msg) -> {
      assertEquals(str, msg.body());
      msg.fail(failureCode, failureMsg);
    });
    long timeout = 1000;
    eb.sendWithOptions(ADDRESS1, str, DeliveryOptions.options().setTimeout(timeout), (AsyncResult<Message<Integer>> ar) -> {
      assertFalse(ar.succeeded());
      Throwable cause = ar.cause();
      assertTrue(cause instanceof ReplyException);
      ReplyException re = (ReplyException)cause;
      assertEquals(failureCode, re.failureCode());
      assertEquals(failureMsg, re.getMessage());
      assertEquals(ReplyFailure.RECIPIENT_FAILURE, re.failureType());
      testComplete();
    });
    await();
  }

  @Test
  public void testSendWithTimeoutReplyAfterTimeout() {
    String str = TestUtils.randomUnicodeString(1000);
    long timeout = 1000;
    eb.registerHandler(ADDRESS1, (Message<String> msg) -> {
      assertEquals(str, msg.body());
      vertx.setTimer((int)(timeout * 1.5), id -> {
        msg.reply("too late!");
      });
    });
    eb.sendWithOptions(ADDRESS1, str, DeliveryOptions.options().setTimeout(timeout), (AsyncResult<Message<Integer>> ar) -> {
      assertFalse(ar.succeeded());
      Throwable cause = ar.cause();
      assertTrue(cause instanceof ReplyException);
      ReplyException re = (ReplyException)cause;
      assertEquals(-1, re.failureCode());
      assertEquals(ReplyFailure.TIMEOUT, re.failureType());
      testComplete();
    });
    await();
  }

  @Test
  public void testSendWithTimeoutNoTimeoutAfterReply() {
    String str = TestUtils.randomUnicodeString(1000);
    long timeout = 1000;
    eb.registerHandler(ADDRESS1, (Message<String> msg) -> {
      assertEquals(str, msg.body());
      msg.reply("a reply");
    });
    AtomicBoolean received = new AtomicBoolean();
    eb.sendWithOptions(ADDRESS1, str, DeliveryOptions.options().setTimeout(timeout), (AsyncResult<Message<Integer>> ar) -> {
      assertTrue(ar.succeeded());
      assertFalse(received.get());
      received.set(true);
      // Now wait longer than timeout and make sure we don't receive any other reply
      vertx.setTimer(timeout * 2, tid -> {
        testComplete();
      });
    });
    await();
  }

  // Sends with different types

  @Test
  public void testPublish() {
    String str = TestUtils.randomUnicodeString(100);
    eb.registerHandler(ADDRESS1, (Message<String> msg) -> {
      assertEquals(str, msg.body());
      testComplete();
    });
    eb.publish(ADDRESS1, str);
    await();
  }

  @Test
  public void testPublishMultipleHandlers() {
    String str = TestUtils.randomUnicodeString(100);
    AtomicInteger count = new AtomicInteger();
    eb.registerHandler(ADDRESS1, (Message<String> msg) -> {
      assertEquals(str, msg.body());
      if (count.incrementAndGet() == 2) {
        testComplete();
      }
    });
    eb.registerHandler(ADDRESS1, (Message<String> msg) -> {
      assertEquals(str, msg.body());
      if (count.incrementAndGet() == 2) {
        testComplete();
      }
    });
    eb.publish(ADDRESS1, str);
    await();
  }

  @Test
  public void testPublishSameHandlerRegisteredTwice() {
    String str = TestUtils.randomUnicodeString(1000);
    AtomicInteger count = new AtomicInteger();
    Handler<Message<String>> handler = (Message<String> msg) -> {
      assertEquals(str, msg.body());
      if (count.incrementAndGet() == 2) {
        testComplete();
      }
    };
    eb.registerHandler(ADDRESS1, handler);
    eb.registerHandler(ADDRESS1, handler);
    eb.publish(ADDRESS1, str);
    await();
  }

  @Test
  public void testPublishMultipleHandlersUnregisterOne() {
    String str = TestUtils.randomUnicodeString(1000);
    Handler<Message<String>> handler1 = (Message<String> msg) -> {
      assertEquals(str, msg.body());
      testComplete();
    };
    Handler<Message<String>> handler2 = (Message<String> msg) -> {
      fail("Should not be called");
    };

    eb.registerHandler(ADDRESS1, handler1);
    Registration reg = eb.registerHandler(ADDRESS1, handler2);
    reg.unregister();
    eb.publish(ADDRESS1, str);

    await();
  }

  @Test
  public void testPublishMultipleHandlersDifferentAddresses() {
    String str = TestUtils.randomUnicodeString(1000);
    eb.registerHandler(ADDRESS1, (Message<String> msg) -> {
      assertEquals(str, msg.body());
      testComplete();
    });
    eb.registerHandler(ADDRESS2, (Message<String> msg) -> {
      fail("Should not receive message");
    });
    eb.publish(ADDRESS1, str);
    await();
  }

  @Test
  public void testNonRegisteredCodecType() {
    class Boom {
    }
    eb.registerHandler("foo", msg -> {
      fail("Should not have gotten here");
    });

    try {
      eb.send("foo", new Boom());
    } catch (IllegalArgumentException e) {
      testComplete();
    }

    await();
  }


  @Test
  public void testCloseEventBus() {
    eb.close(ar -> {
      assertTrue(ar.succeeded());
      testComplete();
    });
    await();
  }

  @Test
  public void testInVerticle() throws Exception {
    testInVerticle(false, false);
  }

  @Test
  public void testInWorkerVerticle() throws Exception {
    testInVerticle(true, false);
  }

  @Test
  public void testInMultithreadedWorkerVerticle() throws Exception {
    testInVerticle(true, true);
  }

  private void testInVerticle(boolean  worker, boolean multiThreaded) throws Exception {
    class MyVerticle extends AbstractVerticle {
      Context ctx;
      @Override
      public void start() {
        ctx = vertx.currentContext();
        if (worker) {
          if (multiThreaded) {
            assertTrue(ctx instanceof MultiThreadedWorkerContext);
          } else {
            assertTrue(ctx instanceof WorkerContext && !(ctx instanceof MultiThreadedWorkerContext));
          }
        } else {
          assertTrue(ctx instanceof EventLoopContext);
        }
        Thread thr = Thread.currentThread();
        Registration reg = vertx.eventBus().registerHandler(ADDRESS1, msg -> {
          assertSame(ctx, vertx.currentContext());
          if (!worker) {
            assertSame(thr, Thread.currentThread());
          }
          msg.reply("bar");
        });
        reg.completionHandler(ar -> {
          assertTrue(ar.succeeded());
          assertSame(ctx, vertx.currentContext());
          if (!worker) {
            assertSame(thr, Thread.currentThread());
          }
          vertx.eventBus().send(ADDRESS1, "foo", onSuccess((Message<Object> reply) -> {
            assertSame(ctx, vertx.currentContext());
            if (!worker) {
              assertSame(thr, Thread.currentThread());
            }
            assertEquals("bar", reply.body());
            testComplete();
          }));
        });
      }
    }
    MyVerticle verticle = new MyVerticle();
    vertx.deployVerticleWithOptions(verticle, DeploymentOptions.options().setWorker(worker).setMultiThreaded(multiThreaded));
    await();
  }

  @Test
  public void testContextsSend() throws Exception {
    Set<ContextImpl> contexts = new ConcurrentHashSet<>();
    CountDownLatch latch = new CountDownLatch(2);
    vertx.eventBus().registerHandler(ADDRESS1, msg -> {
      msg.reply("bar");
      contexts.add(((VertxInternal) vertx).getContext());
      latch.countDown();
    });
    vertx.eventBus().send(ADDRESS1, "foo", onSuccess((Message<Object> reply) -> {
      assertEquals("bar", reply.body());
      contexts.add(((VertxInternal) vertx).getContext());
      latch.countDown();
    }));
    awaitLatch(latch);
    assertEquals(2, contexts.size());
  }
  
  @Test
  public void testContextsPublish() throws Exception {
    Set<ContextImpl> contexts = new ConcurrentHashSet<>();
    AtomicInteger cnt = new AtomicInteger();
    int numHandlers = 10;
    for (int i = 0; i < numHandlers; i++) {
      vertx.eventBus().registerHandler(ADDRESS1, msg -> {
        contexts.add(((VertxInternal) vertx).getContext());
        if (cnt.incrementAndGet() == numHandlers) {
          assertEquals(numHandlers, contexts.size());
          testComplete();
        }
      });
    }
    vertx.eventBus().publish(ADDRESS1, "foo");
    await();
  }

  /*
  Test POJO->POJO explicit decoder
  Test String -> POJO explicit decoder
  Test byte[] -> POJO explicit decoder
  Test POJO default decoder
  Test send object no decoder
  Test try register system decoder
  Test send no decoder
  Test unregister user decoder
  Test unregister default decoder
  Test register already registered codec and default codec
  Codec with null name

   */

  @Test
  public void testDecoder() throws Exception {
    MessageCodec codec = new MyPOJOEncoder1();
    vertx.eventBus().registerCodec(codec);
    String str = TestUtils.randomAlphaString(100);
    vertx.eventBus().registerHandler(ADDRESS1, (Message<String> msg) -> {
      assertEquals(str, msg.body());
      testComplete();
    });
    vertx.eventBus().sendWithOptions(ADDRESS1, new MyPOJO(str), DeliveryOptions.options().setCodecName(codec.name()));
    await();
  }

  public static class MyPOJOEncoder1 implements MessageCodec<MyPOJO, String> {

    @Override
    public void encodeToWire(Buffer buffer, MyPOJO myPOJO) {

    }

    @Override
    public String decodeFromWire(int pos, Buffer buffer) {
      return null;
    }

    @Override
    public String transform(MyPOJO myPOJO) {
      return myPOJO.getStr();
    }

    @Override
    public int bodyLengthGuess(MyPOJO myPOJO) {
      return 0;
    }

    @Override
    public String name() {
      return "mypojoencoder";
    }

    @Override
    public byte systemCodecID() {
      return -1;
    }
  }


  public static class MyPOJO {
    private String str;

    public MyPOJO(String str) {
      this.str = str;
    }

    public String getStr() {
      return str;
    }
  }

  @Override
  protected <T> void testSend(T val, Consumer<T> consumer) {
    eb.registerHandler(ADDRESS1, (Message<T> msg) -> {
      if (consumer == null) {
        assertEquals(val, msg.body());
      } else {
        consumer.accept(msg.body());
      }
      testComplete();
    });
    eb.send(ADDRESS1, val);
    await();
  }

  @Override
  protected <T> void testReply(T val, Consumer<T> consumer) {
    String str = TestUtils.randomUnicodeString(1000);
    eb.registerHandler(ADDRESS1, msg -> {
      assertEquals(str, msg.body());
      msg.reply(val);
    });
    eb.send(ADDRESS1, str, onSuccess((Message<T> reply) -> {
      if (consumer == null) {
        assertEquals(val, reply.body());
      } else {
        consumer.accept(reply.body());
      }
      testComplete();
    }));
    await();
  }

  @Override
  protected <T> void testPublish(T val, Consumer<T> consumer) {
    AtomicInteger count = new AtomicInteger();
    class MyHandler implements Handler<Message<T>> {
      @Override
      public void handle(Message<T> msg) {
        if (consumer == null) {
          assertEquals(val, msg.body());
        } else {
          consumer.accept(msg.body());
        }
        if (count.incrementAndGet() == 2) {
          testComplete();
        }
      }
    }
    eb.registerHandler(ADDRESS1, new MyHandler());
    eb.registerHandler(ADDRESS1, new MyHandler());
    eb.publish(ADDRESS1, (T) val);
    await();
  }
}

