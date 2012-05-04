package org.motechproject.care.qa;

import org.ektorp.BulkDeleteDocument;
import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.commcarehq.domain.AlertDocCase;
import org.motechproject.commcarehq.repository.AllAlertDocCases;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.quartz.utils.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Properties;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:applicationContext-FunctionalTestsQA.xml")
public abstract class SpringQAIntegrationTest {

    @Qualifier("ananyaCareDbConnector")
    @Autowired
    protected CouchDbConnector ananyaCareDbConnector;

    @Qualifier("ananyaCareDummyAppDbConnector")
    @Autowired
    protected CouchDbConnector ananyaCareDummyAppDbConnector;


    @Qualifier("ananyaCareProperties")
    @Autowired
    protected Properties ananyaCareProperties;

    @Autowired
    protected ScheduleTrackingService trackingService;

    @Autowired
    protected AllMothers allMothers;

    @Autowired
    protected AllChildren allChildren;

    @Autowired
    protected AllAlertDocCases allAlertDocCases;

    protected ArrayList<BulkDeleteDocument> toDelete;

    protected ArrayList<BulkDeleteDocument> alertDocCasesToDelete;

    protected ArrayList<Pair> schedulesToDelete;

    @Before
    public void before() {
        toDelete = new ArrayList<BulkDeleteDocument>();
        schedulesToDelete = new ArrayList<Pair>();
        alertDocCasesToDelete = new ArrayList<BulkDeleteDocument>();
    }

    @After
    public void after() {
        ananyaCareDbConnector.executeBulk(toDelete);

        ananyaCareDummyAppDbConnector.executeBulk(alertDocCasesToDelete);

        for(int i=0 ;i< schedulesToDelete.size(); i++){
            Pair s = schedulesToDelete.get(i);
            String externalId = s.getFirst().toString();
            String scheduleName = s.getSecond().toString();
            ArrayList<String> scheduleNames = new ArrayList<String>();
            scheduleNames.add(scheduleName);
            trackingService.unenroll(externalId, scheduleNames);
        }
    }


    protected void markForDeletion(Object document) {
        toDelete.add(BulkDeleteDocument.of(document));
    }

    protected void markAlertDocCaseForDeletion(AlertDocCase alertDocCase) {
        alertDocCasesToDelete.add(BulkDeleteDocument.of(alertDocCase));
    }

    protected void markScheduleForUnEnrollment(String externalId, String scheduleName) {
        schedulesToDelete.add(new Pair(externalId, scheduleName));
    }

    protected String getAppServerPort() {
        return ananyaCareProperties.getProperty("app.server.port");
    }

    protected String getAppServerHostUrl() {
        return "http://localhost:" + getAppServerPort();
    }
}