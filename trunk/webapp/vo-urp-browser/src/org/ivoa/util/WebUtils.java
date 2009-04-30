/*
 * WebUtils.java
 *
 * Created on 20 sept. 2007, 18:19:21
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ivoa.util;

import javax.servlet.http.HttpServletRequest;


/**
 * 
DOCUMENT ME!
 *
 * @author laurent
 */
public class WebUtils {
  //~ Constructors -----------------------------------------------------------------------------------------------------

  /**
   * Creates a new WebUtils object
   */
  private WebUtils() {
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
