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
import org.motechproject.care.service.schedule.DptBoosterService;
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


public class DptBoosterIntegrationTest extends SpringIntegrationTest {

    @Autowired
    private DptBoosterService dptBoosterService;
    @Autowired
    private AllChildren allChilden;

    private String caseId;
    private ChildService childService;
    private String scheduleName = ChildVaccinationSchedule.DPTBooster.getName();

    @After
    public void tearDown() {
        allChilden.removeAll();
    }

    @Before
    public void setUp(){
        caseId = CaseUtils.getUniqueCaseId();
        List<VaccinationService> ancServices = Arrays.asList((VaccinationService) dptBoosterService);
        VaccinationProcessor childVaccinationProcessor = new VaccinationProcessor(ancServices);
        childService = new ChildService(allChilden, childVaccinationProcessor);
    }
    
    @Test
    public void shouldVerifyDPTBoosterScheduleWithStartDateAs16MonthsAgeIfDpt3IsFulfilledMuchBefore() {
        DateTime dob = DateUtil.newDateTime(DateUtil.today());
        DateTime dpt1Date = dob.plusWeeks(7);
        DateTime dpt2Date = dob.plusWeeks(11);
        DateTime dpt3Date = dob.plusWeeks(15);
        DateTime expectedReferenceDate = dob.plusMonths(16).plus(periodUtil.getScheduleOffset());
        DateTime expectedStartDueDate = dob.plusMonths(16);

        Child child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withDpt1Date(null).withDpt2Date(null).withDpt3Date(null).withDptBoosterDate(null).build();
        childService.process(child);
        markScheduleForUnEnrollment(caseId, scheduleName);
        EnrollmentRecord enrollment;
        assertNull(getEnrollmentRecord(scheduleName, caseId, EnrollmentStatus.ACTIVE));

        child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withDpt1Date(dpt1Date).withDpt2Date(dpt2Date).withDpt3Date(dpt3Date).withDptBoosterDate(null).build();
        childService.process(child);
        enrollment = getEnrollmentRecord(scheduleName, caseId, EnrollmentStatus.ACTIVE);
        assertEquals(MilestoneType.DPTBooster.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(expectedReferenceDate, enrollment.getReferenceDateTime().withTimeAtStartOfDay());
        assertEquals(expectedStartDueDate, enrollment.getStartOfDueWindow().withTimeAtStartOfDay());
        assertEquals(expectedReferenceDate.plusMonths(8).plusWeeks(2), enrollment.getStartOfLateWindow().withTimeAtStartOfDay());
    }

    @Test
    public void shouldVerifyDPTBoosterScheduleWithStartDateAfter16MonthsAgeIfDpt3IsNotFulfilledMuchBefore() {
        DateTime dob = DateUtil.newDateTime(DateUtil.today());
        DateTime dpt1Date = dob.plusWeeks(7);
        DateTime dpt2Date = dob.plusWeeks(11);
        DateTime dpt3Date = dob.plusMonths(15);
        DateTime expectedReferenceDate = dpt3Date.plusDays(180).plus(periodUtil.getScheduleOffset());
        DateTime expectedStartDueDate = dpt3Date.plusDays(180);
        DateTime expectedStartLateDate = expectedReferenceDate.plusMonths(8).plusWeeks(2);

        Child child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withDpt1Date(null).withDpt2Date(null).withDpt3Date(null).withDptBoosterDate(null).build();
        childService.process(child);
        markScheduleForUnEnrollment(caseId, scheduleName);
        EnrollmentRecord enrollment;
        assertNull(getEnrollmentRecord(scheduleName, caseId, EnrollmentStatus.ACTIVE));

        child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withDpt1Date(dpt1Date).withDpt2Date(dpt2Date).withDpt3Date(dpt3Date).withDptBoosterDate(null).build();
        childService.process(child);
        enrollment = getEnrollmentRecord(scheduleName, caseId, EnrollmentStatus.ACTIVE);
        assertEquals(MilestoneType.DPTBooster.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(expectedReferenceDate, enrollment.getReferenceDateTime().withTimeAtStartOfDay());
        assertEquals(expectedStartDueDate, enrollment.getStartOfDueWindow().withTimeAtStartOfDay());
        assertEquals(expectedStartLateDate, enrollment.getStartOfLateWindow().withTimeAtStartOfDay());
    }

    @Test
    public void shouldVerifyDPTBoosterScheduleFulfillmentWhenDPT4VisitIsOver() {
        DateTime dob = DateUtil.newDateTime(DateUtil.today());
        DateTime dpt1Date = dob.plusWeeks(7);
        DateTime dpt2Date = dob.plusWeeks(11);
        DateTime dpt3Date = dob.plusMonths(15);
        DateTime dptBoosterDate = dob.plusMonths(21);

        Child child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withDpt1Date(null).withDpt2Date(null).withDpt3Date(null).withDptBoosterDate(null).build();
        childService.process(child);
        markScheduleForUnEnrollment(caseId, scheduleName);
        assertNull(getEnrollmentRecord(scheduleName, caseId, EnrollmentStatus.ACTIVE));

        child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withDpt1Date(dpt1Date).withDpt2Date(dpt2Date).withDpt3Date(dpt3Date).withDptBoosterDate(dptBoosterDate).build();
        childService.process(child);

        assertNull(trackingService.getEnrollment(caseId, scheduleName));
    }
}