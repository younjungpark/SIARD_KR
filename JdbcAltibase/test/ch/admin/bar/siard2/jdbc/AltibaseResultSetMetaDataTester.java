package ch.admin.bar.siard2.jdbc;

import java.sql.*;
import java.util.*;

import static org.junit.Assert.*;

import ch.admin.bar.siard2.altibase.TestAltibaseDatabase;
import ch.admin.bar.siard2.altibase.TestSqlDatabase;
import org.junit.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;
import ch.enterag.utils.jdbc.*;
import ch.enterag.sqlparser.identifier.*;
import ch.admin.bar.siard2.jdbcx.*;

public class AltibaseResultSetMetaDataTester extends BaseResultSetMetaDataTester
{
  private static final ConnectionProperties _cp = new ConnectionProperties();
	private static final String _sDB_URL = AltibaseDriver.getUrl("//" + _cp.getHost() + ":" + _cp.getPort()+"/"+_cp.getCatalog()+"?date_format=yyyy-MM-dd");
  private static final String _sDB_USER = _cp.getUser();
  private static final String _sDB_PASSWORD = _cp.getPassword();
  private static final String _sDBA_USER = _cp.getDbaUser();
  private static final String _sDBA_PASSWORD = _cp.getDbaPassword();

	private static String getTableQuery(QualifiedId qiTable, List<TestColumnDefinition> listCd)
	{
		StringBuilder sbSql = new StringBuilder("SELECT\r\n  ");
		for (int iColumn = 0; iColumn < listCd.size(); iColumn++)
		{
			if (iColumn > 0)
				sbSql.append(",\r\n  ");
			TestColumnDefinition tcd = listCd.get(iColumn);
			sbSql.append(tcd.getName());
		}
		sbSql.append("\r\nFROM ");
		sbSql.append(qiTable.format());
		return sbSql.toString();
	} /* getTableQuery */

	private static String _sNativeQuerySimple  = getTableQuery(TestAltibaseDatabase.getQualifiedSimpleTable(), TestAltibaseDatabase._listCdSimple);
	private static String _sNativeQueryComplex = getTableQuery(TestAltibaseDatabase.getQualifiedComplexTable(), TestAltibaseDatabase._listCdComplex);
	private static String _sSqlQuerySimple     = getTableQuery(TestSqlDatabase.getQualifiedSimpleTable(), TestSqlDatabase._listCdSimple);
	private static String _sSqlQueryComplex    = getTableQuery(TestSqlDatabase.getQualifiedComplexTable(), TestSqlDatabase._listCdComplex);

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

	private Connection _conn = null;

	private Connection closeResultSet()
			throws SQLException
	{
		ResultSet rs = getResultSet();
		if (rs != null)
		{
			if (!rs.isClosed())
			{
				Statement stmt = rs.getStatement();
				rs.close();
				setResultSetMetaData(null, null);
				if (!stmt.isClosed())
					stmt.close();
				_conn.commit();
			}
		}
		return _conn;
	} /* closeResultSet */

	private void openResultSet(String sQuery)
			throws SQLException
	{
		closeResultSet();
		Statement stmt = _conn.createStatement();
		ResultSet rs = stmt.executeQuery(sQuery);
		ResultSetMetaData rsmd = rs.getMetaData();
		setResultSetMetaData(rsmd, rs);
	} /* openResultSet */

	@Before
	public void setUp()
	{
		try
		{
			AltibaseDataSource dsAltibase = new AltibaseDataSource();
			dsAltibase.setUrl(_sDB_URL);
			dsAltibase.setUser(_sDB_USER);
			dsAltibase.setPassword(_sDB_PASSWORD);
			_conn = dsAltibase.getConnection();
			_conn.setAutoCommit(false);
			openResultSet(_sNativeQuerySimple);
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	}

	@Override
	@After
	public void tearDown()
	{
		try
		{
			closeResultSet();
			if (!_conn.isClosed())
			{
				_conn.commit();
				_conn.close();
			}
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	} /* tearDown */

	@Test
	public void testClass()
	{
		assertEquals("Wrong result set metadata class!", AltibaseResultSetMetaData.class, getResultSetMetaData().getClass());
	} /* testClass */

	@Test
	public void testNativeSimple()
	{
		try
		{
			openResultSet(_sNativeQuerySimple);
			super.testAll();
			System.out.println("Tested all!");
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	} /* testNativeSimple */

	@Test
	public void testNativeComplex()
	{
		try
		{
			openResultSet(_sNativeQueryComplex);
			super.testAll();
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	} /* testNativeComplex */

	@Test
	public void testSqlSimple()
	{
		try
		{
			openResultSet(_sSqlQuerySimple);
			super.testAll();
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	} /* testSqlSimple */

	@Test
	public void testSqlComplex()
	{
		try
		{
			openResultSet(_sSqlQueryComplex);
			super.testAll();
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	} /* testSqlComplex */

}
