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
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** serial UID for Serializable interface */
  private static final long serialVersionUID = 1L;
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
  private List<?> headers = new ArrayList<Object>();
  /**
   * TODO : Field Description
   */
  private List<?> results;

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Constructor
   */
  public SQLQuery() {
    /* no-op */
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
   * @param pSql 
   */
  public void setSql(final String pSql) {
    this.sql = pSql;
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
   * @param pMaxRows 
   */
  public void setMaxRows(final int pMaxRows) {
    this.maxRows = pMaxRows;
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
   * @param pError 
   */
  public void setError(final String pError) {
    this.error = pError;
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
   * @param pDuration 
   */
  public void setDuration(final long pDuration) {
    this.duration = pDuration;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  @SuppressWarnings("unchecked")
  public List getHeaders() {
    return headers;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  @SuppressWarnings("unchecked")
  public List getResults() {
    return results;
  }

  /**
   * TODO : Method Description
   *
   * @param pResults 
   */
  @SuppressWarnings("unchecked")
  public void setResults(final List pResults) {
    this.results = pResults;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
