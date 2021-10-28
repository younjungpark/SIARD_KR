/*======================================================================
AltibaseDataType implements the type translation from ISO SQL to Altibase for complex types.
Version     : $Id: $
Application : SIARD2
Description : AltibaseDataType implements the type translation from ISO
              SQL:2008 to Altibase for complex types.
Platform    : Java 7
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 26.05.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.altibase.datatype;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;

/*====================================================================*/
/** AltibaseDataType implements the type translation from ISO SQL to Altibase for
 * complex types.
 * @author YounJung Park
 */
public class AltibaseDataType
		extends DataType
{

	/*------------------------------------------------------------------*/
	/** format an ARRAY type.
	 * In Altibase an ARRAY is stored as a ARRAY (without base type) and with
	 * parentheses instead of brackets around the length.
	 * @return SQL for ARRAY type.
	 */
	@Override
	protected String formatArrayType()
	{
		String sDataType = K.ARRAY.getKeyword();
		if (getLength() != iUNDEFINED)
			sDataType = sDataType + sLEFT_PAREN + String.valueOf(getLength()) + sRIGHT_PAREN;
		return sDataType;
	} /* formatArrayType */

	/*------------------------------------------------------------------*/
	/** format the data type for Altibase.
	 * @return the SQL string corresponding to the fields of the data type.
	 */
	@Override
	public String format()
	{
		String sDataType = null;
		switch (getType())
		{
			case PRE: sDataType = getPredefinedType().format(); break;
			case STRUCT: sDataType = formatStructType(); break;
			case ROW: sDataType = formatRowType(); break;
			case REF: sDataType = formatRefType(); break;
			case ARRAY: sDataType = formatArrayType(); break;
			case MULTISET: sDataType = formatMultisetType(); break;
		}
		return sDataType;
	} /* format */

	/*------------------------------------------------------------------*/
	/** constructor with factory only to be called by factory.
	 * @param sf factory.
	 */
	public AltibaseDataType(SqlFactory sf)
	{
		super(sf);
	} /* constructor */

} /* class AltibaseDataType */
