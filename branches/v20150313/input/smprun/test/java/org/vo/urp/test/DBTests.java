package org.vo.urp.test;

import org.ivoa.dm.model.MetadataObject;
import org.ivoa.dm.model.reference.ReferenceResolver;

import org.ivoa.env.ApplicationMain;

import org.ivoa.jaxb.XmlBindException;
import org.ivoa.jpa.JPAFactory;

import javax.persistence.EntityManager;
import org.ivoa.bean.LogSupport;
import org.ivoa.dm.MetaModelFactory;
import org.vo.urp.test.jaxb.XMLTests;

/**
 * Database Tests
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class DBTests extends LogSupport implements ApplicationMain {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /**
   * test xml sample file
   */
  public static final String SAMPLE_FILE = XMLTests.TEST_PATH + "aSampStub" + XMLTests.XML_EXT;
  //~ Members ----------------------------------------------------------------------------------------------------------
  /**
   * XMLTests
   */
  private XMLTests xmlTest = new XMLTests();
  /**
   * InspectorTests
   */
  private InspectorTests inspectorTest = new InspectorTests();

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

    testJAXB(args);

    testJPA(args);

    log.warn("DBTests.run : exit");
  }

  /**
   * TODO : Method Description
   *
   * @param args
   */
  public void testJAXB(final String[] args) {
    log.warn("DBTests.testJAXB : enter");

    log.warn("DBTests.testJAXB : get MetaModelFactory and load model : " + MetaModelFactory.MODEL_FILE);

    MetaModelFactory.getInstance();

    log.warn("DBTests.testJAXB : get MetaModelFactory : OK");

    log.warn("DBTests.testJAXB : exit");
  }

  /**
   * TODO : Method Description
   *
   * @param args
   */
  public void testJPA(final String[] args) {
    log.warn("DBTests.testJPA : enter");

    String jpa_pu = args[0];

    log.warn("DBTests.testJPA : get JPAFactory : " + jpa_pu + " ...");

    final JPAFactory jf = JPAFactory.getInstance(jpa_pu);

    log.warn("DBTests.testJPA : get JPAFactory : " + jpa_pu + " : OK");

    EntityManager em = null;

    try {
      log.warn("DBTests.testJPA : get EntityManager ...");
      em = jf.getEm();

      log.warn("DBTests.testJPA : get EntityManager : OK");
    } finally {
      close(em);
    }

    // TESTS:
    Long id = null;
    try {
      id = testLOAD_WRITE(jf, SAMPLE_FILE, true);
    } catch (RuntimeException re) {
      log.error("DBTests: testWRITE() failed: ", re);
    }

    final String[] stubs = new String[]{
      "aladin",
      "Aspro2",
      "LITpro",
      "topcat",
      "SearchCal"
    };

    for (String name : stubs) {
      try {
        id = testLOAD_WRITE(jf, XMLTests.TEST_PATH + name + XMLTests.XML_EXT, true);
      } catch (RuntimeException re) {
        log.error("DBTests: testWRITE() failed: ", re);
      }
    }

    log.warn("DBTests.testJPA : exit");
  }

  /**
   * TODO : Method Description
   *
   * @param jf
   * @param xmlDocumentPath
   * @param doLogRootElement
   */
  public Long testLOAD_WRITE(final JPAFactory jf, final String xmlDocumentPath, final boolean doLogRootElement) {
    log.warn("DBTests.testLOAD_WRITE : enter");

    EntityManager em = null;
    Long id = null;

    try {
      em = jf.getEm();
      // starts TX :
      // starts transaction on database :
      log.warn("DBTests.testLOAD_WRITE : starting TX ...");
      em.getTransaction().begin();

      ReferenceResolver.initContext(em);

      log.warn("DBTests.testLOAD_WRITE on document@ " + xmlDocumentPath);

      final MetadataObject root = xmlTest.testUnMashall(xmlDocumentPath);

      if (doLogRootElement) {
        log.error("DBTests.testLOAD_WRITE : Root after unmarshall : " + root.deepToString());
      }

      em.persist(root);

      if (doLogRootElement) {
        log.error("DBTests.testLOAD_WRITE : Root after persist : " + root.deepToString());
      }

      // force transaction to be flushed in database :
      log.warn("DBTests.testLOAD_WRITE : flushing TX");
      em.flush();
      log.warn("DBTests.testLOAD_WRITE : TX flushed.");

      if (doLogRootElement) {
        log.error("DBTests.testLOAD_WRITE : Root after flush : " + root.deepToString());
      }

      // finally : commits transaction on snap database :
      log.warn("DBTests.testLOAD_WRITE : committing TX");
      em.getTransaction().commit();
      log.warn("DBTests.testLOAD_WRITE : TX commited.");

      id = root.getId();

      log.error("DBTests.testLOAD_WRITE : test JAXB MARSHALLING : ");
      xmlTest.testMarshall(root);

      xmlTest.saveMarshall(root, xmlDocumentPath.replace(XMLTests.XML_EXT, "-out" + XMLTests.XML_EXT));

      inspectorTest.test(root);

    } catch (XmlBindException xe) {
      log.error("DBTests.testLOAD_WRITE: error parsing XML document : \n" + xe.getMessage());
      throw xe;
    } catch (final RuntimeException re) {
      log.error("DBTests.testLOAD_WRITE : runtime failure : ", re);

      // if connection failure => em is null :
      if (em != null && em.getTransaction().isActive()) {
        log.warn("DBTests.testLOAD_WRITE : rollbacking TX ...");
        em.getTransaction().rollback();
        log.warn("DBTests.testLOAD_WRITE : TX rollbacked.");
      }
      throw re;
    } finally {
      close(em);

      // free resolver context (thread local) :
      ReferenceResolver.freeContext();
    }
    log.warn("DBTests.testLOAD_WRITE : exit : " + id);
    return id;
  }

  /**
   * TODO : Method Description
   *
   * @param em
   */
  public void close(final EntityManager em) {
    if (em != null) {
      em.close();
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
