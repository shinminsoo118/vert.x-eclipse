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

package custom;

import io.vertx.core.Verticle;
import io.vertx.core.spi.VerticleFactory;

/**
 * A verticle factory that is not part of Vert.x, i.e does not have a package prefixed by "io.vertx.".
 *
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class CustomVerticleFactory implements VerticleFactory {

  @Override
  public String prefix() {
    return "custom";
  }

  @Override
  public CustomVerticle createVerticle(String verticleName, ClassLoader classLoader) throws Exception {
    String fqn = VerticleFactory.removePrefix(verticleName);
    Class<?> clazz = classLoader.loadClass(fqn);
    return (CustomVerticle) clazz.newInstance(); // Make sure it's the same class by casting to CustomVerticle
  }
}
