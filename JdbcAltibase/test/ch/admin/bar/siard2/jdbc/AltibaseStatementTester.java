package ch.admin.bar.siard2.jdbc;

import java.sql.*;
import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;
import ch.enterag.utils.jdbc.*;
import ch.enterag.sqlparser.*;
import ch.admin.bar.siard2.jdbcx.*;
import ch.admin.bar.siard2.altibase.*;

public class AltibaseStatementTester extends BaseStatementTester
{
	private static final ConnectionProperties _cp = new ConnectionProperties();
	private static final String _sDB_URL = AltibaseDriver.getUrl("//" + _cp.getHost() + ":" + _cp.getPort()+"/"+_cp.getCatalog()+"?date_format=yyyy-MM-dd");
	private static final String _sDB_USER = _cp.getUser();
	private static final String _sDB_PASSWORD = _cp.getPassword();
	private static final String _sDBA_USER = _cp.getDbaUser();
	private static final String _sDBA_PASSWORD = _cp.getDbaPassword();

	private AltibaseStatement _stmtAltibase = null;

	private static final String _sSQL_DDL = "CREATE TABLE TESTTABLE(CCHAR CHARACTER,\r\n" +
					"CVARCHAR VARCHAR(256),\r\n" +
					"CCLOB CLOB(4M),\r\n" +
					"CNCHAR NCHAR,\r\n" +
					"CNCHAR_VARYING NCHAR VARYING(256),\r\n" +
					"CNCLOB NCLOB(4G),\r\n" +
					"CXML XML,\r\n" +
					"CBINARY BINARY,\r\n" +
					"CVARBINARY VARBINARY(256),\r\n" +
					"CBLOB BLOB,\r\n" +
					"CNUMERIC NUMERIC(10, 3),\r\n" +
					"CDECIMAL DECIMAL,\r\n" +
					"CSMALLINT SMALLINT,\r\n" +
					"CINTEGER INTEGER,\r\n" +
					"CBIGINT BIGINT,\r\n" +
					"CFLOAT FLOAT(7),\r\n" +
					"CREAL REAL,\r\n" +
					"CDOUBLE DOUBLE PRECISION,\r\n" +
					"CBOOLEAN BOOLEAN,\r\n" +
					"CDATE DATE,\r\n" +
					"CTIME TIME(3),\r\n" +
					"CTIMESTAMP TIMESTAMP(9),\r\n" +
					"CINTERVALYEAR INTERVAL YEAR(2) TO MONTH,\r\n" +
					"CINTERVALDAY INTERVAL DAY TO MINUTE,\r\n" +
					"CINTERVALSECOND INTERVAL SECOND(2, 5),\r\n" +
					"PRIMARY KEY(CINTEGER),\r\n" +
					"UNIQUE(CCHAR))";
	private static final String _sSQL_CLEAN = "DROP TABLE TESTTABLE RESTRICT";
	private static final String _sSQL_QUERY = "SELECT 1 FROM dual";

	protected void clean()
					throws SQLException
	{
		try
		{
			getStatement().executeUpdate(_sSQL_CLEAN);
			getStatement().getConnection().commit();
		}
		catch(SQLException se) { getStatement().getConnection().rollback(); }
	} /* clean */

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
			AltibaseConnection connAltibase = (AltibaseConnection) dsAltibase.getConnection();
			TestAltibaseDatabase.createUser(connAltibase, _sDB_USER, _sDB_PASSWORD);
			connAltibase.close();

			dsAltibase.setUrl(_sDB_URL);
			dsAltibase.setUser(_sDB_USER);
			dsAltibase.setPassword(_sDB_PASSWORD);
			connAltibase = (AltibaseConnection) dsAltibase.getConnection();

