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
package org.vertx.java.core.http.impl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.net.SocketAddress;

/**
 * {@link Channel} which wraps a {@link ChannelHandlerContext} to delegate most operations to it.
 *
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 */
final class ChannelAdapter implements Channel {
  private final ChannelHandlerContext ctx;

  ChannelAdapter(ChannelHandlerContext ctx) {
    this.ctx = ctx;
  }

  @Override
  public EventLoop eventLoop() {
    return ctx.channel().eventLoop();
  }

  @Override
  public Channel parent() {
    return ctx.channel().parent();
  }

  @Override
  public ChannelConfig config() {
    return ctx.channel().config();
  }

  @Override
  public boolean isOpen() {
    return ctx.channel().isOpen();
  }

  @Override
  public boolean isRegistered() {
    return ctx.channel().isRegistered();
  }

  @Override
  public boolean isActive() {
    return ctx.channel().isActive();
  }

  @Override
  public ChannelMetadata metadata() {
    return ctx.channel().metadata();
  }

  @Override
  public SocketAddress localAddress() {
    return ctx.channel().localAddress();
  }

  @Override
  public SocketAddress remoteAddress() {
    return ctx.channel().remoteAddress();
  }

  @Override
  public ChannelFuture closeFuture() {
    return ctx.channel().closeFuture();
  }

  @Override
  public boolean isWritable() {
    return ctx.channel().isWritable();
  }

  @Override
  public Unsafe unsafe() {
    return ctx.channel().unsafe();
  }

  @Override
  public ChannelPipeline pipeline() {
    return ctx.pipeline();
  }

  @Override
  public ByteBufAllocator alloc() {
    return ctx.alloc();
  }

  @Override
  public ChannelPromise newPromise() {
    return ctx.channel().newPromise();
  }

  @Override
  public ChannelProgressivePromise newProgressivePromise() {
    return ctx.channel().newProgressivePromise();
  }

  @Override
  public ChannelFuture newSucceededFuture() {
    return ctx.channel().newSucceededFuture();
  }

  @Override
  public ChannelFuture newFailedFuture(Throwable cause) {
    return ctx.channel().newFailedFuture(cause);
  }

  @Override
  public ChannelPromise voidPromise() {
    return ctx.channel().voidPromise();
  }

  @Override
  public ChannelFuture bind(SocketAddress localAddress) {
    return ctx.bind(localAddress);
  }

  @Override
  public ChannelFuture connect(SocketAddress remoteAddress) {
     return ctx.connect(remoteAddress);
  }

  @Override
  public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
    return ctx.connect(remoteAddress, localAddress);
  }

  @Override
  public ChannelFuture disconnect() {
    return ctx.disconnect();
  }

  @Override
  public ChannelFuture close() {
    return ctx.close();
  }

  @Override
  public ChannelFuture deregister() {
    return ctx.deregister();
  }

  @Override
  public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
    return ctx.bind(localAddress, promise);
  }

  @Override
  public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
    return ctx.connect(remoteAddress, promise);
  }

  @Override
  public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
    return ctx.connect(remoteAddress, localAddress, promise);
  }

  @Override
  public ChannelFuture disconnect(ChannelPromise promise) {
    return ctx.disconnect(promise);
  }

  @Override
  public ChannelFuture close(ChannelPromise promise) {
    return ctx.close(promise);
  }

  @Override
  public ChannelFuture deregister(ChannelPromise promise) {
    return ctx.deregister(promise);
  }

  @Override
  public Channel read() {
    ctx.read();
    return this;
  }

  @Override
  public ChannelFuture write(Object msg) {
    return ctx.write(msg);
  }

  @Override
  public ChannelFuture write(Object msg, ChannelPromise promise) {
    return ctx.write(msg, promise);
  }

  @Override
  public Channel flush() {
    ctx.flush();
    return this;
  }

  @Override
  public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
    return ctx.writeAndFlush(msg, promise);
  }

  @Override
  public ChannelFuture writeAndFlush(Object msg) {
    return ctx.writeAndFlush(msg);
  }

  @Override
  public <T> Attribute<T> attr(AttributeKey<T> key) {
    return ctx.channel().attr(key);
  }

  @Override
  public int compareTo(Channel o) {
    return ctx.channel().compareTo(o);
  }
}
