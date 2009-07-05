package org.ivoa.util.timer;

import org.ivoa.util.stat.StatLong;

/**
 * This class contains statistics for time metrics
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class Timer extends AbstractTimer {
  // ~ Members
  // ----------------------------------------------------------------------------------------------------------

  /** statistics for elapsed time */
  private final StatLong monitorTime = new StatLong();

  // ~ Constructors
  // -----------------------------------------------------------------------------------------------------
  /**
   * Protected Constructor for Timer objects : use the factory pattern
   * 
   * @see TimerFactory.UNIT
   * @see TimerFactory#getTimer(String)
   * @param pCategory a string representing the kind of operation
   * @param pUnit MILLI_SECONDS or NANO_SECONDS
   */
  protected Timer(final String pCategory, final TimerFactory.UNIT pUnit) {
    super(pCategory, pUnit);
  }

  // ~ Methods
  // ----------------------------------------------------------------------------------------------------------
  /**
   * Add a time value
   *
   * @param time value to add in statistics
   */
  @Override
  public void add(final double time) {
    // LINUX bug :
    if (time >= 0) {
      monitorTime.add(time);
    }
  }

  /**
   * Return the usage counter
   *
   * @return usage counter
   */
  public int getCounter() {
    return this.monitorTime.getCounter();
  }

  /**
   * Return the time statistics
   *
   * @return time statistics
   */
  public StatLong getTimeStats() {
    return this.monitorTime;
  }

  /**
   * Return a string representation
   *
   * @return string representation
   */
  @Override
  public String toString() {
    String res = super.toString();

    if (getCounter() > 0) {
      final StatLong stat = getTimeStats();
      res += "{" +
              "min = " + adjustValue(stat.getMin()) + ", " +
              "avg = " + adjustValue(stat.getAverage()) + ", " +
              "max = " + adjustValue(stat.getMax()) + ", " +
              "acc = " + adjustValue(stat.getAccumulator()) + ", " +
              "std = " + adjustValue(stat.getStdDev()) +
              "}";
    }

    return res;
  }

  private double adjustValue(final double value) {
    final long intValue = (long) (1000d * value);

    return intValue / 1000d;
  }
}
// ~ End of file
// --------------------------------------------------------------------------------------------------------
