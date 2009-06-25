package org.ivoa.util.concurrent;

/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
import java.util.concurrent.atomic.AtomicReference;

import org.ivoa.util.concurrent.ThreadLocalUtils.ThreadLocalLifecycle;
import org.ivoa.util.concurrent.ThreadLocalUtils.ThreadLocalManager;

/**
 * Implementation of ThreadLocalLifecycle for request-scoped ThreadLocals, registered through
 * META-INF/SERVICES. This class cooperates with GlobalConfiguratorImpl in order to ensure that the
 * request-scoped ThreadLocals are cleaned up at the end of each request.
 * 
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public class ThreadLocalResetter extends ThreadLocalLifecycle {

  /** ThreadLocalManager to clean up */
  private final AtomicReference<ThreadLocalManager> threadLocalManager = new AtomicReference<ThreadLocalManager>();

  /**
   * Forbidden constructor
   */
  protected ThreadLocalResetter() {
    /* no-op */
  }

  /**
   * Called by ThreadLocalUtils after instantiating the ThreadLocalResetter in order to pass the
   * ThreadLocalManager to use to clean up the request-scoped ThreadLocals.
   * 
   * @param manager ThreadLocalManager
   */
  @Override
  protected void init(final ThreadLocalManager manager) {
    if (manager == null) {
      throw new NullPointerException();
    }

    // save the ThreadLocalManager to use to clean up the request-scoped ThreadLocals
    threadLocalManager.set(manager);
  }

  /**
   * Called by the GlobalConfiguratorImpl when the request is finished so that the
   * ThreadLocalResetter can ask the ThreadLocalManager to clean itself up
   */
  public void removeThreadLocals() {
    final ThreadLocalManager manager = threadLocalManager.get();

    if (manager != null) {
      // clean up all of the request-scoped ThreadLocals
      manager.removeThreadLocals();
    }
  }
}
