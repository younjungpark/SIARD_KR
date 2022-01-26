/*======================================================================
AltibaseConnection implements a wrapped Altibase Connection.
Version     : $Id: $
Application : SIARD2
Description : AltibaseConnection implements a wrapped Altibase Connection.
Platform    : Java 7
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 26.10.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.sql.*;

import ch.enterag.utils.jdbc.*;
import ch.enterag.utils.logging.*;
import ch.enterag.sqlparser.*;
import ch.admin.bar.siard2.altibase.*;

/*====================================================================*/
/** AltibaseConnection implements a wrapped Altibase Connection.
 * @author YounJung Park
 */
public class AltibaseConnection extends BaseConnection implements Connection
{
	// logger
	private static IndentLogger _il = IndentLogger.getIndentLogger(AltibaseConnection.class.getName());

	/*------------------------------------------------------------------*/
	/** constructor
	 * @param connWrapped connection to be wrapped.
	 */
	public AltibaseConnection(Connection connWrapped)
	{
		super(connWrapped);
	} /* constructor */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public String nativeSQL(String sql)
	{
		_il.enter(sql);
		SqlFactory sf = new AltibaseSqlFactory();
		SqlStatement ss = sf.newSqlStatement();
		ss.parse(sql);
		sql = ss.format();
		_il.exit(sql);
		return sql;
	} /* nativeSQL */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc}
	 * wraps database meta data.
	 */
	@Override
	public DatabaseMetaData getMetaData() throws SQLException
	{
		return new AltibaseDatabaseMetaData(super.getMetaData(), this);
	} /* getMetadata */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc}
	 * wraps statement.
	 */
	@Override
	public Statement createStatement() throws SQLException
	{
		return new AltibaseStatement(super.createStatement(), this);
	} /* createStatement */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc}
	 * wraps statement.
	 */
	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException
	{
		return new AltibaseStatement(super.createStatement(resultSetType, resultSetConcurrency), this);
	} /* createStatement */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc}
	 * wraps statement.
	 */
	@Override
	public Statement createStatement(int resultSetType,
									 int resultSetConcurrency, int resultSetHoldability)
			throws SQLException
	{
		return new AltibaseStatement(super.createStatement(resultSetType,
				resultSetConcurrency, resultSetHoldability), this);
	} /* createStatement */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public PreparedStatement prepareStatement(String sql)
			throws SQLException
	{
		PreparedStatement ps = super.prepareStatement(nativeSQL(sql));
		return ps;
	} /* prepareStatement */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public CallableStatement prepareCall(String sql)
			throws SQLException
	{
		CallableStatement cs = super.prepareCall(nativeSQL(sql));
		return cs;
	} /* prepareCall */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public PreparedStatement prepareStatement(String sql,
											  int resultSetType, int resultSetConcurrency)
			throws SQLException
	{
		PreparedStatement ps = super.prepareStatement(nativeSQL(sql), resultSetType, resultSetConcurrency);
		return ps;
	} /* prepareStatement */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public CallableStatement prepareCall(String sql,
										 int resultSetType, int resultSetConcurrency)
			throws SQLException
	{
		CallableStatement cs = super.prepareCall(nativeSQL(sql), resultSetType, resultSetConcurrency);
		return cs;
	} /* prepareCall */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public PreparedStatement prepareStatement(String sql,
											  int resultSetType, int resultSetConcurrency,
											  int resultSetHoldability)
			throws SQLException
	{
		PreparedStatement ps = super.prepareStatement(nativeSQL(sql), resultSetType,
				resultSetConcurrency, resultSetHoldability);
		return ps;
	} /* prepareStatement */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public CallableStatement prepareCall(String sql, int resultSetType,
										 int resultSetConcurrency, int resultSetHoldability)
			throws SQLException
	{
		CallableStatement cs = super.prepareCall(nativeSQL(sql), resultSetType,
				resultSetConcurrency, resultSetHoldability);
		return cs;
	} /* prepareCall */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
			throws SQLException
	{
		PreparedStatement ps = super.prepareStatement(nativeSQL(sql), autoGeneratedKeys);
		return ps;
	} /* prepareStatement */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
			throws SQLException
	{
		PreparedStatement ps = super.prepareStatement(nativeSQL(sql), columnIndexes);
		return ps;
	} /* prepareStatement */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames)
			throws SQLException
	{
		PreparedStatement ps = super.prepareStatement(nativeSQL(sql), columnNames);
		return ps;
	} /* prepareStatement */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc}
	 * return AltibaseSqlXml.
	 */
	@Override
	public SQLXML createSQLXML()
			throws SQLException
	{
		SQLXML sqlxml = AltibaseSqlXml.newInstance(null);
		return sqlxml;
	} /* createSQLXML */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc}
	 */
	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException
	{
		throw new SQLFeatureNotSupportedException("Altibase supports no arrays as table columns!");
	} /* createArrayOf */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc}
	 */
	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException
	{
		throw new SQLFeatureNotSupportedException("UDT creation not supported for Altibase database!");
	} /* createStruct */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public NClob createNClob()
			throws SQLException
	{
		return new AltibaseNClob();
	} /* createNClob */
} /* class H2Connection */
