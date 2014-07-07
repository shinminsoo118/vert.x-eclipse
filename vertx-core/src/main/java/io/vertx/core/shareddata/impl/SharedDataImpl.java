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

package io.vertx.core.shareddata.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.impl.FutureResultImpl;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.Counter;
import io.vertx.core.shareddata.Lock;
import io.vertx.core.shareddata.MapOptions;
import io.vertx.core.shareddata.SharedData;
import io.vertx.core.spi.cluster.ClusterManager;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class SharedDataImpl implements SharedData {

  private static final long DEFAULT_LOCK_TIMEOUT = Long.MAX_VALUE;

  private final Vertx vertx;
  private final ClusterManager clusterManager;
  private final ConcurrentMap<String, AsyncMap<?, ?>> localMaps = new ConcurrentHashMap<>();

  public SharedDataImpl(Vertx vertx, ClusterManager clusterManager) {
    this.vertx = vertx;
    this.clusterManager = clusterManager;
  }

  @Override
  public <K, V> void getClusterWideMap(String name, Handler<AsyncResult<AsyncMap<K, V>>> resultHandler) {
    getClusterWideMapWithOptions(name, null, resultHandler);
  }

  @Override
  public <K, V> void getClusterWideMapWithOptions(String name, MapOptions options, Handler<AsyncResult<AsyncMap<K, V>>> resultHandler) {
    if (clusterManager == null) {
      // Just return the local map
      getLocalMap(name, resultHandler);
    } else {
      clusterManager.<K, V>getAsyncMap(name, options, ar -> {
        if (ar.succeeded()) {
          // Wrap it
          resultHandler.handle(new FutureResultImpl<>(new WrappedAsyncMap<K, V>(ar.result())));
        } else {
          resultHandler.handle(new FutureResultImpl<>(ar.cause()));
        }
      });
    }
  }

  @Override
  public <K, V> void getLocalMap(String name, Handler<AsyncResult<AsyncMap<K, V>>> resultHandler) {
    AsyncMap<K, V> map = (AsyncMap<K, V>)localMaps.get(name);
    if (map == null) {
      map = new LocalAsyncMap<>();
      AsyncMap<K, V> prev = (AsyncMap<K, V>)localMaps.putIfAbsent(name, map);
      if (prev != null) {
        map = prev;
      }
    }
    AsyncMap<K, V> theMap = map;
    vertx.runOnContext(v -> resultHandler.handle(new FutureResultImpl<>(theMap)));
  }


  /*
  We allow:

  all primitive types and their boxed objects
  string
  Buffer (copied if local)
  byte[] copied if local
   */



  class WrappedAsyncMap<K, V> implements AsyncMap<K, V> {

    private final AsyncMap<K, V> delegate;

    WrappedAsyncMap(AsyncMap<K, V> other) {
      this.delegate = other;
    }

    @Override
    public void get(K k, Handler<AsyncResult<V>> asyncResultHandler) {
      delegate.get(k, asyncResultHandler);
    }

    @Override
    public void put(K k, V v, Handler<AsyncResult<Void>> completionHandler) {
      //checkType(k, v);
      delegate.put(k, v, completionHandler);
    }

    @Override
    public void putIfAbsent(K k, V v, Handler<AsyncResult<V>> completionHandler) {
      delegate.putIfAbsent(k, v, completionHandler);
    }

    @Override
    public void remove(K k, Handler<AsyncResult<Boolean>> resultHandler) {
      delegate.remove(k, resultHandler);
    }

    @Override
    public void clear() {
      delegate.clear();
    }
  }

  class LocalAsyncMap<K, V> implements AsyncMap<K, V> {

    private final ConcurrentMap<K, V> map = new ConcurrentHashMap<>();

    @Override
    public void get(K k, Handler<AsyncResult<V>> asyncResultHandler) {
      vertx.runOnContext(v -> asyncResultHandler.handle(new FutureResultImpl<V>(map.get(k))));
    }

    @Override
    public void put(K k, V v, Handler<AsyncResult<Void>> completionHandler) {
      map.put(k, v);
      vertx.runOnContext(vo -> completionHandler.handle(new FutureResultImpl<>((Void) null)));
    }

    @Override
    public void putIfAbsent(K k, V v, Handler<AsyncResult<V>> completionHandler) {
      V prev = map.putIfAbsent(k, v);
      vertx.runOnContext(vo -> completionHandler.handle(new FutureResultImpl<>(prev)));
    }

    @Override
    public void remove(K k, Handler<AsyncResult<Boolean>> resultHandler) {
      boolean removed = map.remove(k) != null;
      vertx.runOnContext(v -> resultHandler.handle(new FutureResultImpl<>(removed)));
    }

    @Override
    public void clear() {
      map.clear();
    }
  }

  // Locks and counters - TODO

  @Override
  public void getLock(String name, Handler<AsyncResult<Lock>> resultHandler) {
    if (clusterManager == null) {
      getLocalLock(name, DEFAULT_LOCK_TIMEOUT, resultHandler);
    } else {
      // TODO
    }
  }

  @Override
  public void getLockWithTimeout(String name, long timeout, Handler<AsyncResult<Lock>> resultHandler) {

  }

  @Override
  public void getCounter(String name, Handler<AsyncResult<Counter>> resultHandler) {

  }

  private ConcurrentMap<String, LocalLock> localLocks = new ConcurrentHashMap<>();

  private static class LockWaiter {
    final Context context;
    final Handler<AsyncResult<Lock>> resultHandler;

    private LockWaiter(Context context, Handler<AsyncResult<Lock>> resultHandler) {
      this.context = context;
      this.resultHandler = resultHandler;
    }
  }

  private void getLocalLock(String name, long timeout, Handler<AsyncResult<Lock>> resultHandler) {
    LocalLock lock = new LocalLock();
    LocalLock prev = localLocks.putIfAbsent(name, lock);
    if (prev != null) {
      lock = prev;
    }
    lock.acquire(timeout, resultHandler);
  }

  class LocalLock implements Lock {

    LinkedList<LockWaiter> waiters = new LinkedList<>();

    boolean owned;

    private void checkWaiters() {

    }

    void acquire(long timeout, Handler<AsyncResult<Lock>> resultHandler) {

    }

    @Override
    public void release() {

    }
  }



}
