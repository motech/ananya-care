package org.motechproject.care.integration.schedule;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.vaccinations.MotherVaccinationSchedule;
import org.motechproject.care.service.MotherService;
import org.motechproject.care.service.VaccinationProcessor;
import org.motechproject.care.service.builder.MotherBuilder;
import org.motechproject.care.service.schedule.Anc4Service;
import org.motechproject.care.service.schedule.VaccinationService;
import org.motechproject.care.service.util.PeriodUtil;
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


public class Anc4IntegrationTest extends SpringIntegrationTest {

    @Autowired
    private Anc4Service anc4Service;
    @Autowired
    private AllMothers allMothers;

    private final String caseId = CaseUtils.getUniqueCaseId();
    private MotherService motherService;
    private String scheduleName = MotherVaccinationSchedule.Anc4.getName();

    @After
    public void tearDown() {
        allMothers.removeAll();
    }

    @Before
    public void setUp(){
        List<VaccinationService> ancServices = Arrays.asList((VaccinationService) anc4Service);
        VaccinationProcessor motherVaccinationProcessor = new VaccinationProcessor(ancServices);
        motherService = new MotherService(allMothers, motherVaccinationProcessor);
    }
    
    @Test
    public void shouldVerifyAnc4ScheduleWithStartOfTrimesterWhenAnc3IsFulfilledMuchBefore() {
        DateTime today = DateUtil.newDateTime(DateUtil.today());
        DateTime edd = today.plusMonths(8);
        DateTime anc1Date = today.plusDays(10);
        DateTime anc2Date = today.plusDays(50);
        DateTime anc3Date = today.plusMonths(3);
        DateTime expectedReferenceDate = edd.minusDays(PeriodUtil.DAYS_IN_3RD_TRIMESTER).plus(periodUtil.getScheduleOffset());
        DateTime expectedStartDueDate = edd.minusDays(PeriodUtil.DAYS_IN_3RD_TRIMESTER);

        Mother mother=new MotherBuilder().withCaseId(caseId).withEdd(edd).withANC1(null).withANC2(null).withANC3(null).withANC4(null).build();
        motherService.process(mother);
        markScheduleForUnEnrollment(caseId, scheduleName);
        EnrollmentRecord enrollment;
        assertNull(getEnrollmentRecord(scheduleName, caseId, EnrollmentStatus.ACTIVE));

        mother=new MotherBuilder().withCaseId(caseId).withEdd(edd).withANC1(anc1Date).withANC2(anc2Date).withANC3(anc3Date).withANC4(null).build();
        motherService.process(mother);
        enrollment = getEnrollmentRecord(scheduleName, caseId, EnrollmentStatus.ACTIVE);
        assertEquals(MilestoneType.Anc4.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(expectedReferenceDate, enrollment.getReferenceDateTime().withTimeAtStartOfDay());
        assertEquals(expectedStartDueDate, enrollment.getStartOfDueWindow().withTimeAtStartOfDay());
        assertEquals(edd.plusWeeks(2), enrollment.getStartOfLateWindow().withTimeAtStartOfDay());
    }

    @Test
    public void shouldVerifyAnc4ScheduleWithAnc3DateWhenAnc3IsFulfilledInThirdTrimester() {
        DateTime today = DateUtil.newDateTime(DateUtil.today());
        DateTime edd = today.plusMonths(9);
        DateTime anc1Date = today.plusDays(10);
        DateTime anc2Date = today.plusDays(50);
        DateTime anc3Date = today.plusMonths(7);
        DateTime expectedReferenceDate = anc3Date.plusDays(30).plus(periodUtil.getScheduleOffset());
        DateTime expectedStartDueDate = anc3Date.plusDays(30);
        DateTime expectedStartLateDate = anc3Date.plusDays(30).plusDays(PeriodUtil.DAYS_IN_3RD_TRIMESTER);

        Mother mother=new MotherBuilder().withCaseId(caseId).withEdd(edd).withANC1(null).withANC2(null).withANC3(null).withANC4(null).build();
        motherService.process(mother);
        markScheduleForUnEnrollment(caseId, scheduleName);
        EnrollmentRecord enrollment = getEnrollmentRecord(scheduleName, caseId, EnrollmentStatus.ACTIVE);
        assertNull(enrollment);

        mother=new MotherBuilder().withCaseId(caseId).withEdd(edd).withANC1(anc1Date).withANC2(anc2Date).withANC3(anc3Date).withANC4(null).build();
        motherService.process(mother);
        enrollment = getEnrollmentRecord(scheduleName, caseId, EnrollmentStatus.ACTIVE);
        assertEquals(MilestoneType.Anc4.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(expectedReferenceDate, enrollment.getReferenceDateTime().withTimeAtStartOfDay());
        assertEquals(expectedStartDueDate, enrollment.getStartOfDueWindow().withTimeAtStartOfDay());
        assertEquals(expectedStartLateDate.plusWeeks(2), enrollment.getStartOfLateWindow().withTimeAtStartOfDay());
    }

    @Test
    public void shouldVerifyAnc4ScheduleFulfillmentWhenAnc4VisitIsOver() {
        DateTime today = DateUtil.newDateTime(DateUtil.today());
        DateTime edd = today.plusMonths(9);
        DateTime anc1Date = today.plusDays(10);
        DateTime anc2Date = today.plusDays(50);
        DateTime anc3Date = today.plusMonths(7);
        DateTime anc4Date = today.plusMonths(8);

        Mother mother=new MotherBuilder().withCaseId(caseId).withEdd(edd).withANC1(anc1Date).withANC2(anc2Date).withANC3(anc3Date).withANC4(null).build();
        motherService.process(mother);
        mother=new MotherBuilder().withCaseId(caseId).withEdd(edd).withANC1(anc1Date).withANC2(anc2Date).withANC3(anc3Date).withANC4(anc4Date).build();
        motherService.process(mother);

        assertNull(trackingService.getEnrollment(caseId, scheduleName));
    }
}