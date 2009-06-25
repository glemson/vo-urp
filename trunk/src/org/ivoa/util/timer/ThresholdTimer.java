package org.ivoa.util.timer;

/**
 * Special Timer with a threshold to separate low & high values
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class ThresholdTimer {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  private final Timer low = new Timer();

  /**
   * TODO : Field Description
   */
  private final Timer high = new Timer();

  /**
   * TODO : Field Description
   */
  private final double threshold;

  //~ Constructors -----------------------------------------------------------------------------------------------------

  /**
   * Creates a new ThresholdTimer object
   *
   * @param th 
   */
  public ThresholdTimer(final double th) {
    this.threshold = th;
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Add a time value
   * 
   * @param start t0
   * @param now t1
   * 
   * @see TimerFactory#elapsed(long, long)
   */
  public void add(final long start, final long now) {
    add(TimerFactory.elapsed(start, now));
  }
  
  /**
   * TODO : Method Description
   *
   * @param time 
   */
  public void add(final double time) {
    if (time > threshold) {
      high.add(time);
    } else {
      low.add(time);
    }
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public Timer getTimerHigh() {
    return high;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public Timer getTimerLow() {
    return low;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  @Override
  public String toString() {
    return "Low: " + getTimerLow().toString() + " - High: " + getTimerHigh().toString();
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
