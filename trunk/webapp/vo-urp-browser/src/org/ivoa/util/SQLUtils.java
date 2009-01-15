package org.ivoa.util;

import java.util.List;
import javax.persistence.EntityManager;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.Session;
import org.ivoa.conf.RuntimeConfiguration;
import org.ivoa.jpa.JPAFactory;

/**
 * Simple SQL Query executor using eclipseLink
 *
 * @author laurent bourges (voparis)
 */
public class SQLUtils { //extends BaseService {

  public final static int MAX_ROWS_UNLIMITED = -1;
  public final static int QUERY_NO_TIMEOUT = -1;
  
  /**
   * Constructor
   */
  public SQLUtils() {
  }
  
  public static List executeQuery(final EntityManager em, final String sql) {
    return executeQuery(em, sql, MAX_ROWS_UNLIMITED, QUERY_NO_TIMEOUT);
  }  

  public static List executeQuery(final String sql, final int maxRows, final int timeout) {
    EntityManager em = null;
    try {
      final JPAFactory jf = JPAFactory.getInstance(RuntimeConfiguration.getInstance().getJPAPU());
      em = jf.getEm();
      

      return executeQuery(em, sql, maxRows, timeout);

    } finally {
      em.close();
    }
  }  
  
  public static List executeQuery(final EntityManager em, final String sql, final int maxRows, final int timeout) {


    List rows = null;
    
    final JpaEntityManager eem = JpaHelper.getEntityManager(em);

    if (eem != null) {
      final Session s = eem.getSession();
      
      // Returns a List<Record implements Map> :
      final DatabaseCall call = new SQLCall(sql);
      if (maxRows > 0) {
        call.setMaxRows(maxRows);
      }
      if (timeout > 0) {
        call.setQueryTimeout(timeout);
      }
      rows = s.executeSelectingCall(call);
    }

    return rows;
  }
  
}