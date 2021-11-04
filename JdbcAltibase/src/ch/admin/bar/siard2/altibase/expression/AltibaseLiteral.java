/*======================================================================
AltibaseLiteral implements the value translation from ISO SQL to Altibase.
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

import ch.admin.bar.siard2.altibase.AltibaseLiterals;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.expression.*;


/*====================================================================*/

/**
 * AltibaseLiteral implements the value translation from ISO SQL:2008
 * to Altibase
 *
 * @author YounJung Park
 */
public class AltibaseLiteral extends Literal
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
    else
    {
      sFormatted = super.format();
    }
    return sFormatted;
  } /* format */

  public AltibaseLiteral(SqlFactory sf)
  {
    super(sf);
  } /* constructor */

} /* class AltibaseLiteral */