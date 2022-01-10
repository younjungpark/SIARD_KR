package ch.admin.bar.siard2.cmd;

import static org.junit.Assert.*;

import java.io.*;
import java.sql.*;

import ch.enterag.sqlparser.identifier.QualifiedId;
import ch.enterag.utils.jdbc.BaseDatabaseMetaData;
import org.junit.*;
import ch.admin.bar.siard2.altibase.*;
import ch.admin.bar.siard2.jdbc.*;
import ch.admin.bar.siard2.jdbcx.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;

public class AltibaseFromDbTester extends BaseFromDbTester
{

    private static final String _sAltibase_DB_URL;
    private static final String _sAltibase_DB_USER;
    private static final String _sAltibase_DB_PASSWORD;
    private static final String _sAltibase_DBA_USER;
    private static final String _sAltibase_DBA_PASSWORD;

    static
    {
        ConnectionProperties cp = new ConnectionProperties("Altibase");
        _sAltibase_DB_URL = AltibaseDriver.getUrl("//" + cp.getHost() + ":" + cp.getPort()+"/"+cp.getCatalog()+"?date_format=yyyy-MM-dd");
        _sAltibase_DB_USER = cp.getUser();
        _sAltibase_DB_PASSWORD = cp.getPassword();
        _sAltibase_DBA_USER = cp.getDbaUser();
        _sAltibase_DBA_PASSWORD = cp.getDbaPassword();
    }

    private static final String _sAltibase_SIARD_FILE     = "tmp/sfdbAltibase.siard";
    private static final String _sAltibase_METADATA_FILE  = "tmp/sfdbAltibase.xml";
    private static final File   _fileAltibase_SIARD_FINAL = new File("testfiles/sfdbAltibase.siard");

    @Test
    public void testAltibaseFromDb()
    {
        System.out.println("testAltibaseFromDb");
        try
        {
            AltibaseDataSource dsAltibase = new AltibaseDataSource();
            dsAltibase.setUrl(_sAltibase_DB_URL);
            dsAltibase.setUser(_sAltibase_DBA_USER);
            dsAltibase.setPassword(_sAltibase_DBA_PASSWORD);
            AltibaseConnection connAltibase = (AltibaseConnection)dsAltibase.getConnection();
            /* drop and create the test database */
            clearDatabase(connAltibase,
                          _sAltibase_DB_USER, // in Altibase the default schema is the same as the DB user.
                          TestAltibaseDatabase._sTEST_SCHEMA,
                          ch.admin.bar.siard2.altibase.TestSqlDatabase._sTEST_SCHEMA,
                          null);
            System.out.println("Create TestSqlDatabase");
            new ch.admin.bar.siard2.altibase.TestSqlDatabase(connAltibase);
            System.out.println("Create TestAltibaseDatabase");
            new TestAltibaseDatabase(connAltibase);
            connAltibase.close();
            String[] args = new String[] {
                    "-o",
                    "-j:" + _sAltibase_DB_URL,
                    "-u:" + _sAltibase_DB_USER,
                    "-p:" + _sAltibase_DB_PASSWORD,
                    "-e:" + _sAltibase_METADATA_FILE,
                    "-s:" + _sAltibase_SIARD_FILE,
                    "-t=SYS.* "
            };
            SiardFromDb sfdb = new SiardFromDb(args);
            assertEquals("SiardFromDb failed!", 0, sfdb.getReturn());
            if (!_fileAltibase_SIARD_FINAL.exists())
                FU.copy(new File(_sAltibase_SIARD_FILE), _fileAltibase_SIARD_FINAL);
            System.out.println("---------------------------------------");
        }
        catch (IOException ie)
        {
            fail(EU.getExceptionMessage(ie));
        }
        catch (SQLException se)
        {
            se.printStackTrace();
        }
        catch (ClassNotFoundException cnfe)
        {
            fail(EU.getExceptionMessage(cnfe));
        }
    } /* testAltibaseFromDb */

    protected void dropTables(Connection conn, String sSchema, String sType)
            throws SQLException
    {
        DatabaseMetaData dmd = conn.getMetaData();
        ResultSet rsTables = dmd.getTables(null,
                                           ((BaseDatabaseMetaData)dmd).toPattern(sSchema), "%",
                                           new String[] { sType });
        AltibaseDataSource dsAltibase = new AltibaseDataSource();
        dsAltibase.setUrl(_sAltibase_DB_URL);
        dsAltibase.setUser(_sAltibase_DBA_USER);
        dsAltibase.setPassword(_sAltibase_DBA_PASSWORD);
        AltibaseConnection connAltibase = (AltibaseConnection)dsAltibase.getConnection();
        Statement stmt = connAltibase.createStatement();
        while (rsTables.next())
        {
            String sTableName = rsTables.getString("TABLE_NAME");
            QualifiedId qiTable = new QualifiedId(null, sSchema, sTableName);
            String sCascade = ("TABLE".equals(sType) ? " CASCADE" : "");
            String sSql = "DROP " + sType + " " + qiTable.format() + sCascade;
            int iResult = stmt.executeUpdate(sSql);
            if (iResult == 0)
                System.out.println(sType + " " + qiTable.format() + " dropped.");
            else
                fail(sType + " " + qiTable.format() + " NOT dropped!");
        }
        connAltibase.close();
        stmt.close();
        rsTables.close();
        conn.commit();
    } /* dropTables */

    protected void clearDatabase(Connection conn, String sDefaultSchema, String sDbSchema, String sSqlSchema, String sBlobSchema)
            throws SQLException
    {
        conn.setAutoCommit(false);
        dropTables(conn, sDefaultSchema, "VIEW");
        dropTables(conn, sDefaultSchema, "TABLE");
        dropTables(conn, sDbSchema, "VIEW");
        dropTables(conn, sDbSchema, "TABLE");
        dropTables(conn, sSqlSchema, "VIEW");
        dropTables(conn, sSqlSchema, "TABLE");
    } /* clearDatabase */
}
