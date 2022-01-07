/*======================================================================
DropTableStatement overrides DropTableStatement of SQL parser.
Version     : $Id: $
Application : SIARD2
Description : AltibaseDropViewStatement overrides DropViewStatement
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
import ch.enterag.sqlparser.ddl.DropViewStatement;
import ch.enterag.sqlparser.ddl.enums.DropBehavior;

/*====================================================================*/
/** AltibaseDropViewStatement implements the formatting of DROP
 * VIEW statements
 * @author YounJung Park
 */
public class AltibaseDropViewStatement extends DropViewStatement
{
  /*------------------------------------------------------------------*/
  /**
   * format the drop view statement
   * @return the SQL string corresponding to a drop view statement
   */
  @Override
  public String format()
  {
    String sStatement = null;
    // Altibase does not support RESTRICT, CASCADE in VIEWS
    sStatement = K.DROP.getKeyword() + sSP + K.VIEW.getKeyword() + sSP + getViewName().quote();

    return sStatement;
  } /* format */

  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public AltibaseDropViewStatement(SqlFactory sf) {
    super(sf);
  } /* constructor */
}
