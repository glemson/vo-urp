

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;




import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class managing a table with CLOB columns storing drop/migrate table/view
 * scripts.
 * 
 * @author gerard
 *
 */
public class AdminDBHandler {
	Driver driver = null; 
	String driverName = null;
	String url = null;
	Properties dbProperties = new Properties();

	int offset;
	Connection connection = null;

	public static void main(String[] args) throws Exception {
		try {
			String action = args[0];
			AdminDBHandler handler = new AdminDBHandler(args);
			if("I".equals(action))
				handler.insert(args);
			else if ("R".equals(action))
				handler.read(args);
			else if ("L".equals(action))
				handler.last(args);
			else
				printUsage();
		} catch(ArrayIndexOutOfBoundsException e)
		{
//			printUsage();
		}
	}

	private static void printUsage(){
		System.out.print("usage: java org.ivoa.jpa.AdminDBHandler [I|R|L] driver url user password [other options]");
	}
	private static void printUsage4I(){
			System.out.print("usage: java org.ivoa.jpa.AdminDBHandler 'I' driver url user password modelXML ddl-folder");
	}
	private static void printUsage4R(){
		System.out.print("usage: java org.ivoa.jpa.AdminDBHandler 'R' driver url user password version workfolder");
	}
	private static void printUsage4L(){
		System.out.print("usage: java org.ivoa.jpa.AdminDBHandler 'L' driver url user password workfolder");
	}
	private AdminDBHandler(String[] args) throws Exception{
		offset = 1;
		driverName = args[offset++];
		url = args[offset++];
		   try {
			      // See if the driver class exists
			      Class driverClass = Class.forName(driverName);
			      // If so create an instance
			      this.driver = DriverManager.getDriver(url);
//			      this.driver = (Driver) driverClass.newInstance();
			    } catch (ClassNotFoundException e) {
			      throw new Exception("Could not locate driver class " + driverName, e);
			    } 

		String user = args[offset++];
		String password = args[offset++];
	    dbProperties.put("user", user);
	    dbProperties.put("username", user);
	    dbProperties.put("password", password);
	    offset = 5;
	}

	private void createTables() throws SQLException, IOException {
		String sql = getCreateTableSQL();
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.execute();
	}

	private String getCreateTableSQL(){
		if(url.indexOf("sqlserver") >= 0)
			return MSSQL_create();
		else 
			throw new IllegalStateException("Only MS SQLServer supported for now");
	}
	
	private String MSSQL_create(){
		return  " if  not exists (select * from dbo.sysobjects "+
				"	where id = object_id(N'[_Models]')) "+
				"CREATE TABLE [dbo].[_Models]( "+
				"	[created] [datetime] NULL DEFAULT (getdate()),"+
				"     modelCreated varchar(20),"+
				"	[modelVersion] [varchar](128) NULL,"+
				"	[modelXML] [text] NULL,"+
				"	[createTables] [text] NULL,"+
				"	[createViews] [text] NULL,"+
				"	[dropTables] [text] NULL,"+
				"	[dropViews] [text] NULL,"+
				"	[backupTables] [text] NULL,"+
				"	[dropBackupTables] [text] NULL,"+
				"	[migrateTables] [text] NULL"+
				") ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]";

	}
	private String Postgres_create(){
		throw new IllegalStateException("No create tables for postgres defined yet");
	}
	
