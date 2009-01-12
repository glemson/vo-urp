package org.ivoa.http;

import org.apache.commons.httpclient.HttpMethodBase;


/**
 * Data Bean for Http Connector & DownloadLink
 */
public final class HttpDocument {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /** url document to load */
  private final String url;

  /** parent url document to load */
  private String parentUrl;

  /** method Http : clear it absolutely ! */
  private transient HttpMethodBase method;

  /** http status code given by loadHeader */
  private int status = -1;

  /** contentLength associated with the returned document */
  private int contentLength = -1;

  /** the mimeType associated with the returned document */
  private String contentType = null;

  /** Last-Modified header */
  private long last = 0L;

  /** the byte version of document */
  private byte[] binary = null; // document retrieved by HTTP

  //~ Constructors -----------------------------------------------------------------------------------------------------

  /**
   * new Document for that url
   *
   * @param pMethod method Http
   * @param pUrl document to load
   */
  public HttpDocument(final HttpMethodBase pMethod, final String pUrl) {
    this.method = pMethod;
    this.url = pUrl;
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Return the mimeType associated with the returned document
   *
   * @return a the mime-type as a String
   */
  public String getContentType() {
    return this.contentType;
  }

  /**
   * Defines the mimeType associated with the returned document
   *
   * @param pContentType the mime-type as a String
   */
  public void setContentType(final String pContentType) {
    this.contentType = pContentType;
  }

  /**
   * Return contentLength
   *
   * @return integer
   */
  public int getContentLength() {
    return this.contentLength;
  }

  /**
   * Defines contentLength
   *
   * @param contentLen
   */
  public void setContentLength(final int contentLen) {
    this.contentLength = contentLen;
  }

  /**
   * returns http status code
   *
   * @return http status
   */
  public int getStatus() {
    return status;
  }

  /**
   * Defines http status code
   *
   * @param pStatus
   */
  public void setStatus(final int pStatus) {
    this.status = pStatus;
  }

  /**
   * returns method Http : can be null
   *
   * @return internal http method
   */
  public HttpMethodBase getMethod() {
    return method;
  }

  /**
   * method Http clear it !
   *
   * @param pMethod
   */
  public void setMethod(final HttpMethodBase pMethod) {
    this.method = pMethod;
  }

  /**
   * Return the byte version of document
   *
   * @return binary byte buffer of the document downloaded
   */
  public byte[] getBinary() {
    return binary;
  }

  /**
   * Defines the byte version of document
   *
   * @param pBinary byte buffer of the document downloaded
   */
  public void setBinary(final byte[] pBinary) {
    this.binary = pBinary;
  }

  /**
   * returns Last-Modified header
   *
   * @return value TODO : Value Description
   */
  public long getLast() {
    return last;
  }

  /**
   * Defines Last-Modified header
   *
   * @param pLast
   */
  public void setLast(final long pLast) {
    this.last = pLast;
  }

  /**
   * Donne l'url du document to load
   *
   * @return url to load
   */
  public final String getUrl() {
    return url;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String getParentUrl() {
    return parentUrl;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
