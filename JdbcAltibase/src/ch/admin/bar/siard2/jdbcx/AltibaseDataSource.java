/*======================================================================
AltibaseDataSource implements a wrapped Altibase DataSource.
Version     : $Id: $
Application : SIARD2
Description : AltibaseDataSource implements a wrapped Altibase DataSource.
Platform    : Java 7
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rueti ZH, Switzerland
Created    : 15.06.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.jdbcx;

import javax.sql.DataSource;
import java.sql.*;

import ch.admin.bar.siard2.jdbc.*;
import ch.enterag.utils.jdbcx.*;
import ch.enterag.utils.logging.*;

/** AltibaseDataSource implements a wrapped Altibase DataSource.
 * @author YounJung Park
 */
public class AltibaseDataSource
        extends BaseDataSource
        implements DataSource
{
    /** logger */
    private static IndentLogger _il = IndentLogger.getIndentLogger(AltibaseDataSource.class.getName());

    /*------------------------------------------------------------------*/
    /** constructor
     * @throws SQLException */
    public AltibaseDataSource() throws SQLException
    {
        super(new Altibase.jdbc.driver.AltibaseDataSource());
    } /* constructor AltibaseDataSource */

    /*------------------------------------------------------------------*/
    /** constructor
     * @param sUrl JDBC URL identifying the database instance to connect to.
     * @param sUser database user.
     * @param sPassword password of database user.
     * @throws SQLException if a database error occurred.
     */
    public AltibaseDataSource(String sUrl, String sUser, String sPassword)
            throws SQLException
    {
        super(new Altibase.jdbc.driver.AltibaseDataSource());
        setUrl(sUrl);
        setUser(sUser);
        setPassword(sPassword);
    } /* constructor */

    /*------------------------------------------------------------------*/
    /** {@inheritDoc}
     * returns the appropriately wrapped Altibase Connection.
     */
    @Override
    public Connection getConnection() throws SQLException
    {
        return new AltibaseConnection(super.getConnection());
    } /* getConnection */

    /*------------------------------------------------------------------*/
    /** @return unwrapped Altibase DataSource
     */
    private Altibase.jdbc.driver.AltibaseDataSource getUnwrapped()
    {
        Altibase.jdbc.driver.AltibaseDataSource ssds = null;
        try { ssds = (Altibase.jdbc.driver.AltibaseDataSource)unwrap(DataSource.class); }
        catch(SQLException se) { _il.exception(se); }
        return ssds;
    } /* getUnwrapped */

    /*------------------------------------------------------------------*/
    /** set URL to be used for connection.
     * @param url URL (format: "jdbc:Altibase://<databasehost>:<databasePort>/<dbName>")
     */
    public void setUrl(String url)
    {
        try { getUnwrapped().setURL(url); }
        catch (SQLException se) { _il.exception(se); }
    } /* setUrl */

    /*------------------------------------------------------------------*/
    /** @return URL used for connection.
     */
    public String getUrl() throws SQLException
    {
        Altibase.jdbc.driver.AltibaseDataSource ds = getUnwrapped();
        String sHost = ds.getServerName();
        int iPort = ds.getPortNumber();
        String sDatabase = ds.getDatabaseName();
        String sUrl = "jdbc:Altibase://" + sHost + ":" + iPort + "/" + sDatabase;
        return sUrl;
    } /* getUrl */

    /*------------------------------------------------------------------*/
    /** set database user to be used for connection.
     * @param user database user to be used for connection.
     */
    public void setUser(String user)
    {
        getUnwrapped().setUser(user);
    } /* setUser */

    /*------------------------------------------------------------------*/
    /** @return database user used for connection.
     */
    public String getUser()
    {
        return getUnwrapped().getUser();
    } /* getUser */

    /*------------------------------------------------------------------*/
    /** set database password to be used for connection. 
     * @param password database password to be used for connection.
     */
    public void setPassword(String password)
    {
        getUnwrapped().setPassword(password);
    } /* setPassword */

    /*------------------------------------------------------------------*/
    /** set database description.
     * @param description database description.
     */
    public void setDescription(String description)
    {
        try { getUnwrapped().setDescription(description); }
        catch (SQLException se) { _il.exception(se); }
    } /* setDescription */

    /*------------------------------------------------------------------*/
    /** @return database description.
     */
    public String getDescription()
    {
        return getUnwrapped().getDescription();
    } /* getDescription */

} /* class AltibaseDataSource */
