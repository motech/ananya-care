package org.motechproject.care.qa;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.care.utils.DbUtils;
import org.motechproject.care.utils.TestCaseThreadRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:applicationContext-FunctionalTestsE2E.xml")
public class E2ETestsRunner extends TestCaseThreadRunner {

    @Qualifier("ananyaCareProperties")
    @Autowired
    private Properties ananyaCareProperties;

    @Autowired
    private DbUtils dbUtils;

    @Test
    public void e2eTest() {
        String userId = "d823ea3d392a06f8b991e9e4933348bd";
        String ownerId = "d823ea3d392a06f8b991e9e49394ce45";
        this.addTest(new ChildCaseE2EThread(ananyaCareProperties, dbUtils, userId, ownerId));
        this.addTest(new MotherCaseE2EThread(ananyaCareProperties, dbUtils, userId, ownerId));
        this.addTest(new MotherCaseFunctionalThread(ananyaCareProperties, dbUtils, userId, ownerId));
        this.run();
    }

    @Test
    @Ignore
    public void concurrencyTest() {
        List<String> userIds = Arrays.asList("2e2908b643875806156e49ac6b540e3b",
                "2e2908b643875806156e49ac6b540dae",
                "2e2908b643875806156e49ac6b540ada",
                "2e2908b643875806156e49ac6b53fd5f",
                "2e2908b643875806156e49ac6b53ee0c",
                "2e2908b643875806156e49ac6b53e74f",
                "2e2908b643875806156e49ac6b53e183",
                "2e2908b643875806156e49ac6b53dc96",
                "2e2908b643875806156e49ac6b53d71d",
                "2e2908b643875806156e49ac6b53cb99");

        String ownerId = "2e2908b643875806156e49ac6b2e4abd";

        for(String userId: userIds) {
            for(int i=0; i<10; i++) {
                this.addTest(new MotherCaseFunctionalThread(ananyaCareProperties, dbUtils, userId, ownerId));
                this.addTest(new ChildCaseE2EThread(ananyaCareProperties, dbUtils, userId, ownerId));
                this.addTest(new MotherCaseE2EThread(ananyaCareProperties, dbUtils, userId, ownerId));
            }
        }
        this.run();
    }
}