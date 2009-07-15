package org.ivoa.util.timer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ivoa.bean.LogSupport;
import org.ivoa.util.text.LocalStringBuilder;

/**
 * The Timer factory contains a map[key - Timer] to associate time metrics statistics to several
 * categories of operations TODO : Use the warmup 2 values to define an estimated latency in order
 * to adjust time measure :<br/>
 * corrected elapsed time = (elapsed time - latency)
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class TimerFactory extends LogSupport {

  // ~ Constants
  // --------------------------------------------------------------------------------------------------------
  /** warmup 1 cycles = 20000 */
  public final static int WARMUP_CYCLES_1 = 10 * 1000;
  /** warmup 2 cycles = 100000 */
  public final static int WARMUP_CYCLES_2 = 20 * 1000;
  /** category for the warm-up to optimize the timer code */
  private final static String WARMUP_CATEGORY = "warmup";
  /** initial capacity = 64 */
  private final static int CAPACITY = 32;
  /** internal lock object for synchroniz'd blocks */
  private final static Object lock = new Object();
  /** List[timer] */
  private static List<AbstractTimer> timerList = new ArrayList<AbstractTimer>(CAPACITY);
  /** fast Map[key - timer] */
  private static Map<String, AbstractTimer> timerMap = new HashMap<String, AbstractTimer>(CAPACITY);
  /** threshold for long time = 1s (ms) or 1 milliSeconds (ns) */
  private final static double THRESHOLD = 1 * 1000d;

  /** timer unit constants */
  public static enum UNIT {

    /** MilliSeconds */
    ms,
    /** NanoSeconds */
    ns
  }


  static {
    // warm up 1 :
    warmUp(WARMUP_CYCLES_1, WARMUP_CATEGORY);

    if (logB.isWarnEnabled()) {
      logB.warn("TimerFactory : warmup 1 (ns) : " + dumpTimers());
    }
    resetTimers();

    // warm up 2 to get latency :
    warmUp(WARMUP_CYCLES_2, WARMUP_CATEGORY);

    if (logB.isWarnEnabled()) {
      logB.warn("TimerFactory : warmup 2 (ns) : " + dumpTimers());
    }
    resetTimers();
  }

  /** 
   * Warm-up timer code (hotspot)
   * 
   * @param cycles empty cycles to operate
   * @param category name of the category 
   */
  private static void warmUp(final int cycles, final String category) {
    // preallocate the timer :
    TimerFactory.getTimer(category, UNIT.ns);

    long start;

    // EMPTY LOOP to compile the code of Timer classes  :
    for (int i = 0, size = cycles; i < size; i++) {
      start = System.nanoTime();
      TimerFactory.getTimer(category, UNIT.ns).addNanoSeconds(start, System.nanoTime());
    }
  }

  // ~ Constructors
  // -----------------------------------------------------------------------------------------------------
  /**
   * Forbidden Constructor
   */
  private TimerFactory() {
    /* no-op */
  }

  // ~ Methods
  // ----------------------------------------------------------------------------------------------------------
  /**
   * Returns elapsed time between 2 time values get from System.nanoTime() in milliseconds
   *
   * @see System#nanoTime()
   * @param start t0
   * @param now t1
   * @return (t1 - t0) in milliseconds
   */
  public static final long elapsedMilliSeconds(final long start, final long now) {
    return (now - start) / 1000000L;
  }

  /**
   * Returns elapsed time between 2 time values get from System.nanoTime() in nanoseconds
   *
   * @see System#nanoTime()
   * @param start t0
   * @param now t1
   * @return (t1 - t0) in nanoseconds
   */
  public static final long elapsedNanoSeconds(final long start, final long now) {
    return now - start;
  }

  /**
   * Return an existing or a new ThresholdTimer for that category (lazy) with the default threshold
   * and unit (milliseconds)
   *
   * @see #THRESHOLD
   * @param category a string representing the kind of operation
   * @return timer instance
   */
  public static final AbstractTimer getTimer(final String category) {
    return getTimer(category, UNIT.ms, THRESHOLD);
  }

  /**
   * Return an existing or a new ThresholdTimer for that category (lazy) with the default threshold
   * and the given unit
   * 
   * @see #THRESHOLD
   * @see UNIT
   * @param category a string representing the kind of operation
   * @param unit MILLI_SECONDS or NANO_SECONDS
   * @return timer instance
   */
  public static final AbstractTimer getTimer(final String category, final UNIT unit) {
    return getTimer(category, unit, THRESHOLD);
  }

  /**
   * Return an existing or a new Timer for that category (lazy) with the given threshold
   *
   * @see UNIT
   * @param category a string representing the kind of operation
   * @param th threshold to detect an high value
   * @param unit MILLI_SECONDS or NANO_SECONDS
   * @return timer instance
   */
  public static final AbstractTimer getTimer(final String category, final UNIT unit, final double th) {
    AbstractTimer timer = timerMap.get(category);

    if (timer == null) {
      timer = new ThresholdTimer(category, unit, th);

      synchronized (lock) {
        timerList.add(timer);
        timerMap.put(category, timer);
      }
    }

    return timer;
  }

  /**
   * Return a string representation for all timer instances present in the timerMap map
   *
   * @return string representation for all timer instances
   */
  public static final String dumpTimers() {
    final StringBuilder sb = LocalStringBuilder.getBuffer();

    synchronized (lock) {
      for (final AbstractTimer timer : timerList) {
        sb.append("\n").append(timer.toString());
      }
    }

    return LocalStringBuilder.toString(sb);
  }

  /**
   * Reset all timer instances
   */
  public static final void resetTimers() {
    synchronized (lock) {
      timerList.clear();
      timerMap.clear();
    }
  }

  /**
   * Return true if there is no existing timer
   * @return true if there is no existing timer
   */
  public static final boolean isEmpty() {
    return timerList.isEmpty();
  }
}
// ~ End of file
// --------------------------------------------------------------------------------------------------------
