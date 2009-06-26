package org.ivoa.jaxb;

import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.helpers.DefaultValidationEventHandler;

import org.ivoa.bean.SingletonSupport;
import org.ivoa.conf.Configuration;
import org.ivoa.util.StringBuilderWriter;

/**
 * JAXBFactory is an utility class to configure JAXB Connection & properties
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class JAXBFactory extends SingletonSupport {
    //~ Constants --------------------------------------------------------------------------------------------------------

    /** all factories */
    private static ConcurrentHashMap<String, JAXBFactory> managedInstances = new ConcurrentHashMap<String, JAXBFactory>(4);
    /** configuration test flag */
    public static final boolean isTest = Configuration.getInstance().isTest();

    //~ Members ----------------------------------------------------------------------------------------------------------
    /** jaxb context path : used to find a factory */
    private final String jaxbPath;
    /** JAXB Context for the given jaxb context path */
    private JAXBContext jc = null;

    //~ Constructors -----------------------------------------------------------------------------------------------------
    /**
     * Creates a new JPAFactory object
     *
     * @param pJaxbPath jaxb context path
     */
    private JAXBFactory(final String pJaxbPath) {
        this.jaxbPath = pJaxbPath;
    }

    //~ Methods ----------------------------------------------------------------------------------------------------------
    /**
     * Factory singleton per jaxb-context-path pattern
     *
     * @param jaxbPath jaxb context path
     *
     * @return JAXBFactory initialized
     */
    public static final JAXBFactory getInstance(final String jaxbPath) {
        JAXBFactory jf = managedInstances.get(jaxbPath);

        if (jf == null) {
            if (log.isWarnEnabled()) {
                log.warn("JAXBFactory.getInstance : creating new instance for : " + jaxbPath);
            }

            jf = prepareInstance(new JAXBFactory(jaxbPath));

            if (jf != null) {
                managedInstances.putIfAbsent(jaxbPath, jf);
                // to be sure to return the singleton :
                jf = managedInstances.get(jaxbPath);
            }
        }

        return jf;
    }

    /**
     * Concrete implementations of the SingletonSupport's clearStaticReferences() method :<br/>
     * Callback to clean up the possible static references used by this SingletonSupport instance
     * iso clear static references
     *
     * @see SingletonSupport#clearStaticReferences()
     */
    @Override
    protected void clearStaticReferences() {
        if (log.isWarnEnabled()) {
            log.warn("JAXBFactory.clearStaticReferences : enter");
        }
        // reset managed instances :
        if (managedInstances != null) {
            managedInstances.clear();
            managedInstances = null;
        }
        if (log.isWarnEnabled()) {
            log.warn("JAXBFactory.clearStaticReferences : exit");
        }
    }

    /**
     * Concrete implementations of the SingletonSupport's clear() method :<br/>
     * Callback to clean up this SingletonSupport instance iso clear instance fields<br/>
     * Ends the JAXB Context
     *
     * @see SingletonSupport#clear()
     */
    @Override
    protected void clear() {
        if (log.isWarnEnabled()) {
            log.warn("JAXBFactory.clear : enter : " + this.jaxbPath);
        }
        // force GC :
        this.jc = null;

        if (log.isWarnEnabled()) {
            log.warn("JAXBFactory.clear : exit : " + this.jaxbPath);
        }
    }

    /**
     * Concrete implementations of the SingletonSupport's initialize() method :<br/>
     * Callback to initialize this SingletonSupport instance<br/>
     * Initializes the JAXB Context
     *
     * @see SingletonSupport#initialize()
     *
     * @throws IllegalStateException if a problem occured
     */
    @Override
    protected void initialize() throws IllegalStateException {
        if (log.isDebugEnabled()) {
            log.debug("JAXBFactory.initialize : enter : " + this.jaxbPath);
        }

        try {
            this.jc = getContext(jaxbPath);

            if (log.isWarnEnabled()) {
                log.warn("JAXBFactory.initialize : done : " + jaxbPath);
            }
        } catch (final RuntimeException re) {
            log.error("JAXBFactory.initialize : JAXB failure : ", re);
            throw re;
        }

        if (log.isWarnEnabled()) {
            log.warn("JAXBFactory.initialize : exit : OK");
        }
    }

    /**
     * JAXBContext factory for a given path
     *
     * @param path given path
     *
     * @return JAXBContext instance
     *
     * @throws IllegalStateException
     */
    private JAXBContext getContext(final String path) {
        JAXBContext c = null;

        try {
            // create a JAXBContext capable of handling classes generated into
            // ivoa schema package
            c = JAXBContext.newInstance(path);

            if (isTest) {
                // This shows all the classes JAXBContext recognizes
                if (log.isInfoEnabled()) {
                    log.info("====== Dump JAXBContext START =====");
                    log.info(c.toString());
                    log.info("====== Dump JAXBContext END   =====");
                }
            }
        } catch (final JAXBException je) {
            log.error("JAXBFactory.getContext : JAXB Failure : ", je);
            throw new IllegalStateException("unable to create JAXBContext : " + path);
        }

        return c;
    }

    /**
     * Returns JAXB Context for the given jaxb context path
     *
     * @return JAXB Context for the given jaxb context path
     */
    private JAXBContext getJAXBContext() {
        return jc;
    }

    /**
     * Creates a JAXB Unmarshaller
     *
     * @return JAXB Unmarshaller
     */
    public Unmarshaller createUnMarshaller() {
        Unmarshaller u = null;

        try {
            // create an Unmarshaller
            u = getJAXBContext().createUnmarshaller();

            // this implementation is a part of the API and convenient for
            // trouble-shooting, as it prints out errors to System.out
            if (isTest) {
                u.setEventHandler(new DefaultValidationEventHandler());
            }
        } catch (final JAXBException je) {
            log.error("JAXBFactory.createUnMarshaller : JAXB Failure : ", je);
        }

        return u;
    }

    /**
     * Creates a JAXB Marshaller
     *
     * @return JAXB Marshaller
     */
    public Marshaller createMarshaller() {
        Marshaller m = null;

        try {
            // create an Unmarshaller
            m = getJAXBContext().createMarshaller();

            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        } catch (final JAXBException je) {
            log.error("JAXBFactory.createMarshaller : JAXB Failure : ", je);
        }

        return m;
    }

    /**
     * Marshall object to a String
     *
     * @param o object to serialize in XML
     * @param bufferCapacity memory buffer capacity
     *
     * @return String containing XML document or null
     */
    public String toString(final Object o, final int bufferCapacity) {
        if (o != null) {
            // create an Unmarshaller
            final Marshaller m = createMarshaller();

            // output in memory (should stay small) :
            final StringBuilderWriter out = new StringBuilderWriter(bufferCapacity);

            try {
                m.marshal(o, out);

                return out.toString();
            } catch (final JAXBException je) {
                log.error("JAXBFactory.toString : JAXB Failure : ", je);
            }
        }

        return null;
    }

    /**
     * Dumps an object in logs
     *
     * @param o object to serialize in XML
     * @param bufferCapacity memory buffer capacity
     */
    public void dump(final Object o, final int bufferCapacity) {
        if (log.isDebugEnabled()) {
            final String xml = toString(o, bufferCapacity);

            if (xml != null) {
                if (log.isDebugEnabled()) {
                    log.debug("-------------------------------------------------------------------------------");
                }

                if (log.isDebugEnabled()) {
                    log.debug("dump loaded document : ");
                }

                if (log.isDebugEnabled()) {
                    log.debug(xml);
                }

                if (log.isDebugEnabled()) {
                    log.debug("-------------------------------------------------------------------------------");
                }
            }
        }
    }
}
//~ End of file --------------------------------------------------------------------------------------------------------
