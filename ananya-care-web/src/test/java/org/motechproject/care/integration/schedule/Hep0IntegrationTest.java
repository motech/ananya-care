package org.motechproject.care.integration.schedule;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.care.domain.Child;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.vaccinations.ChildVaccinationSchedule;
import org.motechproject.care.service.ChildService;
import org.motechproject.care.service.VaccinationProcessor;
import org.motechproject.care.service.builder.ChildBuilder;
import org.motechproject.care.service.schedule.Hep0Service;
import org.motechproject.care.service.schedule.VaccinationService;
import org.motechproject.care.utils.CaseUtils;
import org.motechproject.care.utils.SpringIntegrationTest;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.commons.date.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class Hep0IntegrationTest extends SpringIntegrationTest {

    @Autowired
    private Hep0Service hep0Service;
    @Autowired
    private AllChildren allChildren;

    private String caseId;
    private ChildService childService;

    @Before
    public void setUp(){
        caseId = CaseUtils.getUniqueCaseId();
        List<VaccinationService> vaccinationServices = Arrays.asList((VaccinationService) hep0Service);
        VaccinationProcessor childVaccinationProcessor = new VaccinationProcessor(vaccinationServices);
        childService = new ChildService(allChildren, childVaccinationProcessor);
    }

    @After
    public void tearDown() {
        allChildren.removeAll();
    }

    @Test
    public void shouldVerifyHep0ScheduleCreationWhenChildIsRegistered() {
        String scheduleName = ChildVaccinationSchedule.Hepatitis0.getName();
        DateTime dob = DateUtil.newDateTime(DateUtil.today());

        String motherCaseId = "motherCaseId";

        Child child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withHep0Date(null).withMotherCaseId(motherCaseId).build();
        childService.process(child);

        markScheduleForUnEnrollment(caseId,  scheduleName);
        EnrollmentsQuery query = new EnrollmentsQuery()
                .havingExternalId(caseId)
                .havingState(EnrollmentStatus.ACTIVE)
                .havingSchedule(scheduleName);

        EnrollmentRecord enrollment = trackingService.searchWithWindowDates(query).get(0);

        assertEquals(MilestoneType.Hep0.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(dob, enrollment.getReferenceDateTime().withTimeAtStartOfDay());
        assertEquals(dob, enrollment.getStartOfDueWindow().withTimeAtStartOfDay());
        assertEquals(dob.plusDays(1), enrollment.getStartOfLateWindow().withTimeAtStartOfDay());

        Child childFromDb = allChildren.findByCaseId(caseId);
        assertEquals(dob, childFromDb.getDOB());
        assertNull(childFromDb.getHep0Date());
    }

    @Test
    public void shouldVerifyHep0ScheduleIsNotActiveWhenChildIsOlderThanADay() {
        String scheduleName = ChildVaccinationSchedule.Hepatitis0.getName();
        DateTime dob = DateUtil.newDateTime(DateUtil.today()).minusDays(3);

        String motherCaseId = "motherCaseId";

        Child child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withHep0Date(null).withMotherCaseId(motherCaseId).build();
        childService.process(child);

        markScheduleForUnEnrollment(caseId, scheduleName);
        EnrollmentsQuery query = new EnrollmentsQuery()
                .havingExternalId(caseId)
                .havingState(EnrollmentStatus.ACTIVE)
                .havingSchedule(scheduleName);

        List<EnrollmentRecord> enrollmentRecords = trackingService.searchWithWindowDates(query);
        assertTrue(enrollmentRecords.isEmpty());

        Child childFromDb = allChildren.findByCaseId(caseId);
        assertEquals(dob, childFromDb.getDOB());
        assertNull(childFromDb.getHep0Date());
    }

    @Test
    public void shouldVerifyHep0ScheduleFulfillmentWhenChildHasTakenHep0() {
        String scheduleName = ChildVaccinationSchedule.Hepatitis0.getName();
        DateTime dob = DateUtil.newDateTime(DateUtil.today()).minusMonths(2);
        DateTime hep0Taken = DateUtil.newDateTime(DateUtil.today()).minusMonths(1);
        String motherCaseId = "motherCaseId";

        Child child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withHep0Date(null).withMotherCaseId(motherCaseId).build();
        childService.process(child);

        EnrollmentsQuery query = new EnrollmentsQuery()
                .havingExternalId(caseId)
                .havingState(EnrollmentStatus.DEFAULTED)
                .havingSchedule(scheduleName);

        assertFalse(trackingService.searchWithWindowDates(query).isEmpty());


        child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withHep0Date(hep0Taken).withMotherCaseId(motherCaseId).build();
        childService.process(child);

        assertNull(trackingService.getEnrollment(caseId, scheduleName));

        Child childFromDb = allChildren.findByCaseId(caseId);
        assertEquals(dob, childFromDb.getDOB());
        assertEquals(hep0Taken, childFromDb.getHep0Date());
    }
}
