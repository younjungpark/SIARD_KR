/*======================================================================
OracleStatement  implements a wrapped Oracle Statement.
Version     : $Id: $
Application : SIARD2
Description : Db2Statement implements a wrapped DB/2 Statement.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 20.06.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.sql.*;
import ch.enterag.utils.jdbc.*;

/*====================================================================*/
/**
 * OracleStatement implements a wrapped Oracle Statement
 * 
 * @author Simon Jutz
 */
public class OracleStatement 
	extends BaseStatement 
	implements Statement 
{
  private Connection _conn = null;
  
	/*------------------------------------------------------------------*/
	/** constructor
	 * @param stmtWrapped statement to be wrapped
	 * @throws SQLException if a database error occurred.
	 */
	public OracleStatement(Statement stmtWrapped)
	  throws SQLException
	{
		super(stmtWrapped);
    _conn = new OracleConnection(super.getConnection());
	} /* constructor */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public Connection getConnection() throws SQLException 
	{
		return _conn;
	} /* getConnection */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} 
	 * Return OracleResultSet. Convert JdbcSQLException from Oracle
	 * into SQLFeatureNotSupportedError.
	 */
	@Override
	public ResultSet executeQuery(String sql) throws SQLException 
	{
		ResultSet rs = null;
		String sNative = getConnection().nativeSQL(sql);
		rs = new OracleResultSet(super.executeQuery(sNative),getConnection(),this);
		return rs;
	} /* executeQuery */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} 
	 * Convert JdbcSQLException from Oracle into
	 * SQLFeatureNotSupportedError.
	 */
	@Override
	public int executeUpdate(String sql) throws SQLException 
	{
		int iResult = super.executeUpdate(getConnection().nativeSQL(sql));
		return iResult;
	} /* executeUpdate */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} 
	 * Convert JdbcSQLException from Oracle into
	 * SQLFeatureNotSupportedError.
	 */
	@Override
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException
	{
		int iResult = super.executeUpdate(getConnection().nativeSQL(sql), autoGeneratedKeys);
		return iResult;
	} /* executeUpdate */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} 
	 * Convert JdbcSQLException from Oracle into
	 * SQLFeatureNotSupportedError.
	 */
	@Override
	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException
	{
		int iResult = super.executeUpdate(getConnection().nativeSQL(sql), columnIndexes);
		return iResult;
	} /* executeUpdate */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} 
	 * Convert JdbcSQLException from Oracle into
	 * SQLFeatureNotSupportedError.
	 */
	@Override
	public int executeUpdate(String sql, String[] columnNames) throws SQLException 
	{
		int iResult = super.executeUpdate(getConnection().nativeSQL(sql), columnNames);
		return iResult;
	} /* executeUpdate */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean execute(String sql) throws SQLException
  {
    return super.execute(getConnection().nativeSQL(sql));
  } /* execute */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean execute(String sql, int autoGeneratedKeys)
      throws SQLException
  {
    return super.execute(getConnection().nativeSQL(sql), autoGeneratedKeys);
  } /* execute */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean execute(String sql, int[] columnIndexes)
      throws SQLException
  {
    return super.execute(getConnection().nativeSQL(sql), columnIndexes);
  } /* execute */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean execute(String sql, String[] columnNames)
      throws SQLException
  {
    return super.execute(getConnection().nativeSQL(sql), columnNames);
  } /* execute */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} 
	 * Return OracleResultSet. Convert JdbcSQLException from Oracle
	 * into SQLFeatureNotSupportedError.
	 */
	@Override
	public ResultSet getResultSet() throws SQLException 
	{
		ResultSet rs = new OracleResultSet(super.getResultSet(),getConnection(),this);
		return rs;
	} /* getResultSet */

}
