package org.ivoa.util.timer;


/**
 * Simple Timer stats
 * 
 * @author laurent bourges (voparis)
 */
public final class Timer {

  private double acc = 0d;
  private int n = 0;
  private double min = Double.MAX_VALUE;
  private double max = Double.MIN_VALUE;

  public Timer() {
  }

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

  public double getAcc() {
    return acc;
  }

  public double getMean() {
    if (n == 0) {
      return 0d;
    }
    return acc / n;
  }

  public double getMax() {
    return max;
  }

  public double getMin() {
    return min;
  }

  public int getN() {
    return n;
  }

  @Override
  public String toString() {
    return "Timer(ms) {n=" + n + ((n > 0) ? (", min=" + min + ", max=" + max + ", acc=" + acc + ", avg=" + getMean()) : ", n/a") + "}";
  }
}
