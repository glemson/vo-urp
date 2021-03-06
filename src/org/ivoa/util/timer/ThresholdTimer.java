package org.ivoa.util.timer;

import org.ivoa.util.stat.StatLong;


/**
 * Special Timer with a threshold to separate low & high values
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class ThresholdTimer extends AbstractTimer {
  // ~ Members
  // ----------------------------------------------------------------------------------------------------------

  /**
   * Timer instance for the low values
   */
  private final Timer low;
  /**
   * Timer instance for the high values
   */
  private final Timer high;
  /**
   * High-value threshold
   */
  private final double threshold;

  // ~ Constructors
  // -----------------------------------------------------------------------------------------------------
  /**
   * Protected Constructor for ThresholdTimer objects : use the factory pattern
   *
   * @see TimerFactory.UNIT
   * @see TimerFactory#getTimer(String)
   * @param pCategory a string representing the kind of operation
   * @param pUnit MILLI_SECONDS or NANO_SECONDS
   * @param th threshold to detect an high value
   */
  protected ThresholdTimer(final String pCategory, final TimerFactory.UNIT pUnit, final double th) {
    super(pCategory, pUnit);
    low = new Timer(pCategory, pUnit);
    high = new Timer(pCategory, pUnit);
    threshold = th;
  }

  // ~ Methods
  // ----------------------------------------------------------------------------------------------------------
  /**
   * Add a time value given in double precision
   *
   * @param time value to add in statistics
   */
  @Override
  public final void add(final double time) {
    this.usage++;
    if (time > threshold) {
      high.add(time);
    } else {
      low.add(time);
    }
  }

  /**
   * Return the Timer instance for the high values
   *
   * @return Timer instance for the high values
   */
  public final Timer getTimerHigh() {
    return high;
  }

  /**
   * Return the Timer instance for the low values
   *
   * @return Timer instance for the low values
   */
  public final Timer getTimerLow() {
    return low;
  }

  /**
   * Return the time statistics
   *
   * @return time statistics
   */
  @Override
  public final StatLong getTimeStatistics() {
    return this.getTimerHigh().getTimeStatistics();
  }

  /**
   * Return a string representation
   *
   * @return string representation
   */
  @Override
  public final String toString() {
    return super.toString() + "(threshold = " + StatLong.adjustValue(threshold) + " " + getUnit() + ") {\n  " +
        "Low  : " + getTimerLow().toString() + "\n  " +
        "High : " + getTimerHigh().toString() + "\n}";
  }
}
// ~ End of file
// --------------------------------------------------------------------------------------------------------

