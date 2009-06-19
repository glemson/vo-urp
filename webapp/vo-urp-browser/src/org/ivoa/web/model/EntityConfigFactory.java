package org.ivoa.web.model;

import org.ivoa.dm.MetaModelFactory;
import org.ivoa.dm.ObjectClassType;

import java.util.HashMap;
import java.util.Map;
import org.ivoa.bean.SingletonSupport;

/**
 * EntityConfig factory to hold references to EntityConfig
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
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
     * Abstract method to be implemented by concrete implementations :
     * Callback to initialize this SingletonSupport instance
     */
    @Override
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
     * Abstract method to be implemented by concrete implementations :
     * Callback to clean up this SingletonSupport instance
     */
    @Override
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
    public Map<String, EntityConfig> getConfigs() {
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
