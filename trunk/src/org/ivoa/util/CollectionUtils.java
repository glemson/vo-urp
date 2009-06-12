package org.ivoa.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;


/**
 * Collection toString() methods
 *
 * @author laurent bourges (voparis)
 */
public final class CollectionUtils {

  public final static String LINE_SEPARATOR = System.getProperty("line.separator");

  public final static String BEGIN_SEPARATOR = LINE_SEPARATOR + "{" + LINE_SEPARATOR;

  public final static String END_SEPARATOR = LINE_SEPARATOR + "}";

  //~ Constructors -----------------------------------------------------------------------------------------------------

  /**
   * Creates a new CollectionUtils object
   */
  private CollectionUtils() {
    // empty block
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------
 /**
   * TODO : Method Description
   * 
   * @param c
   * @return value TODO : Value Description
   */
  public static String toString(final Collection<?> c) {
    return toString(c, LINE_SEPARATOR, BEGIN_SEPARATOR, END_SEPARATOR);
  }

  /**
   * TODO : Method Description
   * 
   * @param m
   * @return value TODO : Value Description
   */
  public static String toString(final Map<?, ?> m) {
    return toString(m, LINE_SEPARATOR, BEGIN_SEPARATOR, END_SEPARATOR);
  }

  /**
   * TODO : Method Description
   * 
   * @param c
   * @param lineSep
   * @return value TODO : Value Description
   */
  public static String toString(final Collection<?> c, final String lineSep) {
    return toString(c, lineSep, "", "");
  }

  /**
   * TODO : Method Description
   * 
   * @param m
   * @param lineSep
   * @return value TODO : Value Description
   */
  public static String toString(final Map<?, ?> m, final String lineSep) {
    return toString(m, lineSep, "", "");
  }

  /**
   * TODO : Method Description
   * 
   * @param m
   * @param lineSep
   * @param startSep
   * @param endSep
   * @return value TODO : Value Description
   */
  @SuppressWarnings("unchecked")
  public static String toString(final Map<?, ?> m, final String lineSep,
      final String startSep, final String endSep) {
    final StringBuffer sb = new StringBuffer(1024);
    final Iterator it = m.entrySet().iterator();

    sb.append(startSep);

    Map.Entry e;
    Object key;
    Object value;

    for (int i = 0, max = m.size() - 1; i <= max; i++) {
      e = (Map.Entry)it.next();
      key = e.getKey();
      value = e.getValue();
      sb.append(key).append(" = ").append(value);

      if (i < max) {
        sb.append(lineSep);
      }
    }

    sb.append(endSep);

    return sb.toString();
  }

  /**
   * TODO : Method Description
   * 
   * @param c
   * @param sep
   * @param startSep
   * @param endSep
   * @return value TODO : Value Description
   */
  public static String toString(final Collection<?> c, final String sep,
      final String startSep, final String endSep) {
    final StringBuffer sb = new StringBuffer(256);
    final Iterator<?> it = c.iterator();

    sb.append(startSep);

    for (int i = 0, max = c.size() - 1; i <= max; i++) {
      sb.append(it.next());

      if (i < max) {
        sb.append(sep);
      }
    }

    sb.append(endSep);

    return sb.toString();
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
