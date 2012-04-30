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
import org.motechproject.care.service.schedule.Opv0Service;
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

public class Opv0IntegrationTest extends SpringIntegrationTest {

    @Autowired
    private Opv0Service Opv0Service;
    @Autowired
    private ScheduleTrackingService scheduleTrackingService;
    @Autowired
    private AllChildren allChildren;

    private final String caseId = CaseUtils.getUniqueCaseId();
    private ChildService childService;
    private final String scheduleName = ChildVaccinationSchedule.OPV0.getName();

    @Before
    public void setUp(){
        List<VaccinationService> vaccinationServices = Arrays.asList((VaccinationService) Opv0Service);
        ChildVaccinationProcessor childVaccinationProcessor = new ChildVaccinationProcessor(vaccinationServices);
        childService = new ChildService(allChildren, childVaccinationProcessor);
    }

    @After
    public void tearDown() {
        allChildren.removeAll();
    }

    @Test
    public void shouldVerifyOPV0ScheduleCreationWhenChildIsRegistered() {
        DateTime dob = DateUtil.newDateTime(DateUtil.today());

        String motherCaseId = "motherCaseId";

        CareCase careCase=new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withOPV0Date(null).withMotherCaseId(motherCaseId).build();
        childService.process(careCase);

        markScheduleForUnEnrollment(caseId, scheduleName);
        EnrollmentsQuery query = new EnrollmentsQuery()
                .havingExternalId(caseId)
                .havingState(EnrollmentStatus.ACTIVE)
                .havingSchedule(scheduleName);

        EnrollmentRecord enrollment = scheduleTrackingService.searchWithWindowDates(query).get(0);

        assertEquals(MilestoneType.OPV0.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(dob, enrollment.getReferenceDateTime());
        assertEquals(dob, enrollment.getStartOfDueWindow());
        assertEquals(dob.plusDays(15), enrollment.getStartOfLateWindow());

        Child child = allChildren.findByCaseId(caseId);
        assertEquals(dob , child.getDOB());
        assertNull(child.getOpv0Date());
    }

    @Test
    public void shouldVerifyOPV0ScheduleNotCreatedWhenChildIsOlderThan15Days() {
        DateTime dob = DateUtil.newDateTime(DateUtil.today().minusDays(15));

        String motherCaseId = "motherCaseId";

        CareCase careCase=new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withOPV0Date(null).withMotherCaseId(motherCaseId).build();
        childService.process(careCase);

        markScheduleForUnEnrollment(caseId,  scheduleName);
        EnrollmentsQuery query = new EnrollmentsQuery()
                .havingExternalId(caseId)
                .havingState(EnrollmentStatus.ACTIVE)
                .havingSchedule(scheduleName);

        assertTrue(scheduleTrackingService.searchWithWindowDates(query).isEmpty());

        Child child = allChildren.findByCaseId(caseId);
        assertEquals(dob, child.getDOB());
        assertNull(child.getOpv0Date());
    }

    @Test
    public void shouldVerifyOPV0ScheduleFulfillmentWhenChildHasTakenOPV0() {
        DateTime dob = DateUtil.newDateTime(DateUtil.today().minusMonths(2));
        DateTime OPV0Taken = DateUtil.newDateTime(DateUtil.today().minusMonths(1));
        String motherCaseId = "motherCaseId";

        CareCase careCase=new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withOPV0Date(null).withMotherCaseId(motherCaseId).build();
        childService.process(careCase);

        EnrollmentsQuery query = new EnrollmentsQuery()
                .havingExternalId(caseId)
                .havingState(EnrollmentStatus.DEFAULTED)
                .havingSchedule(scheduleName);

        assertFalse(scheduleTrackingService.searchWithWindowDates(query).isEmpty());


        careCase=new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withOPV0Date(OPV0Taken.toString()).withMotherCaseId(motherCaseId).build();
        childService.process(careCase);

        markScheduleForUnEnrollment(caseId, scheduleName);

        assertNull(scheduleTrackingService.getEnrollment(caseId, scheduleName));

        Child child = allChildren.findByCaseId(caseId);
        assertEquals(dob, child.getDOB());
        assertEquals(OPV0Taken, child.getOpv0Date());
    }
}