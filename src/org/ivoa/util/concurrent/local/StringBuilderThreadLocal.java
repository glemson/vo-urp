package org.ivoa.util.concurrent.local;

  /**
   * This class uses the ThreadLocal pattern to associate a StringBuilder to the current thread
   *
 * @author laurent
 */
public final class StringBuilderThreadLocal extends ManagedThreadLocal<StringBuilderContext> {
  
    // ~ Constructors
    // ---------------------------------------------------------------------------------------------------

    /**
     * Protected constructor
     */
    public StringBuilderThreadLocal() {
      super();
    }

    // ~ Methods
    // --------------------------------------------------------------------------------------------------------
    /**
     * Return a new StringBuilder for the current thread
     *
     * @return new StringBuilder instance with initial capacity set to CAPACITY
     */
    @Override
    public final StringBuilderContext initialValue() {
      return new StringBuilderContext();
    }

  }

