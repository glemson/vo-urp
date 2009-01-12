package org.ivoa.web.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.ivoa.util.SQLUtils;

/**
 * SQL Query bean
 *
 * @author laurent bourges (voparis)
 */
public class SQLQuery implements Serializable {
  
  private String error;
  
  private String sql = null;
  
  private int maxRows = SQLUtils.MAX_ROWS_UNLIMITED;
  
  private long duration = 0l;

  private List headers = new ArrayList();
  
  private List results;

  /**
   * Constructor
   */
  public SQLQuery() {
  }
  public void reset() {
    this.error = null;
    this.duration = 0l;
    this.headers.clear();
    this.results = null;
  }

  public String getSql() {
    return sql;
  }

  public void setSql(String sql) {
    this.sql = sql;
  }

  public int getMaxRows() {
    return maxRows;
  }

  public void setMaxRows(int maxRows) {
    this.maxRows = maxRows;
  }

  
  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public long getDuration() {
    return duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public List getHeaders() {
    return headers;
  }

  public List getResults() {
    return results;
  }

  public void setResults(List results) {
    this.results = results;
  }

  
}
