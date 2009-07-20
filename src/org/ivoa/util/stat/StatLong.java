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

  /** default value for the average threshold */
  private final static int DEFAULT_THRESHOLD_AVG = 5;
  /** average threshold used to start considering that the average value is correct */
  private static int THRESHOLD_AVG = DEFAULT_THRESHOLD_AVG;
  /** stddev threshold used to start computing the standard deviation : 2 x THRESHOLD_AVG */
  private static int THRESHOLD_STDDEV = 2 * THRESHOLD_AVG;
  /**
   * Fixed Divisor for stddev : THRESHOLD_STDDEV - THRESHOLD_AVG + 1
   */
  private static int THRESHOLD_STDDEV_N = 1 + THRESHOLD_STDDEV - THRESHOLD_AVG;
  // ~ Members
  // ----------------------------------------------------------------------------------------------------------
  /** occurence counter */
  private int counter;
  /** accumulator */
  private double acc;
  /** average */
  private double average;
  /** minimum value */
  private double min;
  /** maximum value */
  private double max;
  /** high occurence counter */
  private int counterHigh;
  /** delta accumulator (higher values in compare to the average value) */
  private double accDeltaHigh;
  /** low occurence counter */
  private int counterLow;
  /** delta accumulator (lower values in compare to the average value) */
  private double accDeltaLow;

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
   * Define the occurence thresholds to compute average, standard deviation ...
   * @param thresholdAverage
   */
  public static void defineThreshold(final int thresholdAverage) {
    if (thresholdAverage > 0) {
      THRESHOLD_AVG = thresholdAverage;
      THRESHOLD_STDDEV = 2 * THRESHOLD_AVG;
      THRESHOLD_STDDEV_N = 1 + THRESHOLD_STDDEV - THRESHOLD_AVG;
    }
  }

  /**
   * reset values
   */
  public final void reset() {
    this.counter = 0;
    this.acc = 0d;
    this.average = 0d;
    this.min = Double.MAX_VALUE;
    this.max = Double.MIN_VALUE;
    this.counterHigh = 0;
    this.accDeltaHigh = 0d;
    this.counterLow = 0;
    this.accDeltaLow = 0d;
  }

  /**
   * Merge the given statistics in this instance
   *
   * @param stat statistics to add in this instance
   */
  public final void add(final StatLong stat) {
    this.counter += stat.getCounter();
    this.acc += stat.getAccumulator();
    this.average = this.acc / this.counter;
    if (stat.getMin() < this.min) {
      this.min = stat.getMin();
    }
    if (stat.getMax() > this.max) {
      this.max = stat.getMax();
    }
    this.counterHigh += stat.getCounterHigh();
    this.accDeltaHigh += stat.getDeltaAccumulatorHigh();
    this.counterLow += stat.getCounterLow();
    this.accDeltaLow += stat.getDeltaAccumulatorLow();
  }

  /**
   * Add the given value in statistics
   *
   * @param value integer value to add in statistics
   */
  public final void add(final int value) {
    add((double) value);
  }

  /**
   * Add the given value in statistics
   *
   * @param value long value to add in statistics
   */
  public final void add(final long value) {
    add((double) value);
  }

  /**
   * Add the given value in statistics
   *
   * @param value double value to add in statistics
   */
  public final void add(final double value) {
    if (value < this.min) {
      this.min = value;
    }

    if (value > this.max) {
      this.max = value;
    }

    final int count = ++this.counter;

    this.acc += value;
    this.average = this.acc / count;

    if (count >= THRESHOLD_AVG) {
      /**
       * X-       =     (1/n) * Sum (Xn)
       * stdDev^2 = (1/(n-1)) * Sum [ (Xn - * X-)^2 ]
       */
      // the standard deviation is estimated with a clipping algorithm :
      final double delta = this.average - value;
      if (delta > 0) {
        this.counterLow++;
        // Sum of delta square :
        this.accDeltaLow += delta * delta;
      } else {
        this.counterHigh++;
        // Sum of delta square :
        this.accDeltaHigh += delta * delta;
      }
    }
  }

  /**
   * Return the occurence counter
   *
   * @return occurence counter
   */
  public final int getCounter() {
    return counter;
  }

  /**
   * Return the accumulator value
   *
   * @return accumulator value
   */
  public final double getAccumulator() {
    return this.acc;
  }

  /**
   * Return the high occurence counter
   *
   * @return high occurence counter
   */
  public final int getCounterHigh() {
    return counterHigh;
  }

  /**
   * Return the delta accumulator value
   *
   * @return delta accumulator value
   */
  public final double getDeltaAccumulatorHigh() {
    return this.accDeltaHigh;
  }

  /**
   * Return the low occurence counter
   *
   * @return low occurence counter
   */
  public final int getCounterLow() {
    return counterLow;
  }

  /**
   * Return the delta accumulator value
   *
   * @return delta accumulator value
   */
  public final double getDeltaAccumulatorLow() {
    return this.accDeltaLow;
  }

  /**
   * Return the average value
   *
   * @return average value
   */
  public final double getAverage() {
    return this.average;
  }

  /**
   * Return the minimum value
   *
   * @return minimum value
   */
  public final double getMin() {
    return this.min;
  }

  /**
   * Return the maximum value
   *
   * @return maximum value
   */
  public final double getMax() {
    return this.max;
  }

  /**
   * Return the standard deviation (estimated)
   *
   * @return standard deviation
   */
  public final double getStdDev() {
    double stddev = 0d;
    if (this.counter >= THRESHOLD_STDDEV) {
      stddev = Math.sqrt((this.accDeltaHigh + this.accDeltaLow) / (this.counter - THRESHOLD_STDDEV_N));
    }

    return stddev;
  }

  /**
   * Return the standard deviation (estimated)
   *
   * @return standard deviation
   */
  public final double getStdDevHigh() {
    double stddev = 0d;
    if (this.counterHigh >= THRESHOLD_STDDEV) {
      stddev = Math.sqrt(this.accDeltaHigh / (this.counterHigh - THRESHOLD_STDDEV_N));
    }

    return stddev;
  }

  /**
   * Return the standard deviation (estimated)
   *
   * @return standard deviation
   */
  public final double getStdDevLow() {
    double stddev = 0d;
    if (this.counterLow >= THRESHOLD_STDDEV) {
      stddev = Math.sqrt(this.accDeltaLow / (this.counterLow - THRESHOLD_STDDEV_N));
    }

    return stddev;
  }

  /**
   * Return a string representation
   * @param detailed true to get full statistics
   * @return string representation
   */
  public final String toString(final boolean detailed) {
    String res = "";
    if (detailed && getCounter() > 0) {
      res = "{" +
          "num = " + getCounter() + " : " +
          "min = " + adjustValue(getMin()) + ", " +
          "avg = " + adjustValue(getAverage()) + ", " +
          "max = " + adjustValue(getMax()) + ", " +
          "acc = " + adjustValue(getAccumulator()) + ", " +
          "std = " + adjustValue(getStdDev()) + 
          " [" + (getCounterLow() + getCounterHigh()) + "] " +
          "std low  = " + adjustValue(getStdDevLow()) + 
          " [" + (getCounterLow()) + "] " +
          "std high = " + adjustValue(getStdDevHigh()) +
          " [" + (getCounterHigh()) + "] " +
          "}";
    }
    return res;
  }

  /**
   * Format the given double value to keep only 3 decimal digits
   * @param value value to adjust
   * @return double value with only 3 decimal digits
   */
  public final static double adjustValue(final double value) {
    return ((long) (1e5d * value)) / 1e5d;
  }
}
