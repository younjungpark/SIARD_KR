package ch.admin.bar.siard2.altibase;

import java.sql.*;
import ch.enterag.utils.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.expression.enums.*;

public abstract class AltibaseLiterals
        extends SqlLiterals
{
    /*------------------------------------------------------------------*/
    /** format byte buffer value.
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

    /*------------------------------------------------------------------*/
    /** format a boolean literal value
     * in Altibase the BOOLEAN data type is realized as a "CHAR(1)" data type
     * @param blValue boolean value to be formatted
     * @return boolean (char) literal
     */
    public static String formatBooleanLiteral(BooleanLiteral blValue)
    {
        String sFormatted = null;
        switch(blValue)
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
    /** format a time literal value
     * in Altibase the TIME data type is realized as a "TIMESTAMP" data type
     * @param timeValue TIME value to be formatted
     * @return TIME (TIMESTAMP) literal
     */
    public static String formatTimeLiteral(Time timeValue)
    {
        Timestamp ts = new Timestamp(timeValue.getTime());
        return formatTimestampLiteral(ts);
    } /* formatTimeLiteral */

} /* class AltibaseSqlLiteral */
