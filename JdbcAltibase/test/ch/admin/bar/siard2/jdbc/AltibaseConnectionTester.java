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
  protected static final String _sSQL = "SELECT 1 FROM dual";
  private static final ConnectionProperties _cp = new ConnectionProperties();
  private static final String _sDB_URL = AltibaseDriver.getUrl("//" + _cp.getHost() + ":" + _cp.getPort()+"/"+_cp.getCatalog()+"?date_format=yyyy-MM-dd");
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
			dropUser();
			AltibaseDataSource dsAltibase = new AltibaseDataSource();
			dsAltibase.setUrl(_sDB_URL);
			dsAltibase.setUser(_sDBA_USER);
			dsAltibase.setPassword(_sDBA_PASSWORD);
			AltibaseConnection connAltibase = (AltibaseConnection)dsAltibase.getConnection();
			TestAltibaseDatabase.createUser(connAltibase, _sDB_USER, _sDB_PASSWORD);
			connAltibase.close();

			dsAltibase.setUrl(_sDB_URL);
			dsAltibase.setUser(_sDB_USER);
			dsAltibase.setPassword(_sDB_PASSWORD);
			connAltibase = (AltibaseConnection)dsAltibase.getConnection();

			new TestAltibaseDatabase(connAltibase);
			new TestSqlDatabase(connAltibase);
			connAltibase.close();
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	} /* setUpClass */

	@AfterClass
	public static void tearDownClass()
	{
		dropUser();
	} /* AfterClass */

	private static void dropUser()
	{
		try
		{
			AltibaseDataSource dsAltibase = new AltibaseDataSource();
			dsAltibase.setUrl(_sDB_URL);
			dsAltibase.setUser(_sDBA_USER);
			dsAltibase.setPassword(_sDBA_PASSWORD);
			AltibaseConnection connAltibase = (AltibaseConnection)dsAltibase.getConnection();
			TestAltibaseDatabase.dropUser(connAltibase, _sDB_USER);
		}
		catch (SQLException se)
		{ /* ignore */ }
	}

	@Before
	public void setUp()
	{
		try
		{
			AltibaseDataSource dsAltibase = new AltibaseDataSource();
			dsAltibase.setUrl(_sDB_URL);
			dsAltibase.setUser(_sDB_USER);
			dsAltibase.setPassword(_sDB_PASSWORD);
			_connAltibase = (AltibaseConnection)dsAltibase.getConnection();
			_connAltibase.setAutoCommit(false);
			setConnection(_connAltibase);
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
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
		catch (SQLFeatureNotSupportedException sfnse)
		{
			System.out.println(EU.getExceptionMessage(sfnse));
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	} /* testGetMetadata */

	@Test
	@Override
	public void testSetCatalog()
	{
		try
		{
			_connAltibase.setCatalog(_sDB_CATALOG);
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	} /* testSetCatalog */

	@Test
	@Override
	public void testPrepareStatement()
	{
		enter();
		try
		{
			_connAltibase.prepareStatement(_sSQL);
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	}

	@Test
	@Override
	public void testPrepareCall()
	{
		enter();
		try
		{
			_connAltibase.prepareCall(_sSQL);
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	} /* testPrepareCall*/

	@Test
	@Override
	public void testPrepareStatement_String_Int()
	{
		enter();
		try
		{
			_connAltibase.prepareStatement(_sSQL, Statement.NO_GENERATED_KEYS);
		}
		catch (SQLFeatureNotSupportedException sfnse)
		{
			System.out.println(EU.getExceptionMessage(sfnse));
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	} /* testPrepareStatement_String_Int */

	@Test
	@Override
	public void testPrepareCall_String_Int_Int()
	{
		enter();
		try
		{
			_connAltibase.prepareCall(_sSQL,
																ResultSet.TYPE_SCROLL_INSENSITIVE,
																ResultSet.CONCUR_READ_ONLY);
		}
		catch (SQLFeatureNotSupportedException sfnse)
		{
			System.out.println(EU.getExceptionMessage(sfnse));
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	} /* testPrepareCall_String_Int_Int */

	@Test
	public void testPrepareStatement_String_Int_Int()
	{
		enter();
		try
		{
			_connAltibase.prepareStatement(_sSQL,
																		 ResultSet.TYPE_SCROLL_INSENSITIVE,
																		 ResultSet.CONCUR_READ_ONLY);
		}
		catch (SQLFeatureNotSupportedException sfnse)
		{
			System.out.println(EU.getExceptionMessage(sfnse));
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	} /* testPrepareStatement_String_Int_Int */

	@Test
	@Override
	public void testPrepareStatement_String_AString()
	{
		enter();
		try
		{
			_connAltibase.prepareStatement(_sSQL, new String[] { "COL_A", "COL_B" });
		}
		catch (SQLFeatureNotSupportedException sfnse)
		{
			System.out.println(EU.getExceptionMessage(sfnse));
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	} /* testPrepareStatement_String_AString */

	@Test
	public void testPrepareStatement_String_Int_Int_Int()
	{
		enter();
		try
		{
			_connAltibase.prepareStatement(_sSQL,
																		 ResultSet.TYPE_SCROLL_INSENSITIVE,
																		 ResultSet.CONCUR_READ_ONLY,
																		 ResultSet.CLOSE_CURSORS_AT_COMMIT);
		}
		catch (SQLFeatureNotSupportedException sfnse)
		{
			System.out.println(EU.getExceptionMessage(sfnse));
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	} /* testPrepareStatement_String_Int_Int_Int */

	@Test
	public void testPrepareStatement_String_AInt()
	{
		enter();
		try
		{
			_connAltibase.prepareStatement(_sSQL, new int[] { 1, 2 });
		}
		catch (SQLFeatureNotSupportedException sfnse)
		{
			System.out.println(EU.getExceptionMessage(sfnse));
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	} /* testPrepareStatement_String_AInt */

	@Test
	public void testPrepareCall_String_Int_Int_Int()
	{
		enter();
		try
		{
			_connAltibase.prepareCall(_sSQL,
																ResultSet.TYPE_SCROLL_INSENSITIVE,
																ResultSet.CONCUR_READ_ONLY,
																ResultSet.CLOSE_CURSORS_AT_COMMIT);
		}
		catch (SQLFeatureNotSupportedException sfnse)
		{
			System.out.println(EU.getExceptionMessage(sfnse));
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	} /* testPrepareCall_String_Int_Int_Int */
} /* class AltibaseConnectionTester */
