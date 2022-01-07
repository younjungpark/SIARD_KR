/*======================================================================
AltibaseUnsignedLiteral implements the value translation from ISO SQL to Altibase.
Version     : $Id: $
Application : SIARD2
Description : AltibaseLiteral implements the value translation from
              ISO SQL:2008 to Altibase.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rueti ZH, Switzerland
Created    : 28.06.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.altibase.expression;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.expression.*;
import ch.admin.bar.siard2.altibase.*;

/*====================================================================*/

/**
 * AltibaseUnsignedLiteral implements the value translation from ISO SQL:2008
 * to Altibase
 *
 * @author YounJung Park
 */
public class AltibaseUnsignedLiteral extends UnsignedLiteral
{
	@Override
	public String format()
	{
		String sFormatted = "";
		if (getBytes() != null)
		{
			sFormatted = AltibaseLiterals.formatBytesLiteral(getBytes());
		}
		else if (getBoolean() != null)
		{
			sFormatted = AltibaseLiterals.formatBooleanLiteral(getBoolean());
		}
		else if (getTime() != null)
		{
			sFormatted = AltibaseLiterals.formatTimeLiteral(getTime());
		}
		else if (getTimestamp() != null)
		{
			sFormatted = AltibaseLiterals.formatTimestampWithMicroSecLiteral(getTimestamp());
		}
		else if (getInterval() != null)
		{
			sFormatted = AltibaseLiterals.formatIntervalLiteral(getInterval());
		}
		else if (getBitString() != null)
		{
			sFormatted = AltibaseLiterals.formatBitLiteral(getBitString());
		}
		else
		{
			sFormatted = super.format();
		}
		return sFormatted;
	} /* format */

	public AltibaseUnsignedLiteral(SqlFactory sf) {
		super(sf);
	} /* constructor */

} /* class AltibaseUnsignedLiteral */