package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.jdbc.AltibaseConnection;
import ch.admin.bar.siard2.jdbc.AltibaseDriver;
import ch.admin.bar.siard2.jdbcx.AltibaseDataSource;
import ch.enterag.utils.EU;
import ch.enterag.utils.base.ConnectionProperties;
import org.junit.Test;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AltibaseToDbTester extends BaseFromDbTester
{
    private static final String _sALTIBASE_DB_URL;
    private static final String _sALTIBASE_DB_USER;
    private static final String _sALTIBAES_DB_PASSWORD;
    private static final String _sALTIBASE_DBA_USER;
    private static final String _sALTIBASE_DBA_PASSWORD;
    private static final String _sALTIBASE_TEST_SCHEMA = "TESTSCHEMA";
    static
    {
        ConnectionProperties cp = new ConnectionProperties("Altibase");
        _sALTIBASE_DB_URL = AltibaseDriver.getUrl("//" + cp.getHost() + ":" + cp.getPort()+"/"+cp.getCatalog()+"?date_format=yyyy-MM-dd");
        _sALTIBASE_DB_USER = cp.getUser();
        _sALTIBAES_DB_PASSWORD = cp.getPassword();
        _sALTIBASE_DBA_USER = cp.getDbaUser();
        _sALTIBASE_DBA_PASSWORD = cp.getDbaPassword();
    }
    private static final String _sALTIBASE_SAMPLE_FILE = "testfiles/sample.siard";
    private static final String _sALTIBASE_SIARD_FILE  = "testfiles/sfdbAltibase.siard";

    @Test
    public void testAltibaseToAltibase()
    {
        System.out.println("testAltibaseToAltibase");
        try
        {
            // now upload sample
            String[] args = new String[]{
                    "-o",
                    "-j:" + _sALTIBASE_DB_URL,
                    "-u:" + _sALTIBASE_DBA_USER,
                    "-p:" + _sALTIBASE_DBA_PASSWORD,
                    "-s:" + _sALTIBASE_SIARD_FILE
            };
            SiardToDb stdb = new SiardToDb(args);
            assertEquals("SiardToDb failed!",0, stdb.getReturn());
            System.out.println("---------------------------------------");
        }
        catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
        catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    } /* testAltibaseToAltibase */

    @Test
    public void testSampleToAltibase()
    {
        System.out.println("testSampleToAltibase");
        try
        {
            AltibaseDataSource dsAltibase = new AltibaseDataSource();
            dsAltibase.setUrl(_sALTIBASE_DB_URL);
            dsAltibase.setUser(_sALTIBASE_DBA_USER);
            dsAltibase.setPassword(_sALTIBASE_DBA_PASSWORD);
            AltibaseConnection connAltibase = (AltibaseConnection)dsAltibase.getConnection();
            connAltibase.setAutoCommit(false);
            try
            {
                Statement stmt = connAltibase.createStatement();
                stmt.executeUpdate("DROP SCHEMA " + _sALTIBASE_TEST_SCHEMA + " CASCADE");
                stmt.close();
                connAltibase.commit();
            }
            catch(SQLException se)
            {
                System.out.println(EU.getExceptionMessage(se));
                /* terminate transaction */
                try { connAltibase.rollback(); }
                catch(SQLException seRollback) { System.out.println("Rollback failed with "+EU.getExceptionMessage(seRollback)); }
            }
            Statement stmt = connAltibase.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            String sSql = "CREATE SCHEMA " + _sALTIBASE_TEST_SCHEMA + " AUTHORIZATION " + _sALTIBASE_DB_USER;
            int iResult = stmt.executeUpdate(sSql);
            if (iResult == 0)
                connAltibase.commit();
            else
            {
                connAltibase.rollback();
                fail(sSql + " failed!");
            }
            sSql = "GRANT ALL PRIVILEGES TO " + _sALTIBASE_TEST_SCHEMA;
           stmt.unwrap(Statement.class).executeUpdate(sSql);
            stmt.close();
            connAltibase.close();
            /* now upload sample */
            String[] args = new String[]{
                    "-o",
                    "-j:" + _sALTIBASE_DB_URL,
                    "-u:" + "TESTSCHEMA",
                    "-p:" + "MANAGER",
                    "-s:" + _sALTIBASE_SAMPLE_FILE,
                    "pg_catalog", "TESTSCHEMA",
                    "SampleSchema", "TESTSCHEMA"
            };
            SiardToDb stdb = new SiardToDb(args);
            assertEquals("SiardToDb failed!",0, stdb.getReturn());
            System.out.println("---------------------------------------");
        }
        catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
        catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    } /* testSampleToAltibase */
}
