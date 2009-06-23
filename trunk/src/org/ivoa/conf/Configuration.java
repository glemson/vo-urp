package org.ivoa.conf;

import org.ivoa.util.CollectionUtils;
import org.ivoa.util.ReflectionUtils;

/**
 * This class acts as a singleton to store the global Configuration for the application :
 * <ul>
 * <li>Values are loaded from the file global.properties located in the class path (first occurrence
 * in the class path is loaded).</li>
 * <li>OS Type is available : {@link OSEnum}</li>
 * </ul>
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public class Configuration extends PropertyHolder implements Configurable {
    //~ Constants --------------------------------------------------------------------------------------------------------

    /** serial UID for Serializable interface */
    private static final long serialVersionUID = 1L;
    /** singleton instance (java 5 memory model) */
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
     * public Constructor used for Introspection. Checks the OS
     */
    public Configuration() {
        super();
        this.os = checkOsName();
    }

    //~ Methods ----------------------------------------------------------------------------------------------------------
    /**
     * Return the Configuration singleton instance
     *
     * @return Configuration singleton instance
     *
     * @throws IllegalStateException if a problem occured
     */
    public static final Configuration getInstance() {
        if (instance == null) {
            final Class<? extends Configuration> confClass = ReflectionUtils.findClass(
                    CONFIGURATION_CLASS, Configuration.class);

            final Configuration conf = ReflectionUtils.newInstance(confClass);
            conf.setPropertyFile(CONFIGURATION_FILE);

            instance = prepareInstance(conf);
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
    private final void dumpSystemProps() {
        if (log.isInfoEnabled()) {
            log.info("System.properties : " + CollectionUtils.toString(System.getProperties()));
        }
    }

    /**
     * Check os type
     *
     * @return OSEnum value
     */
    private final static OSEnum checkOsName() {
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
    public final OSEnum getOs() {
        return this.os;
    }

    /**
     * Is a Mac OS X ?
     *
     * @return true if mac os x
     */
    public final boolean isMacOsX() {
        return this.os == OSEnum.OS_MAC_OS_X;
    }

    /**
     * Is linux OS ?
     *
     * @return true if linux os
     */
    public final boolean isLinux() {
        return this.os == OSEnum.OS_LINUX;
    }

    /**
     * Is Microsoft Windows OS ?
     *
     * @return true if MS Windows os
     */
    public final boolean isMSWindows() {
        return this.os == OSEnum.OS_MSWINDOWS;
    }

    /**
     * Is an other OS ?
     *
     * @return true if mac os x
     */
    public final boolean isOther() {
        return this.os == OSEnum.OS_OTHER;
    }

    /**
     * Returns test mode
     *
     * @return test mode
     */
    public final boolean isTest() {
        return this.getBoolean(MODE_TEST);
    }

    /**
     * Gives the application title : title + ' - ' + version
     *
     * @return title + ' - ' + version
     */
    public final String getTitle() {
        return this.getProperty(Configuration.APP_TITLE) + " - " + this.getProperty(Configuration.APP_VERSION);
    }

    /**
     * Gives the service IVO Id + "#" as prefix for all
     *
     * @return service IVO Id + "#"
     */
    public final String getIVOIdPrefix() {
        return this.ivoIdPrefix;
    }

    /**
     * Gives the service IVO Id + "#" as prefix for all
     *
     * @return service IVO Id + "#"
     */
    protected final String getIvoIdPrefix() {
        return this.ivoIdPrefix;
    }
}
//~ End of file --------------------------------------------------------------------------------------------------------
