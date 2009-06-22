package org.ivoa.util;

import org.ivoa.bean.SingletonSupport;

/**
 * ThreadLocal StringBuilder for performance
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class LocalStringBuilder extends SingletonSupport {
    //~ Constants --------------------------------------------------------------------------------------------------------

    /** initial buffer capacity = 512 chars */
    public final static int CAPACITY = 512;
    /** buffer thread Local */
    private static ThreadLocal<StringBuilder> bufferLocal = new StringBuilderThreadLocal();

    /** singleton instance (java 5 memory model) */
    private static LocalStringBuilder instance = new LocalStringBuilder();
    
    //~ Constructors -----------------------------------------------------------------------------------------------------
    
    /**
     * Forbidden constructor
     */
    private LocalStringBuilder() {
        super();
        
        // register this instance in SingletonSupport
        register(this);
    }

    /**
     * Concrete implementations of the SingletonSupport's clearStaticReferences() method :<br/>
     * Callback to clean up the possible static references used by this SingletonSupport instance iso
     * clear static references
     * 
     * @see SingletonSupport#clearStaticReferences()
     */
    @Override
    protected void clearStaticReferences() {
        // force GC :
      if (instance != null) {
        instance = null;
      }
        bufferLocal = null;
    }

    /**
     * Return an empty threadLocal instance
     *
     * Must be cleared after work by calling buffer.setLength(0) or toStringXxx methods
     *
     * @see StringBuilder#setLength(int)
     * @see #toString(StringBuilder)
     * @see #toStringBuilder(StringBuilder, StringBuilder)
     *
     * @return StringBuilder threadLocal instance
     */
    public final static StringBuilder getBuffer() {
        return bufferLocal.get();
    }

    /**
     * Return the string contained in the given buffer and reset the buffer
     * @param sb buffer
     * @return string contained in the given buffer
     */
    public final static String toString(final StringBuilder sb) {
        final String s = sb.toString();
        // reset without array operation : just set count to 0 / leave buffer available with the same capacity :
        sb.setLength(0);
        return s;
    }

    /**
     * Unsynchronized copy from one buffer to another buffer
     * @param sb source buffer
     * @param sbTo destination buffer
     */
    public final static void toStringBuilder(final StringBuilder sb, final StringBuilder sbTo) {
        sbTo.append(sb);
        // reset without array operation : just set count to 0 / leave buffer available with the same capacity :
        sb.setLength(0);
    }


    //~ Inner Classes ----------------------------------------------------------------------------------------------------

    /**
     * This class uses the ThreadLocal pattern to associate a StringBuilder to the current thread
     */
    protected static final class StringBuilderThreadLocal extends ThreadLocal<StringBuilder> {
        //~ Constructors ---------------------------------------------------------------------------------------------------

        /**
         * Protected constructor
         */
        protected StringBuilderThreadLocal() {
            super();
        }

        //~ Methods --------------------------------------------------------------------------------------------------------
        /**
         * Return a new StringBuilder for the current thread
         * @return new StringBuilder instance with initial capacity set to @see #CAPACITY
         */
        @Override
        public final StringBuilder initialValue() {
            return new StringBuilder(CAPACITY);
        }
    }
}