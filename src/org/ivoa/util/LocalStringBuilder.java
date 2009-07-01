package org.ivoa.util;

import org.ivoa.bean.LogSupport;
import org.ivoa.bean.SingletonSupport;
import org.ivoa.util.concurrent.ThreadLocalUtils;

/**
 * ThreadLocal StringBuilder for performance
 * 
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class LocalStringBuilder extends SingletonSupport {
    // ~ Constants
    // --------------------------------------------------------------------------------------------------------

    /** diagnostic flag */
  public final static boolean DIAGNOSTICS = false;
    /** max reentrance depth = 9 */
    public final static int DEPTH = 9;
    /** initial buffer capacity = 2048 chars */
    public final static int CAPACITY = 256;
    /** max buffer capacity = 32768 chars */
    public final static int MAX_CAPACITY = 4096;

  /** buffer thread Local */
  private static volatile ThreadLocal<Context> bufferLocal = ThreadLocalUtils.registerRequestThreadLocal(new StringBuilderThreadLocal());

    // ~ Constructors
    // -----------------------------------------------------------------------------------------------------
    /**
     * Forbidden constructor
     */
    private LocalStringBuilder() {
        super();
    }

    //~ Methods ----------------------------------------------------------------------------------------------------------
    /**
     * Prepare the LocalStringBuilder singleton instance
     *
     * @throws IllegalStateException if a problem occured
     */
    public static final void prepareInstance() {
      prepareInstance(new LocalStringBuilder());
    }

    /**
     * Reset the bufferLocal ThreadLocal<Context>
     */
  private final static void resetBufferLocal() {
    // free ThreadLocal :
    bufferLocal = null;
  }

    /**
     * Concrete implementations of the SingletonSupport's clearStaticReferences() method :<br/>
     * Callback to clean up the possible static references used by this SingletonSupport instance iso
     * clear static references
     *
     * @see SingletonSupport#clearStaticReferences()
     */
    @Override
    protected void clearStaticReferences() {
    resetBufferLocal();
    }

    /**
     * Free the thread local Context associated to the current thread
     */
  public final static void cleanCurrentThread() {
    if (isRunning()) {
            if (log.isWarnEnabled()) {
        log.warn("cleanCurrentThread : " + bufferLocal.get());
            }
            bufferLocal.remove();
        }
    }

    /**
     * Return an empty threadLocal StringBuilder instance.<br/>
     * MUST BE RELEASED after use by calling toString(StringBuilder) or toStringBuilder(StringBuilder)
     * methods
     *
     * @see #toString(StringBuilder)
     * @see #toStringBuilder(StringBuilder, StringBuilder)
     * @return StringBuilder threadLocal instance
     */
    public final static StringBuilder getBuffer() {
    if (isRunning()) {
            return bufferLocal.get().acquire();
        }
        if (DIAGNOSTICS && logD.isInfoEnabled()) {
            logD.info("LocalStringBuilder.getBuffer : createStringBuilder because threadLocal is not running : ", new Throwable());
        }
        return createStringBuilder();
    }

    /**
     * Return the current threadLocal StringBuilder instance or a empty threadLocal StringBuilder
     * instance.<br/>
     * MUST BE RELEASED after use by calling toString(StringBuilder) or toStringBuilder(StringBuilder)
     * methods
     *
     * @see #toString(StringBuilder)
     * @see #toStringBuilder(StringBuilder, StringBuilder)
     * @return StringBuilder threadLocal instance
     */
    public final static StringBuilder getCurrentBuffer() {
    if (isRunning()) {
            final Context ctx = bufferLocal.get();
            StringBuilder sb = ctx.current();
            if (sb == null) {
                sb = ctx.acquire();
            }
            return sb;
        }
        return createStringBuilder();
    }

    /**
     * Return the string contained in the current local buffer and release the buffer
     *
     * @param sb buffer (optional)
     * @return string contained in the given buffer
     */
    public final static String toString(final StringBuilder sb) {
        String s = null;
    if (isRunning()) {
            final Context ctx = bufferLocal.get();
            final StringBuilder sbLocal = ctx.current();
            s = sb != null ? sb.toString() : sbLocal != null ? sbLocal.toString() : null;
            ctx.release(sbLocal);
        } else {
            if (sb != null) {
                s = sb.toString();
                resetStringBuilder(sb);
            }
        }
        return s;
    }

    /**
     * Unsynchronized copy from the current local buffer to another buffer and release the buffer
     *
     * @param sb buffer (optional)
     * @param sbTo destination buffer
     */
    public final static void toStringBuilder(final StringBuilder sb, final StringBuilder sbTo) {
    if (isRunning()) {
            final Context ctx = bufferLocal.get();
            final StringBuilder sbLocal = ctx.current();
            sbTo.append(sb != null ? sb : sbLocal);
            ctx.release(sbLocal);
        } else {
            if (sb != null) {
                sbTo.append(sb);
                resetStringBuilder(sb);
            }
        }
    }

    /**
     * Create a new StringBuilder with the default capacity
     * @return new StringBuilder with the default capacity
     */
    protected static StringBuilder createStringBuilder() {
        return new StringBuilder(CAPACITY);
    }

    /**
     * Reset the length of the given StringBuilder
     * @param sb buffer to reset
     */
    protected static void resetStringBuilder(final StringBuilder sb) {
        if (sb != null) {
            // reset without array operation : just set count to 0 / leave buffer available with the
            // same capacity :
            sb.setLength(0);
        }
    }

    // ~ Inner Classes
    // ----------------------------------------------------------------------------------------------------
    /**
     * This class uses the ThreadLocal pattern to associate a StringBuilder to the current thread
     */
    private static final class StringBuilderThreadLocal extends ThreadLocal<Context> {
        // ~ Constructors
        // ---------------------------------------------------------------------------------------------------

        /**
         * Protected constructor
         */
        protected StringBuilderThreadLocal() {
            super();
        }

        // ~ Methods
        // --------------------------------------------------------------------------------------------------------
        /**
         * Return a new StringBuilder for the current thread
         *
         * @return new StringBuilder instance with initial capacity set to @see #CAPACITY
         */
        @Override
        public final Context initialValue() {
            return new Context();
        }
    }

    /**
     * This private class defines a Context iso an array of StringBuilder instances to allow
     * reentrance
     */
    private static final class Context extends LogSupport {

        /** Undefined value for current Position */
    public final static int UNDEFINED = -1;
        /** internal array of StringBuilder instances */
        private final StringBuilder[] buffers = new StringBuilder[DEPTH];
        /** currently used buffer */
        private int current = UNDEFINED;

        /**
         * Private Constructor
         */
        protected Context() {
            if (DIAGNOSTICS && logD.isInfoEnabled()) {
                logD.info("LocalStringBuilder.Context.new : " + Thread.currentThread().getName());
            }
        }

        /**
         * Return a new buffer in the stack or create a new one if the stack capacity is exceeded
         *
         * @return empty buffer
         */
        protected StringBuilder acquire() {
            StringBuilder sb = null;

            current++;
            final boolean useCache = current < DEPTH;
            if (useCache) {
                sb = buffers[current];
            }
            if (sb == null) {
              if (logD.isWarnEnabled()) {
                log.warn("LocalStringBuilder.Context.acquire : create a new StringBuilder : consider to increment LocalStringBuilder.DEPTH = " + DEPTH);
              }
                sb = createStringBuilder();
            }
            if (useCache) {
                buffers[current] = sb;
            }
            return sb;
        }

        /**
         * Return the current buffer in the stack or null if the stack capacity is exceeded
         *
         * @return buffer (not empty) or null
         */
        protected StringBuilder current() {
            StringBuilder sb = null;
            final boolean useCache = current >= 0 && current < DEPTH;
            if (useCache) {
                sb = buffers[current];
            }
            return sb;
        }

        /**
         * Release the current buffer in the stack and reset length of the given buffer
         *
         * @param sb buffer (optional)
         */
        protected void release(final StringBuilder sb) {
            if (sb != null) {
                if (DIAGNOSTICS && logD.isInfoEnabled()) {
                    logD.info("LocalStringBuilder.Context.release : buffer capacity : " + sb.length() + " / " + sb.capacity());
                }
                resetStringBuilder(sb);
                if (sb.capacity() > MAX_CAPACITY) {
                    // release it :
                    buffers[current] = null;
                }
            }
            current--;
        }
    }
}