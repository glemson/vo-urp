package org.ivoa.util.timer;

import java.util.LinkedHashMap;
import java.util.Map;

import org.ivoa.bean.LogSupport;
import org.ivoa.util.LocalStringBuilder;


/**
 * The Timer factory contains a map[key - Timer] to associate time metrics statistics to several
 * categories of operations
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class TimerFactory extends LogSupport {

  //~ Constants --------------------------------------------------------------------------------------------------------

  /** default warmup cycles = 2000 */
  private final static int WARMUP_CYCLES = 10000;

  /** category for the first  warmup to optimise the timer code (hotspot) */
  private final static String WARMUP1_CATEGORY = "warmup-1";

  /** category for the second warmup to estimate latency */
  private final static String WARMUP2_CATEGORY = "warmup-2";

  /** Map[key - timer] */
  protected static Map<String, ThresholdTimer> timers = new LinkedHashMap<String, ThresholdTimer>();

  /** threshold for long time = 2s */
  private final static double THRESHOLD = 2000d;

  static {
    // warm up 1 :
    warmUp(WARMUP_CYCLES, WARMUP1_CATEGORY);

    if (logD.isInfoEnabled()) {
      logD.info("TimerFactory : warmup 1 : " + dumpTimers());
    }
    resetTimers();

    // warm up 2 to get latency :
    warmUp(WARMUP_CYCLES, WARMUP2_CATEGORY);

    if (logD.isInfoEnabled()) {
      logD.info("TimerFactory : warmup 2 : " + dumpTimers());
    }
    resetTimers();
  }

  /** 
   * Warmup timer code (hotspot)
   * @param cycles empty cycles to operate
   * @param category name of the category 
   */
  private static void warmUp(final int cycles, final String category) {
    final long start = System.nanoTime();
    // EMPTY LOOP to precompile (hotspot) timer code :
    for (int i = 0, size = cycles; i < size; i++) {
      TimerFactory.getTimer(category).add(start, System.nanoTime());
    }
  }
  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Forbidden Constructor
   */
  private TimerFactory() {
    /* no-op */
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Returns elapsed time between 2 time values get from System.nanoTime().
   *
   * @see System#nanoTime()
   * @param start t0
   * @param now t1
   * @return (t1 - t0) in milliseconds
   */
  public static final long elapsed(final long start, final long now) {
    return (now - start) / 1000000L;
  }

  /**
   * Return an existing or a new Timer for that category (lazy)
   *
   * @param category a string representing the kind of operation
   * @return timer instance
   */
  public static final ThresholdTimer getTimer(final String category) {
    ThresholdTimer timer = timers.get(category);

    if (timer == null) {
      timer = new ThresholdTimer(THRESHOLD);
      timers.put(category, timer);
    }

    return timer;
  }

  /**
   * Return the map of timer instances
   *
   * @return map of timer instances
   */
  public static final Map<String, ThresholdTimer> getTimers() {
    return timers;
  }

  /**
   * Return a string representation for all timer instances present in the timers map
   *
   * @return string representation for all timer instances
   */
  public static final String dumpTimers() {
    final StringBuilder sb = LocalStringBuilder.getBuffer();

    for (final Map.Entry<String, ThresholdTimer> e : timers.entrySet()) {
      sb.append("\n").append(e.getKey()).append(" : ").append(e.getValue());
    }

    return LocalStringBuilder.toString(sb);
  }

  /**
   * Reset all timer instances
   */
  public static final void resetTimers() {
    timers.clear();
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
