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
import org.motechproject.care.service.schedule.OpvBoosterService;
import org.motechproject.care.service.schedule.VaccinationService;
import org.motechproject.care.utils.CaseUtils;
import org.motechproject.care.utils.SpringIntegrationTest;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.commons.date.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class OpvBoosterIntegrationTest extends SpringIntegrationTest {

    @Autowired
    private OpvBoosterService opvBoosterService;
    @Autowired
    private AllChildren allChilden;

    private String caseId;
    private ChildService childService;
    private String scheduleName = ChildVaccinationSchedule.OPVBooster.getName();

    @After
    public void tearDown() {
        allChilden.removeAll();
    }

    @Before
    public void setUp(){
        caseId = CaseUtils.getUniqueCaseId();
        List<VaccinationService> ancServices = Arrays.asList((VaccinationService) opvBoosterService);
        VaccinationProcessor childVaccinationProcessor = new VaccinationProcessor(ancServices);
        childService = new ChildService(allChilden, childVaccinationProcessor);
    }
    
    @Test
    public void shouldVerifyOPVBoosterScheduleWithStartDateAs16MonthsAgeIfOPV3IsFulfilledMuchBefore() {
        DateTime dob = DateUtil.newDateTime(DateUtil.today());
        DateTime opv1Date = dob.plusWeeks(7);
        DateTime opv2Date = dob.plusWeeks(11);
        DateTime opv3Date = dob.plusWeeks(15);
        DateTime expectedReferenceDate = dob.plusMonths(16).plus(periodUtil.getScheduleOffset());
        DateTime expectedStartDueDate = dob.plusMonths(16);

        Child child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withOPV1Date(null).withOPV2Date(null).withOPV3Date(null).withOPVBoosterDate(null).build();
        childService.process(child);
        markScheduleForUnEnrollment(caseId, scheduleName);
        EnrollmentRecord enrollment;
        assertNull(getEnrollmentRecord(scheduleName, caseId, EnrollmentStatus.ACTIVE));

        child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withOPV1Date(opv1Date).withOPV2Date(opv2Date).withOPV3Date(opv3Date).withOPVBoosterDate(null).build();
        childService.process(child);
        enrollment = getEnrollmentRecord(scheduleName, caseId, EnrollmentStatus.ACTIVE);
        assertEquals(MilestoneType.OPVBooster.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(expectedReferenceDate, enrollment.getReferenceDateTime().withTimeAtStartOfDay());
        assertEquals(expectedStartDueDate, enrollment.getStartOfDueWindow().withTimeAtStartOfDay());
        assertEquals(expectedReferenceDate.plusMonths(8).plusWeeks(2), enrollment.getStartOfLateWindow().withTimeAtStartOfDay());
    }

    @Test
    public void shouldVerifyOPVBoosterScheduleWithStartDateAfter16MonthsAgeIfOPV3IsNotFulfilledMuchBefore() {
        DateTime dob = DateUtil.newDateTime(DateUtil.today());
        DateTime opv1Date = dob.plusWeeks(7);
        DateTime opv2Date = dob.plusWeeks(11);
        DateTime opv3Date = dob.plusMonths(15);
        DateTime expectedReferenceDate = opv3Date.plusDays(180).plus(periodUtil.getScheduleOffset());
        DateTime expectedStartDueDate = opv3Date.plusDays(180);
        DateTime expectedStartLateDate = expectedReferenceDate.plusMonths(8).plusWeeks(2);

        Child child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withOPV1Date(null).withOPV2Date(null).withOPV3Date(null).withOPVBoosterDate(null).build();
        childService.process(child);
        markScheduleForUnEnrollment(caseId, scheduleName);
        EnrollmentRecord enrollment;
        assertNull(getEnrollmentRecord(scheduleName, caseId, EnrollmentStatus.ACTIVE));

        child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withOPV1Date(opv1Date).withOPV2Date(opv2Date).withOPV3Date(opv3Date).withOPVBoosterDate(null).build();
        childService.process(child);
        enrollment = getEnrollmentRecord(scheduleName, caseId, EnrollmentStatus.ACTIVE);
        assertEquals(MilestoneType.OPVBooster.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(expectedReferenceDate, enrollment.getReferenceDateTime().withTimeAtStartOfDay());
        assertEquals(expectedStartDueDate, enrollment.getStartOfDueWindow().withTimeAtStartOfDay());
        assertEquals(expectedStartLateDate, enrollment.getStartOfLateWindow().withTimeAtStartOfDay());
    }

    @Test
    public void shouldVerifyOPVBoosterScheduleFulfillmentWhenOPV4VisitIsOver() {
        DateTime dob = DateUtil.newDateTime(DateUtil.today());
        DateTime opv1Date = dob.plusWeeks(7);
        DateTime opv2Date = dob.plusWeeks(11);
        DateTime opv3Date = dob.plusMonths(15);
        DateTime opvBoosterDate = dob.plusMonths(21);

        Child child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withOPV1Date(null).withOPV2Date(null).withOPV3Date(null).withOPVBoosterDate(null).build();
        childService.process(child);
        markScheduleForUnEnrollment(caseId, scheduleName);
        assertNull(getEnrollmentRecord(scheduleName, caseId, EnrollmentStatus.ACTIVE));

        child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withOPV1Date(opv1Date).withOPV2Date(opv2Date).withOPV3Date(opv3Date).withOPVBoosterDate(opvBoosterDate).build();
        childService.process(child);

        assertNull(trackingService.getEnrollment(caseId, scheduleName));
    }
}