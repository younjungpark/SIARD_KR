package ch.admin.bar.siard2.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.enterag.utils.EU;
import ch.enterag.utils.base.ConnectionProperties;

public class AltibaseDriverTester
{
	private static final ConnectionProperties _cp = new ConnectionProperties();

	private static final String _sDB_URL = AltibaseDriver.getUrl("//" + _cp.getHost() + ":" + _cp.getPort() + "/" + _cp.getCatalog() + "?date_format=yyyy-MM-dd");
	private static final String _sDBA_USER = _cp.getDbaUser();
	private static final String _sDBA_PASSWORD = _cp.getDbaPassword();
	private static final String _sDRIVER_CLASS = "ch.admin.bar.siard2.jdbc.AltibaseDriver";
	private static final String _sINVALID_ALTIBASE_URL = "jdbc:oracle:thin:@//localhost";

	private Driver _driver = null;
	private Connection _conn = null;

	@Before
	public void setUp()
	{
		try
		{
			Class.forName(_sDRIVER_CLASS);
			_driver = DriverManager.getDriver(_sDB_URL);
			_conn = DriverManager.getConnection(_sDB_URL, _sDBA_USER, _sDBA_PASSWORD);
		}
		catch (SQLException se)
		{
			fail(EU.getExceptionMessage(se));
		}
		catch (ClassNotFoundException cnfe)
		{
			fail(EU.getExceptionMessage(cnfe));
		}
	} /* setUp */

	@After
	public void tearDown() throws Exception
	{
		try
		{
			if ((_conn != null) && (!_conn.isClosed()))
			{
				_conn.close();
			}
			else
			{
				fail("Connection cannot be closed!");
			}
		}
		catch (SQLException se)
		{
			fail(se.getClass().getName() + ": " + se.getMessage());
		}
	} /* tearDown */

	@Test
	public void testWrapping()
	{
		assertSame("Registration of driver wrapper failed!", AltibaseDriver.class, _driver.getClass());
		assertSame("Registration of connection wrapper failed!", AltibaseConnection.class, _conn.getClass());
	} /* testWrapping */

	@Test
	public void testCompliant()
	{
		assertSame("Altibase driver is suddenly JDBC compliant!", false, _driver.jdbcCompliant());
	} /* testCompliant */

	@Test
	public void testAcceptsURL()
	{
		try
		{
			assertSame("Valid Altibase URL not accepted!", true, _driver.acceptsURL(_sDB_URL));
			assertSame("Invalid Altibase URL accepted!", false, _driver.acceptsURL(_sINVALID_ALTIBASE_URL));
		}
		catch (SQLException se)
		{
			fail(se.getClass().getName() + ": " + se.getMessage());
		}
	} /* testAcceptsURL */

	@Test
	public void testVersion()
	{
		int iMajorVersion = _driver.getMajorVersion();
		int iMinorVersion = _driver.getMinorVersion();
		String sVersion = String.valueOf(iMajorVersion) + "." + String.valueOf(iMinorVersion);
		assertEquals("Wrong Altibase version " + sVersion + " found!", "7.2", sVersion);
	} /* testVersion */

	@Test
	public void testDriverProperties()
	{
		try
		{
			DriverPropertyInfo[] aPropInfo = _driver.getPropertyInfo(_sDB_URL, new Properties());
			for (DriverPropertyInfo propInfo : aPropInfo)
			{
				System.out.println(propInfo.name + ": " + propInfo.value + " (" + propInfo.description + ")");
			}
			assertEquals("Unexpected driver properties!", 39, aPropInfo.length);
		}
		catch (SQLException se)
		{
			fail(se.getClass().getName() + ": " + se.getMessage());
		}
	} /* testDriverProperties */

} /* class AltibaseDriverTester */