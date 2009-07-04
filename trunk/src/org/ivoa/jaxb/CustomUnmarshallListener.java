package org.ivoa.jaxb;

import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.ivoa.dm.model.MetadataObject;
import org.ivoa.dm.model.ReferenceResolver;
import org.ivoa.util.LogUtil;

/**
 * Handles JAXB Unmarshaller events
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class CustomUnmarshallListener extends Unmarshaller.Listener {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /**
   * Main Logger for the application
   * @see org.ivoa.bean.LogSupport
   */
  private static Log log = LogUtil.getLogger();

  /** singleton instance (java 5 memory model) : statically defined (thread safe and stateless) */
  private static CustomUnmarshallListener instance = null;

  /**
   * Return the MarshallObjectPostProcessor singleton instance
   *
   * @return MarshallObjectPostProcessor singleton instance
   *
   * @throws IllegalStateException if a problem occured
   */
  public static final CustomUnmarshallListener getInstance() {
    if (instance == null) {
      instance = new CustomUnmarshallListener();
    }
    return instance;
  }

  //~ Constructors -----------------------------------------------------------------------------------------------------
  /**
   * Private Constructor
   */
  private CustomUnmarshallListener() {
    /* no-op */
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------
  /**
   * Occurs after unmarshalling an xml node : if MetadataObject => add this target to the ReferenceResolver
   * context
   *
   * @param target
   * @param parent
   */
  @Override
  public void afterUnmarshal(final Object target, final Object parent) {
    if (target instanceof MetadataObject) {
      final MetadataObject object = (MetadataObject) target;

      if (log.isDebugEnabled()) {
        log.debug("CustomUnmarshallListener.afterUnmarshal : " + object);
      }

      ReferenceResolver.addInContext(object);
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
