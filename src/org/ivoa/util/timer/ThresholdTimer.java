package org.ivoa.util.timer;


/**
 * Special Timer with a threshold to separate low & high values 
 * 
 * @author laurent bourges (voparis)
 */
public final class ThresholdTimer {

  private Timer low = new Timer();
  private Timer high = new Timer();
  private final double threshold;

  public ThresholdTimer(final double th) {
    this.threshold = th;
  }

  public void add(final double time) {
    if (time > threshold) {
      high.add(time);
    } else {
      low.add(time);
    }
  }

  public Timer getTimerHigh() {
    return high;
  }

  public Timer getTimerLow() {
    return low;
  }

  @Override
  public String toString() {
    return "Low: " + getTimerLow().toString() + " - High: " + getTimerHigh().toString();
  }
}
