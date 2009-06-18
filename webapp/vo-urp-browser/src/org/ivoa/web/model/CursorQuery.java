package org.ivoa.web.model;

import java.io.Serializable;

import java.util.List;

/**
 * CursorQuery.java Created on 22 août 2007, 17:22:27
 * 
 * @author laurent
 */
public class CursorQuery implements Serializable {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** serial UID for Serializable interface */
  private static final long serialVersionUID = 1L;

  /**
   * TODO : Field Description
   */
  public static final int FIRST_POS = 0;

  /**
   * TODO : Field Description
   */
  public static final int NULL_INT = -1;

  // ~ Members
  // ----------------------------------------------------------------------------------------------------------

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
  private String queryClause;

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
  private transient List<?> results = null;

  // ~ Constructors
  // -----------------------------------------------------------------------------------------------------

  /**
   * Simple Query without paging behaviour
   * 
   * @param pQuery query
   */
  public CursorQuery(final String pQuery) {
    this(pQuery, NULL_INT, Integer.MAX_VALUE);
  }

  /**
   * Cursor Query with paging behaviour
   * 
   * @param pQuery query
   * @param pPageSize page size
   * @param pMax max position
   */
  public CursorQuery(final String pQuery, final int pPageSize, final int pMax) {
    this.query = pQuery;
    this.maxPos = pMax;
    this.pageSize = pPageSize;
    setStartPos(FIRST_POS);
  }

  // ~ Methods
  // ----------------------------------------------------------------------------------------------------------

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
  public List<?> getResults() {
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
   * @param pRefresh
   */
  public void setRefresh(final boolean pRefresh) {
    this.refresh = pRefresh;
  }

  /**
   * Force Refresh Count
   * 
   * @param pRefresh
   */
  public void setRefreshCount(final boolean pRefresh) {
    this.refreshCount = pRefresh;
  }

  /**
   * Changing start Position (paging)
   * 
   * @param pStartPos
   */
  public void setStartPos(final int pStartPos) {
    if ((this.startPos != pStartPos) && (pStartPos < this.maxPos) && (pStartPos >= FIRST_POS)) {
      this.startPos = pStartPos;
      setRefresh(true);
    }
  }

  /**
   * TODO : Method Description
   * 
   * @param pMaxPos
   */
  public void setMaxPos(final int pMaxPos) {
    this.maxPos = pMaxPos;
  }

  /**
   * TODO : Method Description
   * 
   * @param q
   */
  public void setQueryClause(final String q) {
    if ((q != null) && !q.equals(queryClause)) {
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
   * @param pResults results
   */
  public void setResults(final List<?> pResults) {
    this.results = pResults;
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
        maxPos = pResults.size();
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
   * @param pDoQuery
   */
  public void setDoQuery(final boolean pDoQuery) {
    this.doQuery = pDoQuery;
  }
}
// ~ End of file
// --------------------------------------------------------------------------------------------------------
