package org.ivoa.bean;

/**
 * Visitor class to implement the visitor pattern T is the type of the visited class.
 * 
 * @see Navigable
 * @see TreeVisitor
 * @param <T> type of the visited class
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public class Visitor<T> extends SingletonSupport {

  /**
   * Empty implementation of the Visitor Design pattern
   * 
   * @param element instance to visit
   * @return true if the traversal is OK
   */
  public final boolean visit(final T element) {
    return this.visit(element, null);
  }

  /**
   * Empty implementation of the Visitor Design pattern
   * 
   * @param element instance to visit
   * @param argument optional argument
   * @return true if the traversal is OK
   */
  public boolean visit(final T element, final Object argument) {
    if (logB.isDebugEnabled()) {
      logB.debug(this.getClass().getSimpleName() + ".visit : element : " + element + " - " + argument);
    }
    return true;
  }

}
