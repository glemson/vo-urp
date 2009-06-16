package org.ivoa.util.timer;

/**
 * This class contains statistics for time metrics
 *
 * @author laurent bourges (voparis)
 */
public final class Timer {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /** accumulator */
  private double acc = 0d;

  /** value counter */
  private int n = 0;

  /** minimum value */
  private double min = Double.MAX_VALUE;

  /** maximum value */
  private double max = Double.MIN_VALUE;

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Creates a new Timer object
   */
  public Timer() {
    /* no-op */
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Add a time value
   *
   * @param time value to add in statistics
   */
  public void add(final double time) {
    // linux bug :
    if (time >= 0) {
      this.n++;
      this.acc += time;

      if (time < this.min) {
        this.min = time;
      }

      if (time > this.max) {
        this.max = time;
      }
    }
  }

  /**
   * Return the accumulator
   *
   * @return accumulator
   */
  public double getAcc() {
    return this.acc;
  }

  /**
   * Return the mean
   *
   * @return mean
   */
  public double getMean() {
    if (this.n == 0) {
      return 0d;
    }

    return this.acc / this.n;
  }

  /**
   * Return the maximum value
   *
   * @return maximum value
   */
  public double getMax() {
    return this.max;
  }

  /**
   * Return the minimum value
   *
   * @return minimum value
   */
  public double getMin() {
    return this.min;
  }

  /**
   * Return the value counter
   *
   * @return value counter
   */
  public int getN() {
    return this.n;
  }

  /**
   * Return a string representation
   *
   * @return string representation
   */
  @Override
  public String toString() {
    return "Timer(ms) {n="
        + this.n
        + ((this.n > 0) ? (", min=" + this.min + ", max=" + this.max + ", acc=" + this.acc
            + ", avg=" + this.getMean()) : ", n/a") + "}";
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
