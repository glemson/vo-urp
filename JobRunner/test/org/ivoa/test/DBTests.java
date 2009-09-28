package org.ivoa.test;

import org.ivoa.env.ApplicationMain;




import org.ivoa.bean.LogSupport;
import org.ivoa.runner.FileManager;
import org.ivoa.util.runner.JobJPAManager;
import org.ivoa.util.runner.LocalLauncher;
import org.ivoa.util.runner.RootContext;
import org.ivoa.util.runner.RunContext;


/**
 * Database Tests
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class DBTests extends LogSupport implements ApplicationMain {

  //~ Constructors -----------------------------------------------------------------------------------------------------
  /**
   * Constructor
   */
  public DBTests() {
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------
  /**
   * TODO : Method Description
   *
   * @param args
   */
  public void run(final String[] args) {
    log.warn("DBTests.run : enter");

    testJPA(args);

    log.warn("DBTests.run : exit");
  }

  /**
   * TODO : Method Description
   *
   * @param args
   */
  public void testJPA(final String[] args) {
    log.warn("DBTests.testJPA : enter");


    final JobJPAManager jm = JobJPAManager.getInstance();

    testWRITE(jm);

    log.warn("DBTests.testJPA : exit");
  }

  /**
   * Write a single context
   *
   * @param jm job persistence manager
   */
  public void testWRITE(final JobJPAManager jm) {
    log.warn("DBTests.testWRITE : enter");

    // Creates a Context :
    final RootContext ctx = LocalLauncher.prepareMainJob("test", "user me", "E:/JobRunner/Jobs", 10, null);

    LocalLauncher.prepareChildJob(ctx, "main", new String[]{FileManager.LEGACYAPPS + "/EyalsSAM/" + "EyalsSAM.exe"});


    jm.persist(ctx);

    Long id = ctx.getId();

    log.warn("DBTests.testWRITE : exit : " + id);

    testREAD(jm, id);
  }

  /**
   * Reload a context from the database
   *
   * @param jm job persistence manager
   * @param id job id
   * @return loaded item
   */
  public RunContext testREAD(final JobJPAManager jm, final Long id) {
    log.warn("DBTests.testEM_READ : enter");

    final RunContext ctx = jm.refresh(id, true);

    log.error("DBTests.testEM_READ : Loaded Item dump : " + ctx);

    log.warn("DBTests.testEM_READ : exit : " + ctx);
    return ctx;
  }

}
//~ End of file --------------------------------------------------------------------------------------------------------

