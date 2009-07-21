package org.ivoa.util.timer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ivoa.bean.LogSupport;
import org.ivoa.util.NumberUtils;
import org.ivoa.util.stat.StatLong;
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
  /** diagnostics flag for warmup sequence */
  public final static boolean WARMUP_DIAGNOSTICS = false;
  /** maximum number of warmup steps */
  public final static int WARMUP_STEPS = 5;
  /** warmup step cycles = 5000 */
  public final static int WARMUP_STEP_CYCLES = 10 * 1000;
  /** maximum number of calibration steps */
  public final static int CALIBRATION_STEPS = 4;
  /** calibration step cycles = 5000 */
  public final static int CALIBRATION_STEP_CYCLES = 20 * 1000;
  /** category for the warm-up to optimize the timer code */
  private final static String CATEGORY_CALIBRATE = "calibration";
  /** initial capacity = 64 */
  private final static int CAPACITY = 32;
  /** threshold for long time = 1s (ms) or 1 milliSeconds (ns) */
  private final static double THRESHOLD = 1 * 1000d;
  /** conversion ratio between nanoseconds and milliseconds */
  private final static double CONVERT_NS_INTO_MS = 1e-6d;
  /** calibration value for milliseconds unit */
  private static double CALIBRATION_MILLI_SECONDS = 0d;
  /** calibration value for nanoseconds unit */
  private static double CALIBRATION_NANO_SECONDS = 0d;
  /* shared state */
  /** guard lock for timer list and map to ensure thread integrity */
  private final static Object lock = new Object();
  /** List[timer] */
  private static List<AbstractTimer> timerList = new ArrayList<AbstractTimer>(CAPACITY);
  /** fast Map[key - timer] */
  private static Map<String, AbstractTimer> timerMap = new HashMap<String, AbstractTimer>(CAPACITY);


  /** timer unit constants */
  public static enum UNIT {

    /** MilliSeconds */
    ms,
    /** NanoSeconds */
    ns
  }

  static {
    final long start = System.nanoTime();

    StatLong stat;

    // Adjust threshold to high values :
    StatLong.defineThreshold(100);

    // warm up loop :
    final StatLong globalStatNs = new StatLong();
    final StatLong globalStatMs = new StatLong();
    for (int i = 0; i < WARMUP_STEPS; i++) {
      // warm up to optimize code (hot spot) :
      stat = calibrateNanoSeconds(WARMUP_STEP_CYCLES);
      globalStatNs.add(stat);

      stat = calibrateMilliSeconds(WARMUP_STEP_CYCLES);
      globalStatMs.add(stat);

      if (WARMUP_DIAGNOSTICS && logB.isWarnEnabled()) {
        logB.warn("TimerFactory : warmup [" + i + "] : " + dumpTimers());
      }
      resetTimers();
    }
    if (WARMUP_DIAGNOSTICS && logB.isWarnEnabled()) {
      logB.warn("TimerFactory : global nanoseconds  statistics : " + globalStatNs.toString(true));
      logB.warn("TimerFactory : global milliseconds statistics : " + globalStatMs.toString(true));
    }

    double delta;
    // calibration loop to get latency :
    for (int i = 0; i < CALIBRATION_STEPS; i++) {
      // nano :
      stat = calibrateNanoSeconds(CALIBRATION_STEP_CYCLES);
      delta = Math.min(stat.getMin(), stat.getAverage() - stat.getStdDevLow());

      CALIBRATION_NANO_SECONDS += delta;

      if (WARMUP_DIAGNOSTICS && logB.isWarnEnabled()) {
        logB.warn("TimerFactory : Nanoseconds   : ");
        logB.warn("TimerFactory : calibration [" + i + "] : " + dumpTimers());
        logB.warn("TimerFactory : min           : " + stat.getMin());
        logB.warn("TimerFactory : avg - stddev  : " + (stat.getAverage() - stat.getStdDevLow()));
        logB.warn("TimerFactory : delta         : " + delta);
        logB.warn("TimerFactory : nanoseconds  calibration correction : " + CALIBRATION_NANO_SECONDS);
      }

      resetTimers();

      // milli :
      stat = calibrateMilliSeconds(CALIBRATION_STEP_CYCLES);
      delta = Math.min(stat.getMin(), stat.getAverage() - stat.getStdDevLow());

      CALIBRATION_MILLI_SECONDS += delta;

      if (WARMUP_DIAGNOSTICS && logB.isWarnEnabled()) {
        logB.warn("TimerFactory : Milliseconds   : ");
        logB.warn("TimerFactory : calibration [" + i + "] : " + dumpTimers());
        logB.warn("TimerFactory : min           : " + stat.getMin());
        logB.warn("TimerFactory : avg - stddev  : " + (stat.getAverage() - stat.getStdDevLow()));
        logB.warn("TimerFactory : delta         : " + delta);
        logB.warn("TimerFactory : milliseconds calibration correction : " + CALIBRATION_MILLI_SECONDS);
      }

      resetTimers();
    }

    final long stop = System.nanoTime();

    if (logB.isWarnEnabled()) {
      logB.warn("TimerFactory : nanoseconds  calibration correction : " + NumberUtils.format(CALIBRATION_NANO_SECONDS, NumberUtils.LESS_PRECISION));
      logB.warn("TimerFactory : milliseconds calibration correction : " + NumberUtils.format(CALIBRATION_MILLI_SECONDS, NumberUtils.LESS_PRECISION));
      logB.warn("TimerFactory : calibration time (ms) : " + TimerFactory.elapsedMilliSeconds(start, stop));
    }

    // Adjust threshold to low values :
    StatLong.defineThreshold(5);

  }

  /** 
   * Warm-up and calibrate timer code (hotspot)
   * 
   * @param cycles empty cycles to operate
   * @return calibration value in double precision
   */
  private static StatLong calibrateNanoSeconds(final int cycles) {
    final String catCalibrate = CATEGORY_CALIBRATE + "-" + UNIT.ns;

    long start;
    // EMPTY LOOP to force hotspot compiler to optimize the code for Timer.* classes  :
    for (int i = 0, size = cycles; i < size; i++) {
      start = System.nanoTime();
      // ...
      TimerFactory.getSimpleTimer(catCalibrate, UNIT.ns).addNanoSeconds(start, System.nanoTime());
    }

    return TimerFactory.getTimer(catCalibrate).getTimeStatistics();
  }

  /**
   * Warm-up and calibrate timer code (hotspot)
   *
   * @param cycles empty cycles to operate
   * @return calibration value in double precision
   */
  private static StatLong calibrateMilliSeconds(final int cycles) {
    final String catCalibrate = CATEGORY_CALIBRATE + "-" + UNIT.ms;

    long start;
    // EMPTY LOOP to force hotspot compiler to optimize the code for Timer.* classes  :
    for (int i = 0, size = cycles; i < size; i++) {
      start = System.nanoTime();
      // ...
      TimerFactory.getSimpleTimer(catCalibrate, UNIT.ms).addMilliSeconds(start, System.nanoTime());
    }

    return TimerFactory.getTimer(catCalibrate).getTimeStatistics();
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
   * Clean up that class
   */
  public static final void onExit() {
    // force GC :
    resetTimers();
    timerList = null;
    timerMap = null;
  }

  /**
   * Returns elapsed time between 2 time values get from System.nanoTime() in milliseconds
   *
   * @see System#nanoTime()
   * @param start t0
   * @param now t1
   * @return (t1 - t0) in milliseconds
   */
  public static final double elapsedMilliSeconds(final long start, final long now) {
    return CONVERT_NS_INTO_MS * (now - start) - CALIBRATION_MILLI_SECONDS;
  }

  /**
   * Returns elapsed time between 2 time values get from System.nanoTime() in nanoseconds
   *
   * @see System#nanoTime()
   * @param start t0
   * @param now t1
   * @return (t1 - t0) in nanoseconds
   */
  public static final double elapsedNanoSeconds(final long start, final long now) {
    return (now - start) - CALIBRATION_NANO_SECONDS;
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
   * Return an existing or a new Timer for that category (lazy) with the given unit
   *
   * @see UNIT
   * @param category a string representing the kind of operation
   * @param unit MILLI_SECONDS or NANO_SECONDS
   * @return timer instance
   */
  public static final AbstractTimer getSimpleTimer(final String category, final UNIT unit) {
    return getTimer(category, unit, 0d);
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
      if (th > 0d) {
        timer = new ThresholdTimer(category, unit, th);
      } else {
        timer = new Timer(category, unit);
      }

      synchronized (lock) {
        final AbstractTimer old = timerMap.get(category);
        // memory thread visibility issue :
        if (old == null) {
          timerMap.put(category, timer);
          timerList.add(timer);
        } else {
          timer = old;
        }
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
    String res;

    synchronized (lock) {
      if (timerList.isEmpty()) {
        res = "";
      } else {
        final StringBuilder sb = LocalStringBuilder.getBuffer();
        for (final AbstractTimer timer : timerList) {
          sb.append("\n").append(timer.toString());
        }
        res = LocalStringBuilder.toString(sb);
      }
    }
    return res;
  }

  /**
   * Reset all timer instances
   */
  public static final void resetTimers() {
    synchronized (lock) {
      timerMap.clear();
      timerList.clear();
    }
  }

  /**
   * Return true if there is no existing timer.<br/>
   * This method is not thread safe but only useful before dumpTimers()
   * 
   * @return true if there is no existing timer
   */
  public static final boolean isEmpty() {
    return timerList.isEmpty();
  }
}
// ~ End of file
// --------------------------------------------------------------------------------------------------------

