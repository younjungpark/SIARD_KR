/*======================================================================
AltibaseAttributeDefinition overrides AttributeDefinition of SQL parser.
Version     : $Id: $
Application : SIARD2
Description : AltibaseAttributeDefinition overrides AttributeDefinition
			  of SQL parser
Platform    : Java 7
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 30.06.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.altibase.ddl;

import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.ddl.AttributeDefinition;

/*====================================================================*/
public class AltibaseAttributeDefinition
		extends AttributeDefinition
{

	/*------------------------------------------------------------------*/
	/**
	 * format the attribute definition
	 * @return the SQL string corresponding to a attribute definition
	 */
	@Override
	public String format()
	{
		String sDefinition = getAttributeName().quote() + sSP + getDataType().format();
		return sDefinition;
	}

	public AltibaseAttributeDefinition(SqlFactory sf)
	{
		super(sf);
	} /* constructor */
}
