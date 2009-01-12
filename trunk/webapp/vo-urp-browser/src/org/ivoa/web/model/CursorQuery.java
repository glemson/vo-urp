/*
 * CursorQuery.java
 *
 * Created on 22 août 2007, 17:22:27
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ivoa.web.model;

import java.io.Serializable;
import java.util.List;


/**
 *
 * @author laurent
 */
public class CursorQuery implements Serializable {
  
  // static :
  public final static int FIRST_POS = 0;
  public final static int NULL_INT = -1;

  // members :
  private boolean doQuery = false;
  
  // current complete query :
  private String query;
  
  // condition part of query :
  private String queryClause;

  private final int pageSize;

  // modified fields :
  private boolean refresh;
  private boolean refreshCount;
  
  // positions :
  private int maxPos;
  private int startPos = NULL_INT;
  private int prevPos = NULL_INT;
  private int nextPos = NULL_INT;

  /** transient to avoid serialization issues */
  private transient List results = null;

  /**
   * Simple Query without paging behaviour
   */ 
  public CursorQuery(final String query) {
    this(query, NULL_INT, Integer.MAX_VALUE);
  }

  /**
   * Cursor Query with paging behaviour
   */ 
  public CursorQuery(final String query, final int pageSize, final int max) {
    this.query = query;
    this.maxPos = max;
    this.pageSize = pageSize;
    setStartPos(FIRST_POS);
  }

  public boolean isDoPaging() {
    return this.pageSize != NULL_INT;
  }
  
// Getters :  
  
  public String getQuery() {
    return query;
  }

  public String getQueryClause() {
    return queryClause;
  }

  public int getMaxPos() {
    return maxPos;
  }

  public int getPageSize() {
    return pageSize;
  }

  public List getResults() {
    return results;
  }

  public int getStartPos() {
    return startPos;
  }

// Navigation :  
  
  public int getPrevPos() {
    return prevPos;
  }
  
  public int getNextPos() {
    return nextPos;
  }
  
  public boolean isRefresh() {
    return refresh || results == null;
  }

  public boolean isRefreshCount() {
    return refreshCount;
  }

// Setters :
  
  /**
   * Force Refresh
   */ 
  public void setRefresh(boolean refresh) {
    this.refresh = refresh;
  }
  
  /**
   * Force Refresh Count
   */ 
  public void setRefreshCount(boolean refresh) {
    this.refreshCount = refresh;
  }

  /**
   * Changing start Position (paging)
   */ 
  public void setStartPos(final int startPos) {
    if (this.startPos != startPos && startPos < this.maxPos && startPos >= FIRST_POS) {
      this.startPos = startPos;
      setRefresh(true);
    }
  }

  public void setMaxPos(int maxPos) {
    this.maxPos = maxPos;
  }

  public void setQueryClause(final String q) {
    if (q != null && !q.equals(queryClause)) {
      queryClause = q;
      startPos = FIRST_POS;
      maxPos = Integer.MAX_VALUE;
      
      setRefreshCount(true);
      setRefresh(true);
    }
  }

  public void setQuery(final String q) {
    query = q;
  }

  /**
   * Called when query gives results
   */
  public void setResults(final List results) {
    this.results = results;
    setRefresh(false);
    if (isDoPaging()) {
      prevPos = getStartPos() - getPageSize();
      if (prevPos < FIRST_POS) {
        prevPos = NULL_INT;
      }
  
      nextPos = getStartPos() + getPageSize();
      if (nextPos >= getMaxPos()) {
        nextPos = NULL_INT;
      }
    } else {
      if (maxPos == Integer.MAX_VALUE) {
        // first time : cached :
        maxPos = results.size();
      }
    }
  }

// Getter - setter :
  
  public boolean isDoQuery() {
    return doQuery;
  }

  public void setDoQuery(boolean doQuery) {
    this.doQuery = doQuery;
  }
  
}
