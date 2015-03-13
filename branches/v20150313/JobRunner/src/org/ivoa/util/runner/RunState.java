package org.ivoa.util.runner;


/**
 * Job States
 * 
 * @author laurent bourges (voparis)
 */
public enum RunState {
  /** undefined state */
  STATE_UNKNOWN("UNDEFINED"),
  /** pending state */
  STATE_PENDING("PENDING"),
  /** running state */
  STATE_RUNNING("RUNNING"),
  /** finished state with an error */
  STATE_FINISHED_ERROR("ERROR"),
  /** finished state */
  STATE_FINISHED_OK("OK"),
  /** interrupted state (shutdown) */
  STATE_INTERRUPTED("INTERRUPTED"),
  /** cancelled state (user) */
  STATE_CANCELLED("CANCELLED"),
  /** killed state (user) */
  STATE_KILLED("KILLED");

      /** string representation */
    private final String value;


    /**
     * Creates a new RunState Enumeration Literal
     *
     * @param v string representation
     */
    RunState(final String v) {
        value = v;
    }

    /**
     * Return the string representation of this enum constant (value)
     * @return string representation of this enum constant (value)
     */
    public final String value() {
        return this.value;
    }

    /**
     * Return the string representation of this enum constant (value)
     * @see #value()
     * @return string representation of this enum constant (value)
     */
    @Override
    public final String toString() {
        return value();
    }

    /**
     * Return the Cardinality enum constant corresponding to the given string representation (value)
     *
     * @param v string representation (value)
     *
     * @return Cardinality enum constant
     *
     * @throws IllegalArgumentException if there is no matching enum constant
     */
    public final static RunState fromValue(final String v) {
        for (RunState c : RunState.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }

        throw new IllegalArgumentException(
            "RunState.fromValue : No enum const for the value : " + v);
    }
  }
