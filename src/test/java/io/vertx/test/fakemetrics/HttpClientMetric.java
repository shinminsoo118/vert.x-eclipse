/*
 * Copyright (c) 2011-2013 The original author or authors
 *  ------------------------------------------------------
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *      The Eclipse Public License is available at
 *      http://www.eclipse.org/legal/epl-v10.html
 *
 *      The Apache License v2.0 is available at
 *      http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */

package io.vertx.test.fakemetrics;

import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class HttpClientMetric {

  public final HttpClientRequest request;
  public final List<Throwable> requestExceptions = Collections.synchronizedList(new ArrayList<>());
  public final AtomicReference<HttpClientResponse> response = new AtomicReference<>();
  public final AtomicBoolean ended = new AtomicBoolean();
  public final SocketMetric socket;

  public HttpClientMetric(HttpClientRequest request, SocketMetric socket) {
    this.request = request;
    this.socket = socket;
  }
}
