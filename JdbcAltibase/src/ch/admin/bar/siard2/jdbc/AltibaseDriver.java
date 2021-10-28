/*======================================================================
AltibaseDriver implements a wrapped Altibase Driver.
Version     : $Id: $
Application : SIARD2
Description : AltibaseDriver implements a wrapped Altibase Driver.
Platform    : Java 7
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 08.03.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.sql.*;
import java.util.*;
import ch.enterag.utils.jdbc.*;
import ch.enterag.utils.logging.*;

/*====================================================================*/
/** AltibaseDriver implements a wrapped Altibase Driver.
 * @author YounJung Park
 */
public class AltibaseDriver
		extends BaseDriver
		implements Driver
{
	/** logger */
	private static IndentLogger _il = IndentLogger.getIndentLogger(AltibaseDriver.class.getName());
	/** protocol sub scheme for Altibase JDBC URL */
	public static final String sAltibase_SCHEME = "Altibase";
	/** URL prefix for Altibase JDBC URL */
	public static final String sAltibase_URL_PREFIX = sJDBC_SCHEME+":"+sAltibase_SCHEME+":";
	/** URL for database name.
	 * @param sDatabaseName host:port/dbname, e.g. localhost:20300/mydb
	 * @return JDBC URL for thin driver.
	 */
	public static String getUrl(String sDatabaseName)
	{
		String sUrl = sDatabaseName;
		if (!sUrl.startsWith(sAltibase_URL_PREFIX))
			sUrl = sAltibase_URL_PREFIX+sDatabaseName;
		return sUrl;
	} /* getUrl */

	/** register this driver, replacing original Altibase driver
	 */
	public static void register()
	{
		try { BaseDriver.register(new AltibaseDriver(), "Altibase.jdbc.driver.AltibaseDriver", "jdbc:Altibase://localhost:20300/mydb"); }
		catch(Exception e) { throw new Error(e); }
	}

	/** replace Altibase driver by this, when this class is loaded */
	static
	{
		register();
	}

	/*------------------------------------------------------------------*/
	/** {@inheritDoc}*/
	@Override
	public boolean acceptsURL(String url) throws SQLException
	{
		_il.enter(url);
		boolean bAccepts = url.startsWith("jdbc:Altibase:");
		_il.exit(bAccepts);
		return bAccepts;
	} /* acceptsUrl */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc}
	 * returns the appropriately wrapped Altibase Connection.
	 */
	@Override
	public Connection connect(String url, Properties info)
			throws SQLException
	{
		_il.enter(url, info);
		Connection conn = super.connect(url, info);
		if (conn != null)
			conn = new AltibaseConnection(conn);
		_il.exit(conn);
		return conn;
	} /* connect */

} /* class AltibaseDriver */
