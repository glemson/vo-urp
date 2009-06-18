package org.ivoa.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Main Java utility methods : isEmpty / asList
 * 
 * @author laurent bourges
 */
public final class JavaUtils {

  /**
   * Test if value is set ie not empty
   *
   * @param value string value
   * @return true if value is NOT empty
   */
  public static boolean isSet(final String value) {
    return !isEmpty(value);
  }

  /**
   * Test if value is empty (null or no chars)
   * 
   * @param value string value
   * @return true if value is empty (null or no chars)
   */
  public static boolean isEmpty(final String value) {
    return value == null || value.length() == 0;
  }

  /**
   * Test if value is empty (null or no chars after trim)
   * 
   * @param value string value
   * @return true if value is empty (null or no chars after trim)
   */
  public static boolean isTrimmedEmpty(final String value) {
    return value == null || value.trim().length() == 0;
  }
  
  /**
   * Is the given array null or empty ?
   * 
   * @param array array to test
   * @return true if the array is null or empty
   */
  public final static boolean isEmpty(final Object[] array) {
    return array == null || array.length == 0;
  }

  /**
   * Is the given collection null or empty ?
   * 
   * @param col collection to test
   * @return true if the collection is null or empty
   */
  public final static boolean isEmpty(final Collection<?> col) {
    return col == null || col.isEmpty();
  }

  /**
   * Is the given map null or empty ?
   * 
   * @param map map to test
   * @return true if the map is null or empty
   */
  public final static boolean isEmpty(final Map<?, ?> map) {
    return map == null || map.isEmpty();
  }

  /**
   * Converts the given array to a List. If the array is empty, it gives the Collections.EMPTY_LIST
   * 
   * @param array array to convert
   * @return list (Collections.EMPTY_LIST if the array is empty)
   * @see java.util.Arrays#asList(Object[])
   */
  public final static List<Object> asList(final Object[] array) {
    List<Object> res;
    if (isEmpty(array)) {
      res = Collections.emptyList();
    } else {
      res = Arrays.asList(array);
    }
    return res;
  }

  /**
   * Get short name for the given class
   * 
   * @param type class
   * @return short name
   */
  public final static String unqualifiedClassName(final Class<?> type) {
    String name = null;
    if (type != null) {
      if (type.isArray()) {
        name = unqualifiedClassName(type.getComponentType()) + "Array";
      } else {
        name = type.getName();
        name = name.substring(name.lastIndexOf('.') + 1);
      }
    }
    return name;
  }
}
