package org.ivoa.util;

import org.ivoa.bean.SingletonSupport;
import org.ivoa.util.concurrent.ThreadLocalUtils;
import org.ivoa.util.concurrent.local.StringBuilderContext;
import org.ivoa.util.concurrent.local.StringBuilderThreadLocal;
import org.ivoa.util.timer.AbstractTimer;
import org.ivoa.util.timer.TimerFactory;
import org.ivoa.util.timer.TimerFactory.UNIT;

/**
 * ThreadLocal StringBuilder for performance
 * 
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class LocalStringBuilder extends SingletonSupport {
  // ~ Constants
  // --------------------------------------------------------------------------------------------------------

  /** flag to execute the micro benchmark at startup */
  private final static boolean DO_MICRO_BENCHMARK = true;
  /** buffer thread Local */
  private static ThreadLocal<StringBuilderContext> bufferLocal = ThreadLocalUtils.registerRequestThreadLocal(new StringBuilderThreadLocal());

  // ~ Constructors
  // -----------------------------------------------------------------------------------------------------
  /**
   * Forbidden constructor
   */
  private LocalStringBuilder() {
    super();
  }

  // ~ Methods
  // ----------------------------------------------------------------------------------------------------------
  /**
   * Prepare the LocalStringBuilder singleton instance
   * 
   * @throws IllegalStateException if a problem occured
   */
  public static final void prepareInstance() {
    prepareInstance(new LocalStringBuilder());

    if (DO_MICRO_BENCHMARK) {
      microbenchmark();
    }
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
        log.warn("LocalStringBuilder.cleanCurrentThread : " + bufferLocal.get());
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
    if (StringBuilderContext.DIAGNOSTICS && logB.isInfoEnabled()) {
      logB.info("LocalStringBuilder.getBuffer : createStringBuilder because threadLocal is not running : ", new Throwable());
    }
    return StringBuilderContext.createStringBuilder();
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
      final StringBuilderContext ctx = bufferLocal.get();
      StringBuilder sb = ctx.current();
      if (sb == null) {
        sb = ctx.acquire();
      }
      return sb;
    }
    return StringBuilderContext.createStringBuilder();
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
      final StringBuilderContext ctx = bufferLocal.get();
      final StringBuilder sbLocal = ctx.current();
      s = sb != null ? sb.toString() : sbLocal != null ? sbLocal.toString() : null;
      ctx.release(sbLocal);
    } else {
      if (sb != null) {
        s = sb.toString();
        StringBuilderContext.resetStringBuilder(sb);
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
      final StringBuilderContext ctx = bufferLocal.get();
      final StringBuilder sbLocal = ctx.current();
      sbTo.append(sb != null ? sb : sbLocal);
      ctx.release(sbLocal);
    } else {
      if (sb != null) {
        sbTo.append(sb);
        StringBuilderContext.resetStringBuilder(sb);
      }
    }
  }

  /**
   * MicroBenchmark : LocalStringBuilder : Low: Timer(ms) {n=9799, min=1676.0, max=9778.0,
   * acc=2.6544453E7, avg=2708.8940708235536} - High: Timer(ms) {n=201, min=10057.0, max=1062985.0,
   * acc=3538450.0, avg=17604.228855721394}
   */
  protected static void microbenchmark() {
    if (logB.isWarnEnabled()) {
      final String value = "---------------------------------------";

      final AbstractTimer timer = TimerFactory.getTimer("LocalStringBuilder", UNIT.ns);

      long start, stop;
      for (int i = 0; i < 50000; i++) {
        start = System.nanoTime();

        LocalStringBuilder.toString(LocalStringBuilder.getBuffer().append(value));

        stop = System.nanoTime();

        timer.addNanoSeconds(start, stop);
      }

      log.warn("LocalStringBuilder : micro benchmark : " + TimerFactory.dumpTimers());

      // TimerFactory reset :
      TimerFactory.resetTimers();
    }
  }
}