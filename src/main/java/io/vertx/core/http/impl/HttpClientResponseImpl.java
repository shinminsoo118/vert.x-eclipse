/*
 * Copyright (c) 2011-2013 The original author or authors
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

package io.vertx.core.http.impl;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.VoidHandler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.net.NetSocket;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is optimised for performance when used on the same event loop that is was passed to the handler with.
 * However it can be used safely from other threads.
 *
 * The internal state is protected using the synchronized keyword. If always used on the same event loop, then
 * we benefit from biased locking which makes the overhead of synchronized near zero.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class HttpClientResponseImpl implements HttpClientResponse  {

  static {
    System.out.println("****************** RUNNING DIAGNOSTIC VERSION ******");
  }

  private final int statusCode;
  private final String statusMessage;
  private final HttpClientRequestImpl request;
  private final Vertx vertx;
  private final ClientConnection conn;
  private final HttpResponse response;

  private Handler<Buffer> dataHandler;
  private Handler<Void> endHandler;
  private Handler<Throwable> exceptionHandler;
  private LastHttpContent trailer;
  private boolean paused;
  private Queue<BufferHolder> pausedChunks;
  private boolean hasPausedEnd;
  private LastHttpContent pausedTrailer;
  private NetSocket netSocket;

  // Track for metrics
  private long bytesRead;

  // Cache these for performance
  private MultiMap headers;
  private MultiMap trailers;
  private List<String> cookies;

  HttpClientResponseImpl(Vertx vertx, HttpClientRequestImpl request, ClientConnection conn, HttpResponse response) {
    this.vertx = vertx;
    this.statusCode = response.getStatus().code();
    this.statusMessage = response.getStatus().reasonPhrase();
    this.request = request;
    this.conn = conn;
    this.response = response;
  }

  @Override
  public int statusCode() {
    return statusCode;
  }

  @Override
  public String statusMessage() {
    return statusMessage;
  }

  @Override
  public synchronized MultiMap headers() {
    if (headers == null) {
      headers = new HeadersAdaptor(response.headers());
    }
    return headers;
  }

  @Override
  public String getHeader(String headerName) {
    return headers().get(headerName);
  }

  @Override
  public synchronized MultiMap trailers() {
    if (trailers == null) {
      trailers = new HeadersAdaptor(new DefaultHttpHeaders());
    }
    return trailers;
  }

  @Override
  public String getTrailer(String trailerName) {
    return trailers.get(trailerName);
  }

  @Override
  public synchronized List<String> cookies() {
    if (cookies == null) {
      cookies = new ArrayList<>();
      cookies.addAll(response.headers().getAll(HttpHeaders.SET_COOKIE));
      if (trailer != null) {
        cookies.addAll(trailer.trailingHeaders().getAll(HttpHeaders.SET_COOKIE));
      }
    }
    return cookies;
  }

  @Override
  public synchronized HttpClientResponse handler(Handler<Buffer> dataHandler) {
    this.dataHandler = dataHandler;
    return this;
  }

  @Override
  public synchronized HttpClientResponse endHandler(Handler<Void> endHandler) {
    this.endHandler = endHandler;
    return this;
  }

  @Override
  public synchronized HttpClientResponse exceptionHandler(Handler<Throwable> exceptionHandler) {
    this.exceptionHandler = exceptionHandler;
    return this;
  }

  @Override
  public synchronized HttpClientResponse pause() {
    paused = true;
    conn.doPause();
    return this;
  }

  @Override
  public synchronized HttpClientResponse resume() {
    paused = false;
    doResume();
    conn.doResume();
    return this;
  }

  @Override
  public HttpClientResponse bodyHandler(final Handler<Buffer> bodyHandler) {
    BodyHandler handler = new BodyHandler();
    handler(handler);
    endHandler(new VoidHandler() {
      public void handle() {
        handler.notifyHandler(bodyHandler);
      }
    });
    return this;
  }

  private void doResume() {
    if (pausedChunks != null) {
      BufferHolder chunk;
      while ((chunk = pausedChunks.poll()) != null) {
        final BufferHolder theChunk = chunk;
        vertx.runOnContext(v -> handleChunk(theChunk));
      }
    }
    if (hasPausedEnd) {
      final LastHttpContent theTrailer = pausedTrailer;
      vertx.runOnContext(v -> handleEnd(theTrailer));
      hasPausedEnd = false;
      pausedTrailer = null;
    }
  }

  private final class BufferHolder {
    final int sequence;
    final Buffer buff;

    public BufferHolder(int sequence, Buffer buff) {
      this.sequence = sequence;
      this.buff = buff;
    }
  }

  private AtomicInteger seq = new AtomicInteger();
  private AtomicInteger handledSeq = new AtomicInteger();

  synchronized void handleChunk(Buffer data) {
    handleChunk(new BufferHolder(seq.getAndIncrement(), data));
  }

  synchronized void handleChunk(BufferHolder data) {
    bytesRead += data.buff.length();
    if (paused) {
      if (pausedChunks == null) {
        pausedChunks = new ArrayDeque<>();
      }
      pausedChunks.add(data);
    } else {
      request.dataReceived();
      if (dataHandler != null) {
        if (data.sequence != handledSeq.getAndIncrement()) {
          System.out.println("!!!! OUT OF SEQUENCE");
        }
        dataHandler.handle(data.buff);
      }
    }
  }

  synchronized void handleEnd(LastHttpContent trailer) {
    conn.reportBytesRead(bytesRead);
    bytesRead = 0;
    request.reportResponseEnd(this);
    if (paused) {
      hasPausedEnd = true;
      pausedTrailer = trailer;
    } else {
      this.trailer = trailer;
      trailers = new HeadersAdaptor(trailer.trailingHeaders());
      if (endHandler != null) {
        endHandler.handle(null);
      }
    }
  }

  synchronized void handleException(Throwable e) {
    if (exceptionHandler != null) {
      exceptionHandler.handle(e);
    }
  }

  @Override
  public synchronized NetSocket netSocket() {
    if (netSocket == null) {
      netSocket = conn.createNetSocket();
    }
    return netSocket;
  }

  private static final class BodyHandler implements Handler<Buffer> {
    private Buffer body;

    @Override
    public void handle(Buffer event) {
      body().appendBuffer(event);
    }

    private Buffer body() {
      if (body == null) {
        body = Buffer.buffer();
      }
      return body;
    }

    void notifyHandler(Handler<Buffer> bodyHandler) {
      bodyHandler.handle(body());
      // reset body so it can get GC'ed
      body = null;
    }
  }
}
