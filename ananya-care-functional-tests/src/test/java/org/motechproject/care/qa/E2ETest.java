package org.motechproject.care.qa;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.care.utils.DbUtils;
import org.motechproject.care.utils.E2EIntegrationTestUtil;
import org.motechproject.care.utils.TestCaseThreadRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:applicationContext-FunctionalTestsE2E.xml")
public class E2ETest extends TestCaseThreadRunner {

    @Autowired
    private E2EIntegrationTestUtil e2EIntegrationTestUtil;

    @Autowired
    private DbUtils dbUtils;


    @Test
    public void test() {
        String userId = "d823ea3d392a06f8b991e9e4933348bd";
        String ownerId = "d823ea3d392a06f8b991e9e49394ce45";
        this.runTest(new ChildCaseE2EThread(e2EIntegrationTestUtil, dbUtils, userId, ownerId));
        this.runTest(new MotherCaseE2EThread(e2EIntegrationTestUtil, dbUtils, userId, ownerId));
        this.runTest(new MotherCaseFunctionalThread(e2EIntegrationTestUtil, dbUtils));
        this.verify();
    }
}
