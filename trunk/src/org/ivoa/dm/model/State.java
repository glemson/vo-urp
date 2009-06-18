/*
 * Status.java
 *
 * Author Gerard Lemson
 * Created on 7 Oct 2008
 */
package org.ivoa.dm.model;

/**
 * Keeps track of life-cycle states such as whether an object is supposed to be marshalled. Possibly whether an
 * object is purely transient (i.e. does not have a rperesentation in a DB yet),  though JPA is supposedly taking care
 * of this. But might be noce for us to know. Can also keep track of removed objects (again, JPA should know), or has
 * been modified, and therefore its state should e updated in the DB (though again JPA will likely take care of
 * this?).  For now only marshalling flags are to be set, so that we know whether an XML IDREF should be set on a
 * reference, or an ivoId.
 *
 * @author Gerard Lemson
 *
 * @since 7 Oct 20087 Oct 2008
 */
public final class State {
    //~ Constants --------------------------------------------------------------------------------------------------------

    /**
     * marshall flag value to indicate that the parent metadata object must be marshalled
     */
    public static final int TO_BE_MARSHALLED = 32;

    //~ Members ----------------------------------------------------------------------------------------------------------
    /**
     * status value (binary flags)
     */
    private int status = 0;

    //~ Methods ----------------------------------------------------------------------------------------------------------
    /**
     * Set the marshall flag
     */
    public void setToBeMarshalled() {
        this.status |= TO_BE_MARSHALLED;
    }

    /**
     * UnSet the marshall flag
     */
    public void unsetToBeMarshalled() {
        if (isToBeMarshalled()) {
            this.status -= TO_BE_MARSHALLED;
        }
    }

    /**
     * Return the marshall flag
     *
     * @return true if the marshall flag is set
     */
    public boolean isToBeMarshalled() {
        return (this.status & TO_BE_MARSHALLED) == TO_BE_MARSHALLED;
    }

    /**
     * Test method
     *
     * @param args arguments (unused)
     */
    public static void main(final String[] args) {
        State s = new State();

        s.setToBeMarshalled();

        if (s.isToBeMarshalled()) {
            System.out.println("CORRECT");
        } else {
            System.out.println("NOT CORRECT");
        }

        s.unsetToBeMarshalled();

        if (s.isToBeMarshalled()) {
            System.out.println("NOT CORRECT");
        } else {
            System.out.println("CORRECT");
        }
    }
}
//~ End of file --------------------------------------------------------------------------------------------------------
