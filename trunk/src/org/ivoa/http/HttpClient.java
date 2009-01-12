package org.ivoa.http;

import org.apache.commons.logging.Log;

import org.ivoa.util.LogUtil;


/**
 * 
 */
public final class HttpClient {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** logger */
  public static final Log log = LogUtil.getLoggerDev();

  //~ Members ----------------------------------------------------------------------------------------------------------

  /* membres */
  /** http connector : inner instance */
  private HTTPConnector http = null; // http client

  //~ Constructors -----------------------------------------------------------------------------------------------------

  /**
   * Creates a new HttpConnector and initialize XML et HTTP Connector
   */
  public HttpClient() {
    http = new HTTPConnector(); // can fail if library HTTP Client not found
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Reset large buffer
   */
  public void reset() {
    http.reset();
  }

  /**
   * Call http to retrieve a document
   *
   * @param doc url
   *
   * @throws CommunicationException if I/O failed
   */
  public final void retrieveDocument(final HttpDocument doc)
                              throws CommunicationException {
    http.getResponse(doc);

    final int clen = doc.getContentLength();
    double    ms   = 0d;

    if ((ms != 0d) && (clen > 0)) {
      final double ratio = (1000d * clen) / (1024d * ms); // k octet / s

      log.error("HttpClient.retrieveDocument : Transfer ratio (ko/s) : " + ratio + " == " + doc.getUrl());
    }
  }

  /**
   * TODO : Method Description
   *
   * @param doc
   */
  public final void reset(final HttpDocument doc) {
    http.reset(doc);
  }

  /**
   * TODO : Method Description
   *
   * @param strURL
   * @param parentURL
   * @param doHead
   *
   * @return value TODO : Value Description
   *
   * @throws CommunicationException
   */
  public final HttpDocument retrieveHeaders(final String strURL, final String parentURL, final boolean doHead)
                                     throws CommunicationException {
    int testNo = -1;

    final HttpDocument doc = http.getHeaders(strURL, parentURL, doHead);

    if (doc == null) {
      throw new CommunicationException("HTTP_NO_HEADER", "Url problem " + strURL + " not found", null);
    }

    return doc;
  }

  /**
   * Definit les HTTP timeout et debug mode du HTTP Connector
   */
  protected final void defineTimeout() {
    final int timeoutValue = 10000;

    http.defineHttpParams(timeoutValue);
  }

  /**
   * Helper for monitoring purpose
   *
   * @return httpConnector for monitoring purpose
   */
  public final HTTPConnector getHttpConnector() {
    return http;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
