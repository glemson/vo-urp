package org.ivoa.jaxb;

import org.apache.commons.logging.Log;

import org.ivoa.conf.Configuration;

import org.ivoa.util.LogUtil;
import org.ivoa.util.StringBuilderWriter;

import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.helpers.DefaultValidationEventHandler;


/**
 * JAXBFactory is an utility class to configure JAXB Connection & properties
 *
 * @author laurent bourges (voparis)
 */
public final class JAXBFactory {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** logger */
  private static final Log log = LogUtil.getLogger();
  /** all factories */
  private static final ConcurrentHashMap<String, JAXBFactory> factories = new ConcurrentHashMap<String, JAXBFactory>(4);
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
   * @param jaxbPath jaxb context path
   */
  private JAXBFactory(final String jaxbPath) {
    this.jaxbPath = jaxbPath;

    init();
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
    JAXBFactory jf = factories.get(jaxbPath);

    if (jf == null) {
      jf = new JAXBFactory(jaxbPath);
      factories.putIfAbsent(jaxbPath, jf);
      // to be sure to return the singleton :
      jf = factories.get(jaxbPath);
    }

    return jf;
  }

  /**
   * Called on exit (clean up code)
   */
  public static final void onExit() {
    if (! factories.isEmpty()) {
      // clean up :
      factories.clear();
    }
  }

  /**
   * Initializes the JAXB Context
   */
  private void init() {
    if (log.isDebugEnabled()) {
      log.debug("JAXBFactory.init : enter : " + this.jaxbPath);
    }

    try {
      this.jc = getContext(jaxbPath);

      if (log.isWarnEnabled()) {
        log.warn("JAXBFactory.init : done.");
      }
    } catch (final RuntimeException re) {
      log.error("JAXBFactory.init : JAXB failure : ", re);
      throw re;
    }

    if (log.isWarnEnabled()) {
      log.warn("JAXBFactory.init : exit : OK");
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
        if (log.isWarnEnabled()) {
          log.warn("====== Dump JAXBContext START =====");
          log.warn(c.toString());
          log.warn("====== Dump JAXBContext END   =====");
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
