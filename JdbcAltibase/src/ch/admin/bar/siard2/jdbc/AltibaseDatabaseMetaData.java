/*======================================================================
AltibaseDatabaseMetaData implements a wrapped Altibase DatabaseMetaData.
Version     : $Id: $
Application : SIARD2
Description : AltibaseDatabaseMetaData implements a wrapped Altibase DatabaseMetaData.
Platform    : Java 7
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 27.10.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.sql.*;

import ch.enterag.utils.jdbc.*;
import ch.enterag.sqlparser.*;

/* =============================================================================== */
/**
 * AltibaseDatabaseMetaData implements a wrapped Altibase DatabaseMetaData
 * @author YounJung Park
 */
public class AltibaseDatabaseMetaData extends BaseDatabaseMetaData implements DatabaseMetaData
{
	private AltibaseConnection _conn = null;
	/**
	 * Constructor
	 * @param dmdWrapped database meta data to wrapped
	 */
	public AltibaseDatabaseMetaData(DatabaseMetaData dmdWrapped, AltibaseConnection conn)
	{
		super(dmdWrapped);
		_conn = conn;
	} /* constructor */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public Connection getConnection() throws SQLException
	{
		return _conn;
	} /* getConnection */

	/* ------------------------------------------------------------------------ */
	@SuppressWarnings("unused")
	private String getNameCondition(String sColumn, String sName)
	{
		StringBuilder sbCondition = new StringBuilder();
		if (sName != null)
		{
			sbCondition.append("  AND ");
			if (sName.startsWith("\""))
			{
				sbCondition.append(sColumn);
				sbCondition.append(" = ");
				sbCondition.append(SqlLiterals.formatStringLiteral(sName));
			}
			else
			{
				sbCondition.append("LOWER(");
				sbCondition.append(sColumn);
				sbCondition.append(") = LOWER(");
				sbCondition.append(SqlLiterals.formatStringLiteral(sName));
				sbCondition.append(")");
			}
			sbCondition.append("\r\n");
		}
		return sbCondition.toString();
	} /* getNameCondition */
} /* class AltibaseDatabaseMetaData */
