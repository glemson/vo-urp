package org.ivoa.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.ivoa.bean.LogSupport;
import org.ivoa.conf.Configuration;
import org.ivoa.conf.RuntimeConfiguration;
import org.ivoa.dm.MetaModelFactory;
import org.ivoa.env.ClassLoaderCleaner;
import org.ivoa.jpa.JPAFactory;
import org.ivoa.util.LogUtil;
import org.ivoa.util.concurrent.ThreadLocalUtils;
import org.ivoa.util.timer.TimerFactory;
import org.ivoa.web.model.CursorQuery;
import org.ivoa.web.model.EntityConfig;
import org.ivoa.web.model.EntityConfigFactory;

/**
 * Facade pattern for the web application
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public class VO_URP_Facade extends LogSupport {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** TODO : Field Description */
  public static final String CTX_KEY = "VO_URP_Facade";
  /** TODO : Field Description */
  public static final int DEF_PAGE_SIZE = 10;
  /** TODO : Field Description */
  private static VO_URP_Facade instance = null;
  /** EntityManager Thread Local (lazy weaving) */
  private static volatile EntityManagerThreadLocal entityLocal = null;

  //~ Members ----------------------------------------------------------------------------------------------------------
  /** TODO : Field Description */
  private EntityConfigFactory ecf;
  /** EntityManager factory */
  private EntityManagerFactory emf;
  /** global Cache (thread safe) */
  @SuppressWarnings("unchecked")
  private Map globalCache = new ConcurrentHashMap();

  //~ Constructors -----------------------------------------------------------------------------------------------------
  /**
   * Constructor
   */
  private VO_URP_Facade() {
    /* no-op */
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------
  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public static final VO_URP_Facade getInstance() {
    return instance;
  }

  /**
   * Creates the facade :
   * Used by ApplicationManager
   *
   * @param ctx application context
   */
  protected static void createInstance(final ServletContext ctx) {
    if (log != null && log.isWarnEnabled()) {
      log.warn("Application is starting ...");
    }

    final VO_URP_Facade facade = new VO_URP_Facade();

    // defines the singleton before initialization :
    // check if really necessary :
    instance = facade;

    facade.prepare();

    // finally add this component to the application context :
    ctx.setAttribute(CTX_KEY, instance);
  }

  /**
   * Release the facade :
   * Used by ApplicationManager
   *
   * @param ctx application context
   */
  protected static void freeInstance(final ServletContext ctx) {
    if (log != null && log.isWarnEnabled()) {
      log.warn("Application is stopping ...");
    }

    // first remove this component from the application context :
    ctx.removeAttribute(CTX_KEY);

    if (instance != null) {
      instance.exit();
    }

    // force GC :
    instance = null;

    // TimerFactory dump :
    if (!TimerFactory.isEmpty() && logB.isWarnEnabled()) {
      logB.warn("TimerFactory : statistics : " + TimerFactory.dumpTimers());
    }

    if (log != null && log.isWarnEnabled()) {
      log.warn("Application is unavailable.");
    }

    try {
      // last one (clear logging) :
      // clean up (GC) :
      ClassLoaderCleaner.clean();

    } catch (final Throwable th) {
      log.error("VO_URP_Facade.freeInstance : fatal error : ", th);
    }
  }

  /**
   * TODO : Method Description
   */
  private void prepare() {
    if (log.isInfoEnabled()) {
      log.info("loading configuration ...");
    }

    Configuration.getInstance();

    try {
      if (log.isInfoEnabled()) {
        log.info("get MetaModelFactory and load model : " + MetaModelFactory.MODEL_FILE);
      }
      MetaModelFactory.getInstance();
    } catch (final IllegalStateException ise) {
      log.error("VO_URP_Facade.prepare : failure : ", ise);
      throw ise;
    }

    if (log.isInfoEnabled()) {
      log.info("get MetaModelFactory : OK");
    }

    this.ecf = EntityConfigFactory.getInstance();

    String jpa_pu = RuntimeConfiguration.get().getJPAPU();

    if (log.isInfoEnabled()) {
      log.info("get JPAFactory for persistence unit " + jpa_pu);
    }

    final JPAFactory jf = JPAFactory.getInstance(jpa_pu);

    if (log.isInfoEnabled()) {
      log.info("get JPAFactory for persistence unit " + jpa_pu + " : OK");
    }

    EntityManager em = null;

    try {
      if (log.isInfoEnabled()) {
        log.info("get EntityManager ...");
      }

      em = jf.getEm();

      if (log.isInfoEnabled()) {
        log.info("get EntityManager : OK");
      }
    } finally {
      if (em != null) {
        em.close();
      }
    }

    this.emf = jf.getEmf();

    // prepare the entityManager thread local :
    entityLocal = new EntityManagerThreadLocal(this.emf);

    ThreadLocalUtils.registerRequestThreadLocal(entityLocal);

    // TimerFactory warmup and reset :
    TimerFactory.resetTimers();

    if (log != null) {
      log.warn("Application is ready.");
    }
  }

  // Exit :
  /**
   * TODO : Method Description
   */
  public void exit() {
    if (log.isInfoEnabled()) {
      log.info("VO_URP_Facade.exit : enter");
    }

    // force GC :
    this.globalCache.clear();

    entityLocal = null;

    // free Session stats Thread :
    SessionMonitor.onExit();

    if (log.isInfoEnabled()) {
      log.info("VO_URP_Facade.exit : exit");
    }
  }

  // Utilities --------------
  /**
   * Return an entity manager associated to the current thread<br/>
   * Do not close it if used by an HTTPServletRequest
   *
   * @see #closeEntityManager()
   *
   * @return value TODO : Value Description
   */
  protected EntityManager createEntityManager() {
    return entityLocal.createValue();
  }

  /**
   * TODO : Method Description
   *
   * Used by :
   * JPARequestListener.requestDestroyed()
   *
   * @see org.ivoa.web.servlet.JPARequestListener#requestDestroyed(javax.servlet.ServletRequestEvent)
   */
  public void closeEntityManager() {
    if (entityLocal != null) {
      entityLocal.releaseValue();
    }
  }

  /**
   * Find an entity given its identifier and its class. Used by FindServlet
   *
   * @param id identifier
   * @param c class of the entity
   *
   * @return value TODO : Value Description
   */
  @SuppressWarnings("unchecked")
  public Object getItem(final Long id, final Class c) {
    final EntityManager em = createEntityManager();

    // close is defered in JPARequestListener
    return em.find(c, id);
  }

  /**
   * TODO : Method Description
   *
   * @param query
   *
   * @return value TODO : Value Description
   */
  public Integer getCount(final String query) {
    final EntityManager em = createEntityManager();

    final Long c = (Long) em.createQuery(query).getSingleResult();

    // close is defered in JPARequestListener
    return Integer.valueOf(c.intValue());
  }

  /**
   * TODO : Method Description
   *
   * @param cq
   */
  private void refreshCursorResults(final CursorQuery cq) {
    List<?> results = null;

    final EntityManager em = createEntityManager();
    final Query q = em.createQuery(cq.getQuery());

    if (cq.isDoPaging()) {
      if (log.isDebugEnabled()) {
        log.debug("refreshCursor : from : " + cq.getStartPos() + " - len : " + cq.getPageSize());
      }

      if (cq.getStartPos() >= 0) {
        q.setFirstResult(cq.getStartPos());
      }

      if (cq.getPageSize() > 0) {
        q.setMaxResults(cq.getPageSize());
      }
    } else {
      if (log.isDebugEnabled()) {
        log.debug("refreshCursor : getall");
      }
    }

    results = q.getResultList();

    if (log.isDebugEnabled()) {
      log.debug("refreshCursor : size : " + results.size());
    }

    // close is defered in JPARequestListener
    cq.setResults(results);
  }

  // CursorQuery
  /**
   * TODO : Method Description
   *
   * @param ec
   *
   * @return value TODO : Value Description
   */
  private Integer getCachedCount(final EntityConfig ec) {
    Integer c = (Integer) getGlobalCache().get(ec.getGlobalCountKey());

    if (c == null) {
      // first time :
      c = getCount(ec.getCountAll());

      if (log.isDebugEnabled()) {
        log.debug("getCount : count : " + c);
      }

    // disable global cache :
    //      getGlobalCache().put(ec.getGlobalCountKey(), c);
    }

    return c;
  }

  /**
   * TODO : Method Description
   *
   * @param ec
   *
   * @return value TODO : Value Description
   */
  public CursorQuery createCursor(final EntityConfig ec) {
    CursorQuery cq = null;

    if (log.isInfoEnabled()) {
      log.info("createCursor : query : " + ec.getFindAll());
    }

    if (ec.isDoPaging()) {
      cq = new CursorQuery(ec.getFindAll(), DEF_PAGE_SIZE, getCachedCount(ec).intValue());
    } else {
      cq = new CursorQuery(ec.getFindAll());
    }

    cq.setDoQuery(ec.isDoQuery());

    return cq;
  }

  /**
   * TODO : Method Description
   *
   * @param ec
   * @param cq
   */
  public void refreshCursor(final EntityConfig ec, final CursorQuery cq) {
    if (cq.isRefreshCount()) {
      // refresh count :
      String query = ec.getCountAll();

      if (cq.getQueryClause() != null) {
        query += cq.getQueryClause();
      }

      if (log.isDebugEnabled()) {
        log.debug("countQuery : " + query);
      }

      cq.setMaxPos(getCount(query).intValue());

      // update Query :
      query = ec.getFindAll();

      if (cq.getQueryClause() != null) {
        query += cq.getQueryClause();
      }

      cq.setQuery(query);
    }

    refreshCursorResults(cq);
  }

  // Specific code :
  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  @SuppressWarnings("unchecked")
  public Map getGlobalCache() {
    return globalCache;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public EntityConfigFactory getEntityConfigFactory() {
    return ecf;
  }


  //~ Inner Classes ----------------------------------------------------------------------------------------------------
  /**
   * This class uses the ThreadLocal pattern to associate an EntityManager to the current thread
   *
   * TODO : manage this as a ManagedThreadLocal to be sure to release the entity manager anyway
   */
  protected static final class EntityManagerThreadLocal extends ThreadLocal<EntityManager> {
    //~ Constants --------------------------------------------------------------------------------------------------------

    /** Logger for this class and subclasses */
    protected static Log logT = LogUtil.getLogger();
    //~ Members ----------------------------------------------------------------------------------------------------------
    /** EntityManager factory */
    private EntityManagerFactory emf;

    //~ Constructors ---------------------------------------------------------------------------------------------------
    /**
     * Protected constructor
     * @param pEmf entity manager factory
     */
    protected EntityManagerThreadLocal(final EntityManagerFactory pEmf) {
      super();
      this.emf = pEmf;
    }

    //~ Methods --------------------------------------------------------------------------------------------------------
    /**
     * Return a new EntityManager for the current thread
     * @return new EntityManager instance using the inner EntityManagerFactory
     */
    protected final EntityManager createValue() {

      EntityManager em = get();

      if (em == null) {
        em = emf.createEntityManager();

        if (logT.isInfoEnabled()) {
          logT.info("doCreateEntityManager : instance created : " + em);
        }
        if (em != null) {
          set(em);
        }
      }
      return em;
    }

    /**
     *
     */
    protected final void releaseValue() {
      final EntityManager em = get();

      if (em != null) {
        // free thread-local first
        remove();

        if (em.isOpen()) {
          if (logT.isInfoEnabled()) {
            logT.info("doCreateEntityManager : instance closed : " + em);
          }

          em.close();
        } else {
          if (logT.isInfoEnabled()) {
            logT.info("doCreateEntityManager : instance already closed : " + em);
          }
        }
      }
    }

    /**
     *
     */
    protected void clear() {
      this.emf = null;
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
