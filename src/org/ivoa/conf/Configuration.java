package org.ivoa.conf;

import org.ivoa.util.CollectionUtils;


/**
 * Global Configuration for the application
 *
 * @author laurent bourges (voparis)
 */
public final class Configuration extends PropertyHolder {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** serial UID for Serializable interface */
  private static final long serialVersionUID = 1L;
  /** file name for property file */
  public static final String PROPS = "runtime.properties";
  /** singleton instance */
  private static volatile Configuration instance = null;

  /* property keys */
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
  /** os type */
  private final OSEnum os;

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Constructor (private) : checks OS
   */
  private Configuration() {
    super();
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

      if (c.init(PROPS)) {
        instance = c;
      } else {
        throw new IllegalStateException("Unable to create Configuration !");
      }
    }

    return instance;
  }

  /**
   * Pre Init event (can be overridden in child classes)
   */
  @Override
  protected void preInit() {
    // first dump properties :
    dumpSystemProps();
  }

  /**
   * Post Init event (can be overridden in child classes)
   *
   * @return true if well done
   */
  @Override
  protected boolean postInit() {
    this.ivoIdPrefix = getProperty(SERVICE_IVOID, "").trim() + "#";

    return true;
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
   * Gives the service IVO Id + "#" as prefix for all
   *
   * @return service IVO Id + "#"
   */
  public String getIVOIdPrefix() {
    return ivoIdPrefix;
  }

  /**
   * Gives the service IVO Id + "#" as prefix for all
   *
   * @return service IVO Id + "#"
   */
  protected String getIvoIdPrefix() {
    return ivoIdPrefix;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
