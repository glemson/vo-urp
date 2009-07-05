package org.ivoa.util.timer;

/**
 * This class defines an Abstract Timer Object to have statistics on time metrics
 * 
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public abstract class AbstractTimer {
  // ~ Members
  // ----------------------------------------------------------------------------------------------------------

  /** category */
  private final String category;
  /** unit */
  private final TimerFactory.UNIT unit;

  // ~ Constructors
  // -----------------------------------------------------------------------------------------------------
  /**
   * Protected Constructor for AbstractTimer objects : use the factory pattern
   * 
   * @see TimerFactory.UNIT
   * @see TimerFactory#getTimer(String)
   * @param pCategory a string representing the kind of operation
   * @param pUnit MILLI_SECONDS or NANO_SECONDS
   */
  protected AbstractTimer(final String pCategory, final TimerFactory.UNIT pUnit) {
    category = pCategory;
    unit = pUnit;
  }

  // ~ Methods
  // ----------------------------------------------------------------------------------------------------------
  /**
   * Add a time measure in milliseconds
   * 
   * @param start t0
   * @param now t1
   * @see TimerFactory#elapsedMilliSeconds(long, long)
   */
  public final void addMilliSeconds(final long start, final long now) {
    add(TimerFactory.elapsedMilliSeconds(start, now));
  }

  /**
   * Add a time measure in nanoseconds
   * 
   * @param start t0
   * @param now t1
   * @see TimerFactory#elapsedNanoSeconds(long, long)
   */
  public final void addNanoSeconds(final long start, final long now) {
    add(TimerFactory.elapsedNanoSeconds(start, now));
  }

  /**
   * Add a time value
   * 
   * @param time value to add in statistics
   */
  public abstract void add(final double time);

  /**
   * Return the category
   * 
   * @return category
   */
  public final String getCategory() {
    return category;
  }

  /**
   * Return the unit
   * 
   * @return usage counter
   */
  public final TimerFactory.UNIT getUnit() {
    return unit;
  }

  /**
   * Return the usage counter
   * 
   * @return usage counter
   */
  public abstract int getCounter();

  /**
   * Return a string representation like "Timer (#unit) [#n]"
   * 
   * @return string representation
   */
  @Override
  public String toString() {
    return "Timer [" + getCategory() + " - " + getUnit() + "] [" + getCounter() + "] ";
  }
}
// ~ End of file
// --------------------------------------------------------------------------------------------------------
