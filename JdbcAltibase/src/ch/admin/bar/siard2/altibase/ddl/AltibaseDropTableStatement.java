/*======================================================================
DropTableStatement overrides DropTableStatement of SQL parser.
Version     : $Id: $
Application : SIARD2
Description : AltibaseDropTableStatement overrides DropTableStatement
			  of SQL parser
Platform    : Java 7
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 21.06.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.altibase.ddl;

import ch.enterag.sqlparser.K;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.ddl.DropTableStatement;
import ch.enterag.sqlparser.ddl.enums.DropBehavior;

/*====================================================================*/
/** AltibaseDropTableStatement implements the formatting of DROP
 * TABLE statements
 * @author YounJung Park
 */
public class AltibaseDropTableStatement	extends DropTableStatement
{
	/*------------------------------------------------------------------*/
	/**
	 * format the drop schema statement
	 * @return the SQL string corresponding to a drop schema statement
	 */
	@Override
	public String format()
	{
		String sStatement = null;

		sStatement = K.DROP.getKeyword() + sSP + K.TABLE.getKeyword() + sSP + getTableName().quote();
		if (getDropBehavior() == DropBehavior.CASCADE)
			sStatement += sSP + K.CASCADE.getKeyword();

		return sStatement;
	} /* format */

	/*------------------------------------------------------------------*/
	/** constructor with factory only to be called by factory.
	 * @param sf factory.
	 */
	public AltibaseDropTableStatement(SqlFactory sf) {
		super(sf);
	} /* constructor */
}
