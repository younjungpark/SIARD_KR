package ch.admin.bar.siard2.altibase;

import java.sql.*;
import java.text.SimpleDateFormat;

import ch.enterag.utils.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.expression.enums.*;

public abstract class AltibaseLiterals
		extends SqlLiterals
{
	public static final SimpleDateFormat sdfTIMESTAMPWITHMICROSEC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
	/*------------------------------------------------------------------*/

	/**
	 * format byte buffer value.
	 *
	 * @param bufValue byte buffer value to be formatted.
	 * @return byte string literal.
	 */
	public static String formatBytesLiteral(byte[] bufValue)
	{
		String sFormatted = sNULL;
		if (bufValue != null)
			sFormatted = "BYTE" + sAPOSTROPHE + BU.toHex(bufValue) + sAPOSTROPHE;
		return sFormatted;
	} /* formatBytesLiteral */

	public static String formatBitLiteral(String bitString)
	{
		String sFormatted = sNULL;
		if (bitString != null)
			sFormatted = "BIT" + sAPOSTROPHE + bitString + sAPOSTROPHE;
		return sFormatted;
	}

	/*------------------------------------------------------------------*/

	/**
	 * format a boolean literal value
	 * in Altibase the BOOLEAN data type is realized as a "CHAR(1)" data type
	 *
	 * @param blValue boolean value to be formatted
	 * @return boolean (char) literal
	 */
	public static String formatBooleanLiteral(BooleanLiteral blValue)
	{
		String sFormatted = null;
		switch (blValue)
		{
			case UNKNOWN:
				sFormatted = K.NULL.getKeyword();
				break;
			case FALSE:
				sFormatted = "0";
				break;
			case TRUE:
				sFormatted = "1";
				break;
		}
		return sFormatted;
	} /* formatBooleanLiteral */

	/*------------------------------------------------------------------*/

	/**
	 * format a time literal value
	 * in Altibase the TIME data type is realized as a "TIMESTAMP" data type
	 *
	 * @param timeValue TIME value to be formatted
	 * @return TIME (TIMESTAMP) literal
	 */
	public static String formatTimeLiteral(Time timeValue)
	{
		Timestamp ts = new Timestamp(timeValue.getTime());
		return formatTimestampLiteral(ts);
	} /* formatTimeLiteral */

	public static String formatTimestampLiteral(java.sql.Timestamp tsValue)
	{
		String sFormatted = sNULL;
		if (tsValue != null)
		{
			sFormatted = sdfTIMESTAMP.format(tsValue);
			int iNanos = tsValue.getNanos();
			if (iNanos > 0)
			{
				String sNanos = String.valueOf(iNanos);
				while (sNanos.length() < 9)
					sNanos = "0" + sNanos;
				while (sNanos.endsWith("0"))
					sNanos = sNanos.substring(0, sNanos.length() - 1);
				sFormatted = sFormatted + sDOT + sNanos;
			}
			sFormatted = "TO_DATE(" + formatStringLiteral(sFormatted) + ",'YYYY-MM-DD HH24:MI:SS') ";
		}
		return sFormatted;
	}

	public static String formatTimestampWithMicroSecLiteral(java.sql.Timestamp tsValue)
	{
		String sFormatted = sNULL;
		if (tsValue != null)
		{
			sFormatted = sdfTIMESTAMPWITHMICROSEC.format(tsValue);
			sFormatted = "TO_DATE(" + formatStringLiteral(sFormatted) + ",'YYYY-MM-DD HH24:MI:SS.SSSSSS') ";
		}
		return sFormatted;
	}

	/* ------------------------------------------------------------------------ */

	/**
	 * format interval value
	 *
	 * @param intervalValue the interval value to be formatted
	 * @return byte string literal
	 */
	public static String formatIntervalLiteral(Interval intervalValue)
	{
		String sFormatted = sNULL;
		if (intervalValue != null)
		{
			sFormatted = formatBytesLiteral(serialize(intervalValue));
		}
		return sFormatted;
	}

} /* class AltibaseSqlLiteral */
