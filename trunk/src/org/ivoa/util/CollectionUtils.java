package org.ivoa.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;


/**
 * Collection toString() methods 
 * 
 * @author laurent bourges (voparis)
 */
public class CollectionUtils {

  private CollectionUtils() {
  }

  public static String toString(final Map m) {
    return toString(m, System.getProperty("line.separator"), "{", "}");
  }

  public static String toString(final Map m, final String lineSep) {
    return toString(m, lineSep, "", "");
  }

  public static String toString(final Map m, final String lineSep, final String startSep, final String endSep) {
    final StringBuffer sb = new StringBuffer(1024);
    final Iterator it = m.entrySet().iterator();

    sb.append(startSep);

    Map.Entry e;
    Object key, value;
    for (int i = 0,  max = m.size() - 1; i <= max; i++) {
      e = (Map.Entry) it.next();
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

  public static String toString(final Collection c) {
    return toString(c, System.getProperty("line.separator"), "{", "}");
  }

  public static String toString(final Collection c, final String lineSep) {
    return toString(c, lineSep, "", "");
  }

  public static String toString(final Collection c, final String sep, final String startSep, final String endSep) {
    final StringBuffer sb = new StringBuffer(256);
    final Iterator it = c.iterator();

    sb.append(startSep);

    for (int i = 0,  max = c.size() - 1; i <= max; i++) {
      sb.append(it.next());

      if (i < max) {
        sb.append(sep);
      }
    }
    sb.append(endSep);
    return sb.toString();
  }
}
