/*
 * BaseResult.java
 *
 * Author lemson
 * Created on Oct 31, 2008
 */
package org.ivoa.web.model;

/**
 * TODO : Class Description
 *
 * @author $author$
  */
public class BaseResult {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  private int    resultCode;
  /**
   * TODO : Field Description
   */
  private String error;

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public int getResultCode() {
    return resultCode;
  }

  /**
   * TODO : Method Description
   *
   * @param resultCode 
   */
  public void setResultCode(final int resultCode) {
    this.resultCode = resultCode;
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
}
//~ End of file --------------------------------------------------------------------------------------------------------
