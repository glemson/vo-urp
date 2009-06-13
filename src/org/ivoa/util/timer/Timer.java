package org.ivoa.util.timer;

/**
 * Simple Timer stats
 *
 * @author laurent bourges (voparis)
 */
public final class Timer {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /** TODO : Field Description */
  private double acc = 0d;
  /** TODO : Field Description */
  private int n = 0;
  /** TODO : Field Description */
  private double min = Double.MAX_VALUE;
  /** TODO : Field Description */
  private double max = Double.MIN_VALUE;

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Creates a new Timer object
   */
  public Timer() {
    // empty block
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @param time
   */
  public void add(final double time) {
    // linux bug :
    if (time >= 0) {
      n++;
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
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public double getAcc() {
    return acc;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public double getMean() {
    if (n == 0) {
      return 0d;
    }

    return acc / n;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public double getMax() {
    return max;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public double getMin() {
    return min;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public int getN() {
    return n;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  @Override
  public String toString() {
    return "Timer(ms) {n=" + n +
           ((n > 0) ? (", min=" + min + ", max=" + max + ", acc=" + acc + ", avg=" + getMean()) : ", n/a") + "}";
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
