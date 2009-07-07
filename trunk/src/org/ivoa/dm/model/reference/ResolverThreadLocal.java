package org.ivoa.dm.model.reference;

import org.ivoa.util.concurrent.ManagedThreadLocal;

/**
 * This class uses the ThreadLocal pattern to associate a ResolverContext to the current thread
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class ResolverThreadLocal extends ManagedThreadLocal<ResolverContext> {
  //~ Constructors ---------------------------------------------------------------------------------------------------

  /**
   * Public constructor
   */
  public ResolverThreadLocal() {
    super();
  }

  //~ Methods --------------------------------------------------------------------------------------------------------
  /**
   * Creates a new ResolverContext for the current thread
   *
   * @return new ResolverContext instance
   */
  @Override
  public ResolverContext onInitialValue() {
    return new ResolverContext();
  }

  /**
   * Empty method to be implemented by concrete implementations :<br/>
   * Callback to handle the remove() event for this ResolverThreadLocal instance
   *
   * @see ThreadLocal#remove()
   *
   * @throws IllegalStateException if a problem occurred
   */
  @Override
  protected boolean onRemoveValue() throws IllegalStateException {
    final ResolverContext context = get();
    if (logB.isInfoEnabled() && context.getEm() != null && context.getEm().isOpen()) {
      logB.info("ResolverThreadLocal : the entity manager is not closed : " + context.getEm());
    }
    // force GC :
    context.clear();

    return true;
  }
}
