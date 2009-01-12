package org.ivoa.util.timer;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.ivoa.util.LogUtil;


/**
 * Timer factory
 * 
 * @author laurent bourges (voparis)
 */
public final class TimerFactory {

  protected static final Log log = LogUtil.getLoggerDev();
  protected final static Map<String, ThresholdTimer> timers = new LinkedHashMap<String, ThresholdTimer>();

  /**
   * Forbidden Constructor
   */
  private TimerFactory() {
  }

  /**
   * Returns elapsed time between 2 values get from @see System#nanoTime()
   * @param start t0
   * @param now t1
   * @return (t1 - t0) in milliseconds
   */
  public static final long elapsed(final long start, final long now) {
    return (now - start) / 1000000l;
  }

  public static final ThresholdTimer getTimer(final String category) {
    ThresholdTimer timer = timers.get(category);
    if (timer == null) {
      timer = new ThresholdTimer(10000d); // 10s
      timers.put(category, timer);
    }
    return timer;
  }

  public final static Map getTimers() {
    return timers;
  }

  public final static String dumpTimers() {
    final StringBuilder sb = new StringBuilder(4096);
    for (Map.Entry<String, ThresholdTimer> e : timers.entrySet()) {
      sb.append("\n").append(e.getKey()).append(" : ").append(e.getValue());
    }
    return sb.toString();
  }

  public final static void resetTimers() {
    timers.clear();
  }
}
