package org.ivoa.util.timer;

/**
 * Special Timer with a threshold to separate low & high values
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class ThresholdTimer extends AbstractTimer {
  // ~ Members
  // ----------------------------------------------------------------------------------------------------------

  /** occurence counter */
  private int counter = 0;
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
   * Add a time value
   *
   * @param time value to add in statistics
   */
  @Override
  public void add(final double time) {
    this.counter++;
    if (time > threshold) {
      high.add(time);
    } else {
      low.add(time);
    }
  }

  /**
   * Return the usage counter
   *
   * @return usage counter
   */
  @Override
  public int getCounter() {
    return this.counter;
  }

  /**
   * Return the Timer instance for the high values
   *
   * @return Timer instance for the high values
   */
  public Timer getTimerHigh() {
    return high;
  }

  /**
   * Return the Timer instance for the low values
   *
   * @return Timer instance for the low values
   */
  public Timer getTimerLow() {
    return low;
  }

  /**
   * Return a string representation
   *
   * @return string representation
   */
  @Override
  public String toString() {
    return super.toString() + "(threshold = " + threshold + " " + getUnit() + ") {\n  " +
            "Low  : " + getTimerLow().toString() + "\n  " +
            "High : " + getTimerHigh().toString() + "\n}";
  }
}
// ~ End of file
// --------------------------------------------------------------------------------------------------------