			new TestAltibaseDatabase(connAltibase);
			new TestSqlDatabase(connAltibase);
			connAltibase.close();
		}
		catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
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
		catch(SQLException se) { /* ignore */ }
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
			AltibaseConnection connAltibase = (AltibaseConnection)dsAltibase.getConnection();
			connAltibase.setAutoCommit(false);
			_stmtAltibase = (AltibaseStatement)connAltibase.createStatement();
			setStatement(_stmtAltibase);
		}
		catch(SQLException se) { fail(se.getClass().getName()+": "+se.getMessage()); }
	} /* setUp */

	@Test
	public void testClass()
	{
		assertEquals("Wrong statement class!", AltibaseStatement.class, _stmtAltibase.getClass());
	} /* testClass */

	@Test
	@Override
	public void testExecuteUpdate()
	{
		enter();
		try
		{
			clean();
			_stmtAltibase.executeUpdate(_sSQL_DDL);
		}
		catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
		catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
		finally
		{
			try { clean(); }
			catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
		}
	} /* testExecuteUpdate */

	@Test
	@Override
	public void testExecuteUpdate_String_int()
	{
		enter();
		try
		{
			clean();
			_stmtAltibase.executeUpdate(_sSQL_DDL, Statement.NO_GENERATED_KEYS);
		}
		catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
		catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
		catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
		finally
		{
			try { clean(); }
			catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
		}
	} /* testExecuteUpdate_String_int */

	@Test
	@Override
	public void testExecuteUpdate_String_AInt()
	{
		enter();
		try
		{
			clean();
			_stmtAltibase.executeUpdate(_sSQL_DDL, new int[] {1});
		}
		catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
		catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
		catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
		finally
		{
			try { clean(); }
			catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
		}
	} /* testExecuteUpdate_String_AInt */

	@Test
	@Override
	public void testExecuteUpdate_String_AString()
	{
		enter();
		try
		{
			clean();
			_stmtAltibase.executeUpdate(_sSQL_DDL, new String[]{"COL_A"});
		}
		catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
		catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
		catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
		finally
		{
			try { clean(); }
			catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
		}
	} /* testExecuteUpdate */

	@Test
	@Override
	public void testGetGeneratedKeys()
	{
		enter();
		try { _stmtAltibase.getGeneratedKeys(); }
		catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
		catch(SQLException se) { System.out.println(EU.getExceptionMessage(se)); }
	} /* testGetGeneratedKeys */

	@Test
	@Override
	public void testExecute_String_AInt()
	{
		enter();
		try { _stmtAltibase.execute(_sSQL_QUERY, new int[] {1}); }
		catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
		catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
		catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
	} /* testExecute_String_AInt */

	@Test
	@Override
	public void testExecute_String_AString()
	{
		enter();
		try { _stmtAltibase.execute(_sSQL_QUERY, new String[]{"COL_A"}); }
		catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
		catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
		catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
	} /* testExecute_String_AString */

	@Test
	public void testExecuteQuery()
	{
		enter();
		try { _stmtAltibase.executeQuery("SELECT 1 FROM dual"); }
		catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
		catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
	} /* testExecuteQuery */

	@Test
	public void testExecuteQueryWithUpdate()
	{
		enter();
		try { _stmtAltibase.executeUpdate(_sSQL_CLEAN); }
		catch(SQLException se) {}
		try
		{
			int iResult = _stmtAltibase.executeUpdate("CREATE TABLE TESTTABLE(ID INTEGER, CVARCHAR VARCHAR(255))");
			assertEquals("CREATE TABLE failed!",0,iResult);
			Statement stmt = _stmtAltibase.getConnection().createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE,ResultSet.HOLD_CURSORS_OVER_COMMIT);
			ResultSet rs = stmt.executeQuery("SELECT ID, CVARCHAR FROM TESTTABLE");
			rs.moveToInsertRow();
			rs.updateInt("ID", 2);
			rs.updateString("CVARCHAR", "TESTSTRING");
			rs.insertRow(); // this fails without primary keys ...
			rs.close();
		}
		catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
		catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
	}

	@Test
	public void testGetResultSet()
	{
		enter();
		try
		{
			_stmtAltibase.execute("SELECT * FROM v$session");
			_stmtAltibase.getResultSet();
		}
		catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
	} /* testGetResultSet */

	@Test
	public void testExecuteSelectSizes()
	{
		StringBuilder sbSql = new StringBuilder("SELECT COUNT(*) AS RECORDS");
		for (int iColumn = 0; iColumn < TestSqlDatabase._listCdSimple.size(); iColumn++)
		{
			TestColumnDefinition tcd = TestSqlDatabase._listCdSimple.get(iColumn);
			if (tcd.getType().startsWith("BLOB") ||
							tcd.getType().startsWith("CLOB") ||
							tcd.getType().startsWith("NCLOB"))
			{
				sbSql.append(",\r\n  SUM(OCTET_LENGTH(");
				sbSql.append(SqlLiterals.formatId(tcd.getName()));
				sbSql.append(")) AS ");
				sbSql.append(SqlLiterals.formatId(tcd.getName()+"_SIZE"));
			}
		}
		sbSql.append("\r\nFROM ");
		sbSql.append(TestSqlDatabase.getQualifiedSimpleTable().format());
		try
		{
			ResultSet rs = _stmtAltibase.executeQuery(sbSql.toString());
			ResultSetMetaData rsmd = rs.getMetaData();
			while(rs.next())
			{
				for (int iColumn = 0; iColumn < rsmd.getColumnCount(); iColumn++)
				{
					String sColumnName = rsmd.getColumnLabel(iColumn+1);
					long lValue = rs.getLong(iColumn+1);
					System.out.println(sColumnName+": "+String.valueOf(lValue));
				}
			}
			rs.close();
		}
		catch(SQLException se) { System.out.println(EU.getExceptionMessage(se)); }
	} /* testExecuteSelectSize */

	@Test
	public void testDropTableCascade()
	{
		enter();
		try
		{
			_stmtAltibase.executeUpdate("DROP TABLE "+TestSqlDatabase.getQualifiedSimpleTable().format()+" CASCADE");
			// restore the database
			tearDown();
			setUpClass();
		}
		catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
		catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
	}

	@Test
	public void testCreateTable()
	{
		enter();
		try { _stmtAltibase.executeUpdate("DROP TABLE BUG479 CASCADE"); }
		catch(SQLException se) { System.out.println(EU.getExceptionMessage(se)); }
		try
		{
			String sSql = "CREATE TABLE BUG479(ID INT, TEXT VARCHAR)";
			int iResult = _stmtAltibase.executeUpdate(sSql);
			System.out.println("Result: "+String.valueOf(iResult));
		}
		catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
		catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
		finally
		{
			try { clean(); }
			catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
		}
	} /* testCreateTable */

	@Test
	@Override
	public void testExecute()
	{
		enter();
		try { _stmtAltibase.execute(_sSQL_QUERY); }
		catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
		catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
	} /* testExecute */

	@Test
	@Override
	public void testExecute_String_int()
	{
		enter();
		try { _stmtAltibase.execute(_sSQL_QUERY, Statement.NO_GENERATED_KEYS); }
		catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
		catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
		catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
	} /* testExecute_String_int */

	@Test
	@Override
	public void testGetUpdateCount()
	{
		enter();
		try { _stmtAltibase.getUpdateCount(); }
		catch(SQLException se) { /* Altibase throws SQLException */ }
	} /* testGetUpdateCount */
}
