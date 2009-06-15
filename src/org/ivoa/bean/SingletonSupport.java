package org.ivoa.bean;

/**
 * Singleton design pattern implementation for Java 5+
 * @author laurent bourges
 */
public abstract class SingletonSupport extends LogSupport {

//~ Constructors -----------------------------------------------------------------------------------------------------
    /**
     * Private empty Constructor
     */
    protected SingletonSupport() {
      /* no-op */
    }

//~ Methods ----------------------------------------------------------------------------------------------------------
    /**
     * TODO : Method Description
     */
    protected abstract void initialize();

    /**
     * TODO : Method Description
     */
    protected abstract void clear();

//~ End of file --------------------------------------------------------------------------------------------------------
}
