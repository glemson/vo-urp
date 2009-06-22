package org.vo.urp.test;

import java.util.ArrayList;
import java.util.Date;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.ivoa.dm.DataModelManager;
import org.ivoa.dm.MetaModelFactory;
import org.ivoa.dm.model.MetadataObject;
import org.ivoa.dm.model.MetadataRootEntityObject;
import org.ivoa.dm.model.ReferenceResolver;

import org.ivoa.env.ApplicationMain;

import org.ivoa.jaxb.XmlBindException;
import org.ivoa.jpa.JPAFactory;

import org.ivoa.simdb.Quantity;
import org.ivoa.simdb.experiment.Simulation;
import org.ivoa.simdb.experiment.Snapshot;
import org.ivoa.simdb.protocol.Simulator;


import javax.persistence.EntityManager;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.Session;
import org.ivoa.bean.LogSupport;
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
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public class DBTests extends LogSupport implements ApplicationMain {
    //~ Members ----------------------------------------------------------------------------------------------------------

    /** testLOAD_BATCH_WRITE iteration */
    private final static int WRITE_ITERATION = 4;
    /** testLOAD_THREADS_WRITE jobs */
    private final static int WRITE_JOBS = 4;
    /** testLOAD_THREADS_WRITE number of threads */
    private final static int POOL_THREADS = 2;
    /** testLOAD_BATCH_WRITE jobs wait */
    private final static int WRITE_WAIT_SECONDS = 120;
    /** XMLTests */
    private XMLTests xmlTest = new XMLTests();
    /** InspectorTests */
    private InspectorTests inspectorTest = new InspectorTests();
    /**
     * test Gadget2 protocol file
     */
    public static final String PROTOCOL_FILE_GADGET2 = XMLTests.TEST_PATH + "Gadget2" + XMLTests.XML_EXT;
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

        // Loads & write an xml instance :
        testLOAD_WRITE(jf, PROTOCOL_FILE_GADGET2);
        testLOAD_WRITE(jf, PROTOCOL_FILE_PDR);
        testLOAD_WRITE(jf, PROTOCOL_FILE_HALOMAKER);

        // Batch writes : Laurent : In progress :
        testLOAD_BATCH_WRITE(jf, PROTOCOL_FILE_PDR);
        testLOAD_BATCH_WRITE_SINGLE_TRANSACTION(jf, PROTOCOL_FILE_PDR);

        // Batch writes : Laurent : In progress :
        testLOAD_THREADS_WRITE(jf, PROTOCOL_FILE_PDR);

        testSQLQuery(jf);

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
        Long id = null;

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
            simulator.setPublisherDID("ivo://cool.simulators/SoCool/v1.2.3");

            // Resource attributes :
            simulator.setName("SoCool");
            simulator.setDescription("SoCool is an N-body simulation code");
            simulator.setReferenceURL("http://cool.simulators/SoCool/v1.2.3/readme.html");

            simulator.setCreated(new Date());
            simulator.setUpdated(new Date());
            simulator.setStatus("published");

            // Protocol attributes :
            simulator.setCode("http://cool.simulators/SoColl/v1.2.3/download.html");
            simulator.setVersion("v1.2.3");

            // Resource references : set main contact party
            Contact me = new Contact(simulator);
            me.setAddress("Somewhere in the world");
            me.setName("Its Me");
            me.setEmail("me@cool.simulators");
            me.setTelephone("+1 234 567 890");
            me.setRole(ContactRole.OWNER);

            // Protocol references : none

            // Resource collections : contacts

            // Protocol collections : representations, parameters, parameterGroups

            // Parameter Group :
            ParameterGroup paramGroup = new ParameterGroup(simulator);
            paramGroup.setName("Cosmological Parameters");
            paramGroup.setDescription("Sample Parameter Group");

            // Parameters :
            InputParameter param = null;
            ParameterGroupMember pgm = null;

            param = new InputParameter(simulator);

            // Identity attributes :
            param.getIdentity().setPublisherDID("ivo://cool.simulators/SoCool/v1.2.3/params/boxSize");

            // Field attributes :
            param.setName("box size");
            param.setDescription("size of the cubic box");
            param.setDatatype(DataType.FLOAT);
            param.setCardinality(Cardinality.ONE);
            param.setIsEnumerated(false);

            // InputParameter attributes :
            param.setLabel("sim.boxsize ?");

            pgm = new ParameterGroupMember(paramGroup);
            pgm.setParameter(param);

            param = new InputParameter(simulator);

            // Identity attributes :
            param.getIdentity().setPublisherDID("ivo://cool.simulators/SoCool/v1.2.3/params/lamda");

            // Field attributes :
            param.setName("lambda");
            param.setDescription("lambda");
            param.setDatatype(DataType.FLOAT);
            param.setCardinality(Cardinality.ONE);
            param.setIsEnumerated(false);

            // InputParameter attributes :
            param.setLabel("sim.cosmo.lambda");

            pgm = new ParameterGroupMember(paramGroup);
            pgm.setParameter(param);

            param = new InputParameter(simulator);

            // Identity attributes :
            param.getIdentity().setPublisherDID("ivo://cool.simulators/SoCool/v1.2.3/params/hubble");

            // Field attributes :
            param.setName("hubble constant");
            param.setDescription("hubble constant");
            param.setDatatype(DataType.FLOAT);
            param.setCardinality(Cardinality.ONE);
            param.setIsEnumerated(false);

            // InputParameter attributes :
            param.setLabel("sim.hubble");

            pgm = new ParameterGroupMember(paramGroup);
            pgm.setParameter(param);

            // Representation objects :
            final RepresentationObjectType repDM = new RepresentationObjectType(simulator);

            // Identity attributes :
            repDM.getIdentity().setPublisherDID("ivo://cool.simulators/SoCool/v1.2.3/darkMatter");

            // Representation attributes :
            repDM.setName("dark Matter");
            repDM.setDescription("dark Matter particles");
            repDM.setType(RepresentationObject.POINT_PARTICLE);
            repDM.setLabel("dark matter");


            // Property Group :
            PropertyGroup propGroup = new PropertyGroup(repDM);
            propGroup.setName("Position");
            propGroup.setDescription("X-Y-Z Coordinates Group");

            // Properties :
            Property propDM = null;
            PropertyGroupMember ppgm = null;

            // x :
            propDM = new Property(repDM);

            // Identity attributes :
            propDM.getIdentity().setPublisherDID("ivo://cool.simulators/SoCool/v1.2.3/darkMatter/positionX");

            // Field attributes :
            propDM.setName("x");
            propDM.setDescription("X (cartesian) coordinate of the particle in the cubic box");
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
            propDM.getIdentity().setPublisherDID("ivo://cool.simulators/SoCool/v1.2.3/darkMatter/positionY");

            // Field attributes :
            propDM.setName("y");
            propDM.setDescription("Y (cartesian) coordinate of the particle in the cubic box");
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
            propDM.getIdentity().setPublisherDID("ivo://cool.simulators/SoCool/v1.2.3/darkMatter/positionZ");

            // Field attributes :
            propDM.setName("z");
            propDM.setDescription("Z (cartesian) coordinate of the particle in the cubic box");
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
            propDM.getIdentity().setPublisherDID("ivo://cool.simulators/SoCool/v1.2.3/darkMatter/mass");

            // Field attributes :
            propDM.setName("virial mass");
            propDM.setDescription("Virial mass of the local dark matter");
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

            // Resource attributes :
            simulation.setName("Run SoCool 001");
            simulation.setDescription("this run uses parameters ... ");
            simulation.setReferenceURL(null);

            simulation.setPublisherDID("ivo://cool.simulators/SoCool/v1.2.3/RUN_SOCOOL_001");
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
            final Experiment loadedObject = (Simulation) testREAD(jf, Experiment.class, id);
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
                cha.getValue().setValue(1000 * Math.random());
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
            o = (MetadataObject) em.createNamedQuery(type.getSimpleName() + ".findById").setParameter("id", id).setHint(QueryHints.REFRESH, HintValues.TRUE).getSingleResult();

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
        Long id = null;

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
        } catch(XmlBindException je)
        {
          log.error("DBTests.testLOAD_WRITE: error parsing XML document : \n" + je.getMessage());
          throw je;
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
        if (id != null) {
            final Protocol loadedObject = (Protocol) testREAD(jf, Protocol.class, id);
            xmlTest.saveMarshall(loadedObject, xmlDocumentPath.replace(XMLTests.XML_EXT, "-out-afterLoad" + XMLTests.XML_EXT));
        }
        log.info("DBTests.testLOAD_WRITE : exit : " + id);
    }

    /**
     * TODO : Method Description
     *
     * @param jf
     * @param xmlDocumentPath
     */
    public void testLOAD_BATCH_WRITE(final JPAFactory jf, final String xmlDocumentPath) {
        log.info("DBTests.testLOAD_BATCH_WRITE : enter");

        EntityManager em = null;
        Long id = null;

        for (int i = 0; i < WRITE_ITERATION; i++) {

            try {
                em = jf.getEm();

                // starts TX :
                em.getTransaction().begin();

                ReferenceResolver.initContext(em);

                final MetadataObject simulator = xmlTest.testUnMashall(xmlDocumentPath);

                em.persist(simulator);

                // finally : commits transaction on snap database :
                em.getTransaction().commit();
                log.warn("DBTests.testLOAD_BATCH_WRITE : TX commited : " + i);

            } catch(XmlBindException je)
            {
              log.error("DBTests.testLOAD_BATCH_WRITE: error parsing XML document : \n" + je.getMessage());
              throw je;
            } catch (final RuntimeException re) {
                log.error("DBTests.testLOAD_BATCH_WRITE : runtime failure : ", re);

                // if connection failure => em is null :
                if (em.getTransaction().isActive()) {
                    log.warn("DBTests.testLOAD_BATCH_WRITE : rollbacking TX ...");
                    em.getTransaction().rollback();
                    log.warn("DBTests.testLOAD_BATCH_WRITE : TX rollbacked.");
                }

                throw re;
            } finally {
                // free resolver context (thread local) :
                ReferenceResolver.freeContext();

                em.close();
            }
        }

        log.info("DBTests.testLOAD_BATCH_WRITE : exit : " + id);
    }

    /**
     * TODO : Method Description
     *
     * @param jf
     * @param xmlDocumentPath
     */
    public void testLOAD_THREADS_WRITE(final JPAFactory jf, final String xmlDocumentPath) {
        log.info("DBTests.testLOAD_THREADS_WRITE : enter");

        final ExecutorService executor = Executors.newFixedThreadPool(POOL_THREADS);

        try {

            for (int i = 0; i < WRITE_JOBS; i++) {
                executor.submit(new WriteJob(this, jf, xmlDocumentPath, i));
            }

            executor.shutdown();

            log.warn("DBTests.testLOAD_THREADS_WRITE : waits for job termination ...");

            final boolean ok = executor.awaitTermination(WRITE_WAIT_SECONDS, TimeUnit.SECONDS);

            if (!ok) {
                log.warn("DBTests.testLOAD_THREADS_WRITE : stop jobs ...");
                executor.shutdownNow();
            }

        } catch (InterruptedException ie) {
            log.error("DBTests.testLOAD_THREADS_WRITE : runtime failure : ", ie);
        } catch (final RuntimeException re) {
            log.error("DBTests.testLOAD_THREADS_WRITE : runtime failure : ", re);
        }

        log.info("DBTests.testLOAD_THREADS_WRITE : exit");
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

                log.error("DBTests.testSQLQuery : query : " + sql);

                // Returns a List<Record implements Map> :
                List<?> rows = s.executeSelectingCall(new SQLCall(sql));

                log.error("DBTests.testSQLQuery : results : " + CollectionUtils.toString(rows));
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

    /**
     * TODO : Method Description
     * Alternative implementation of testLOAD_BATCH_WRITE. Persists a whole collection of objects in one transaction.<br/>
     *
     * @param jf
     * @param xmlDocumentPath
     */
    public void testLOAD_BATCH_WRITE_SINGLE_TRANSACTION(final JPAFactory jf, final String xmlDocumentPath) {
        log.info("DBTests.testLOAD_BATCH_WRITE_SINGLE_TRANSACTION : enter");
    
        EntityManager em = null;
    
        StringBuffer ids = new StringBuffer();
            try {
                em = jf.getEm();
   
                DataModelManager dm = new DataModelManager(jf.getPu());

                ReferenceResolver.initContext(em);
    
                final List<MetadataRootEntityObject> simList = new ArrayList<MetadataRootEntityObject>();
                for (int i = 0; i < WRITE_ITERATION; i++) 
                    simList.add((MetadataRootEntityObject)xmlTest.testUnMashall(xmlDocumentPath));
    
                dm.persist(simList,"testuser");
                for (MetadataRootEntityObject sim: simList) 
                  ids.append(sim.getId()).append(" ");
                log.warn("DBTests.testLOAD_BATCH_WRITE_SINGLE_TRANSACTION : TX wrote " + simList.size()+" simulators to database: " + ids.toString());
    
            } catch(XmlBindException je)
            {
              log.error("DBTests.testLOAD_BATCH_WRITE_SINGLE_TRANSACTION: error parsing XML document : \n" + je.getMessage());
              throw je;
            } catch (final RuntimeException re) {
                log.error("DBTests.testLOAD_BATCH_WRITE_SINGLE_TRANSACTION : runtime failure : ", re);
    
                throw re;
            } finally {
                // free resolver context (thread local) :
                ReferenceResolver.freeContext();
    
                em.close();
            }
        log.info("DBTests.testLOAD_BATCH_WRITE_SINGLE_TRANSACTION : exit");
    }

    private static final class WriteJob implements Runnable {

        private final DBTests test;
        private final JPAFactory jf;
        private final String xmlDocumentPath;
        private final int i;

        protected WriteJob(final DBTests test, final JPAFactory jf, final String xmlDocumentPath, final int i) {
            this.test = test;
            this.jf = jf;
            this.xmlDocumentPath = xmlDocumentPath;
            this.i = i;
        }

        public void run() {
            log.warn("WriteJob : run : IN : " + i);

            test.testLOAD_BATCH_WRITE(jf, xmlDocumentPath);

            log.warn("WriteJob : run : OUT : " + i);
        }
    }
}
//~ End of file --------------------------------------------------------------------------------------------------------
