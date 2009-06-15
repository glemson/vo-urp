package org.ivoa.service;

import org.ivoa.conf.RuntimeConfiguration;

import org.ivoa.dm.MetaModelFactory;

import org.ivoa.env.ClassLoaderCleaner;

import org.ivoa.jpa.JPAFactory;

import org.ivoa.util.CollectionUtils;

import org.ivoa.web.model.CursorQuery;
import org.ivoa.web.model.EntityConfig;
import org.ivoa.web.model.EntityConfigFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import javax.servlet.ServletContext;
import org.ivoa.bean.LogSupport;
import org.ivoa.conf.Configuration;

/**
 * Facade pattern for the web application
 *
 * @author laurent
 */
public class VO_URP_Facade extends LogSupport {
    //~ Constants --------------------------------------------------------------------------------------------------------

    /** TODO : Field Description */
    public static final String CTX_KEY = "VO_URP_Facade";
    /** TODO : Field Description */
    public static final int DEF_PAGE_SIZE = 10;
    /** TODO : Field Description */
    private static VO_URP_Facade instance = null;
    //~ Members ----------------------------------------------------------------------------------------------------------
    /** TODO : Field Description */
    private EntityConfigFactory ecf;
    /** EntityManager factory */
    private EntityManagerFactory emf;
    /** EntityManager Thread Local (lazy weaving) */
    private ThreadLocal<EntityManager> threadLocal;

    // global Cache (thread safe) :
    /** TODO : Field Description */
    private Map globalCache = new ConcurrentHashMap();

    //~ Constructors -----------------------------------------------------------------------------------------------------
    /**
     * Constructor
     */
    private VO_URP_Facade() {
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

        if (log != null && log.isWarnEnabled()) {
            log.warn("Application is unavailable.");
        }

        // last one (clear logging) :
        ClassLoaderCleaner.clean();
    }

    /**
     * TODO : Method Description
     */
    private void prepare() {
        if (log.isInfoEnabled()) {
            log.info("loading configuration ...");
        }

        Configuration.getInstance();
        RuntimeConfiguration.getInstance();

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
            log.info("DBTests.testJAXB : get MetaModelFactory : OK");
        }

        this.ecf = EntityConfigFactory.getInstance();

        String jpa_pu = RuntimeConfiguration.getInstance().getJPAPU();

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
 
        this.threadLocal = new ThreadLocal<EntityManager>();

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
        this.threadLocal = null;
        this.ecf = null;
        this.emf = null;

        // free Session stats Thread :
        SessionMonitor.onExit();

        EntityConfigFactory.onExit();

        if (log.isInfoEnabled()) {
            log.info("VO_URP_Facade.exit : exit");
        }
    }

    // Utilities --------------
    /**
     * TODO : Method Description
     *
     * @return value TODO : Value Description
     */
    public EntityManager createEntityManager() {
        EntityManager em = this.threadLocal.get();

        if (em == null) {
            em = doCreateEntityManager();
            if (em != null) {
                threadLocal.set(em);
            }
        }

        return em;
    }

    /**
     * TODO : Method Description
     *
     * @return value TODO : Value Description
     */
    private EntityManager doCreateEntityManager() {
        final EntityManager em = emf.createEntityManager();

        if (log.isInfoEnabled()) {
            log.info("doCreateEntityManager : instance created : " + em);
        }

        return em;
    }

    /**
     * TODO : Method Description
     *
     * Used by :
     * JPARequestListener.requestDestroyed()
     */
    public void closeEntityManager() {
        if (this.threadLocal != null) {
            final EntityManager em = threadLocal.get();

            if (em != null) {
                // free thread-local first
                threadLocal.remove();

                if (em.isOpen()) {
                    if (log.isInfoEnabled()) {
                        log.info("doCreateEntityManager : instance closed : " + em);
                    }

                    em.close();
                } else {
                    if (log.isInfoEnabled()) {
                        log.info("doCreateEntityManager : instance already closed : " + em);
                    }
                }
            }
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

        return em.find(c, id);
    // close is defered in JPARequestListener
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

        return Integer.valueOf(c.intValue());
    // close is defered in JPARequestListener
    }

    /**
     * TODO : Method Description
     *
     * @param cq
     */
    private void refreshCursorResults(final CursorQuery cq) {
        List results = null;

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

        cq.setResults(results);
    // close is defered in JPARequestListener
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

            cq.setMaxPos(getCount(query));

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
}
//~ End of file --------------------------------------------------------------------------------------------------------
