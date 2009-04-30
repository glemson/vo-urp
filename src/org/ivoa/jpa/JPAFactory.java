package org.ivoa.jpa;

import org.apache.commons.logging.Log;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.SessionCustomizer;

import org.eclipse.persistence.sessions.Session;

import org.ivoa.util.CollectionUtils;
import org.ivoa.util.FileUtils;
import org.ivoa.util.LogUtil;
import org.ivoa.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


/**
 * JPAFactory is an utility class to configure JPA Connection & properties
 *
 * @author laurent bourges (voparis)
 */
public final class JPAFactory {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** logger */
  private static final Log log = LogUtil.getLogger();
  /** all factories */
  private static final ConcurrentHashMap<String, JPAFactory> factories = new ConcurrentHashMap<String, JPAFactory>(4);
  /** Default config file */
  public static final String CONFIG_FILE = "jpa-config";
  /** seems not working with postgres JDBC driver */
  public static final boolean USE_INTEGRITY_CHECKER = false;

  //~ Members ----------------------------------------------------------------------------------------------------------

  /** persistence Unit Label */
  private final String pu;
  /** config file name */
  private final String config;
  /** properties */
  private Properties properties = null;
  /** JPA factory */
  private EntityManagerFactory emf = null;

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Creates a new JPAFactory object
   *
   * @param pu
   */
  private JPAFactory(final String pu) {
    this(pu, CONFIG_FILE);
  }

/**
   * Creates a new JPAFactory object
   *
   * @param pu
   * @param fileName config file name
   */
  private JPAFactory(final String pu, final String fileName) {
    this.pu = pu;
    this.config = fileName;

    init();
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Factory singleton per pu pattern
   *
   * @param pu persistence unit name
   *
   * @return JPAFactory initialized
   */
  public static final JPAFactory getInstance(final String pu) {
    JPAFactory jf = factories.get(pu);

    if (jf == null) {
      jf = new JPAFactory(pu);
      factories.putIfAbsent(pu, jf);
      // to be sure to return the singleton :
      jf = factories.get(pu);
    }

    return jf;
  }

  /**
   * Called on exit (clean up code)
   */
  public static final void onExit() {
    if (! factories.isEmpty()) {
      // clean up :
      JPAFactory jf;

      for (Iterator<JPAFactory> it = factories.values().iterator(); it.hasNext();) {
        jf = it.next();

        if (jf != null) {
          jf.stop();
        }

        it.remove();
      }
    }
  }

  /**
   * Initializes the EntityManagerFactory
   *
   * @throws IllegalStateException
   */
  private void init() {
    if (log.isDebugEnabled()) {
      log.debug("JPAFactory.init : enter : " + this.pu + " with configuration file : " + this.config);
    }

    if (! loadConfigFile()) {
      throw new IllegalStateException("Unable to load Configuration " + this.config + " !");
    }

    try {
      final Map<Object, Object> props = new HashMap<Object, Object>(this.properties);

      if (log.isInfoEnabled()) {
        log.info("JPAFactory.init : properties : " + CollectionUtils.toString(props));
      }

      // adds integrity checker for postgres only :
      final String targetDB = this.properties.getProperty(PersistenceUnitProperties.TARGET_DATABASE);

      if (log.isWarnEnabled()) {
        log.warn("JPAFactory.init : connecting to " + targetDB + " ...");
      }

      if (USE_INTEGRITY_CHECKER) {
        props.put(PersistenceUnitProperties.SESSION_CUSTOMIZER, EnableIntegrityChecker.class.getName());
      }

      this.emf = Persistence.createEntityManagerFactory(this.pu, props);
    } catch (final RuntimeException re) {
      log.error("JPAFactory.init : EntityManagerFactory failure : ", re);
      throw re;
    }

    if (log.isWarnEnabled()) {
      log.warn("JPAFactory.init : done.");
    }

    EntityManager em = null;

    try {
      // Create an EntityManager to check if connection is ready & fails fast.
      if (log.isWarnEnabled()) {
        log.warn("JPAFactory.init : test to create an entityManager instance ...");
      }

      em = getEm();

      if (log.isWarnEnabled()) {
        log.warn("JPAFactory.init : test : OK");
      }
    } finally {
      if (em != null) {
        em.close();
      }
    }

    if (log.isWarnEnabled()) {
      log.warn("JPAFactory.init : exit : OK");
    }
  }

  /**
   * Load PU config file
   *
   * @return true if properties loaded correctly
   */
  private boolean loadConfigFile() {
    InputStream in = null;

    try {
      in = FileUtils.getSystemFileInputStream(this.config + ".properties");

      this.properties = new Properties();
      this.properties.load(in);

      // filter empty strings :
      String k;

      // filter empty strings :
      String s;

      for (final Iterator it = this.properties.keySet().iterator(); it.hasNext();) {
        k = (String) it.next();
        s = this.properties.getProperty(k);

        if (StringUtils.isEmpty(s)) {
          it.remove();
        }
      }

      return true;
    } catch (final IOException ioe) {
      log.error("IO Failure : ", ioe);

      return false;
    } finally {
      FileUtils.closeStream(in);
    }
  }

  /**
   * Stop pattern : closes the EntityManagerFactory
   */
  public void stop() {
    if (getEmf() != null) {
      try {
        getEmf().close();
      } catch (final Exception e) {
        log.error("JPAFactory.stop : failure : ", e);
      }

      this.emf = null;
    }

    this.properties.clear();

    if (log.isWarnEnabled()) {
      log.warn("JPAFactory : closed.");
    }
  }

  /**
   * Returns the persistence Unit ID
   *
   * @return persistence Unit ID
   */
  public String getPu() {
    return pu;
  }

  /**
   * Returns the EntityManagerFactory
   *
   * @return EntityManagerFactory
   */
  public EntityManagerFactory getEmf() {
    return emf;
  }

  /**
   * Creates a new EntityManager from factory
   *
   * @return EntityManager
   */
  public EntityManager getEm() {
    return getEmf().createEntityManager();
  }

  //~ Inner Classes ----------------------------------------------------------------------------------------------------

  /**
   * SessionCustomizer implementation to check Database schema at runtime
   */
  public static final class EnableIntegrityChecker implements SessionCustomizer {
    //~ Methods --------------------------------------------------------------------------------------------------------

    public final void customize(final Session session) {
      session.getIntegrityChecker().checkDatabase();
      session.getIntegrityChecker().setShouldCatchExceptions(true);
      session.getIntegrityChecker().setShouldCheckInstantiationPolicy(true);
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
