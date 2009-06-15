package org.ivoa.util.timer;



import java.util.LinkedHashMap;
import java.util.Map;

import org.ivoa.bean.LogSupport;


/**
 * Timer factory
 *
 * @author laurent bourges (voparis)
 */
public final class TimerFactory extends LogSupport {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** TODO : Field Description */
  protected static final Map<String, ThresholdTimer> timers = new LinkedHashMap<String, ThresholdTimer>();

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Forbidden Constructor
   */
  private TimerFactory() {
    /* no-op */
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Returns elapsed time between 2 values get from
   *
   * @param start t0
   * @param now t1
   *
   * @return (t1 - t0) in milliseconds
   *
   * @see System#nanoTime()
   */
  public static final long elapsed(final long start, final long now) {
    return (now - start) / 1000000L;
  }

  /**
   * TODO : Method Description
   *
   * @param category
   *
   * @return value TODO : Value Description
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
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public static final Map<String, ThresholdTimer> getTimers() {
    return timers;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public static final String dumpTimers() {
    final StringBuilder sb = new StringBuilder(4096);

    for (final Map.Entry<String, ThresholdTimer> e : timers.entrySet()) {
      sb.append("\n").append(e.getKey()).append(" : ").append(e.getValue());
    }

    return sb.toString();
  }

  /**
   * TODO : Method Description
   */
  public static final void resetTimers() {
    timers.clear();
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
