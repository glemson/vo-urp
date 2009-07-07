package org.ivoa.bean;

/**
 * Represents a generic visitor in the visitor pattern.
 *
 * @param <T> Type of the navigable class
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public interface Visitor<T> {

    //~ Methods ----------------------------------------------------------------------------------------------------------

    /**
     * Process the specified object before its collections are being processed.</br>
     * @param object instance
     */
    public void preProcess(final T object);

    /**
     * Process the specified object after its collections have been processed.</br>
     * @param object instance
     */
    public void postProcess(final T object);
}
//~ End of file --------------------------------------------------------------------------------------------------------
