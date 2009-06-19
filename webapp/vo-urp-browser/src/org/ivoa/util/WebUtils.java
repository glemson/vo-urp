package org.ivoa.util;

import javax.servlet.http.HttpServletRequest;


/**
 * WebUtils
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public class WebUtils {
  //~ Constructors -----------------------------------------------------------------------------------------------------

  /**
   * Creates a new WebUtils object
   */
  private WebUtils() {
    /* no-op */
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @param request 
   * @param name 
   *
   * @return value TODO : Value Description
   */
  public static int getIntParameter(final HttpServletRequest request, final String name) {
    return getIntParameter(request, name, 0);
  }

  /**
   * TODO : Method Description
   *
   * @param request 
   * @param name 
   * @param def 
   *
   * @return value TODO : Value Description
   */
  public static int getIntParameter(final HttpServletRequest request, final String name, final int def) {
    final String s = getStringParameter(request, name);

    if (s != null) {
      try {
        return Integer.parseInt(s);
      } catch (final NumberFormatException nfe) {
        // nothing to do
      }
    }

    return def;
  }

  /**
   * TODO : Method Description
   *
   * @param request 
   * @param name 
   *
   * @return value TODO : Value Description
   */
  public static String getStringParameter(final HttpServletRequest request, final String name) {
    return getStringParameter(request, name, null);
  }

  /**
   * TODO : Method Description
   *
   * @param request 
   * @param name 
   * @param def 
   *
   * @return value TODO : Value Description
   */
  public static String getStringParameter(final HttpServletRequest request, final String name, final String def) {
    final String s = request.getParameter(name);

    if ((s != null) && (s.length() > 0)) {
      return s;
    }

    return def;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
