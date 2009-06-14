package org.ivoa.web.model;

import java.io.Serializable;

import java.util.List;


/**
 * CursorQuery.java
 *
 * Created on 22 août 2007, 17:22:27
 *
 * @author laurent
 */
public class CursorQuery implements Serializable {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  public static final int FIRST_POS = 0;
  /**
   * TODO : Field Description
   */
  public static final int NULL_INT = -1;

  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  private boolean doQuery = false;

  // current complete query :
  /**
   * TODO : Field Description
   */
  private String query;

  // condition part of query :
  /**
   * TODO : Field Description
   */
  private String    queryClause;
  /**
   * TODO : Field Description
   */
  private final int pageSize;

  // modified fields :
  /**
   * TODO : Field Description
   */
  private boolean refresh;
  /**
   * TODO : Field Description
   */
  private boolean refreshCount;

  // positions :
  /**
   * TODO : Field Description
   */
  private int maxPos;
  /**
   * TODO : Field Description
   */
  private int startPos = NULL_INT;
  /**
   * TODO : Field Description
   */
  private int prevPos = NULL_INT;
  /**
   * TODO : Field Description
   */
  private int nextPos = NULL_INT;
  /** transient to avoid serialization issues */
  private transient List results = null;

  //~ Constructors -----------------------------------------------------------------------------------------------------

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

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public boolean isDoPaging() {
    return this.pageSize != NULL_INT;
  }

  // Getters :  
  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String getQuery() {
    return query;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String getQueryClause() {
    return queryClause;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public int getMaxPos() {
    return maxPos;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public int getPageSize() {
    return pageSize;
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
   * @return value TODO : Value Description
   */
  public int getStartPos() {
    return startPos;
  }

  // Navigation :  
  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public int getPrevPos() {
    return prevPos;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public int getNextPos() {
    return nextPos;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public boolean isRefresh() {
    return refresh || (results == null);
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public boolean isRefreshCount() {
    return refreshCount;
  }

  // Setters :
  /**
   * Force Refresh
   *
   * @param refresh
   */
  public void setRefresh(final boolean refresh) {
    this.refresh = refresh;
  }

  /**
   * Force Refresh Count
   *
   * @param refresh
   */
  public void setRefreshCount(final boolean refresh) {
    this.refreshCount = refresh;
  }

  /**
   * Changing start Position (paging)
   *
   * @param startPos
   */
  public void setStartPos(final int startPos) {
    if ((this.startPos != startPos) && (startPos < this.maxPos) && (startPos >= FIRST_POS)) {
      this.startPos = startPos;
      setRefresh(true);
    }
  }

  /**
   * TODO : Method Description
   *
   * @param maxPos 
   */
  public void setMaxPos(final int maxPos) {
    this.maxPos = maxPos;
  }

  /**
   * TODO : Method Description
   *
   * @param q 
   */
  public void setQueryClause(final String q) {
    if ((q != null) && ! q.equals(queryClause)) {
      queryClause = q;
      startPos = FIRST_POS;
      maxPos = Integer.MAX_VALUE;

      setRefreshCount(true);
      setRefresh(true);
    }
  }

  /**
   * TODO : Method Description
   *
   * @param q 
   */
  public void setQuery(final String q) {
    query = q;
  }

  /**
   * Called when query gives results
   *
   * @param results
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
  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public boolean isDoQuery() {
    return doQuery;
  }

  /**
   * TODO : Method Description
   *
   * @param doQuery 
   */
  public void setDoQuery(final boolean doQuery) {
    this.doQuery = doQuery;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
