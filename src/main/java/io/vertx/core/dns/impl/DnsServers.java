/*
 * Copyright 2014 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */

package io.vertx.core.dns.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.dns.DnsClient;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

/**
 *
 * Helper class to lookup the OS configured DNS server IP addresses
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class DnsServers {

  private static final Logger log = LoggerFactory.getLogger(DnsServers.class);

  private static List<String> nameServers;

  @SuppressWarnings("unchecked")
  public static synchronized List<String> getServers() {
    if (nameServers == null) {
      try {
        // We can't depend on sun.* classes directly so we use reflection
        Class<?> resolverConfigurationClass = getClassLoader().loadClass("sun.net.dns.ResolverConfiguration");
        Method openMethod = resolverConfigurationClass.getDeclaredMethod("open");
        Object resolverConfiguration = openMethod.invoke(null);
        Method nameServersMethod = resolverConfigurationClass.getDeclaredMethod("nameservers");
        nameServers = Collections.unmodifiableList((List<String>)nameServersMethod.invoke(resolverConfiguration));
      } catch (Exception e) {
        // Can't resolve name servers using sun.* classes
        log.warn("Can't find nameservers using sun.net.dns.ResolverConfiguration");
        nameServers = Collections.emptyList();
      }
    }
    return nameServers;
  }

  private static ClassLoader getClassLoader() {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    if (cl == null) {
      cl = DnsServers.class.getClassLoader();
    }
    return cl;
  }

  public static void lookup(DnsClient client, String name, Handler<AsyncResult<String>> resultHandler) {
    // First try IP v4
    client.lookup4(name, res4 -> {
      if (!res4.succeeded()) {
        resultHandler.handle(Future.failedFuture(res4.cause()));
      } else {
        String ip4 = res4.result();
        if (ip4 == null) {
          // Not found
          // Try IP v6
          client.lookup6(name, res6 -> {
            if (!res6.succeeded()) {
              resultHandler.handle(Future.failedFuture(res6.cause()));
            } else {
              String ip6 = res6.result();
              if (ip6 == null) {
                // Not found
                resultHandler.handle(Future.failedFuture(new UnknownHostException(name)));
              } else {
                resultHandler.handle(Future.succeededFuture(ip6));
              }
            }
          });
        } else {
          resultHandler.handle(Future.succeededFuture(ip4));
        }
      }
    });
  }
}
