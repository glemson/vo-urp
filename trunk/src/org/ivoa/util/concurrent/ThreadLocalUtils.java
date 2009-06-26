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
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import org.ivoa.bean.LogSupport;
import org.ivoa.bean.SingletonSupport;

/**
 * Utility functions related to ThreadLocals. This class provides utilities for managing the life
 * cycles of ThreadLocals. In particular, many application servers do not clean up the ThreadLocals
 * added to the request thread before returning the request thread to its thread pool. This may have
 * several severe side-effects, including
 * <ol>
 * <li>Leakage of objects referenced by the thread local</li>
 * <li>Possible security holes if the ThreadLocals contents are reused on a request for a different
 * user.</li>
 * <li>Leakage of the entire class loader in live patching scenarios</li>
 * <li>ClassCastExceptions in live patching scenarios</li>
 * </ol>
 * To avoid these problems, this class provides utilities for creating and registering ThreadLocals
 * that will be removed from the thread automatically at the end of the current request.<br/>
 * 
 * @see #registerRequestThreadLocal
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class ThreadLocalUtils extends SingletonSupport {
  // ~ Constants
  // --------------------------------------------------------------------------------------------------------

  /** Name used for the default ThreadLocalManager */
  private static final String DEFAULT_MANAGER = ThreadLocalUtils.class.getName();

  /** singleton instance (java 5 memory model) */
  private static volatile ThreadLocalUtils instance = null;

  /** threadLocalGroup -> ThreadLocalManager Map */
  private static volatile ConcurrentMap<String, ResettableThreadLocalManager> threadLocalManagers = new ConcurrentHashMap<String, ResettableThreadLocalManager>();

  // ~ Constructors
  // -----------------------------------------------------------------------------------------------------

  /**
   * Forbidden constructor
   */
  private ThreadLocalUtils() {
    getThreadLocalManager(DEFAULT_MANAGER);
  }

  // ~ Methods
  // ----------------------------------------------------------------------------------------------------------
  /**
   * Return the ThreadLocalUtils singleton instance
   * 
   * @return ThreadLocalUtils singleton instance
   * @throws IllegalStateException if a problem occured
   */
  public static final ThreadLocalUtils getInstance() {
    if (instance == null) {
      instance = prepareInstance(new ThreadLocalUtils());
    }
    return instance;
  }

  /**
   * Reset the threadLocalManagers collection
   */
  private final static void resetManagers() {
    // free ResettableThreadLocalManager :
    threadLocalManagers.clear();
    threadLocalManagers = null;
  }

  /**
   * Concrete implementations of the SingletonSupport's clearStaticReferences() method :<br/>
   * Callback to clean up the possible static references used by this SingletonSupport instance iso
   * clear static references
   * 
   * @see SingletonSupport#onExit()
   */
  @Override
  protected void clearStaticReferences() {
    if (log.isWarnEnabled()) {
      log.warn("ThreadLocalUtils.clearStaticReferences : enter");
    }

    // force GC :
    if (threadLocalManagers != null) {
      clearThreadLocals();

      resetManagers();
    }
    if (log.isWarnEnabled()) {
      log.warn("ThreadLocalUtils.clearStaticReferences : exit");
    }
  }

  /**
   * This method cleans up all of the ThreadLocals registered with all registered ThreadLocalManager
   */
  public final static void clearThreadLocals() {
    if (threadLocalManagers != null) {
      for (ResettableThreadLocalManager manager : threadLocalManagers.values()) {
        if (manager != null) {
          // clean up all ThreadLocals
          manager.removeThreadLocals();
        }
      }
    }
  }

  /**
   * Registers and returns the ThreadLocal to be automatically managed when the classloader is
   * released
   * 
   * @param <T> type used by the ThreadLocal<T>
   * @param threadLocal ThreadLocal to register for automatic removal
   * @return The registered ThreadLocal
   */
  public static <T> ThreadLocal<T> registerRequestThreadLocal(final ThreadLocal<T> threadLocal) {
    return getThreadLocalManager(DEFAULT_MANAGER).registerThreadLocal(threadLocal);
  }

  /**
   * Returns the ResettableThreadLocalManager for the specified threadLocalGroup name. If no
   * ResettableThreadLocalManager is currently registered for the threadLocalGroup, one will be
   * instantiated, its related ThreadLocalLifecycle instantiated and the ThreadLocalManager
   * registered with the ThreadLocalLifecycle
   * 
   * @param threadLocalGroup Identifier for this group
   * @return the ResettableThreadLocalManager for this threadLocalGroup name
   */
  private static ResettableThreadLocalManager getThreadLocalManager(final String threadLocalGroup) {
    // see if we already have a ResettableThreadLocalManager registered
    ResettableThreadLocalManager threadLocalManager = threadLocalManagers.get(threadLocalGroup);

    // if we don't, create one
    if (threadLocalManager == null) {
      threadLocalManager = new ResettableThreadLocalManager();

      // handle simultaneous registration
      final ResettableThreadLocalManager oldThreadLocalManager = threadLocalManagers.putIfAbsent(threadLocalGroup, threadLocalManager);

      if (oldThreadLocalManager != null) {
        // simultaneous registration, use the one that was registered first
        threadLocalManager = oldThreadLocalManager;
      }
    }

    return threadLocalManager;
  }

  /**
   * Integration interface implemented by object holding onto ThreadLocals with a specified lifetime
   */
  public static abstract class ThreadLocalManager extends LogSupport {

    /**
     * Called by the ThreadLocalLifeCycle when the ThreadLocals managed by this ThreadLocalManager
     * need to be cleaned up by removing their instances from the current Thread.
     */
    protected abstract void removeThreadLocals();
  }

  /**
   * ThreadLocalManager implementation class
   */
  private static final class ResettableThreadLocalManager extends ThreadLocalManager {

    /** threadLocal weak references */
    private final Collection<WeakReference<ThreadLocal<?>>> threadLocals;

    /**
     * Protected Constructor
     */
    protected ResettableThreadLocalManager() {
      // create the list of resettable ThreadLocals for this group
      threadLocals = new ConcurrentLinkedQueue<WeakReference<ThreadLocal<?>>>();
    }

    /**
     * Registers the ThreadLocal for cleanup when this ThreadLocalManager is cleaned up
     * 
     * @param <T> type used by the ThreadLocal<T>
     * @param threadLocal ThreadLocal to register for clean up
     * @return The registered ThreadLocal
     */
    protected <T> ThreadLocal<T> registerThreadLocal(final ThreadLocal<T> threadLocal) {
      if (threadLocal == null) {
        throw new NullPointerException();
      }

      // WeakReference might be overkill here, but make sure we don't pin ThreadLocals
      threadLocals.add(new WeakReference<ThreadLocal<?>>(threadLocal));

      return threadLocal;
    }

    /**
     * Called by the SingletonSupport onExit() method to clean up all of the ThreadLocals registered
     * with this ThreadLocalManager
     * 
     * @see ThreadLocalUtils#clearThreadLocals()
     * @see ThreadLocalUtils#clearStaticReferences()
     * @see SingletonSupport#onExit()
     * @see ThreadLocal#remove()
     */
    @Override
    protected void removeThreadLocals() {
      final Iterator<WeakReference<ThreadLocal<?>>> iterator = threadLocals.iterator();

      ThreadLocal<?> threadLocal;
      while (iterator.hasNext()) {
        threadLocal = iterator.next().get();

        if (log.isWarnEnabled()) {
          log.warn("ResettableThreadLocalManager.removeThreadLocals : threadLocal to remove : " + threadLocal);
        }

        // if the threadLocal is null, that means it has been released and we would really
        // like to reclaim the entry, however remove isn't supported on CopyOnWriteArrayLists
        // and the synchronization required to safely remove this item probably isn't
        // worthy the small increase in memory of keeping around this empty item, so we don't
        // bother cleaning up this entry
        if (threadLocal != null) {
          // reset the thread local for this thread
          threadLocal.remove();
        }
      }
    }
  }
}
