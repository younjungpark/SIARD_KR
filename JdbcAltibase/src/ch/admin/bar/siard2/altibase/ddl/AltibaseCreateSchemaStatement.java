/*======================================================================
AltibaseCreateSchemaStatement overrides CreateSchemaStatement of SQL parser.
Application : SIARD2
Description : AltibaseCreateSchemaStatement overrides CreateSchemaStatement
			        of SQL parser, because in Altibase, schemes are created by
			        creating users.
Platform    : Java 7
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 20.06.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.altibase.ddl;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.ddl.*;

/*====================================================================*/
/** AltibaseCreateSchemaStatement implements the formatting of CREATE
 * SCHEMA statements
 * @author YounJung Park
 */
public class AltibaseCreateSchemaStatement extends CreateSchemaStatement
{
	private static final String _sSCHEMA_PASSWORD = "MANAGER";
	public static String getSchemaPassword() { return _sSCHEMA_PASSWORD; };
	private static final String _sDEFAULT_PERMANENT_TABLESPACE = "USERS";

	/*------------------------------------------------------------------*/
	/** * format the create schema statement as a create user statement
	 * not granting anything to the user and using the standard password
	 * SCHEMAPWD.
	 * @return the SQL string corresponding to a create schema statement
	 */
	@Override
	public String format()
	{
		String sStatement = null;
		sStatement = K.CREATE.getKeyword() + sSP + K.USER.getKeyword() +
				sSP + SqlLiterals.quoteId(getSchemaName().getSchema()) +
				sSP + "IDENTIFIED" + sSP + K.BY.getKeyword() + sSP + _sSCHEMA_PASSWORD;
		return sStatement;
	} /* format */

	/*------------------------------------------------------------------*/
	/** constructor with factory only to be called by factory.
	 * @param sf factory.
	 */
	public AltibaseCreateSchemaStatement(SqlFactory sf) {
		super(sf);
	} /* constructor */

} /* class AltibaseCreateStatement */
