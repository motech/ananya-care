package org.motechproject.care.utils;

import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.motechproject.care.service.util.PeriodUtil;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.testing.utils.BaseUnitTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:META-INF/motech/*.xml")
public abstract class SpringIntegrationTest extends BaseUnitTest {

    @Qualifier("ananyaCareDbConnector")
    @Autowired
    protected CouchDbConnector ananyaCareDbConnector;

    @Qualifier("ananyaCareProperties")
    @Autowired
    protected Properties ananyaCareProperties;

    @Autowired
    protected ScheduleTrackingService trackingService;

    @Autowired
    protected PeriodUtil periodUtil;

    protected ArrayList<MotechBaseDataObject> toDelete;
    protected ArrayList<Pair> schedulesToDelete;

    @Before
    public void before() {
        toDelete = new ArrayList<MotechBaseDataObject>();
        schedulesToDelete = new ArrayList<Pair>();
    }

    @After
    public void after() {
        for(MotechBaseDataObject obj : toDelete){
            ananyaCareDbConnector.delete(obj);
        }
        for(int i=0 ;i< schedulesToDelete.size(); i++){
            Pair s = schedulesToDelete.get(i);
            String externalId = s.getFirst().toString();
            String scheduleName = s.getSecond().toString();
            ArrayList<String> scheduleNames = new ArrayList<String>();
            scheduleNames.add(scheduleName);
            trackingService.unenroll(externalId, scheduleNames);
        }
        super.tearDown();
    }


    protected void markForDeletion(MotechBaseDataObject document) {
        toDelete.add(document);
    }

    protected void markScheduleForUnEnrollment(String externalId, String scheduleName) {
        schedulesToDelete.add(new Pair(externalId, scheduleName));
    }

    protected EnrollmentRecord getEnrollmentRecord(String scheduleName, String externalId, EnrollmentStatus status) {
        EnrollmentsQuery query = new EnrollmentsQuery()
                .havingExternalId(externalId)
                .havingState(status)
                .havingSchedule(scheduleName);

        List<EnrollmentRecord> enrollmentRecords = trackingService.searchWithWindowDates(query);
        return enrollmentRecords.isEmpty() ? null : enrollmentRecords.get(0);
    }

    private class Pair {
        private final String first;
        private final String second;

        public Pair(String first, String second) {
            this.first = first;
            this.second = second;
        }

        public String getFirst() {
            return first;
        }

        public String getSecond() {
            return second;
        }
    }
}