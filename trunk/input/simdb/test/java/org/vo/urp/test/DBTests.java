package org.vo.urp.test;

import java.util.Date;

import java.util.List;
import org.ivoa.dm.MetaModelFactory;
import org.ivoa.dm.model.MetadataObject;
import org.ivoa.dm.model.ReferenceResolver;

import org.ivoa.env.ApplicationMain;

import org.ivoa.jpa.JPAFactory;

import org.ivoa.simdb.Quantity;
import org.ivoa.simdb.experiment.Simulation;
import org.ivoa.simdb.experiment.Snapshot;
import org.ivoa.simdb.protocol.Simulator;


import javax.persistence.EntityManager;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.Session;
import org.ivoa.simdb.Cardinality;
import org.ivoa.simdb.Contact;
import org.ivoa.simdb.ContactRole;
import org.ivoa.simdb.DataType;
import org.ivoa.simdb.experiment.Characterisation;
import org.ivoa.simdb.experiment.CharacterisationType;
import org.ivoa.simdb.experiment.Experiment;
import org.ivoa.simdb.experiment.ExperimentProperty;
import org.ivoa.simdb.experiment.ExperimentRepresentationObject;
import org.ivoa.simdb.experiment.NumericParameterSetting;
import org.ivoa.simdb.experiment.ObjectCollection;
import org.ivoa.simdb.object.Property;
import org.ivoa.simdb.object.PropertyGroup;
import org.ivoa.simdb.object.PropertyGroupMember;
import org.ivoa.simdb.protocol.InputParameter;
import org.ivoa.simdb.protocol.ParameterGroup;
import org.ivoa.simdb.protocol.ParameterGroupMember;
import org.ivoa.simdb.protocol.Protocol;
import org.ivoa.simdb.protocol.RepresentationObject;
import org.ivoa.simdb.protocol.RepresentationObjectType;
import org.ivoa.util.CollectionUtils;
import org.vo.urp.test.jaxb.XMLTests;


/**
 * Database Tests
 *
 * @author laurent bourges (voparis)
 */
public class DBTests implements ApplicationMain {
  //~ Members ----------------------------------------------------------------------------------------------------------

    /** testLOAD_WRITE iteration */
    private final static int WRITE_ITERATION = 1;

  /** XMLTests */
  private XMLTests xmlTest = new XMLTests();

  /** InspectorTests */
  private InspectorTests inspectorTest = new InspectorTests();
  
  /**
   * test HaloMaker protocol file
   */
  public static final String PROTOCOL_FILE_HALOMAKER = XMLTests.TEST_PATH + "Halomaker_protocol" + XMLTests.XML_EXT;

  /**
   * test PDR protocol file
   */
  public static final String PROTOCOL_FILE_PDR = XMLTests.TEST_PATH + "PDR_protocol" + XMLTests.XML_EXT;
  

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
    log.info("DBTests.run : enter");

    testJAXB(args);

    testJPA(args);

