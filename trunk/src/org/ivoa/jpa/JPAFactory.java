package org.ivoa.jpa;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.sessions.Session;
import org.ivoa.bean.LogSupport;
import org.ivoa.conf.PropertyHolder;
import org.ivoa.util.CollectionUtils;


/**
 * JPAFactory is an utility class to configure JPA Connection & properties
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class JPAFactory extends LogSupport {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** Table definition checker : seems not working with postgres JDBC driver */
  public static final boolean USE_INTEGRITY_CHECKER = false;
  /** Default config file */
  public static final String CONFIG_FILE = "jpa-config.properties";
  /** all factories */
  private static ConcurrentHashMap<String, JPAFactory> factories = new ConcurrentHashMap<String, JPAFactory>(4);

  //~ Members ----------------------------------------------------------------------------------------------------------

  /** persistence Unit Label */
  private final String pu;
  /** config file name */
  private final String config;
  /** JPA factory */
  private EntityManagerFactory emf = null;

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Creates a new JPAFactory object
   *
   * @param pPU persistence unit name
   */
  private JPAFactory(final String pPU) {
    this(pPU, CONFIG_FILE);
  }

/**
   * Creates a new JPAFactory object
   *
   * @param pPU persistence unit name
   * @param fileName config file name
   */
  private JPAFactory(final String pPU, final String fileName) {
    this.pu = pPU;
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
      if (log.isWarnEnabled()) {
        log.warn("JPAFactory.getInstance : creating new instance for : " + pu);
      }

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
    if (log.isWarnEnabled()) {
        log.warn("JPAFactory.onExit : enter");
    }
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
    factories = null;
    if (log.isWarnEnabled()) {
        log.warn("JPAFactory.onExit : exit");
    }
  }

  /**
   * Initializes the EntityManagerFactory
   */
  private void init() {
    if (log.isDebugEnabled()) {
      log.debug("JPAFactory.init : enter : " + this.pu + " with configuration file : " + this.config);
    }

    final Properties properties = new PropertyHolder(this.config).getProperties();

    try {
      final Map<Object, Object> props = new HashMap<Object, Object>(properties);

      if (log.isInfoEnabled()) {
        log.info("JPAFactory.init : properties : " + CollectionUtils.toString(props));
      }

      // adds integrity checker for postgres only :
      final String targetDB = properties.getProperty(PersistenceUnitProperties.TARGET_DATABASE);

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
   * Stop pattern : closes the EntityManagerFactory
   */
  protected void stop() {
    if (log.isWarnEnabled()) {
      log.warn("JPAFactory.stop : enter : " + this.pu);
    }
    if (getEmf() != null) {
      try {
        getEmf().close();
      } catch (final Exception e) {
        log.error("JPAFactory.stop : failure : ", e);
      }

      this.emf = null;
    }

    if (log.isWarnEnabled()) {
      log.warn("JPAFactory.stop : exit : " + this.pu);
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

    /**
     * Customize the session to add the IntegrityChecker component
     * @param session eclipselink session
     */
    public final void customize(final Session session) {
      session.getIntegrityChecker().checkDatabase();
      session.getIntegrityChecker().setShouldCatchExceptions(true);
      session.getIntegrityChecker().setShouldCheckInstantiationPolicy(true);
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
