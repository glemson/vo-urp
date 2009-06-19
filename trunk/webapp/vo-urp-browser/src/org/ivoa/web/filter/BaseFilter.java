package org.ivoa.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
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

    /**
     * J2EE Filter constructor
     */
    public BaseFilter() {
        /* no-op */
    }

    /**
     * Filter.init
     *
     * @param filterConfig J2EE params from web.xml
     */
    public final void init(final FilterConfig filterConfig) throws ServletException {
        if (log.isInfoEnabled()) {
            log.info("BaseFilter.init : enter");
        }

        if (log.isInfoEnabled()) {
            log.info("BaseFilter.init : exit : filter is started.");
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
     * Filtre : process Request & Response
     *
     * @param req request
     * @param res response
     * @param chain filter chain
     * @throws IOException if IO failure
     * @throws ServletException if servlet exception occured
     */
    public final void doFilter(final ServletRequest req, final ServletResponse res,
            final FilterChain chain) throws IOException, ServletException {
        if (log.isInfoEnabled()) {
            log.info("BaseFilter.doFilter : enter");
        }

        /* no-op */

        if (log.isInfoEnabled()) {
            log.info("BaseFilter.doFilter : exit");
        }
    }
}
