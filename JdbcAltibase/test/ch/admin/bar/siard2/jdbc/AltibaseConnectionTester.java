package ch.admin.bar.siard2.jdbc;

import java.sql.*;

import static org.junit.Assert.*;

import org.junit.*;

import ch.admin.bar.siard2.jdbcx.*;
import ch.admin.bar.siard2.altibase.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;
import ch.enterag.utils.jdbc.*;

public class AltibaseConnectionTester extends BaseConnectionTester
{
  private static final ConnectionProperties _cp = new ConnectionProperties();
  private static final String _sDB_URL = AltibaseDriver.getUrl("//" + _cp.getHost() + ":" + _cp.getPort()+"/"+_cp.getCatalog());
  private static final String _sDB_USER = _cp.getUser();
  private static final String _sDB_PASSWORD = _cp.getPassword();
  private static final String _sDB_CATALOG = _cp.getCatalog();
  private static final String _sDBA_USER = _cp.getDbaUser();
  private static final String _sDBA_PASSWORD = _cp.getDbaPassword();

  private AltibaseConnection _connAltibase = null;

  @BeforeClass
  public static void setUpClass()
  {
    try
    {
      AltibaseDataSource dsAltibase = new AltibaseDataSource();
      dsAltibase.setUrl(_sDB_URL);
      dsAltibase.setUser(_sDBA_USER);
      dsAltibase.setPassword(_sDBA_PASSWORD);
      AltibaseConnection connAltibase = (AltibaseConnection) dsAltibase.getConnection();
      new TestAltibaseDatabase(connAltibase);
      TestAltibaseDatabase.grantSchemaUser(connAltibase,null, _sDB_USER);
      new TestSqlDatabase(connAltibase);
      TestAltibaseDatabase.grantSchemaUser(connAltibase,null, _sDB_USER);
      connAltibase.close();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* setUpClass */

  @Before
  public void setUp()
  {
    try
    {
      AltibaseDataSource dsAltibase = new AltibaseDataSource();
      dsAltibase.setUrl(_sDB_URL);
      dsAltibase.setUser(_sDB_USER);
      dsAltibase.setPassword(_sDB_PASSWORD);
      _connAltibase = (AltibaseConnection) dsAltibase.getConnection();
      _connAltibase.setAutoCommit(false);
      setConnection(_connAltibase);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* setUp */

  @Test
  public void testClass()
  {
    assertEquals("Wrong connection class!", AltibaseConnection.class, _connAltibase.getClass());
  } /* testClass */

  @Test
  @Override
  public void testGetMetadata()
  {
    enter();
    try
    {
      DatabaseMetaData dmd = _connAltibase.getMetaData();
      assertEquals("Wrong metadata class!", AltibaseDatabaseMetaData.class, dmd.getClass());
    }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetMetadata */

  @Test
  @Override
  public void testSetCatalog()
  {
    try { _connAltibase.setCatalog(_sDB_CATALOG); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testSetCatalog */

} /* class AltibaseConnectionTester */