	private void insert(String[] args) throws SQLException, IOException {
		if(args.length != offset+2){
			printUsage4I();
			return;
		}

	    String modelFile = args[offset++];
		String ddlsFolder = args[offset++];
		
		String createTables = ddlsFolder+"/createTables.sql";//args[offset++];
		String createViews = ddlsFolder+"/createViews.sql";//args[offset++];
		String dropTables = ddlsFolder+"/dropTables.sql";//args[offset++];
		String dropViews = ddlsFolder+"/dropViews.sql";//args[offset++];
		String backupTables = ddlsFolder+"/backupTables.sql";//args[offset++];
		String dropBackupTables = ddlsFolder+"/dropBackupTables.sql";//args[offset++];
		String migrateTables = ddlsFolder+"/migrateTables.sql";//args[offset++];
		
		// retrieve some info from model file
		ModelXMLHandler xml = parseModel(modelFile);
		if(!xml.isOK)
			throw new IOException("Error parsing model XML file");
		String modelCreated = xml.created;
		String version = xml.version;
		connect();

		createTables();
		
		String sql = " insert into _Models (modelCreated, modelVersion, modelXML,createTables, createViews, dropTables, "
		+" dropViews, backupTables, dropBackupTables, migrateTables) VALUES (?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement stmt = connection.prepareStatement(sql);
		
		int count = 1;
		stmt.setString(count++, modelCreated);
		stmt.setString(count++, version);
		
		File mf = new File(modelFile);
		FileInputStream mfis = new FileInputStream(mf);
		stmt.setAsciiStream(count++, mfis, (int) mf.length());
		File ct = new File(createTables);
		FileInputStream ctis = new FileInputStream(ct);
		stmt.setAsciiStream(count++, ctis, (int) ct.length());
		File cv = new File(createViews);
		FileInputStream cvis = new FileInputStream(cv);
		stmt.setAsciiStream(count++, cvis, (int) cv.length());
		File dt = new File(dropTables);
		FileInputStream dtis = new FileInputStream(dt);
		stmt.setAsciiStream(count++, dtis, (int) dt.length());
		File dv = new File(dropViews);
		FileInputStream dvis = new FileInputStream(dv);
		stmt.setAsciiStream(count++, dvis, (int) dv.length());
		File bt = new File(backupTables);
		FileInputStream btis = new FileInputStream(bt);
		stmt.setAsciiStream(count++, btis, (int) bt.length());
		File dbt = new File(dropBackupTables);
		FileInputStream dbtis = new FileInputStream(dbt);
		stmt.setAsciiStream(count++, dbtis, (int) dbt.length());
		File mt = new File(migrateTables);
		FileInputStream mtis = new FileInputStream(mt);
		stmt.setAsciiStream(count++, mtis, (int) mt.length());

		stmt.execute();

		mfis.close();
		ctis.close();
		cvis.close();
		btis.close();
		dtis.close();
		dvis.close();
		dbtis.close();
		mtis.close();
	}

	private void read(String[] args) throws SQLException, IOException {
		if(args.length != offset+2){
			printUsage4R();
			return;
		}
	    String version = args[offset++];
		String outDir = args[offset++];
		connect();
		String sql = " select modelVersion, createTables, createViews, backupTables, migrateTables from _Models where modelVersion=?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		
		int count = 1;
		stmt.setString(count++, version);
		
		ResultSet rs = stmt.executeQuery();
		if(!rs.next()) return;

		byte[] bytes = new byte[1024];
		File od = new File(outDir);
		od.mkdirs();
		String[] names= new String[]{"createTables.sql", "createViews.sql", "backupTables.sql","migrateTables.sql"};
		for(int i = 0; i < 4; i++)
		{
			FileOutputStream os = new FileOutputStream(od.getAbsolutePath()+"/"+names[i]);
			InputStream in = rs.getAsciiStream(i+2);
			int len = 0;
			while((len = in.read(bytes)) >=0)
				os.write(bytes, 0, len);
				
			os.flush();
			os.close();
			in.close();
		}
		rs.close();
	}
	
	private void last(String[] args) throws SQLException, IOException {
		if(args.length != offset+1){
			printUsage4L();
			return;
		}
		String workFolder = args[offset++];
		connect();

		String sql = " if  exists (select * from dbo.sysobjects "+
				"	where id = object_id(N'[_Models]'))"
				+ " select top 1 backupTables, migrateTables, dropBackupTables, dropTables, dropViews from _Models order by created desc";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.execute();
		ResultSet rs = stmt.getResultSet();
		if(rs == null) return;

		File dir = new File(workFolder);
		dir.mkdirs();

		if(rs.next()){
			for(int i = 1; i <=5; i++)
				write(rs, i,dir);
		}
		rs.close();
	}
	
	private void write(ResultSet rs , int index, File dir) throws SQLException, IOException{
		InputStream in = rs.getAsciiStream(index);
		ResultSetMetaData rsmd = rs.getMetaData();
		String f = dir.getAbsolutePath()+"/"+rsmd.getColumnName(index)+".sql";
		FileOutputStream mtos = new FileOutputStream(f);

		byte[] bytes = new byte[1024];

		int len = 0;
		while((len = in.read(bytes)) >=0)
			mtos.write(bytes, 0, len);
			
		mtos.flush();
		mtos.close();
		in.close();
	}
	private void disconnect() {
		if (connection == null) return;
		try {
			connection.close();
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	private void connect() throws SQLException {
		if(driver != null)
		    connection = driver.connect(url, dbProperties);
	    if (connection == null)
	      throw new SQLException("Unable to make connection to database " + url
	          + " at this time.");
	}
	
	private ModelXMLHandler parseModel(String modelFile) 
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		ModelXMLHandler handler = new ModelXMLHandler();
		try {
			// Parse the input
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(new File(modelFile), handler);
		} catch (SAXException t) {
			if(handler.isOK)
				return handler;
			else
				t.printStackTrace();
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return handler;
	}
}
