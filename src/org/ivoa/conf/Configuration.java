package org.ivoa.conf;

import org.apache.commons.logging.Log;

import org.ivoa.util.CollectionUtils;
import org.ivoa.util.FileUtils;
import org.ivoa.util.LogUtil;
import org.ivoa.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;

import java.util.Iterator;
import java.util.Properties;


/**
 * Global Configuration for the application
 *
 * @author laurent bourges (voparis)
 */
public final class Configuration {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /* constants */
  /** file name for property file */
  public static final String PROPS = "runtime.properties";
  /** logger */
  protected static final Log log = LogUtil.getLogger();
  /** singleton instance */
  private static volatile Configuration instance = null;

  // property keys :
  /** keyword for application title */
  public static final String APP_TITLE = "project.title";
  /** keyword for application vendor */
  public static final String APP_VENDOR = "project.vendor";
  /** keyword for application version */
  public static final String APP_VERSION = "project.version";
  /**
   * keyword for service ivo id. The IVO Identifier by which the data access service is registered. Used as
   * prefix for ivo-id-s of all objects.
   */
  public static final String SERVICE_IVOID = "service.ivoid";
  /** keyword for java Main Class */
  public static final String MAIN_CLASS = "main.class";
  /** keyword for test mode */
  public static final String MODE_TEST = "mode.test";

  //~ Members ----------------------------------------------------------------------------------------------------------

  /** As the prefix is needed often, store it explicitly. */
  private String ivoIdPrefix = "";

  /* members */
  /** os type */
  private final OSEnum os;
  /** properties */
  private Properties properties = null;

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Constructor (private) : checks OS 
   */
  private Configuration() {
    this.os = checkOsName();
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Get Configuration Instance (singleton)
   *
   * @return Configuration
   *
   * @throws IllegalStateException if init() failed
   */
  public static final Configuration getInstance() {
    if (instance == null) {
      final Configuration c = new Configuration();

      if (c.init()) {
        instance = c;
      } else {
        throw new IllegalStateException("Unable to create Configuration !");
      }
    }

    return instance;
  }

  /**
   * Initialization : dumps Java System Properties and loads configuration file found in the system classpath
   * (removes any empty property value)
   *
   * @return true if well done
   */
  private boolean init() {
    // first dump properties :
    dumpSystemProps();

    InputStream in = null;

    try {
      in = FileUtils.getSystemFileInputStream(PROPS);

      this.properties = new Properties();
      this.properties.load(in);
      this.ivoIdPrefix = this.properties.getProperty(SERVICE_IVOID, "") + "#";

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

      return true;
    } catch (final IOException ioe) {
      log.error("IO Failure : ", ioe);

      return false;
    } finally {
      FileUtils.closeStream(in);
    }
  }

  /**
   * Dumps System.properties in logs (info) ...
   */
  private void dumpSystemProps() {
    if (log.isInfoEnabled()) {
      log.info("System.properties : ");
      log.info(CollectionUtils.toString(System.getProperties()));
    }
  }

  /**
   * Check os type
   *
   * @return OSEnum value
   */
  private static OSEnum checkOsName() {
    final String os = System.getProperty("os.name").toLowerCase();

    if (log.isInfoEnabled()) {
      log.info("OS : " + os);
    }

    OSEnum res = OSEnum.OS_OTHER;

    if (os.contains("linux")) {
      res = OSEnum.OS_LINUX;
    } else if (os.contains("mac os x")) {
      res = OSEnum.OS_MAC_OS_X;
    } else if (os.contains("windows")) {
      res = OSEnum.OS_MSWINDOWS;
    }

    return res;
  }

  // Property utils :
  /**
   * Returns the loaded properties
   *
   * @return Properties (map)
   */
  protected Properties getProperties() {
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
   * Gets a Boolean from the Property value : valueOf(val)
   *
   * @param name property key
   *
   * @return boolean value or false if not found
   */
  public boolean getBoolean(final String name) {
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
  public int getInt(final String name) {
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

  /**
   * Gives the OSEnum value
   *
   * @return OSEnum value
   */
  public OSEnum getOs() {
    return this.os;
  }

  /**
   * Is a Mac OS X ?
   *
   * @return true if mac os x
   */
  public boolean isMacOsX() {
    return this.os == OSEnum.OS_MAC_OS_X;
  }

  /**
   * Is linux OS ?
   *
   * @return true if linux os
   */
  public boolean isLinux() {
    return this.os == OSEnum.OS_LINUX;
  }

  /**
   * Is Microsoft Windows OS ?
   *
   * @return true if MS Windows os
   */
  public boolean isMSWindows() {
    return this.os == OSEnum.OS_MSWINDOWS;
  }

  /**
   * Is an other OS ?
   *
   * @return true if mac os x
   */
  public boolean isOther() {
    return this.os == OSEnum.OS_OTHER;
  }

  /**
   * Returns test mode
   *
   * @return test mode
   */
  public boolean isTest() {
    return getBoolean(MODE_TEST);
  }

  /**
   * Gives the application title : title + ' - ' + version
   *
   * @return title + ' - ' + version
   */
  public String getTitle() {
    return getProperty(Configuration.APP_TITLE) + " - " + getProperty(Configuration.APP_VERSION);
  }

  /**
   * Gives the service IVO Id +"#" as prefix for all
   *
   * @return title + ' - ' + version
   */
  public String getIVOIdPrefix() {
    return ivoIdPrefix;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  protected String getIvoIdPrefix() {
    return ivoIdPrefix;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------