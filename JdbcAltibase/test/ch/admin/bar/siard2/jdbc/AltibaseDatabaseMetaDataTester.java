package ch.admin.bar.siard2.jdbc;

import java.sql.*;
import java.util.*;
import java.util.regex.*;

import static org.junit.Assert.*;

import Altibase.jdbc.driver.AltibaseTypes;
import org.junit.*;

import ch.admin.bar.siard2.jdbcx.*;
import ch.admin.bar.siard2.altibase.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;
import ch.enterag.utils.jdbc.*;
import ch.enterag.sqlparser.identifier.*;

public class AltibaseDatabaseMetaDataTester extends BaseDatabaseMetaDataTester
{
  private static final ConnectionProperties _cp = new ConnectionProperties();
  private static final String _sDB_URL = AltibaseDriver.getUrl("//" + _cp.getHost() + ":" + _cp.getPort()+"/"+_cp.getCatalog()+"?date_format=yyyy-MM-dd");
  private static final String _sDB_USER = _cp.getUser();
  private static final String _sDB_PASSWORD = _cp.getPassword();
  private static final String _sDB_CATALOG = _cp.getCatalog();
  private static final String _sDBA_USER = _cp.getDbaUser();
  private static final String _sDBA_PASSWORD = _cp.getDbaPassword();
  private static Pattern _patTYPE = Pattern.compile("^(.*?)(\\(\\s*((\\d+)(\\s*,\\s*(\\d+))?)\\s*\\))?$");

