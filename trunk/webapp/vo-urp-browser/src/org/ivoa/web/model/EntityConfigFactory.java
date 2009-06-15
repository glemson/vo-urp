package org.ivoa.web.model;

import org.ivoa.dm.MetaModelFactory;
import org.ivoa.dm.ObjectClassType;

import java.util.HashMap;
import java.util.Map;
import org.ivoa.bean.SingletonSupport;

/**
 * EntityConfig factory to hold references to EntityConfig
 *
 * @author laurent
 */
public final class EntityConfigFactory extends SingletonSupport {
    //~ Constants --------------------------------------------------------------------------------------------------------

    /**
     * TODO : Field Description
     */
    private static EntityConfigFactory instance = null;

    //~ Members ----------------------------------------------------------------------------------------------------------

    // members :
    /** EntityConfig Classes mapped by alias */
    private final Map<String, EntityConfig> configs = new HashMap<String, EntityConfig>();

    //~ Constructors -----------------------------------------------------------------------------------------------------
    /**
     * Creates a new EntityConfigFactory object
     */
    protected EntityConfigFactory() {
        super();
    }

    //~ Methods ----------------------------------------------------------------------------------------------------------
    /**
     * TODO : Method Description
     *
     * @return value TODO : Value Description
     */
    public static final EntityConfigFactory getInstance() {
        if (instance == null) {
            final EntityConfigFactory factory = new EntityConfigFactory();
            factory.initialize();

            instance = factory;
        }
        return instance;
    }

    /**
     * TODO : Method Description
     */
    public static void onExit() {
        if (logD.isWarnEnabled()) {
            logD.warn("EntityConfigFactory.onExit : enter");
        }
        if (instance != null) {
            instance.clear();
        }
        // force GC :
        instance = null;

        if (logD.isWarnEnabled()) {
            logD.warn("EntityConfigFactory.onExit : exit");
        }
    }

    /**
     * TODO : Method Description
     */
    public void initialize() {
        // process all ClassTypes :
        final MetaModelFactory mf = MetaModelFactory.getInstance();

        String name;
        EntityConfig ec = null;

        for (final ObjectClassType ct : mf.getObjectClassTypes().values()) {
            name = ct.getType().getName();

            ec = new EntityConfig(mf.getClass(name), true, true);
            ec.addPage("xml", "DM2XML");
            ec.addPage("json", "DM2JSON");
            ec.addPage("edit", "EditMetadataObject");
            addConfig(ec);
        }
    }

    /**
     * TODO : Method Description
     */
    public void clear() {
        // force GC :
        getConfigs().clear();
    }

    /**
     * TODO : Method Description
     *
     * @param e
     */
    public void addConfig(final EntityConfig e) {
        // calls init (compute) :
        e.init();

        configs.put(e.getName(), e);
    }

    /**
     * TODO : Method Description
     *
     * @return value TODO : Value Description
     */
    public Map getConfigs() {
        return configs;
    }

    /**
     * TODO : Method Description
     *
     * @param alias
     *
     * @return value TODO : Value Description
     */
    public EntityConfig getConfig(final String alias) {
        return configs.get(alias);
    }
}
//~ End of file --------------------------------------------------------------------------------------------------------
