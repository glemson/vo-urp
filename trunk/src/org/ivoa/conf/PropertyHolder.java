package org.ivoa.conf;

import org.apache.commons.logging.Log;

import org.ivoa.util.FileUtils;
import org.ivoa.util.LogUtil;
import org.ivoa.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.util.Iterator;
import java.util.Properties;


/**
 * This class loads a property file & exposes a simple API
 *
 * @author laurent
 */
public class PropertyHolder implements Serializable {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** serial UID for Serializable interface */
  private static final long serialVersionUID = 1L;
  /** logger */
  protected static final Log log = LogUtil.getLogger();

  //~ Members ----------------------------------------------------------------------------------------------------------

  /** properties */
  private Properties properties = null;

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Constructor (protected)
   */
  protected PropertyHolder() {
  }

/**
   * Constructor (protected)
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
    preInit();

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

      for (final Iterator it = this.properties.keySet().iterator(); it.hasNext();) {
        k = (String) it.next();
        s = this.properties.getProperty(k);

        if (StringUtils.isEmpty(s)) {
          it.remove();
        }
      }

      if (log.isDebugEnabled()) {
        log.debug("properties : " + getProperties());
      }

      // postInit event :
      res = postInit();
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
    return getProperties().getProperty(name);
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
    return getProperties().getProperty(name, def);
  }

  /**
   * Gets a Boolean from the Property value : valueOf(val)
   *
   * @param name property key
   *
   * @return boolean value or false if not found
   */
  public final boolean getBoolean(final String name) {
    final String val = getProperty(name);

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
    final String val = getProperty(name);

    if (val != null) {
      try {
        return Integer.parseInt(val);
      } catch (final NumberFormatException nfe) {
        // ignore value
      }
    }

    return 0;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
