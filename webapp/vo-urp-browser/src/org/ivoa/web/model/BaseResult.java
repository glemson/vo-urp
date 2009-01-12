/*
 * BaseResult.java
 * 
 * Author lemson
 * Created on Oct 31, 2008
 */
package org.ivoa.web.model;

public class BaseResult {

  private int resultCode;
  private String error;
  public int getResultCode() {
    return resultCode;
  }
  public void setResultCode(int resultCode) {
    this.resultCode = resultCode;
  }
  public String getError() {
    return error;
  }
  public void setError(String error) {
    this.error = error;
  }
  
}
