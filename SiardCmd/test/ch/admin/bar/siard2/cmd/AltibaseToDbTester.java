package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.jdbc.AltibaseDriver;
import ch.enterag.utils.EU;
import ch.enterag.utils.base.ConnectionProperties;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

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
            /* now upload sample */
            String[] args = new String[]{
                    "-o",
                    "-j:" + _sALTIBASE_DB_URL,
                    "-u:" + _sALTIBASE_DBA_USER,
                    "-p:" + _sALTIBASE_DBA_PASSWORD,
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
