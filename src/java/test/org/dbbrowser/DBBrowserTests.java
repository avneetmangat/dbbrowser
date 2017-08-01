package org.dbbrowser;

import java.io.File;

import org.dbbrowser.db.engine.model.filter.CompoundFilterTest;
import org.dbbrowser.db.engine.model.filter.SimpleFilterTest;
import org.dbbrowser.db.engine.queryengine.MySQLDBQueryEngineTest;
import org.dbbrowser.db.engine.queryengine.Oracle9iDBQueryEngineTest;
import org.dbbrowser.db.engine.rawsqlengine.MySQLGenericRawSQLEngineAutoCommitOffTest;
import org.dbbrowser.db.engine.rawsqlengine.MySQLGenericRawSQLEngineAutoCommitOnTest;
import org.dbbrowser.db.engine.rawsqlengine.OracleGenericRawSQLEngineAutoCommitOffTest;
import org.dbbrowser.db.engine.rawsqlengine.OracleGenericRawSQLEngineAutoCommitOnTest;
import org.dbbrowser.db.engine.updateengine.GenericDBUpdateEngineTest;
import org.dbbrowser.drivermanager.ConnectionInfoTest;
import org.dbbrowser.drivermanager.DBBrowserDriverManagerTest;
import org.dbbrowser.security.AsymmetricEncryptionEngineTest;
import org.dbbrowser.ui.MessageFormatterTest;
import org.dbbrowser.ui.helper.KeyBindingsSerializerTest;

import infrastructure.logging.Log;
import infrastructure.propertymanager.PropertyManagementException;
import infrastructure.propertymanager.PropertyManager;
import junit.framework.Test;
import junit.framework.TestSuite;

public class DBBrowserTests
{
    public static Test suite()
    {
		//Initialize the property manager
		try
		{
			PropertyManager.getInstance().initializeProperties( new File("src/properties/db browser.properties") );			
		}
		catch(PropertyManagementException exc)
		{
			System.err.println("PropertyManager property file not found at location - src/properties/db browser.properties" );
			System.exit(-1);			
		}
		
    	Log.getInstance().initialize( "src/properties/log4jproperties.xml" );    	
        TestSuite suite = new TestSuite("All tests for DB Browser project");

        //JUnit tests
        /*suite.addTest(new TestSuite(DBBrowserDriverManagerTest.class));
        suite.addTest(new TestSuite(ConnectionInfoTest.class));
        suite.addTest(new TestSuite(Oracle9iDBQueryEngineTest.class));
        suite.addTest(new TestSuite(MySQLDBQueryEngineTest.class));
        suite.addTest(new TestSuite(GenericDBUpdateEngineTest.class));
        suite.addTest(new TestSuite(OracleGenericRawSQLEngineAutoCommitOnTest.class));
        suite.addTest(new TestSuite(OracleGenericRawSQLEngineAutoCommitOffTest.class));
        suite.addTest(new TestSuite(MySQLGenericRawSQLEngineAutoCommitOffTest.class));
        suite.addTest(new TestSuite(MySQLGenericRawSQLEngineAutoCommitOnTest.class));*/
        //suite.addTest(new TestSuite(SimpleFilterTest.class));
        //suite.addTest(new TestSuite(CompoundFilterTest.class));
        //suite.addTest(new TestSuite(MessageFormatterTest.class));
        //suite.addTest(new TestSuite(Oracle9iDBQueryEngineTest.class));
        //suite.addTest(new TestSuite(MySQLDBQueryEngineTest.class));
        suite.addTest(new TestSuite(ConnectionInfoTest.class));
        suite.addTest(new TestSuite(AsymmetricEncryptionEngineTest.class));

        return suite;
    }
}
