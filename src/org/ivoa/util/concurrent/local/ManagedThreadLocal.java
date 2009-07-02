package org.ivoa.util.concurrent.local;

  /**
   * This class enhances the ThreadLocal class to define initialization and shutdown events
   *
   * TODO : in progress = implement init / release events
   *
   * @param <T> ThreadLocal Type used
   *
 * @author laurent
 */
public class ManagedThreadLocal<T> extends ThreadLocal<T> {
  
    // ~ Constructors
    // ---------------------------------------------------------------------------------------------------

    /**
     * Protected constructor
     */
    public ManagedThreadLocal() {
      super();
    }

    //~ Methods ----------------------------------------------------------------------------------------------------------
    
    /**
     * Empty method to be implemented by concrete implementations :<br/>
     * Callback to initialize this ManagedThreadLocal instance
     *
     * @throws IllegalStateException if a problem occurred
     */
    public void initialize() throws IllegalStateException {
        /* no-op */
    }

    /**
     * Empty method to be implemented by concrete implementations :<br/>
     * Callback to clean up this ManagedThreadLocal instance iso release resources or clear instance fields
     */
    public void clear() throws IllegalStateException {
        /* no-op */
    }

  }

