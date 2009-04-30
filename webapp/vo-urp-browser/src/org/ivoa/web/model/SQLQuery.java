package org.ivoa.web.model;

import org.ivoa.util.SQLUtils;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;


/**
 * SQL Query bean
 *
 * @author laurent bourges (voparis)
 */
public class SQLQuery implements Serializable {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  private String error;
  /**
   * TODO : Field Description
   */
  private String sql = null;
  /**
   * TODO : Field Description
   */
  private int maxRows = SQLUtils.MAX_ROWS_UNLIMITED;
  /**
   * TODO : Field Description
   */
  private long duration = 0L;
  /**
   * TODO : Field Description
   */
  private List headers = new ArrayList();
  /**
   * TODO : Field Description
   */
  private List results;

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Constructor
   */
  public SQLQuery() {
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   */
  public void reset() {
    this.error = null;
    this.duration = 0L;
    this.headers.clear();
    this.results = null;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String getSql() {
    return sql;
  }

  /**
   * TODO : Method Description
   *
   * @param sql 
   */
  public void setSql(final String sql) {
    this.sql = sql;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public int getMaxRows() {
    return maxRows;
  }

  /**
   * TODO : Method Description
   *
   * @param maxRows 
   */
  public void setMaxRows(final int maxRows) {
    this.maxRows = maxRows;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String getError() {
    return error;
  }

  /**
   * TODO : Method Description
   *
   * @param error 
   */
  public void setError(final String error) {
    this.error = error;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public long getDuration() {
    return duration;
  }

  /**
   * TODO : Method Description
   *
   * @param duration 
   */
  public void setDuration(final long duration) {
    this.duration = duration;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public List getHeaders() {
    return headers;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public List getResults() {
    return results;
  }

  /**
   * TODO : Method Description
   *
   * @param results 
   */
  public void setResults(final List results) {
    this.results = results;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
