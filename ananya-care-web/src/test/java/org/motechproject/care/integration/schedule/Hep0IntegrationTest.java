package org.motechproject.care.integration.schedule;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.care.domain.Child;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.request.CareCase;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.vaccinations.ChildVaccinationSchedule;
import org.motechproject.care.service.ChildService;
import org.motechproject.care.service.ChildVaccinationProcessor;
import org.motechproject.care.service.builder.ChildCareCaseBuilder;
import org.motechproject.care.service.schedule.Hep0Service;
import org.motechproject.care.service.schedule.VaccinationService;
import org.motechproject.care.utils.CaseUtils;
import org.motechproject.care.utils.SpringIntegrationTest;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class Hep0IntegrationTest extends SpringIntegrationTest {

    @Autowired
    private Hep0Service hep0Service;
    @Autowired
    private ScheduleTrackingService scheduleTrackingService;
    @Autowired
    private AllChildren allChildren;

    private final String caseId = CaseUtils.getUniqueCaseId();
    private ChildService childService;

    @Before
    public void setUp(){
        List<VaccinationService> vaccinationServices = Arrays.asList((VaccinationService) hep0Service);
        ChildVaccinationProcessor childVaccinationProcessor = new ChildVaccinationProcessor(vaccinationServices);
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

        CareCase careCase=new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withHep0Date(null).withMotherCaseId(motherCaseId).build();
        childService.process(careCase);

        markScheduleForUnEnrollment(caseId,  scheduleName);
        EnrollmentsQuery query = new EnrollmentsQuery()
                .havingExternalId(caseId)
                .havingState(EnrollmentStatus.ACTIVE)
                .havingSchedule(scheduleName);

        EnrollmentRecord enrollment = scheduleTrackingService.searchWithWindowDates(query).get(0);

        assertEquals(MilestoneType.Hep0.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(dob, enrollment.getReferenceDateTime());
        assertEquals(dob, enrollment.getStartOfDueWindow());
        assertEquals(dob.plusDays(1), enrollment.getStartOfLateWindow());

        Child child = allChildren.findByCaseId(caseId);
        assertEquals(dob , child.getDOB());
        assertNull(child.getHep0Date());
    }

    @Test
    public void shouldVerifyHep0ScheduleNotCreatedWhenChildIsOlderThanADay() {
        String scheduleName = ChildVaccinationSchedule.Hepatitis0.getName();
        DateTime dob = DateUtil.newDateTime(DateUtil.today().minusDays(1));

        String motherCaseId = "motherCaseId";

        CareCase careCase=new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withHep0Date(null).withMotherCaseId(motherCaseId).build();
        childService.process(careCase);

        markScheduleForUnEnrollment(caseId,  scheduleName);
        EnrollmentsQuery query = new EnrollmentsQuery()
                .havingExternalId(caseId)
                .havingState(EnrollmentStatus.ACTIVE)
                .havingSchedule(scheduleName);

        assertTrue(scheduleTrackingService.searchWithWindowDates(query).isEmpty());

        Child child = allChildren.findByCaseId(caseId);
        assertEquals(dob, child.getDOB());
        assertNull(child.getHep0Date());
    }

    @Test
    public void shouldVerifyHep0ScheduleFulfillmentWhenChildHasTakenHep0() {
        String scheduleName = ChildVaccinationSchedule.Hepatitis0.getName();
        DateTime dob = DateUtil.newDateTime(DateUtil.today().minusMonths(2));
        DateTime hep0Taken = DateUtil.newDateTime(DateUtil.today().minusMonths(1));
        String motherCaseId = "motherCaseId";

        CareCase careCase=new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withHep0Date(null).withMotherCaseId(motherCaseId).build();
        childService.process(careCase);

        EnrollmentsQuery query = new EnrollmentsQuery()
                .havingExternalId(caseId)
                .havingState(EnrollmentStatus.DEFAULTED)
                .havingSchedule(scheduleName);

        assertFalse(scheduleTrackingService.searchWithWindowDates(query).isEmpty());


        careCase=new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withHep0Date(hep0Taken.toString()).withMotherCaseId(motherCaseId).build();
        childService.process(careCase);

        assertNull(scheduleTrackingService.getEnrollment(caseId, scheduleName));

        Child child = allChildren.findByCaseId(caseId);
        assertEquals(dob, child.getDOB());
        assertEquals(hep0Taken, child.getHep0Date());
    }
}
