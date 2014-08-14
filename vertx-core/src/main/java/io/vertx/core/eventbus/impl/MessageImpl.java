/*
 * Copyright 2014 Red Hat, Inc.
 *
 *   Red Hat licenses this file to you under the Apache License, version 2.0
 *   (the "License"); you may not use this file except in compliance with the
 *   License.  You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *   WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 *   License for the specific language governing permissions and limitations
 *   under the License.
 */

package io.vertx.core.eventbus.impl;

import io.netty.util.CharsetUtil;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.eventbus.ReplyFailure;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.core.net.impl.ServerID;

import java.util.Map;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class MessageImpl<U, V> implements Message<V> {

  private static final Logger log = LoggerFactory.getLogger(MessageImpl.class);

  private static final byte WIRE_PROTOCOL_VERSION = 1;

  private EventBusImpl bus;
  private ServerID sender;
  private String address;
  private String replyAddress;
  private U sentBody;
  private V receivedBody;
  private MessageCodec<U, V> messageCodec;
  private boolean send;
  private Buffer wireBuffer;
  private int bufferPos;

  public MessageImpl() {
  }

  public MessageImpl(ServerID sender, String address, String replyAddress, U sentBody,
                     MessageCodec<U, V> messageCodec, boolean send) {
    this.sender = sender;
    this.address = address;
    this.replyAddress = replyAddress;
    this.sentBody = sentBody;
    this.messageCodec = messageCodec;
    this.send = send;
  }

  private MessageImpl(MessageImpl<U, V> other) {
    this.bus = other.bus;
    this.sender = other.sender;
    this.address = other.address;
    this.replyAddress = other.replyAddress;
    this.messageCodec = other.messageCodec;
    if (other.sentBody != null) {
      // This will only be true if the message has been sent locally
      this.sentBody = other.sentBody;
      this.receivedBody = messageCodec.transform(other.sentBody);
    }
    this.wireBuffer = other.wireBuffer;
    this.bufferPos = other.bufferPos;
    this.send = other.send;
  }

  public MessageImpl<U, V> copyBeforeReceive() {
    return new MessageImpl<>(this);
  }

  @Override
  public String address() {
    return address;
  }

  @Override
  public V body() {
    // Lazily decode the body
    if (receivedBody == null && wireBuffer != null) {
      // The message has been read from the wire
      decodeBody();
    }
    return receivedBody;
  }

  @Override
  public String replyAddress() {
    return replyAddress;
  }

  public Buffer encodeToWire() {

    // TODO sort out the length guess stuff it's currently wrong for most

    byte systemCodecID = messageCodec.systemCodecID();
    int length = 4 + // length int
                 1 + // protocol version
                 1 + // codec code
                 (systemCodecID == -1 ? 4 + messageCodec.name().length() : 0) + // message codec name
                 1 + // send or publish
                 4 + address.length() + // address
                 4 + (replyAddress != null ? replyAddress.length() : 0) + // reply address
                 4 + // sender port
                 4 + sender.host.length() + // sender host
                   + messageCodec.bodyLengthGuess(sentBody);
    Buffer totBuff = Buffer.buffer(length);
    totBuff.appendInt(0);
    totBuff.appendByte(WIRE_PROTOCOL_VERSION);
    totBuff.appendByte(systemCodecID);
    if (systemCodecID == -1) {
      // User codec
      writeString(totBuff, messageCodec.name());
    }
    totBuff.appendByte(send ? (byte)0 : (byte)1);
    writeString(totBuff, address);
    if (replyAddress != null) {
      writeString(totBuff, replyAddress);
    } else {
      totBuff.appendInt(0);
    }
    totBuff.appendInt(sender.port);
    writeString(totBuff, sender.host);
    writeBody(totBuff);
    totBuff.setInt(0, totBuff.length() - 4);
    if (totBuff.length() != length) {
      log.warn("Wrong length calculated, expected " + length + " actual " + totBuff.length());
    }
    return totBuff;
  }

  public void readFromWire(Buffer buffer, Map<String, MessageCodec> codecMap, MessageCodec[] systemCodecs) {
    // Overall Length already read when passed in here
    byte protocolVersion = buffer.getByte(bufferPos);
    if (protocolVersion > WIRE_PROTOCOL_VERSION) {
      throw new IllegalStateException("Invalid wire protocol version " + protocolVersion +
                                      " should be <= " + WIRE_PROTOCOL_VERSION);
    }
    bufferPos++;
    byte systemCodecCode = buffer.getByte(bufferPos);
    bufferPos++;
    if (systemCodecCode == -1) {
      // User codec
      int length = buffer.getInt(bufferPos);
      bufferPos += 4;
      byte[] bytes = buffer.getBytes(bufferPos, bufferPos + length);
      String codecName = new String(bytes, CharsetUtil.UTF_8);
      messageCodec = codecMap.get(codecName);
      if (messageCodec == null) {
        throw new IllegalStateException("No message codec registered with name " + codecName);
      }
      bufferPos += length;
    } else {
      messageCodec = systemCodecs[systemCodecCode];
    }
    byte bsend = buffer.getByte(bufferPos);
    send = bsend == 0;
    bufferPos++;
    int length = buffer.getInt(bufferPos);
    bufferPos += 4;
    byte[] bytes = buffer.getBytes(bufferPos, bufferPos + length);
    address = new String(bytes, CharsetUtil.UTF_8);
    bufferPos += length;
    length = buffer.getInt(bufferPos);
    bufferPos += 4;
    if (length != 0) {
      bytes = buffer.getBytes(bufferPos, bufferPos + length);
      replyAddress = new String(bytes, CharsetUtil.UTF_8);
      bufferPos += length;
    }
    int senderPort = buffer.getInt(bufferPos);
    bufferPos += 4;
    length = buffer.getInt(bufferPos);
    bufferPos += 4;
    bytes = buffer.getBytes(bufferPos, bufferPos + length);
    String senderHost = new String(bytes, CharsetUtil.UTF_8);
    bufferPos += length;
    sender = new ServerID(senderPort, senderHost);
    wireBuffer = buffer;
  }

  private void decodeBody() {
    receivedBody = messageCodec.decodeFromWire(bufferPos, wireBuffer);
    wireBuffer = null;
  }

  private void writeBody(Buffer buff) {
    messageCodec.encodeToWire(buff, sentBody);
  }

  private void writeString(Buffer buff, String str) {
    byte[] strBytes = str.getBytes(CharsetUtil.UTF_8);
    buff.appendInt(strBytes.length);
    buff.appendBytes(strBytes);
  }

  @Override
  public void fail(int failureCode, String message) {
    sendReply(bus.createMessage(true, replyAddress, new ReplyException(ReplyFailure.RECIPIENT_FAILURE, failureCode, message), null), null, null);
  }

  @Override
  public void reply(Object message) {
    replyWithOptions(message, DeliveryOptions.options(), null);
  }

  @Override
  public <R> void reply(Object message, Handler<AsyncResult<Message<R>>> replyHandler) {
    replyWithOptions(message, DeliveryOptions.options(), replyHandler);
  }

  @Override
  public void replyWithOptions(Object message, DeliveryOptions options) {
    replyWithOptions(message, options, null);
  }

  @Override
  public <R> void replyWithOptions(Object message, DeliveryOptions options, Handler<AsyncResult<Message<R>>> replyHandler) {
    sendReply(bus.createMessage(true, replyAddress, message, options.getCodecName()), options, replyHandler);
  }

  protected void setReplyAddress(String replyAddress) {
    this.replyAddress = replyAddress;
  }

  protected boolean send() {
    return send;
  }

  protected void setBus(EventBusImpl eventBus) {
    this.bus = eventBus;
  }

  protected MessageCodec codec() {
    return messageCodec;
  }

  private <R> void sendReply(MessageImpl msg, DeliveryOptions options, Handler<AsyncResult<Message<R>>> replyHandler) {
    if (bus != null && replyAddress != null) {
      bus.sendReply(sender, msg, options, replyHandler);
    }
  }


}
