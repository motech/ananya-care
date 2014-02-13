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
import org.motechproject.care.service.schedule.Opv0Service;
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

public class Opv0IntegrationTest extends SpringIntegrationTest {

    @Autowired
    private Opv0Service Opv0Service;
    @Autowired
    private AllChildren allChildren;

    private String caseId;
    private ChildService childService;
    private final String scheduleName = ChildVaccinationSchedule.OPV0.getName();

    @Before
    public void setUp(){
        caseId = CaseUtils.getUniqueCaseId();
        List<VaccinationService> vaccinationServices = Arrays.asList((VaccinationService) Opv0Service);
        VaccinationProcessor childVaccinationProcessor = new VaccinationProcessor(vaccinationServices);
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

        Child child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withOPV0Date(null).withMotherCaseId(motherCaseId).build();
        childService.process(child);

        markScheduleForUnEnrollment(caseId, scheduleName);
        EnrollmentsQuery query = new EnrollmentsQuery()
                .havingExternalId(caseId)
                .havingState(EnrollmentStatus.ACTIVE)
                .havingSchedule(scheduleName);

        EnrollmentRecord enrollment = trackingService.searchWithWindowDates(query).get(0);

        assertEquals(MilestoneType.OPV0.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(dob, enrollment.getReferenceDateTime().withTimeAtStartOfDay());
        assertEquals(dob, enrollment.getStartOfDueWindow().withTimeAtStartOfDay());
        assertEquals(dob.plusDays(15), enrollment.getStartOfLateWindow().withTimeAtStartOfDay());

        Child childFromDb = allChildren.findByCaseId(caseId);
        assertEquals(dob, childFromDb.getDOB());
        assertNull(childFromDb.getOpv0Date());
    }

    @Test
    public void shouldVerifyOPV0ScheduleNotCreatedWhenChildIsOlderThan15Days() {
        DateTime dob = DateUtil.newDateTime(DateUtil.today().minusDays(17));

        String motherCaseId = "motherCaseId";

        Child child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withOPV0Date(null).withMotherCaseId(motherCaseId).build();
        childService.process(child);

        markScheduleForUnEnrollment(caseId,  scheduleName);
        EnrollmentsQuery query = new EnrollmentsQuery()
                .havingExternalId(caseId)
                .havingState(EnrollmentStatus.ACTIVE)
                .havingSchedule(scheduleName);

        assertTrue(trackingService.searchWithWindowDates(query).isEmpty());

        Child childFromDb = allChildren.findByCaseId(caseId);
        assertEquals(dob, childFromDb.getDOB());
        assertNull(childFromDb.getOpv0Date());
    }

    @Test
    public void shouldVerifyOPV0ScheduleFulfillmentWhenChildHasTakenOPV0() {
        DateTime dob = DateUtil.newDateTime(DateUtil.today().minusMonths(2));
        DateTime OPV0Taken = DateUtil.newDateTime(DateUtil.today().minusMonths(1));
        String motherCaseId = "motherCaseId";

        Child child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withOPV0Date(null).withMotherCaseId(motherCaseId).build();
        childService.process(child);

        EnrollmentsQuery query = new EnrollmentsQuery()
                .havingExternalId(caseId)
                .havingState(EnrollmentStatus.DEFAULTED)
                .havingSchedule(scheduleName);

        assertFalse(trackingService.searchWithWindowDates(query).isEmpty());


        child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withOPV0Date(OPV0Taken).withMotherCaseId(motherCaseId).build();
        childService.process(child);

        markScheduleForUnEnrollment(caseId, scheduleName);

        assertNull(trackingService.getEnrollment(caseId, scheduleName));

        Child childFromDb = allChildren.findByCaseId(caseId);
        assertEquals(dob, childFromDb.getDOB());
        assertEquals(OPV0Taken, childFromDb.getOpv0Date());
    }
}
