package ch.admin.bar.siard2.jdbc;

import ch.admin.bar.siard2.jdbcx.AltibaseDataSourceTester;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	AltibaseDriverTester.class,
	AltibaseConnectionTester.class,
	AltibaseDatabaseMetaDataTester.class,
	AltibaseDataSourceTester.class,
	AltibaseStatementTester.class,
	AltibaseResultSetTester.class,
	AltibaseResultSetMetaDataTester.class
})
public class _AltibaseJdbcTestSuite {

}
