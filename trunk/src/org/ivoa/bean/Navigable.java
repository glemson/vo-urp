package org.ivoa.bean;

/**
 * This interface defines an accept method with a Visitor instance
 * 
 * TODO : use the standard Visitor class
 * 
 * @see TreeVisitor
 * @see Visitor
 * 
 * @param <T> type of the visited class
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public interface Navigable<T> {

  /**
   * Navigate through this instance and possibly its children using the given visitor instance
   * 
   * @param visitor visitor instance
   * @param argument optional argument
   */
  public void accept(TreeVisitor<T> visitor, Object argument);
}
