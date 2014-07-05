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
import io.vertx.core.impl.FutureResultImpl;
import io.vertx.core.shareddata.Counter;
import io.vertx.core.shareddata.Lock;
import io.vertx.core.shareddata.NewSharedData;
import io.vertx.core.spi.cluster.AsyncMap;
import io.vertx.core.spi.cluster.ClusterManager;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class NewSharedDataImpl implements NewSharedData {

  private final ClusterManager clusterManager;

  public NewSharedDataImpl(ClusterManager clusterManager) {
    this.clusterManager = clusterManager;
  }

  @Override
  public <K, V> void getMap(String name, Handler<AsyncResult<AsyncMap<K, V>>> resultHandler) {
    if (clusterManager == null) {
      resultHandler.handle(new FutureResultImpl<>(new LocalAsyncMap<>()));
    } else {
      // TODO
    }
  }

  private static final long DEFAULT_LOCK_TIMEOUT = Long.MAX_VALUE;

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

  class LocalAsyncMap<K, V> implements AsyncMap<K, V> {

    private final ConcurrentMap<K, V> map = new ConcurrentHashMap<>();

    @Override
    public void get(K k, Handler<AsyncResult<V>> asyncResultHandler) {
      asyncResultHandler.handle(new FutureResultImpl<V>(map.get(k)));
    }

    @Override
    public void put(K k, V v, Handler<AsyncResult<Void>> completionHandler) {
      map.put(k, v);
      completionHandler.handle(new FutureResultImpl<>((Void)null));
    }

    @Override
    public void remove(K k, Handler<AsyncResult<Boolean>> resultHandler) {
      boolean removed = map.remove(k) != null;
      resultHandler.handle(new FutureResultImpl<>(removed));
    }
  }

}
