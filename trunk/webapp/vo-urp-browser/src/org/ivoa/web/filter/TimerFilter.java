package org.ivoa.web.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ivoa.util.timer.TimerFactory;

/**
 * This filter simply logs elapsed time in the request pipeline to handle the request and response
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public class TimerFilter extends BaseFilter {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** serial UID for Serializable interface */
  private static final long serialVersionUID = 1L;

  //~ Methods ----------------------------------------------------------------------------------------------------------
  /**
   * Custom filter method to monitor the elapsed time in the request pipeline to handle the request and response
   * @param request HTTP request
   * @param response HTTP response
   * @param chain filter chain
   * @throws IOException if IO failure
   * @throws ServletException if servlet exception occured
   */
  @Override
  protected void onFilter(final HttpServletRequest request,
          final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {

    long start, stop;

    try {
      if (log.isInfoEnabled()) {
        log.info("TimerFilter.doFilter : before filter");
      }

      start = System.nanoTime();

      // must call forward(chain, req, res);
      sendForward(request, response, chain);

      stop = System.nanoTime();

      if (log.isInfoEnabled()) {
        log.info("TimerFilter.doFilter : after filter");
      }

      // TODO : find the proper category according to the servlet name (uri pattern) :
      TimerFactory.getTimer("TimerFilter").addMilliSeconds(start, stop);

    } catch (Throwable th) {
      handleThrowable(th);
    } finally {
      if (log.isInfoEnabled()) {
        log.info("TimerFilter.doFilter : exit");
      }
    }
  }
}
