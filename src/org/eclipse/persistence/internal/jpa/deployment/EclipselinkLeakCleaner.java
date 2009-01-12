package org.eclipse.persistence.internal.jpa.deployment;

import org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider;

/**
 * This class is a patch to fix memory leaks in EclipseLink 1.0 - 1.0.2.
 * Note : EclipseLink is deployed in the web app classloader
 *
 * @author laurent bourges (voparis)
 */
public class EclipselinkLeakCleaner {

  /**
   * Constructor
   */
  public EclipselinkLeakCleaner() {
  }
  
  public static void clean() {
    // EntityManagerFactory.emSetupImpls leak with undeployed factories :
/*    
{file:/java/apache-tomcat-6.0.18/webapps/simDB-browser/WEB-INF/lib/ivoa-simdb.jarSimDbPU = org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl@17e84b}
*/    
    EntityManagerFactoryProvider.emSetupImpls.clear();

    JavaSECMPInitializer.globalInstrumentation = null;
    
    // singleton leak on classLoader (JPAInitializer.initializationClassloader = WebAppClassLoader issue) :
    JavaSECMPInitializer.javaSECMPInitializer = null;
  }
}
