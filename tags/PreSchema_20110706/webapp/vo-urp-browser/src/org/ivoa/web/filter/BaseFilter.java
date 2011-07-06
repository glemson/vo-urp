package org.ivoa.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ivoa.bean.LogSupport;

/**
 * Base class for VO-URP filters
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public class BaseFilter extends LogSupport implements Filter {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** serial UID for Serializable interface */
  private static final long serialVersionUID = 1L;

  //~ Constructors -----------------------------------------------------------------------------------------------------
  /**
   * J2EE Filter constructor
   */
  public BaseFilter() {
    /* no-op */
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------
  /**
   * Filter.init
   *
   * @param filterConfig J2EE configuration from web.xml
   */
  public final void init(final FilterConfig filterConfig) throws ServletException {
    if (log.isInfoEnabled()) {
      log.info(getClass().getSimpleName() + ".init : enter");
    }

    this.onInit(filterConfig);

    if (log.isInfoEnabled()) {
      log.info(getClass().getSimpleName() + ".init : exit : filter is started.");
    }
  }

  /**
   * Filter destructor
   */
  public final void destroy() {
    if (log.isInfoEnabled()) {
      log.info("BaseFilter.destroy : enter");
    }

    if (log.isInfoEnabled()) {
      log.info("BaseFilter.destroy : exit : filter is stopped.");
    }
  }

  /**
   * Filter : process Request & Response
   *
   * @param request HTTP request
   * @param response HTTP response
   * @param chain filter chain
   * @throws IOException if IO failure
   * @throws ServletException if servlet exception occured
   */
  public final void doFilter(final ServletRequest request, final ServletResponse response,
          final FilterChain chain) throws IOException, ServletException {
    if (log.isInfoEnabled()) {
      log.info("BaseFilter.doFilter : enter");
    }

    onFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);

    if (log.isInfoEnabled()) {
      log.info("BaseFilter.doFilter : exit");
    }
  }

  /**
   * Custom filter method to add pre and post processing events : May be overriden
   * @param request HTTP request
   * @param response HTTP response
   * @param chain filter chain
   * @throws IOException if IO failure
   * @throws ServletException if servlet exception occured
   */
  protected void onFilter(final HttpServletRequest request,
          final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {

    boolean filter = false;
    try {
      filter = onPreFilter(request, response);

      if (filter) {

        if (log.isInfoEnabled()) {
          log.info(getClass().getSimpleName() + ".doFilter : before filter");
        }

        // must call forward(chain, req, res);
        sendForward(request, response, chain);

        if (log.isInfoEnabled()) {
          log.info(getClass().getSimpleName() + ".doFilter : after filter");
        }
      }

    } catch (Throwable th) {
      handleThrowable(th);
    } finally {
      if (filter) {
        onPostFilter(request, response);
      }

      if (log.isInfoEnabled()) {
        log.info(getClass().getSimpleName() + ".doFilter : exit");
      }
    }
  }

  /**
   * Calls forward on the filter chain
   *
   * @param request HTTP request
   * @param response HTTP response
   * @param chain filter chain
   * @throws IOException if IO failure
   * @throws ServletException if servlet exception occured
   */
  protected final void sendForward(final HttpServletRequest request,
          final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {

    if (log.isInfoEnabled()) {
      log.info(getClass().getSimpleName() + ".sendForward : forward request to pipeline ...");
    }

    // gives request to pipeline :
    chain.doFilter(request, response);

    if (log.isInfoEnabled()) {
      log.info(getClass().getSimpleName() + ".sendForward : after forward request to pipeline ...");
    }
  }

  /**
   * Log the given exception
   * @param th throwable
   * @throws java.io.IOException
   * @throws javax.servlet.ServletException
   */
  protected void handleThrowable(final Throwable th) throws IOException, ServletException {
    if (th != null) {
      if (th instanceof IOException) {
        log.error(getClass().getSimpleName() + ".handleThrowable : IO exception : ", th);
        throw (IOException) th;
      } else if (th instanceof ServletException) {
        log.error(getClass().getSimpleName() + ".handleThrowable : servlet exception : ", th);
        throw (ServletException) th;
      } else if (th instanceof RuntimeException) {
        log.error(getClass().getSimpleName() + ".handleThrowable : runtime exception : ", th);
        throw (RuntimeException) th;
      } else {
        log.error(getClass().getSimpleName() + ".handleThrowable : error " + th.getMessage(), th);
        if (th.getCause() != null) {
          log.error(getClass().getSimpleName() + ".handleThrowable : ", th.getCause());
        }
        throw new RuntimeException(getClass().getSimpleName() + ".handleThrowable : error : ", th);
      }
    }
  }

  // --- event calls ----------------------------------------------------------
  /**
   * Initialization event
   * @param filterConfig J2EE configuration from web.xml
   */
  protected void onInit(final FilterConfig filterConfig) {
    if (log.isInfoEnabled()) {
      log.info(getClass().getSimpleName() + ".onInit");
    }
  }

  /**
   * Destroy event
   */
  protected void onDestroy() {
    if (log.isInfoEnabled()) {
      log.info(getClass().getSimpleName() + ".onDestroy");
    }
  }

  /**
   * Pre filter event to decide if the request can be processed
   * @param request HTTP request
   * @param response HTTP response
   * @return true to continue the request pipeline
   */
  protected boolean onPreFilter(final HttpServletRequest request, final HttpServletResponse response) {
    if (log.isInfoEnabled()) {
      log.info(getClass().getSimpleName() + ".onPreFilter : " + request.getRequestURI());
    }
    return true;
  }

  /**
   * Post filter event
   * @param request HTTP request
   * @param response HTTP response
   */
  protected void onPostFilter(final HttpServletRequest request, final HttpServletResponse response) {
    if (log.isInfoEnabled()) {
      log.info(getClass().getSimpleName() + ".onPostFilter : " + request.getRequestURI());
    }
  }
}
