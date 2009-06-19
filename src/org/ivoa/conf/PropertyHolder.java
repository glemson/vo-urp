package org.ivoa.conf;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Properties;

import org.ivoa.bean.LogSupport;
import org.ivoa.util.CollectionUtils;
import org.ivoa.util.FileUtils;
import org.ivoa.util.JavaUtils;


/**
 * This class loads a property file & exposes a simple API
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public class PropertyHolder extends LogSupport implements Serializable {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** serial UID for Serializable interface */
  private static final long serialVersionUID = 1L;

  //~ Members ----------------------------------------------------------------------------------------------------------

  /** properties */
  private Properties properties = null;

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * public Constructor used for Introspection.
   */
  public PropertyHolder() {
    /* no-op */
  }

/**
   * Constructor (protected)
   * 
   * @param propertyFile property file name
   */
  public PropertyHolder(final String propertyFile) {
    if (! this.init(propertyFile)) {
      throw new IllegalStateException("Unable to load the configuration : " + propertyFile + " !");
    }
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Initialization : loads the configuration file found in the system classpath (removes any empty property
   * value)
   *
   * @param propertyFile property file name
   *
   * @return true if well done
   */
  protected final boolean init(final String propertyFile) {
    // preInit event :
    this.preInit();

    boolean     res = false;
    InputStream in  = null;

    try {
      in = FileUtils.getSystemFileInputStream(propertyFile);

      this.properties = new Properties();
      this.properties.load(in);

      // filter empty strings :
      String k;

      // filter empty strings :
      String s;

      for (final Iterator<Object> it = this.properties.keySet().iterator(); it.hasNext();) {
        k = (String) it.next();
        s = this.properties.getProperty(k);

        if (JavaUtils.isTrimmedEmpty(s)) {
          it.remove();
        }
      }

      if (log.isDebugEnabled()) {
        log.debug("properties [" + propertyFile + "] : " + CollectionUtils.toString(getProperties()));
      }

      // postInit event :
      res = this.postInit();
    } catch (final IOException ioe) {
      log.error("IO Failure : ", ioe);
    } finally {
      FileUtils.closeStream(in);
    }

    return res;
  }

  /**
   * Pre Init event (can be overridden in child classes)
   */
  protected void preInit() {
    /* no-op */
  }

  /**
   * Post Init event (can be overridden in child classes)
   *
   * @return true if well done
   */
  protected boolean postInit() {
    return true;
  }

  /**
   * Returns the loaded properties. Warning : DO NOT change keys or values in the Properties (map)
   *
   * @return Properties (map)
   */
  public final Properties getProperties() {
    return this.properties;
  }

  /**
   * Get a String property
   *
   * @param name given key
   *
   * @return string value or null if not found or contains only white spaces
   */
  public final String getProperty(final String name) {
    return this.getProperties().getProperty(name);
  }

  /**
   * Get a required String property
   * 
   * @param name given key
   * @return string value
   * @throws IllegalStateException if the value is empty
   */
  public final String getRequiredProperty(final String name) {
    final String value = this.getProperties().getProperty(name);

    if (JavaUtils.isTrimmedEmpty(value)) {
      throw new IllegalStateException("undefined property [" + name
          + "] in the configuration file = " + Configurable.CONFIGURATION_FILE + " !");
    }
    return value;
  }

  /**
   * Get a String property
   *
   * @param name given key
   * @param def default value
   *
   * @return string value or null if not found or contains only white spaces
   */
  public final String getProperty(final String name, final String def) {
    return this.getProperties().getProperty(name, def);
  }

  /**
   * Gets a Boolean from the Property value : valueOf(val)
   *
   * @param name property key
   *
   * @return boolean value or false if not found
   */
  public final boolean getBoolean(final String name) {
    final String val = this.getProperty(name);

    if (val == null) {
      return false;
    }

    return Boolean.valueOf(val).booleanValue();
  }

  /**
   * Gets an integer value from the Property value : parseInt(val)
   *
   * @param name property key
   *
   * @return integer value or 0 if not found
   */
  public final int getInt(final String name) {
    final String val = this.getProperty(name);

    if (val != null) {
      try {
        return Integer.parseInt(val);
      } catch (final NumberFormatException nfe) {
        // ignore value
      }
    }

    return 0;
  }

  /**
   * Gets a long value from the Property value : parseLong(val)
   * 
   * @param name property key
   * @return long value or 0l if not found
   */
  public long getLong(final String name) {
    final String val = this.getProperty(name);
    if (val != null) {
      try {
        return Long.parseLong(val);
      } catch (final NumberFormatException nfe) {
        // ignore value
      }
    }
    return 0L;
  }

  /**
   * Gets a double value from the Property value : parseDouble(val)
   * 
   * @param name property key
   * @return double value or 0d if not found
   */
  public double getDouble(final String name) {
    final String val = this.getProperty(name);
    if (val != null) {
      try {
        return Double.parseDouble(val);
      } catch (final NumberFormatException nfe) {
        // ignore value
      }
    }
    return 0d;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
