package org.vo.urp.test;

import org.ivoa.bean.LogSupport;
import org.ivoa.dm.ObjectClassType;
import org.ivoa.dm.model.MetadataObject;

import org.ivoa.env.ApplicationMain;

import org.ivoa.metamodel.Attribute;
import org.ivoa.metamodel.Collection;
import org.ivoa.metamodel.Reference;

import org.ivoa.metamodel.Type;
import org.ivoa.util.CollectionUtils;


/**
 * TODO
 *
 * @author laurent bourges (voparis)
 */
public final class InspectorTests extends LogSupport implements ApplicationMain {
  //~ Constructors -----------------------------------------------------------------------------------------------------

  /**
   * Constructor
   */
  public InspectorTests() {
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @param args
   */
  public void run(final String[] args) {
    // nothing to do
  }

  /**
   * TODO : Method Description
   *
   * @param m MetadataObject to process
   */
  public void test(final MetadataObject m) {
    if (log.isInfoEnabled()) {
      log.info("InspectorTests.test : enter : " + m);
    }

    final ObjectClassType ct = m.getClassMetaData();

    if (ct != null) {
      final Type t = ct.getType();
      if (log.isInfoEnabled()) {
        log.info("InspectorTests.test : type : " + t.getName() + " ( " + t.getDescription() + ")");
      }

      if (ct.isHasAttributes()) {
        if (log.isInfoEnabled()) {
          log.info("InspectorTests.test : attributes : ");
        }

        dumpAttributes(ct, m);
      }

      if (ct.isHasReferences()) {
        if (log.isInfoEnabled()) {
          log.info("InspectorTests.test : references : ");
        }

        dumpReferences(ct, m);
      }

      if (ct.isHasCollections()) {
        if (log.isInfoEnabled()) {
          log.info("InspectorTests.test : collections : ");
        }

        dumpCollections(ct, m);
      }
    }

    if (log.isInfoEnabled()) {
      log.info("InspectorTests.test : exit");
    }
  }

  /**
   * Dump all attribute with name, description, data type and value
   *
   * @param ct classType to use
   * @param m MetadataObject to inspect
   */
  public void dumpAttributes(final ObjectClassType ct, final MetadataObject m) {
    String propertyName;
    Object value;

    for (final Attribute a : ct.getAttributes().values()) {
      propertyName = a.getName();

      if (log.isInfoEnabled()) {
        log.info("attribute = " + a.getDatatype().getName() + " :: " + a.getName() + " ( " + a.getDescription() + ")");
      }

      value = m.getProperty(propertyName);

      if (log.isInfoEnabled()) {
        log.info("value = " + value);
      }
    }
  }

  /**
   * Dump all references with name, description, data type and value
   *
   * @param ct classType to use
   * @param m MetadataObject to inspect
   */
  public void dumpReferences(final ObjectClassType ct, final MetadataObject m) {
    String propertyName;
    Object value;

    for (final Reference r : ct.getReferences().values()) {
      propertyName = r.getName();

      if (log.isInfoEnabled()) {
        log.info("reference = " + r.getDatatype().getName() + " :: " + r.getName() + " ( " + r.getDescription() + ")");
      }

      value = m.getProperty(propertyName);

      if (log.isInfoEnabled()) {
        log.info("value = " + value);
      }
    }
  }

  /**
   * Dump all collections with name, description, data type and value
   *
   * @param ct classType to use
   * @param m MetadataObject to inspect
   */
  public void dumpCollections(final ObjectClassType ct, final MetadataObject m) {
    String               propertyName;
    Object               value;
    java.util.Collection col;
    int                  size;

    for (final Collection c : ct.getCollections().values()) {
      propertyName = c.getName();

      if (log.isInfoEnabled()) {
        log.info("collection = " + c.getDatatype().getName() + " :: " + c.getName() + " ( " + c.getDescription() + ")");
      }

      value = m.getProperty(propertyName);

      if (value instanceof java.util.Collection) {
        col = (java.util.Collection) value;
        size = col.size();

        if (log.isInfoEnabled()) {
          log.info("value = collection[" + size + "]");
        }

        if (size > 0) {
          if (log.isInfoEnabled()) {
            log.info(CollectionUtils.toString(col, "", "{", "}"));
          }
        }
      }
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
