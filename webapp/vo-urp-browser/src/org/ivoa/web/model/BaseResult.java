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
 * @author laurent bourges (voparis) / Gerard Lemson (mpe)
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
   * @param pResultCode 
   */
  public void setResultCode(final int pResultCode) {
    this.resultCode = pResultCode;
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
}
//~ End of file --------------------------------------------------------------------------------------------------------
