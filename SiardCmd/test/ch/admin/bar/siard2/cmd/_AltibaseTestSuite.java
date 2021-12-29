package ch.admin.bar.siard2.cmd;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        AltibaseFromDbTester.class,
        AltibaseToDbTester.class
})
public class _AltibaseTestSuite {

}
