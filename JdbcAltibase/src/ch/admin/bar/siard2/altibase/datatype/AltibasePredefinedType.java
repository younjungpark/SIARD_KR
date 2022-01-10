/*======================================================================
AltibasePredefinedType implements the type translation from ISO SQL to Altibase
Version     : $Id: $
Application : SIARD2
Description : AltibasePredefinedType implements the type translation from ISO SQL to Altibase
Platform    : Java 7
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 02.11.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.altibase.datatype;

import java.util.HashMap;
import java.util.Map;

import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.datatype.PredefinedType;
import ch.enterag.sqlparser.datatype.enums.PreType;

/*====================================================================*/
/**
 * AltibasePredefinedType implements the type translation from ISO SQL to Altibase
 * @author YounJung Park
 */
public class AltibasePredefinedType extends PredefinedType
{
	private static Map<PreType, String> mapISO_TO_ALTIBASE = new HashMap<PreType, String>();

	static
	{
		mapISO_TO_ALTIBASE.put(PreType.BINARY, "BYTE");
		mapISO_TO_ALTIBASE.put(PreType.VARBINARY, "VARBYTE");
		mapISO_TO_ALTIBASE.put(PreType.CHAR, "CHAR");
		mapISO_TO_ALTIBASE.put(PreType.VARCHAR, "VARCHAR");
		mapISO_TO_ALTIBASE.put(PreType.NCHAR, "NCHAR");
		mapISO_TO_ALTIBASE.put(PreType.NVARCHAR, "NVARCHAR");
		mapISO_TO_ALTIBASE.put(PreType.CLOB, "CLOB");
		mapISO_TO_ALTIBASE.put(PreType.BLOB, "BLOB");
		mapISO_TO_ALTIBASE.put(PreType.XML, "CLOB");
		mapISO_TO_ALTIBASE.put(PreType.NCLOB, "NVARCHAR(10666)");
		mapISO_TO_ALTIBASE.put(PreType.NUMERIC, "NUMERIC");
		mapISO_TO_ALTIBASE.put(PreType.DECIMAL, "DECIMAL");
		mapISO_TO_ALTIBASE.put(PreType.SMALLINT, "SMALLINT");
		mapISO_TO_ALTIBASE.put(PreType.INTEGER, "INTEGER");
		mapISO_TO_ALTIBASE.put(PreType.BIGINT, "BIGINT");
		mapISO_TO_ALTIBASE.put(PreType.FLOAT, "FLOAT");
		mapISO_TO_ALTIBASE.put(PreType.REAL, "REAL");
		mapISO_TO_ALTIBASE.put(PreType.DOUBLE, "DOUBLE");
		mapISO_TO_ALTIBASE.put(PreType.BOOLEAN, "CHAR(1)");
		mapISO_TO_ALTIBASE.put(PreType.DATE, "DATE");
		mapISO_TO_ALTIBASE.put(PreType.TIME, "DATE");
		mapISO_TO_ALTIBASE.put(PreType.TIMESTAMP, "DATE");
		mapISO_TO_ALTIBASE.put(PreType.INTERVAL, "VARBYTE(255)");
	}
	/**
	 * Constructor
	 * @param sf factory
	 */
	public AltibasePredefinedType(SqlFactory sf) {
		super(sf);
	} /* constructor */

	/**
	 * format the predefined data type.
	 * @return the SQL string corresponding to the fields of the data type.
	 */
	public String format()
	{
		String sType = null;
		sType = mapISO_TO_ALTIBASE.get(getType());
		if( getType() == PreType.NUMERIC ||
				getType() == PreType.DECIMAL)
			sType = sType + formatPrecisionScale(); // [(M,D)]
		else if (getType() == PreType.FLOAT ||
				getType() == PreType.REAL ||
				getType() == PreType.DOUBLE ||
				getType() == PreType.FLOAT)
			sType = sType + formatPrecisionScale(); // [(M[,D])]
		else if (getType() == PreType.SMALLINT ||
				getType() == PreType.INTEGER ||
				getType() == PreType.BIGINT)
			sType = sType + formatPrecisionScale(); // [(M)]
		else if (getType() == PreType.CHAR ||
				getType() == PreType.VARCHAR ||
				getType() == PreType.NCHAR ||
				getType() == PreType.NVARCHAR ||
				getType() == PreType.BINARY ||
				getType() == PreType.VARBINARY)
		{
			sType = sType + formatLength(); // (M)
		}

		return sType;
	} /* format */

} /* class AltibasePredefinedType */
