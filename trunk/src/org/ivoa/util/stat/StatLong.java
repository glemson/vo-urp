package org.ivoa.util.stat;

/**
 * Utility Class to store statistics : accumulated, average, accumulated delta,
 * stddev
 *
 * WARNING : Synchronization for coherence must be done OUTSIDE in the calling class !
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class StatLong {
  // ~ Constants
  // --------------------------------------------------------------------------------------------------------

  /** threshold used to start considering that the average value is correct */
  private final static int THRESHOLD_MOY = 5;
  /** threshold used to start computing the standard deviation */
  private final static int THRESHOLD_SIGMA = 2 * THRESHOLD_MOY;
  /**
   * Fixed Divisor for std dev : THRESHOLD_SIGMA - THRESHOLD_MOY + 1
   */
  private final static int THRESHOLD_SIGMA_N = 1 + THRESHOLD_SIGMA - THRESHOLD_MOY;

  // ~ Members
  // ----------------------------------------------------------------------------------------------------------
  /** occurence counter */
  private int counter = 0;
  /** accumulator */
  private double acc = 0d;
  /** delta accumulator */
  private double accDelta;
  /** average */
  private double average = 0d;
  /** minimum value */
  private double min = Double.MAX_VALUE;
  /** maximum value */
  private double max = Double.MIN_VALUE;

  // ~ Constructors
  // -----------------------------------------------------------------------------------------------------
  /**
   * Creates a new StatLong object.
   */
  public StatLong() {
    reset();
  }

  // ~ Methods
  // ----------------------------------------------------------------------------------------------------------
  /**
   * reset values
   */
  public final void reset() {
    this.counter = 0;
    this.acc = 0d;
    this.accDelta = 0d;
    this.average = 0d;
    this.min = Double.MAX_VALUE;
    this.max = Double.MIN_VALUE;
  }

  /**
   * Add the given value in statistics
   *
   * @param value integer value to add in statistics
   */
  public final void add(final int value) {
    add(1d * value);
  }

  /**
   * Add the given value in statistics
   *
   * @param value long value to add in statistics
   */
  public final void add(final long value) {
    add(1d * value);
  }

  /**
   * Add the given value in statistics
   *
   * @param value double value to add in statistics
   */
  public final void add(final double value) {
    this.counter++;

    if (value < this.min) {
      this.min = value;
    }

    if (value > this.max) {
      this.max = value;
    }

    if (value > 0) {
      this.acc += value;
      this.average = this.acc / this.counter;

      if (this.counter >= THRESHOLD_MOY) {
        /**
         * X-       =     (1/n) * Sum (Xn)
         * stdDev^2 = (1/(n-1)) * Sum [ (Xn - * X-)^2 ]
         */

        // the standard deviation is estimated with a clipping algorithm :
        final double delta = this.average - value;
        // Sum of delta square :
        this.accDelta += delta * delta;
      }
    }
  }

  /**
   * Return the usage counter
   *
   * @return usage counter
   */
  public final int getCounter() {
    return counter;
  }

  /**
   * Return the accumulator value
   *
   * @return accumulator value
   */
  public double getAccumulator() {
    return this.acc;
  }

  /**
   * Return the delta accumulator value
   *
   * @return delta accumulator value
   */
  public final double getDeltaAccumulator() {
    return this.accDelta;
  }

  /**
   * Return the average value
   *
   * @return average value
   */
  public double getAverage() {
    return this.average;
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
   * Return the maximum value
   *
   * @return maximum value
   */
  public double getMax() {
    return this.max;
  }

  /**
   * Return the standard deviation (esimated)
   *
   * @return standard deviation
   */
  public final double getStdDev() {
    double stddev = 0d;
    if (this.counter >= THRESHOLD_SIGMA) {
      stddev = Math.sqrt(this.accDelta / (this.counter - THRESHOLD_SIGMA_N));
    }

    return stddev;
  }
}