	private AltibaseDatabaseMetaData _dmdAltibase = null;

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
	public void setUp() throws Exception
	{
		try
		{
			AltibaseDataSource dsAltibase = new AltibaseDataSource();
			dsAltibase.setUrl(_sDB_URL);
			dsAltibase.setUser(_sDB_USER);
			dsAltibase.setPassword(_sDB_PASSWORD);
			AltibaseConnection connAltibase = (AltibaseConnection)dsAltibase.getConnection();
			connAltibase.setAutoCommit(false);
			_dmdAltibase = (AltibaseDatabaseMetaData)connAltibase.getMetaData();
			setDatabaseMetaData(_dmdAltibase);
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	} /* setUp */

	@Test
	public void testClass()
	{
		assertEquals("Wrong result set meta class!", AltibaseDatabaseMetaData.class, _dmdAltibase.getClass());
	} /* testClass */

	@Test
	@Override
	public void testGetTypeInfo()
	{
		enter();
		try
		{
			print(_dmdAltibase.getTypeInfo());
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	} /* getTypeInfo */

	private void testColumns(QualifiedId qiTable, List<TestColumnDefinition> listCd)
	{
		try
		{
			Map<String, TestColumnDefinition> mapCd = new HashMap<String, TestColumnDefinition>();
			for (int iColumn = 0; iColumn < listCd.size(); iColumn++)
			{
				TestColumnDefinition tcd = listCd.get(iColumn);
				mapCd.put(tcd.getName(), tcd);
			}
			ResultSet rs = _dmdAltibase.getColumns(
					qiTable.getCatalog(),
					_dmdAltibase.toPattern(qiTable.getSchema()),
					_dmdAltibase.toPattern(qiTable.getName()),
					"%");
			if ((rs != null) && (!rs.isClosed()))
			{
				while (rs.next())
				{
					String sCatalogName = rs.getString("TABLE_CAT");
					String sSchemaName = rs.getString("TABLE_SCHEM");
					String sTableName = rs.getString("TABLE_NAME");
					if (sCatalogName != null)
					{
						if (!_sDB_CATALOG.equalsIgnoreCase(sCatalogName))
							fail("Unexpected catalog: " + sCatalogName);
					}
					if (!qiTable.getName().equals(sTableName.toUpperCase()))
						fail("Unexpected table: " + sTableName);
					String sColumnName = rs.getString("COLUMN_NAME");
					int iDataType = rs.getInt("DATA_TYPE");
					String sTypeName = rs.getString("TYPE_NAME");
					long lColumnSize = rs.getLong("COLUMN_SIZE");
					// int iDecimalDigits = rs.getInt("DECIMAL_DIGITS");
					switch (sTypeName)
					{
						case "CHAR":
							assertEquals("Invalid char mapping!", Types.CHAR, iDataType);
							break;
						case "VARCHAR":
							assertEquals("Invalid varchar mapping!", Types.VARCHAR, iDataType);
							break;
						case "NCHAR":
							assertEquals("Invalid nchar mapping!", Types.NCHAR, iDataType);
							break;
						case "NVARCHAR":
							assertEquals("Invalid nvarchar mapping!", AltibaseTypes.NVARCHAR, iDataType);
							break;
						case "BYTE":
							assertEquals("Invalid byte mapping!", Types.BINARY, iDataType);
							break;
						case "VARBYTE":
							assertEquals("Invalid varbyte mapping!", Types.VARBINARY, iDataType);
							break;
						case "BLOB":
							assertEquals("Invalid blob mapping!", Types.BLOB, iDataType);
							break;
						case "CLOB":
							assertEquals("Invalid clob mapping!", Types.CLOB, iDataType);
							break;
						case "INTEGER":
							assertEquals("Invalid int mapping!", Types.INTEGER, iDataType);
							break;
						case "SMALLINT":
							assertEquals("Invalid smallint mapping!", Types.SMALLINT, iDataType);
							break;
						case "BIGINT":
							assertEquals("Invalid bigint mapping!", Types.BIGINT, iDataType);
							break;
						case "decimal":
							assertEquals("Invalid decimal mapping!", Types.DECIMAL, iDataType);
							break;
						case "NUMERIC":
							assertEquals("Invalid numeric mapping!", Types.NUMERIC, iDataType);
							break;
						case "REAL":
							assertEquals("Invalid real mapping!", Types.REAL, iDataType);
							break;
						case "FLOAT":
							assertEquals("Invalid float mapping!", Types.FLOAT, iDataType);
							break;
						case "DOUBLE":
							assertEquals("Invalid double mapping!", Types.DOUBLE, iDataType);
							break;
						case "BIT":
							assertEquals("Invalid bit mapping!", Types.BINARY, iDataType);
							break;
						case "bool":
							assertEquals("Invalid bool mapping!", Types.BOOLEAN, iDataType);
							break;
						case "DATE":
							assertEquals("Invalid date mapping!", Types.TIMESTAMP, iDataType);
							break;
						case "GEOMETRY":
							assertEquals("Invalid geometry mapping!", Types.VARBINARY, iDataType);
							break;
						default:
							fail("Invalid type " + sTypeName + "!");
							break;
					}
					TestColumnDefinition tcd = mapCd.get(sColumnName);
					String sType = tcd.getType();
					if (!sType.startsWith("INTERVAL"))
					{
						// parse type
						Matcher matcher = _patTYPE.matcher(sType);
						if (matcher.matches())
						{
							/* compare column size with explicit precision */
							String sPrecision = matcher.group(4);
							if (sPrecision != null)
							{
								int iPrecision = Integer.parseInt(sPrecision);
								if ((iDataType == Types.DOUBLE) ||
										(iDataType == Types.FLOAT) ||
										(iDataType == Types.REAL) ||
										(iDataType == Types.CLOB) ||
										(iDataType == Types.BLOB) ||
										(iDataType == Types.TIMESTAMP) ||
										(iDataType == Types.TIME) ||
										(iDataType == Types.NVARCHAR) || // Altibas NClob converted into NVARCHAR
										(sTypeName.startsWith("datetimeoffset")))
								{
									assertTrue("Explicit precision too large!", (iPrecision <= lColumnSize));
									lColumnSize = iPrecision;
								}
								assertEquals("Explicit precision does not match!", iPrecision, lColumnSize);
							}
						}
					}
				}
			}
			else
				fail("Invalid column meta data result set!");
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	} /* testColumns */

	@Test
	public void testColumnsAltibaseSimple()
	{
		enter();
		testColumns(TestAltibaseDatabase.getQualifiedSimpleTable(), TestAltibaseDatabase._listCdSimple);
	} /* testColumnsAltibaseSimple */

	@Test
	public void testColumnsAltibaseComplex()
	{
		enter();
		testColumns(TestAltibaseDatabase.getQualifiedComplexTable(), TestAltibaseDatabase._listCdComplex);
	} /* testColumnsAltibaseComplex */

	@Test
	public void testColumnsSqlSimple()
	{
		enter();
		testColumns(TestSqlDatabase.getQualifiedSimpleTable(), TestSqlDatabase._listCdSimple);
	} /* testColumnsSqlSimple */

	@Test
	public void testColumnsSqlComplex()
	{
		enter();
		testColumns(TestSqlDatabase.getQualifiedComplexTable(), TestSqlDatabase._listCdComplex);
	} /* testColumnsSqlSimple */

	@Test
	@Override
	public void testGetTableTypes()
	{
		enter();
		try
		{
			print(_dmdAltibase.getTableTypes());
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	}

	@Test
	@Override
	public void testGetProcedures()
	{
		enter();
		try
		{
			print(_dmdAltibase.getProcedures(null, TestSqlDatabase._sTEST_SCHEMA, "%"));
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	}

	@Test
	@Override
	public void testGetProcedureColumns()
	{
		enter();
		try
		{
			print(_dmdAltibase.getProcedureColumns(null, TestSqlDatabase._sTEST_SCHEMA, "%", "%"));
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	}

	@Test
	@Override
	public void testGetTables()
	{
		enter();
		try
		{
			print(_dmdAltibase.getTables(null, TestSqlDatabase._sTEST_SCHEMA, "%", new String[] { "TABLE" }));
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	}

	@Test
	public void testGetViews()
	{
		enter();
		try
		{
			print(_dmdAltibase.getTables(null, TestSqlDatabase._sTEST_SCHEMA, "%", new String[] { "VIEW" }));
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	}

	@Test
	@Override
	public void testGetUDTs()
	{
		enter();
		try
		{
			print(_dmdAltibase.getUDTs(null, "%", "%", null));
		}
		catch (SQLException se)
		{ /* Altibase does not support UDT */ }
	}

	@Test
	@Override
	public void testGetAttributes()
	{
		enter();
		try
		{
			print(_dmdAltibase.getAttributes(null, TestSqlDatabase._sTEST_SCHEMA, "%", "%"));
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	}

	@Test
	@Override
	public void testGetPrimaryKeys()
	{
		enter();
		QualifiedId qiTable = TestAltibaseDatabase.getQualifiedSimpleTable();
		try
		{
			print(_dmdAltibase.getPrimaryKeys(qiTable.getCatalog(), qiTable.getSchema(), qiTable.getName()));
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	}

	@Test
	@Override
	public void testGetImportedKeys()
	{
		enter();
		QualifiedId qiTable = TestSqlDatabase.getQualifiedComplexTable();
		try
		{
			print(_dmdAltibase.getImportedKeys(qiTable.getCatalog(), qiTable.getSchema(), qiTable.getName()));
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	}

	@Test
	@Override
	public void testGetExportedKeys()
	{
		enter();
		QualifiedId qiTable = TestSqlDatabase.getQualifiedSimpleTable();
		try
		{
			print(_dmdAltibase.getExportedKeys(qiTable.getCatalog(), qiTable.getSchema(), qiTable.getName()));
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	}

	@Test
	@Override
	public void testGetCrossReference()
	{
		enter();
		try
		{
			print(_dmdAltibase.getCrossReference(null, TestSqlDatabase._sTEST_SCHEMA, "%", null, TestSqlDatabase._sTEST_SCHEMA, "%"));
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	}

	@Test
	public void testGetColumnPrivileges()
	{
		enter();
		try
		{
			print(_dmdAltibase.getColumnPrivileges(null, null, "%", "%"));
		}
		/* do not fail: Altibase throws exception because of insufficient privileges of testuser */
		catch (SQLException se)
		{
			System.err.println(EU.getExceptionMessage(se));
		}
	}

	@Test
	public void testGetVersionColumns()
	{
		enter();
		QualifiedId qiTable = TestSqlDatabase.getQualifiedSimpleTable();
		try
		{
			print(_dmdAltibase.getVersionColumns(
					qiTable.getCatalog(),
					qiTable.getSchema(),
					qiTable.getName()));
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	}

	@Test
	public void testGetBestRowIdentifier()
	{
		enter();
		QualifiedId qiTable = TestSqlDatabase.getQualifiedSimpleTable();
		try
		{
			print(_dmdAltibase.getBestRowIdentifier(
					qiTable.getCatalog(),
					qiTable.getSchema(),
					qiTable.getName(),
					DatabaseMetaData.bestRowUnknown, true));
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
	}
} /* class AltibaseDatabaseMetaData */
