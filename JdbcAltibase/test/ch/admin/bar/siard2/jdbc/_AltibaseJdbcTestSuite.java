package ch.admin.bar.siard2.jdbc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	AltibaseDriverTester.class,
	AltibaseConnectionTester.class
})
public class _AltibaseJdbcTestSuite {

}
