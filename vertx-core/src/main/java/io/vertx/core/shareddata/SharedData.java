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

package io.vertx.core.shareddata;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.gen.VertxGen;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
@VertxGen
public interface SharedData {

  <K, V> void getClusterWideMap(String name, Handler<AsyncResult<AsyncMap<K, V>>> resultHandler);

  <K, V> void getClusterWideMapWithOptions(String name, MapOptions options, Handler<AsyncResult<AsyncMap<K, V>>> resultHandler);

  <K, V> void getLocalMap(String name, Handler<AsyncResult<AsyncMap<K, V>>> resultHandler);

  // What about lists? multimap? set?

  void getLock(String name, Handler<AsyncResult<Lock>> resultHandler);

  void getLockWithTimeout(String name, long timeout, Handler<AsyncResult<Lock>> resultHandler);

  void getCounter(String name, Handler<AsyncResult<Counter>> resultHandler);
}
