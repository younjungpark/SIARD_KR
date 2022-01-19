package ch.admin.bar.siard2.jdbcx;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.enterag.utils.base.ConnectionProperties;

public class AltibaseDataSourceTester {
    private static final ConnectionProperties _cp = new ConnectionProperties();

    private static final String _sDB_URL = "jdbc:Altibase://" + _cp.getHost() + ":" + _cp.getPort() + "/" + _cp.getCatalog();
    private static final String _sDBA_USER = _cp.getDbaUser();
    private static final String _sDBA_PASSWORD = _cp.getDbaPassword();

    private AltibaseDataSource _dsAltibase = null;
    private Connection         _conn       = null;

    @Before
    public void setUp() throws Exception {
        _dsAltibase = new AltibaseDataSource();
    } /* setUp */

    @After
    public void tearDown() throws Exception {
        if((_conn != null) && (!_conn.isClosed())) {
            _conn.close();
        }
    } /* tearDown */

    @Test
    public void testWrapper()
    {
        try {
            Assert.assertSame("Invalid wrapper!", true, _dsAltibase.isWrapperFor(DataSource.class));
            DataSource dsWrapped = _dsAltibase.unwrap(DataSource.class);
            assertSame("Invalid wrapper class!", Altibase.jdbc.driver.AltibaseDataSource.class, dsWrapped.getClass());
        } catch(SQLException se) {
            fail(se.getClass().getName() + ": " + se.getMessage());
        }
    } /* testWrapper */

    @Test
    public void testConnection()
    {
        _dsAltibase.setUrl(_sDB_URL);
        _dsAltibase.setUser(_sDBA_USER);
        _dsAltibase.setPassword(_sDBA_PASSWORD);

        try {
            _conn = _dsAltibase.getConnection();
        } catch (SQLException se) {
            fail(se.getClass().getName() + ": " + se.getMessage());
        }
    } /* testConnection */

    @Test
    public void testLoginTimeout()
    {
        try {
            int iLoginTimeout = _dsAltibase.getLoginTimeout();
            assertSame("Unexpected login timeout " + String.valueOf(iLoginTimeout) + "!", iLoginTimeout, 0);
        } catch (SQLException se) {
            fail(se.getClass().getName() + ": " + se.getMessage());
        }
    } /* testLoginTimeout */

} /* class AltibaseDataSourceTester */
