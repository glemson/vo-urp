package org.ivoa.http;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NoHttpResponseException;

import org.apache.commons.httpclient.cookie.CookiePolicy;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;

import org.apache.commons.logging.Log;

import org.ivoa.util.LogUtil;
import org.ivoa.util.TypeWrapper;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * <br> Description : Client HTTP Client HTTP base sur Apache Jakarta HTTP Client 3.0 capable de : <br>
 * - HTTP Post avec fichier joint <br>
 * - HTTP Get avec fichier joint <br>
 */
public final class HTTPConnector {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** logger */
  public static final Log log = LogUtil.getLoggerDev();
  /** traces Http de debuggage */
  private static final boolean DO_HTTP_DEBUG = false;
  /** traces Http de monitoring */
  private static final boolean DO_HTTP_MONITOR = true;
  /** TODO : Field Description */
  private static final int MONITOR_COUNT = 20;
  /** TODO : Field Description */
  private static final int MONITOR_RETAIN_COUNT = 15;
  /** TODO : Field Description */
  public static final int RETRY_COUNT = 3;
  /** header content type */
  protected static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
  /** header content length */
  protected static final String HTTP_HEADER_CONTENT_LENGTH = "Content-Length";
  /** header last modified */
  protected static final String HTTP_HEADER_LAST_MODIFIED = "Last-Modified";
  /** UTF-8 encoding */
  protected static final String HTTP_DEFAULT_ENCODING = "text/xml; charset=UTF-8"; // encoding
  /** us calendar for lastModified */
  protected static final Calendar usCalendar;

  static {
    // can not avoid : usefull to set TimeZone FR
    usCalendar = Calendar.getInstance(Locale.US);
    usCalendar.setTimeZone(TimeZone.getTimeZone("FR"));
  }

  /** default http timeout */
  protected static final int HTTP_DEFAULT_TIMEOUT = 30; // 10s
  /** default buffer file size */
  private static final int IO_BUFFER_SIZE = 16384;
  /** big file size for progressive stats */
  private static final int BIG_FILE = IO_BUFFER_SIZE * 4;
  /** default buffer file size */
  private static final int DEFAULT_BUFFER_SIZE = 524288; // 512 ko
  /** http client singleton */
  protected static volatile HttpClient httpclient = null; // shared HTTP Client library

  //~ Members ----------------------------------------------------------------------------------------------------------

  // membres :
  /** fixed byte buffer */
  protected byte[] buff = new byte[IO_BUFFER_SIZE];
  /** growing buffer */
  protected ByteBufferStream out = null;
  /** http timeout */
  protected int HTTP_TIMEOUT = -1;

  /* stats */
  /** TODO : Field Description */
  protected List<HttpState> currentOps = null;
  /** TODO : Field Description */
  protected List<HttpState> ops = null;
  /** TODO : Field Description */
  private AtomicInteger opsId = null;

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Creates a new HTTPConnector object avec les timeouts par defaut et gestion du multithreading
   */
  public HTTPConnector() {
    if (httpclient == null) {
      // Debug HttpClient logs :
      if (DO_HTTP_DEBUG) {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "debug");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "debug");
      }

      httpclient = new HttpClient(new MultiThreadedHttpConnectionManager());

      final HttpClientParams params = httpclient.getParams();

      params.setCookiePolicy(CookiePolicy.NETSCAPE);
      params.setParameter(HttpMethodParams.RETRY_HANDLER, new MyHttpMethodRetryHandler(RETRY_COUNT));

