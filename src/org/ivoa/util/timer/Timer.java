package org.ivoa.util.timer;

/**
 * This class contains statistics for time metrics
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class Timer extends AbstractTimer {
  // ~ Members
  // ----------------------------------------------------------------------------------------------------------

  /** accumulator */
  private double acc = 0d;

  /** minimum value */
  private double min = Double.MAX_VALUE;

  /** maximum value */
  private double max = Double.MIN_VALUE;

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
      incN();
      acc += time;

      if (time < min) {
        min = time;
      }

      if (time > max) {
        max = time;
      }
    }
  }

  /**
   * Return the accumulator
   *
   * @return accumulator
   */
  public double getAcc() {
    return acc;
  }

  /**
   * Return the mean
   *
   * @return mean
   */
  public double getMean() {
    final int n = getN();
    if (n == 0) {
      return 0d;
    }

    return acc / n;
  }

  /**
   * Return the maximum value
   *
   * @return maximum value
   */
  public double getMax() {
    return max;
  }

  /**
   * Return the minimum value
   *
   * @return minimum value
   */
  public double getMin() {
    return min;
  }

  /**
   * Return a string representation
   *
   * @return string representation
   */
  @Override
  public String toString() {
    return super.toString() + "{" + (getN() > 0 ? "min = " + min + ", max = " + max + ", acc = " + acc + ", avg = " + getMean() : "") + "}";
  }
}
// ~ End of file
// --------------------------------------------------------------------------------------------------------
