package org.ivoa.util.timer;



import java.util.LinkedHashMap;
import java.util.Map;

import org.ivoa.bean.LogSupport;


/**
 * The Timer factory contains a map[key - Timer] to associate time metrics statistics to several
 * categories of operations
 *
 * @author laurent bourges (voparis)
 */
public final class TimerFactory extends LogSupport {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** Map[key - timer] */
  protected static Map<String, ThresholdTimer> timers = new LinkedHashMap<String, ThresholdTimer>();

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
      timer = new ThresholdTimer(10000d); // 10s
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
    final StringBuilder sb = new StringBuilder(4096);

    for (final Map.Entry<String, ThresholdTimer> e : timers.entrySet()) {
      sb.append("\n").append(e.getKey()).append(" : ").append(e.getValue());
    }

    return sb.toString();
  }

  /**
   * Reset all timer instances
   */
  public static final void resetTimers() {
    timers.clear();
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
