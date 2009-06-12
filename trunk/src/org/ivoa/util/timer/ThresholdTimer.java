package org.ivoa.util.timer;

/**
 * Special Timer with a threshold to separate low & high values
 *
 * @author laurent bourges (voparis)
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
