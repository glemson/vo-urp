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

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Constructor
   */
  public CustomUnmarshallListener() {
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

      final Log log = LogUtil.getLoggerDev();
      if (log.isDebugEnabled()) {
        log.debug("CustomUnmarshallListener.afterUnmarshal : " + object);
      }

      ReferenceResolver.addInContext(object);
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