      defineHttpParams(HTTP_DEFAULT_TIMEOUT);
    }

    if (DO_HTTP_MONITOR && (MONITOR_COUNT > 0)) {
      ops = new ArrayList<HttpState>(MONITOR_COUNT);
      currentOps = new ArrayList<HttpState>(MONITOR_COUNT);
      opsId = new AtomicInteger(0);
    }
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Clears large buffers : up to file size !
   */
  public void reset() {
    if (log.isDebugEnabled()) {
      log.debug("HTTPConnector.reset : done");
    }

    out = null;

    if (DO_HTTP_MONITOR) {
      ops.clear();
      currentOps.clear();
    }
  }

  /**
   * definit les timeouts si valeur diffente de l'actuelle
   *
   * @param timeout
   */
  public final void defineHttpParams(final int timeout) {
    if (timeout != HTTP_TIMEOUT) {
      HTTP_TIMEOUT = timeout * 1000;

      final HttpConnectionManagerParams httpParams = httpclient.getHttpConnectionManager().getParams();

      httpParams.setDefaultMaxConnectionsPerHost(20);
      httpParams.setMaxTotalConnections(50);
      httpParams.setConnectionTimeout(HTTP_TIMEOUT);
      httpParams.setSoTimeout(HTTP_TIMEOUT);
      httpParams.setStaleCheckingEnabled(true);
    }
  }

  /**
   * Envoie la requete HTTP
   *
   * @param method objet de requete HTTP
   *
   * @throws CommunicationException si erreur I/O ou HTTP
   */
  protected final void sendRequest(final HttpMethodBase method)
                            throws CommunicationException {
    if (log.isDebugEnabled()) {
      log.debug("HTTPConnector.sendRequest : before ");
    }

    try {
      httpclient.executeMethod(method);
    } catch (final HttpException he) {
      method.releaseConnection();
      throw new CommunicationException("kMsgErrorHTTPConnection", "HTTP IO failure", he);
    } catch (final IOException ioe) {
      method.releaseConnection();
      throw new CommunicationException("kMsgErrorHTTPConnection", "HTTP Connection failure", ioe);
    }

    if (log.isDebugEnabled()) {
      log.debug("HTTPConnector.sendRequest : done ");
    }
  }

  /**
   * TODO : Method Description
   *
   * @param op
   * @param msg
   *
   * @return value TODO : Value Description
   */
  public synchronized HttpState startOperation(final int op, final String msg) {
    final int size = currentOps.size();

    if (size > MONITOR_COUNT) {
      // remove first :
      currentOps = currentOps.subList(size - MONITOR_RETAIN_COUNT, size);
    }

    final HttpState hs = new HttpState(opsId.incrementAndGet(), op, msg);

    currentOps.add(hs);

    return hs;
  }

  /**
   * TODO : Method Description
   *
   * @param hs
   * @param clen
   * @param msg
   */
  public synchronized void stopOperation(final HttpState hs, final int clen, final String msg) {
    if (hs != null) {
      hs.setTime(((System.nanoTime() - hs.getStartNanos()) / 1000000L));
      hs.setClen(clen);
      hs.setRatio(100f);
      hs.setMsgOut(msg);

      final int size = ops.size();

      if (size > MONITOR_COUNT) {
        // remove first :
        ops = ops.subList(size - MONITOR_RETAIN_COUNT, size);
      }

      ops.add(hs);
      currentOps.remove(hs);
    }
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public synchronized List getOperations() {
    return new ArrayList<HttpState>(ops);
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public synchronized List<HttpState> getCurrentOperations() {
    return new ArrayList<HttpState>(currentOps);
  }

  /**
   * getHeaders
   *
   * @param url String
   * @param parentURL
   * @param doHead
   *
   * @return HttpDocument
   *
   * @throws CommunicationException
   */
  public final HttpDocument getHeaders(final String url, final String parentURL, final boolean doHead)
                                throws CommunicationException {
    if (log.isDebugEnabled()) {
      log.debug("HTTPConnector.getHeaders  : URL : " + url);
    }

    HttpMethodBase method = null;

    if (doHead) {
      method = new HeadMethod(url);
    } else {
      method = new GetMethod(url);
    }

    if (parentURL != null) {
      method.setRequestHeader("referer", parentURL);
    }

    HttpDocument doc = null;

    HttpState    hs = null;

    try {
      if (DO_HTTP_MONITOR) {
        hs = startOperation((doHead) ? HttpState.OP_HEADER : HttpState.OP_GET, url);
      }

      sendRequest(method); // can throw CommunicationException

      doc = new HttpDocument(method, url);

      extractHeader(doc);
    } finally {
      if (DO_HTTP_MONITOR) {
        if (doc != null) {
          stopOperation(hs, doc.getContentLength(), "last : " + doc.getLast() + " ctype : " + doc.getContentType());
        } else {
          stopOperation(hs, 0, "call failure ");
        }
      }
    }

    if (log.isDebugEnabled()) {
      log.debug("HTTPConnector.getHeaders  : exit");
    }

    return doc;
  }

  /**
   * TODO : Method Description
   *
   * @param doc
   *
   * @throws CommunicationException
   */
  public final void getResponse(final HttpDocument doc)
                         throws CommunicationException {
    if (log.isDebugEnabled()) {
      log.debug("HTTPConnector.getResponse  : enter");
    }

    extractResponse(doc);

    if (log.isDebugEnabled()) {
      log.debug("HTTPConnector.getResponse  : exit");
    }
  }

  /**
   * TODO : Method Description
   *
   * @param doc
   */
  public final void reset(final HttpDocument doc) {
    if (log.isDebugEnabled()) {
      log.debug("HTTPConnector.reset  : enter");
    }

    if (doc != null) {
      final HttpMethodBase method = doc.getMethod();

      doc.setMethod(null);

      // Release current connection to the connection pool once you are done
      method.releaseConnection();
    }

    if (log.isDebugEnabled()) {
      log.debug("HTTPConnector.reset  : exit");
    }
  }

  /**
   * Recupere la reponse de la requete HTTP
   *
   * @param doc objet de requete HTTP
   */
  protected final void extractHeader(final HttpDocument doc) {
    final HttpMethodBase method = doc.getMethod();
    final int            status = method.getStatusCode();

    doc.setStatus(status);

    if (log.isDebugEnabled()) {
      log.debug("HTTPConnector.extractHeader : status code : " + status);
    }

    final Header[] headers = method.getResponseHeaders();

    if (headers != null) {
      for (int i = 0, size = headers.length; i < size; i++) {
        if (log.isDebugEnabled()) {
          log.debug("header " + headers[i].getName() + " : " + headers[i].getValue());
        }
      }
    }

    Header header = method.getResponseHeader(HTTP_HEADER_CONTENT_TYPE);

    if (header != null) {
      doc.setContentType(header.getValue());

      if (log.isDebugEnabled()) {
        log.debug("HTTPConnector.extractHeader : content type : " + doc.getContentType());
      }
    }

    header = method.getResponseHeader(HTTP_HEADER_CONTENT_LENGTH);

    if (header != null) {
      try {
        final int len = Integer.parseInt(header.getValue());

        if (log.isDebugEnabled()) {
          log.debug("HTTPConnector.extractHeader : content-length : " + len);
        }

        if (len > 0) {
          doc.setContentLength(len);
        }
      } catch (final NumberFormatException nfe) {
        log.error("HTTPConnector.extractHeader : parse content-length failure : ", nfe);
      }
    }

    header = method.getResponseHeader(HTTP_HEADER_LAST_MODIFIED);

    if (header != null) {
      final Date usDate = TypeWrapper.parseInternationalFormat(header.getValue());

      if (usDate != null) {
        usCalendar.setTime(usDate);

        final long lastModified = usCalendar.getTime().getTime();

        if (log.isDebugEnabled()) {
          log.debug("HTTPConnector.extractHeader : Last-Modified : " + usCalendar.getTime());
        }

        doc.setLast(lastModified);
      }
    }
  }

  /**
   * TODO : Method Description
   *
   * @param doc
   *
   * @throws CommunicationException
   */
  protected final void extractResponse(final HttpDocument doc)
                                throws CommunicationException {
    HttpMethodBase method = doc.getMethod();

    if (! (method instanceof GetMethod)) {
      method = new GetMethod(doc.getUrl());

      if (log.isDebugEnabled()) {
        log.debug("HTTPConnector.extractResponse  : send Get Request : " + doc.getUrl());
      }

      HttpState hs = null;

      if (DO_HTTP_MONITOR) {
        hs = startOperation(HttpState.OP_GET, doc.getUrl());
      }

      try {
        sendRequest(method); // can throw CommunicationException
      } finally {
        if (DO_HTTP_MONITOR) {
          stopOperation(hs, doc.getContentLength(), "GET done.");
        }
      }

      if (log.isDebugEnabled()) {
        log.debug("HTTPConnector.extractResponse  : sendRequest finished");
      }
    }

    if (doc.getStatus() != HttpStatus.SC_OK) {
      if (log.isDebugEnabled()) {
        log.debug("HTTPConnector.extractResponse : status code : " + doc.getStatus());
      }

      doc.setMethod(null);

      // Release current connection to the connection pool once you are done
      method.releaseConnection();

      return; // server response problem
    }

    HttpState hs = null;

    if (DO_HTTP_MONITOR) {
      hs = startOperation(HttpState.OP_GET_BODY, doc.getUrl());
      hs.setClen(doc.getContentLength());
    }

    final int clen = doc.getContentLength();

    if (out == null) {
      if ((clen > 0) && (clen > DEFAULT_BUFFER_SIZE)) {
        out = new ByteBufferStream(clen);
      } else {
        out = new ByteBufferStream(DEFAULT_BUFFER_SIZE);
      }
    } else {
      out.reset();

      if (clen > 0) {
        if (log.isInfoEnabled()) {
          log.info("HTTPConnector.extractResponse : ensureCapacity : " + clen);
        }

        out.ensureCapacity(clen);
      }
    }

    final int progressive = ((clen == -1) || (clen > BIG_FILE)) ? BIG_FILE : (-1);

    byte[]    b  = null;
    InputStream in = null;

    try {
      if (log.isInfoEnabled()) {
        log.info("HTTPConnector.extractResponse : before download ");
      }

      in = new BufferedInputStream(method.getResponseBodyAsStream(), IO_BUFFER_SIZE);

      float pos = 0f;
      int   sum = 0;
      int   len;

      while ((len = in.read(buff)) > 0) {
        out.write(buff, 0, len);

        if (progressive > 0) {
          sum += len;

          if (sum > progressive) {
            pos += sum;

            if (DO_HTTP_MONITOR) {
              if (clen == -1) {
                // 20% arbitraire
                hs.setRatio(20f);
              } else {
                hs.setRatio((100f * pos) / clen);
              }
            }

            if (log.isInfoEnabled()) {
              log.info("HTTPConnector.extractResponse : downloaded part  : " + pos);

              if (clen > 0) {
                log.info("HTTPConnector.extractResponse : downloaded ratio = " + (pos / clen));
              }
            }

            sum = 0;
          }
        }
      }

      if (log.isInfoEnabled()) {
        log.info("HTTPConnector.extractResponse : after download ");
      }

      b = out.toByteArray();

      if (log.isInfoEnabled()) {
        log.info("HTTPConnector.extractResponse : after toByteArray ");
      }
    } catch (final IOException ioe) {
      log.error("HTTPConnector.extractResponse : content binary failure ", ioe);
    } finally {
      try {
        in.close();
      } catch (final IOException ioe) {
        log.error("HTTPConnector.extractResponse : content binary failure ", ioe);
      }

      doc.setMethod(null);

      // Release current connection to the connection pool once you are done
      method.releaseConnection();
    }

    if ((doc.getContentLength() == -1) && (b.length > 0)) {
      doc.setContentLength(b.length);
    }

    if (log.isDebugEnabled()) {
      log.debug("HTTPConnector.extractResponse : binary length : " + b.length);
    }

    doc.setBinary(b);

    if (DO_HTTP_MONITOR) {
      stopOperation(hs, doc.getContentLength(), "last : " + doc.getLast() + " ctype : " + doc.getContentType());
    }

    if (log.isDebugEnabled()) {
      log.debug("HTTPConnector.extractResponse : exit");
    }
  }

  /**
   * Envoie via HTTP un flux : Not Used !
   *
   * @param url URL complete du HTTP POST
   * @param in InputStream de type FileInputStream ou ByteArrayInputStream ...
   *
   * @return String[2] avec String[0] = content-type et String[1] = response body
   *
   * @throws CommunicationException si erreur I/O ou HTTP
   */
  public final Object[] postFile(final String url, final InputStream in)
                          throws CommunicationException {
    int len     = 0;

    try {
      len = in.available();
    } catch (final IOException ioe) {
      return new String[0]; // stream in problem
    }

    if ((len < 1) || (len >= Integer.MAX_VALUE)) {
      return new String[0]; // stream in problem
    }

    if (log.isDebugEnabled()) {
      log.debug("HTTPConnector.postFile  : URL : " + url);
    }

    PostMethod post = new PostMethod(url);

    post.setRequestHeader(HTTP_HEADER_CONTENT_TYPE, HTTP_DEFAULT_ENCODING);
    // TODO : Deprecated :
    // post.setRequestContentLength(len);
    // post.setRequestBody(in);
    sendRequest(post); // can throw CommunicationException

    // Object[] retour = extractResponse(post, false);
    // Release current connection to the connection pool once you are done
    post.releaseConnection();

    return null;
  }

  //~ Inner Classes ----------------------------------------------------------------------------------------------------

  /**
   * Gestion des erreurs Http avec un nombre reglable de tentatives
   */
  private final class MyHttpMethodRetryHandler implements HttpMethodRetryHandler {
    //~ Members --------------------------------------------------------------------------------------------------------

    private final int nb;

    //~ Constructors ---------------------------------------------------------------------------------------------------

    public MyHttpMethodRetryHandler(final int val) {
      nb = val;
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    public boolean retryMethod(final HttpMethod method, final IOException exception, final int executionCount) {
      if (executionCount >= nb) {
        // Do not retry if over max retry count
        return false;
      }

      if (log.isDebugEnabled()) {
        log.debug("MyHttpMethodRetryHandler.retryMethod : " + method.getPath() + " : " + executionCount, exception);
      }

      if (exception instanceof NoHttpResponseException) {
        // Retry if the server dropped connection on us
        return true;
      }

      if (exception instanceof ConnectTimeoutException) {
        // Retry if the server has found a socket or connection timeout
        return true;
      }

      if (! method.isRequestSent()) {
        // Retry if the request has not been sent fully or
        // if it's OK to retry methods that have been sent
        return true;
      }

      if (log.isDebugEnabled()) {
        log.debug("MyHttpMethodRetryHandler.retryMethod : " + method.getPath() + " : skip retry : ", exception);
      }

      // otherwise do not retry
      return false;
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
