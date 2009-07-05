package org.ivoa.env;


/**
 * Application Main entry point
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public interface ApplicationMain {

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * This method is the main entry point to execute code
   *
   * @param args command line arguments
   */
  public void run(final String[] args);
}
//~ End of file --------------------------------------------------------------------------------------------------------