    log.info("DBTests.run : exit");
  }

  /**
   * TODO : Method Description
   *
   * @param args
   */
  public void testJAXB(final String[] args) {
    log.info("DBTests.testJAXB : enter");

    log.info("DBTests.testJAXB : get MetaModelFactory and load model : " + MetaModelFactory.MODEL_FILE);

    MetaModelFactory.getInstance();

    log.info("DBTests.testJAXB : get MetaModelFactory : OK");

    log.info("DBTests.testJAXB : exit");
  }

  /**
   * TODO : Method Description
   *
   * @param args
   */
  public void testJPA(final String[] args) {
    log.info("DBTests.testJPA : enter");

    String jpa_pu = args[0];
    
    log.info("DBTests.testJPA : get JPAFactory : " + jpa_pu + " ...");

    final JPAFactory jf = JPAFactory.getInstance(jpa_pu);

    try {
      log.info("DBTests.testJPA : get JPAFactory : " + jpa_pu + " : OK");

      EntityManager em = null;

      try {
        log.info("DBTests.testJPA : get EntityManager ...");
        em = jf.getEm();

        log.info("DBTests.testJPA : get EntityManager : OK");
      } finally {
        close(em);
      }

      testWRITE(jf);

      // Gerard : load XML -> JPA -> database test case :

      // Laurent : In progress :
      testLOAD_WRITE(jf, PROTOCOL_FILE_PDR);
      testLOAD_WRITE(jf, PROTOCOL_FILE_HALOMAKER);
      
      testSQLQuery(jf);
      
    } finally {
      jf.stop();
    }

    log.info("DBTests.testJPA : exit");
  }

  /**
   * TODO : Method Description
   *
   * @param jf
   */
  public void testWRITE(final JPAFactory jf) {
    log.info("DBTests.testWRITE : enter");

    EntityManager em = null;
    Long          id = null;

    try {
      em = jf.getEm();
      // starts TX :
      // starts transaction on snap database :
      log.warn("DBTests.testWRITE : starting TX ...");
      em.getTransaction().begin();

      // Add missing Parties :
//      findOrCreateParty(em, "ivo://horizon/herve_wozniak", "Herve Wozniak, CRAL, Lyon", null, null, null);

//      findOrCreateParty(em, "ivo://voparis/luth/franck_le_petit", "Franck Le Petit, LUTh, VOParis", null, null, null);
      
      
      // Creates a Simulator = Gadget :

      final Simulator simulator = new Simulator();
      
      // Identity attributes :
      simulator.setPublisherDID("ivo://www.mpa-garching.mpg.de/gadget2");
      
      // Resource attributes :
      simulator.setName("Gadget");
      simulator.setDescription("Gadget is a N-body simulation code ... <i>gadget description</i>");
      simulator.setReferenceURL("http://host/gadget/index.html");

      simulator.setCreated(new Date());
      simulator.setUpdated(new Date());
      simulator.setStatus("published");
      
      // Protocol attributes :
      simulator.setCode("http://host/download/gadget");
      simulator.setVersion("1.2.3");
      
      // Resource references : set main contact party
      Contact volker = new Contact(simulator);
      volker.setAddress("MPA, Garching, Germany");
      volker.setName("Volker Springel");
      volker.setEmail("***@***");
      volker.setTelephone("+49 (0)89 30000 ****");
      volker.setRole(ContactRole.OWNER);
      
      // Protocol references : none
      
      // Resource collections : contacts

      // Protocol collections : representations, parameters, parameterGroups
      
      // Parameter Group :
      ParameterGroup paramGroup = new ParameterGroup(simulator);
      paramGroup.setName("Cosmological Parameters");
      paramGroup.setDescription("Sample Parameter Group <i>parameter group description</i>");
      
      // Parameters :
      InputParameter param = null;
      ParameterGroupMember pgm = null;
      
      param = new InputParameter(simulator);

      // Identity attributes :
      param.getIdentity().setPublisherDID("ivo://org/gadget/params/boxSize");
      
      // Field attributes :
      param.setName("box size");
      param.setDescription("size of the cubic box <i>parameter description</i>");
      param.setDatatype(DataType.FLOAT);
      param.setCardinality(Cardinality.ONE);
      param.setIsEnumerated(false);
      
      // InputParameter attributes :
      param.setLabel("sim.boxsize ?");
      
      pgm = new ParameterGroupMember(paramGroup);
      pgm.setParameter(param);
      
      param = new InputParameter(simulator);

      // Identity attributes :
      param.getIdentity().setPublisherDID("ivo://org/gadget/params/lamda");
      
      // Field attributes :
      param.setName("lambda");
      param.setDescription("lambda <i>parameter description</i>");
      param.setDatatype(DataType.FLOAT);
      param.setCardinality(Cardinality.ONE);
      param.setIsEnumerated(false);
      
      // InputParameter attributes :
      param.setLabel("sim.cosmo.lambda ?");

      pgm = new ParameterGroupMember(paramGroup);
      pgm.setParameter(param);
      
      param = new InputParameter(simulator);

      // Identity attributes :
      param.getIdentity().setPublisherDID("ivo://org/gadget/params/hubble");
      
      // Field attributes :
      param.setName("hubble constant");
      param.setDescription("hubble constant <i>parameter description</i>");
      param.setDatatype(DataType.FLOAT);
      param.setCardinality(Cardinality.ONE);
      param.setIsEnumerated(false);
      
      // InputParameter attributes :
      param.setLabel("sim.hubble ?");

      pgm = new ParameterGroupMember(paramGroup);
      pgm.setParameter(param);
      
      // Representation objects :
      final RepresentationObjectType repDM = new RepresentationObjectType(simulator);

      // Identity attributes :
      repDM.getIdentity().setPublisherDID("ivo://org/gadget/darkMatter");
      
      // Representation attributes :
      repDM.setName("dark Matter");
      repDM.setDescription("dark Matter particules <i>representation description</i>");
      repDM.setType(RepresentationObject.POINT_PARTICLE);
      repDM.setLabel("dark matter");

      
      // Property Group :
      PropertyGroup propGroup = new PropertyGroup(repDM);
      propGroup.setName("Position");
      propGroup.setDescription("X-Y-Z Coordinates Group <i>property group description</i>");

      // Properties :
      Property propDM = null;
      PropertyGroupMember ppgm = null;

      // x :
      propDM = new Property(repDM);

      // Identity attributes :
      propDM.getIdentity().setPublisherDID("ivo://org/gadget/darkMatter/positionX");
      
      // Field attributes :
      propDM.setName("x");
      propDM.setDescription("X (cartesian) coordinate of the particle in the cubic box <i>property description</i>");
      propDM.setDatatype(DataType.DOUBLE);
      propDM.setCardinality(Cardinality.ONE);
      propDM.setIsEnumerated(false);
      
      // Property attributes :
      propDM.setUcd("pos.cartesian.x");

      ppgm = new PropertyGroupMember(propGroup);
      ppgm.setProperty(propDM);
      
      // y :
      propDM = new Property(repDM);

      // Identity attributes :
      propDM.getIdentity().setPublisherDID("ivo://org/gadget/darkMatter/positionY");
      
      // Field attributes :
      propDM.setName("y");
      propDM.setDescription("Y (cartesian) coordinate of the particle in the cubic box <i>property description</i>");
      propDM.setDatatype(DataType.DOUBLE);
      propDM.setCardinality(Cardinality.ONE);
      propDM.setIsEnumerated(false);
      
      // Property attributes :
      propDM.setUcd("pos.cartesian.y");

      ppgm = new PropertyGroupMember(propGroup);
      ppgm.setProperty(propDM);
      
      // z :
      propDM = new Property(repDM);

      // Identity attributes :
      propDM.getIdentity().setPublisherDID("ivo://org/gadget/darkMatter/positionZ");
      
      // Field attributes :
      propDM.setName("z");
      propDM.setDescription("Z (cartesian) coordinate of the particle in the cubic box <i>property description</i>");
      propDM.setDatatype(DataType.DOUBLE);
      propDM.setCardinality(Cardinality.ONE);
      propDM.setIsEnumerated(false);
      
      // Property attributes :
      propDM.setUcd("pos.cartesian.z");

      ppgm = new PropertyGroupMember(propGroup);
      ppgm.setProperty(propDM);

      // mass :
      propDM = new Property(repDM);

      // Identity attributes :
      propDM.getIdentity().setPublisherDID("ivo://org/gadget/darkMatter/mass");
      
      // Field attributes :
      propDM.setName("virial mass");
      propDM.setDescription("Virial mass of the local dark matter <i>property description</i>");
      propDM.setDatatype(DataType.DOUBLE);
      propDM.setCardinality(Cardinality.ONE);
      propDM.setIsEnumerated(false);
      
      // Property attributes :
      propDM.setUcd("phys.mass");
      
      
      log.error("DBTests.testWRITE : Simulator : " + simulator.deepToString());

      em.persist(simulator);

      log.error("DBTests.testWRITE : Simulator after persist : " + simulator.deepToString());

      // force transaction to be flushed in database :
      log.warn("DBTests.testWRITE : flushing TX");
      em.flush();
      log.warn("DBTests.testWRITE : TX flushed.");

      log.error("DBTests.testWRITE : Simulator after flush : " + simulator.deepToString());

      log.error("DBTests.testWRITE : test JAXB MARSHALLING : PROTOCOL : ");
      xmlTest.testMarshall(simulator);

      inspectorTest.test(simulator);
      
      
      // simulation = a Gadget run :
      final Simulation simulation = new Simulation();

      // Identity attributes :
      simulation.getIdentity().setPublisherDID("ivo://org/gadget/runs/001");

      // Resource attributes :
      simulation.setName("Run Gadget 001");
      simulation.setDescription("this run uses parameters ... <i>simulation description</i>");
      simulation.setReferenceURL(null);

      simulation.setPublisherDID("RUN_GADGET_001");
      simulation.setCreated(new Date());
      simulation.setUpdated(new Date());
      simulation.setStatus("published");

      // Simulation attributes :
      simulation.setExecutionTime(new Date());


      // reference to simulator :
      simulation.setProtocol(simulator);
      
      // ParameterSettings :
      for (InputParameter p : simulator.getParameter()) {
        if (p.getDatatype().equals(DataType.FLOAT)) {
          NumericParameterSetting np = new NumericParameterSetting(simulation);
          
          np.setInputParameter(p);
          
          Quantity value = new Quantity();
          value.setValue(100 * Math.random());
          
          np.setValue(value);
          
          simulation.addParameter(np);
        }
      }

      // ExperimentRepresentationObject with ExperimentProperties :
      
      final ExperimentRepresentationObject erep = new ExperimentRepresentationObject(simulation);
      erep.setType(repDM);
      
      ExperimentProperty ep = null;
      for (Property prop : repDM.getProperty()) {
        ep = new ExperimentProperty(erep);
        ep.setProperty(prop);
      }

      // Snapshots :
      addSnapshot(simulation, Integer.valueOf(1));
      addSnapshot(simulation, Integer.valueOf(2));
      addSnapshot(simulation, Integer.valueOf(3));
      addSnapshot(simulation, Integer.valueOf(4));
      addSnapshot(simulation, Integer.valueOf(5));
      
      log.error("DBTests.testWRITE : Simulation : " + simulation.deepToString());

      em.persist(simulation);

      log.error("DBTests.testWRITE : Simulation after persist : " + simulation.deepToString());

      // force transaction to be flushed in database :
      log.warn("DBTests.testWRITE : flushing TX");
      em.flush();
      log.warn("DBTests.testWRITE : TX flushed.");

      log.error("DBTests.testWRITE : Simulation after flush : " + simulation.deepToString());

      id = simulation.getId();

      // finally : commits transaction on snap database :
      log.warn("DBTests.testWRITE : committing TX");
      em.getTransaction().commit();
      log.warn("DBTests.testWRITE : TX commited.");

      log.error("DBTests.testWRITE : Simulation after commit : " + simulation.deepToString());

      log.error("DBTests.testWRITE : test JAXB MARSHALLING : SIMULATION : ");
      xmlTest.testMarshall(simulation);

      inspectorTest.test(simulation);
    } catch (final RuntimeException re) {
      log.error("DBTests.testWRITE : runtime failure : ", re);

      // if connection failure => em is null :
      if (em.getTransaction().isActive()) {
        log.warn("DBTests.testWRITE : rollbacking TX ...");
        em.getTransaction().rollback();
        log.warn("DBTests.testWRITE : TX rollbacked.");
      }

      throw re;
    } finally {
      em.close();
    }

    log.info("DBTests.testWRITE : exit : " + id);

    if (id != null) {
      final Experiment loadedObject = (Simulation)testREAD(jf, Experiment.class, id);
      xmlTest.saveMarshall(loadedObject, XMLTests.TEST_PATH + "testSim-out" + XMLTests.XML_EXT);
    }
  }
  
  private void addSnapshot(final Simulation simulation, final Integer num) {

      Quantity time, space;
      ExperimentProperty ep = null;

      final Snapshot snapshot = new Snapshot(simulation);

      snapshot.setPublisherDID(simulation.getPublisherDID() + "_" + num);

      time = new Quantity();

      time.setValue(3d * num.intValue());
      time.setUnit("Gyr");
      snapshot.setTime(time);

      space = new Quantity();

      space.setValue(100.d);
      space.setUnit("kpc");

      snapshot.setSpatialSizePhysical(space);
      
      ObjectCollection col = null;
      Characterisation cha = null;
      for (ExperimentRepresentationObject rep : simulation.getRepresentationObject()) {
        // for all 
        col = new ObjectCollection(snapshot);
        col.setObjectType(rep.getType());
        col.setNumberOfObjects(num.intValue() * 113);

        for (ExperimentProperty prop : rep.getProperty()) {
          cha = new Characterisation(col);
          cha.setType(CharacterisationType.MEAN);
          cha.setAxis(prop.getProperty());
          
          cha.setValue(new Quantity());
          cha.getValue().setValue(1000*Math.random());
        }
      }
  }
  

  
  /**
   * TODO : Method Description
   *
   * @param jf
   * @param type
   * @param id
   * @return loaded item
   */
  public MetadataObject testREAD(final JPAFactory jf, final Class<? extends MetadataObject> type, final Long id) {
    log.info("DBTests.testEM_READ : enter");

    MetadataObject o = null;
    EntityManager em = null;

    try {
      em = jf.getEm();

      // forces to use a query to refresh data from database :
      o = (MetadataObject)em.createNamedQuery(type.getSimpleName() + ".findById").setParameter("id", id)
              .setHint(QueryHints.REFRESH, HintValues.TRUE)
              .getSingleResult();

      log.error("DBTests.testEM_READ : Loaded Item dump : " + o.deepToString());
      
    } catch (final RuntimeException re) {
      log.error("DBTests.testEM_READ : runtime failure : ", re);
    } finally {
      em.close();
    }

    log.info("DBTests.testEM_READ : exit");
    return o;
  }

  /**
   * TODO : Method Description
   *
   * @param jf
   * @param xmlDocumentPath
   */
  public void testLOAD_WRITE(final JPAFactory jf, final String xmlDocumentPath) {
    log.info("DBTests.testLOAD_WRITE : enter");

    EntityManager em = null;
    Long          id = null;

    for (int i = 0; i < WRITE_ITERATION; i++) {

        try {
          em = jf.getEm();
          // starts TX :
          // starts transaction on snap database :
          log.warn("DBTests.testLOAD_WRITE : starting TX ...");
          em.getTransaction().begin();

          ReferenceResolver.initContext(em);

          final MetadataObject simulator = xmlTest.testUnMashall(xmlDocumentPath);

          log.error("DBTests.testLOAD_WRITE : Simulator after unmarshall : " + simulator.deepToString());

          em.persist(simulator);

          log.error("DBTests.testLOAD_WRITE : Simulator after persist : " + simulator.deepToString());

          // force transaction to be flushed in database :
          log.warn("DBTests.testLOAD_WRITE : flushing TX");
          em.flush();
          log.warn("DBTests.testLOAD_WRITE : TX flushed.");

          log.error("DBTests.testLOAD_WRITE : Simulator after flush : " + simulator.deepToString());

          // finally : commits transaction on snap database :
          log.warn("DBTests.testLOAD_WRITE : committing TX");
          em.getTransaction().commit();
          log.warn("DBTests.testLOAD_WRITE : TX commited.");

          id = simulator.getId();

          log.error("DBTests.testLOAD_WRITE : test JAXB MARSHALLING : PROTOCOL : ");
          xmlTest.testMarshall(simulator);

          xmlTest.saveMarshall(simulator, xmlDocumentPath.replace(XMLTests.XML_EXT, "-out" + XMLTests.XML_EXT));

          inspectorTest.test(simulator);
        } catch (final RuntimeException re) {
          log.error("DBTests.testLOAD_WRITE : runtime failure : ", re);

          // if connection failure => em is null :
          if (em.getTransaction().isActive()) {
            log.warn("DBTests.testLOAD_WRITE : rollbacking TX ...");
            em.getTransaction().rollback();
            log.warn("DBTests.testLOAD_WRITE : TX rollbacked.");
          }

          throw re;
        } finally {
          // free resolver context (thread local) :
          ReferenceResolver.freeContext();

          em.close();
        }

        log.info("DBTests.testLOAD_WRITE : exit : " + id);

        if (id != null) {
          final Protocol loadedObject = (Protocol)testREAD(jf, Protocol.class, id);
          xmlTest.saveMarshall(loadedObject, xmlDocumentPath.replace(XMLTests.XML_EXT, "-out-afterLoad" + XMLTests.XML_EXT));
        }
    }
  }
  
  public void testSQLQuery(final JPAFactory jf) {
    log.info("DBTests.testSQLQuery : enter");

    EntityManager em = null;
    // executeSelectingCall

    try {
      em = jf.getEm();
      
      JpaEntityManager eem = JpaHelper.getEntityManager(em);
      
      if (eem != null) {
        Session s = eem.getSession();
        
        String sql = "select * from t_Resource";

        log.error("DBTests.testSQLQuery : query : "+sql);
        
        // Returns a List<Record implements Map> :
        List rows = s.executeSelectingCall(new SQLCall(sql));
        
        log.error("DBTests.testSQLQuery : results : "+CollectionUtils.toString(rows));
      }
      
      
    } finally {

      em.close();
    }

    log.info("DBTests.testSQLQuery : exit");
  }

  /**
   * TODO : Method Description
   *
   * @param em
   */
  public final void close(final EntityManager em) {
    if (em != null) {
      em.close();
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
