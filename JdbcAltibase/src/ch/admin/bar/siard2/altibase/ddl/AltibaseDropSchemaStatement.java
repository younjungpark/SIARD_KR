/*======================================================================
AltibaseDropSchemaStatement overrides DropSchemaStatement of SQL parser.
Version     : $Id: $
Application : SIARD2
Description : AltibaseDropSchemaStatement overrides DropSchemaStatement of
              SQL because in Altibase schemas are users.
              Also we need to quote all identifiers rather than formatting
              them.
Platform    : Java 7
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 09.06.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.altibase.ddl;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.ddl.*;
import ch.enterag.sqlparser.ddl.enums.*;

/*====================================================================*/
/** AltibaseDropSchemaStatement overrides DropSchemaStatement of SQL
 * because in Altibase schemas are users.
 * Also we need to quote all identifiers rather than formatting them.
 * @author YounJung Park
 */
public class AltibaseDropSchemaStatement extends DropSchemaStatement
{
	/*------------------------------------------------------------------*/
	/** format the drop schema statement for Altibase as a drop user statement.
	 * @return the SQL string corresponding to the fields of the drop schema statement.
	 */
	@Override
	public String format()
	{
		String sStatement = null;
		String sSchema = getSchemaName().getSchema();
		if ((!sSchema.equals("SYS")) && (!sSchema.equals("SYSTEM")))
		{
			sStatement = K.DROP.getKeyword() + sSP + K.USER.getKeyword() + sSP +
					getSchemaName().quote();
			if (getDropBehavior() == DropBehavior.CASCADE)
				sStatement = sStatement + sSP + K.CASCADE.getKeyword();
		}
		else
			throw new IllegalArgumentException("User SYS or SYSTEM may never be dropped!");
		return sStatement;
	} /* format */

	/*------------------------------------------------------------------*/
	/** constructor with factory only to be called by factory.
	 * @param sf factory.
	 */
	public AltibaseDropSchemaStatement(SqlFactory sf)
	{
		super(sf);
	} /* constructor */

} /* class AltibaseDropSchemaStatement */
