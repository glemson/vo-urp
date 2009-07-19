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
   * Add a time value given in double precision
   *
   * @param time value to add in statistics
   */
  @Override
  protected final void add(final double time) {
    this.usage++;
    monitorTime.add(time);
  }

  /**
   * Return the time statistics
   *
   * @return time statistics
   */
  @Override
  public final StatLong getTimeStatistics() {
    return this.monitorTime;
  }

  /**
   * Return a string representation
   *
   * @return string representation
   */
  @Override
  public final String toString() {
    String res = super.toString();

    final StatLong stat = getTimeStatistics();
    if (stat.getCounter() > 0) {
      res += stat.toString(true);
    }
    return res;
  }
}
// ~ End of file
// --------------------------------------------------------------------------------------------------------

