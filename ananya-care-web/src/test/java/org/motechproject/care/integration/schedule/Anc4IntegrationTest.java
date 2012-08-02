package org.motechproject.care.integration.schedule;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.request.CareCase;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.vaccinations.MotherVaccinationSchedule;
import org.motechproject.care.service.MotherService;
import org.motechproject.care.service.MotherVaccinationProcessor;
import org.motechproject.care.service.builder.MotherCareCaseBuilder;
import org.motechproject.care.service.schedule.Anc4Service;
import org.motechproject.care.service.schedule.VaccinationService;
import org.motechproject.care.service.util.PeriodUtil;
import org.motechproject.care.utils.CaseUtils;
import org.motechproject.care.utils.SpringIntegrationTest;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.util.DateUtil;
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
        MotherVaccinationProcessor motherVaccinationProcessor = new MotherVaccinationProcessor(ancServices);
        motherService = new MotherService(allMothers, motherVaccinationProcessor);
    }
    
    @Test
    public void shouldVerifyAnc4ScheduleWithStartOfTrimesterWhenAnc3IsFulfilledMuchBefore() {
        LocalDate today = DateUtil.today();
        LocalDate edd = today.plusMonths(8);
        LocalDate anc1Date = today.plusDays(10);
        LocalDate anc2Date = today.plusDays(50);
        LocalDate anc3Date = today.plusMonths(3);
        LocalDate expectedReferenceDate = edd.minusDays(PeriodUtil.DAYS_IN_3RD_TRIMESTER).plus(periodUtil.getScheduleOffset());
        LocalDate expectedStartDueDate = edd.minusDays(PeriodUtil.DAYS_IN_3RD_TRIMESTER);

        CareCase careCase=new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withANC1(null).withANC2(null).withANC3(null).withANC4(null).build();
        motherService.process(careCase);
        markScheduleForUnEnrollment(caseId, scheduleName);
        EnrollmentRecord enrollment;
        assertNull(getEnrollmentRecord(scheduleName, caseId, EnrollmentStatus.ACTIVE));

        careCase=new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withANC1(anc1Date.toString()).withANC2(anc2Date.toString()).withANC3(anc3Date.toString()).withANC4(null).build();
        motherService.process(careCase);
        enrollment = getEnrollmentRecord(scheduleName, caseId, EnrollmentStatus.ACTIVE);
        assertEquals(MilestoneType.Anc4.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(DateUtil.newDateTime(expectedReferenceDate), enrollment.getReferenceDateTime().withTimeAtStartOfDay());
        assertEquals(DateUtil.newDateTime(expectedStartDueDate), enrollment.getStartOfDueWindow().withTimeAtStartOfDay());
        assertEquals(DateUtil.newDateTime(edd), enrollment.getStartOfLateWindow().withTimeAtStartOfDay());
    }

    @Test
    public void shouldVerifyAnc4ScheduleWithAnc3DateWhenAnc3IsFulfilledInThirdTrimester() {
        LocalDate today = DateUtil.today();
        LocalDate edd = today.plusMonths(9);
        LocalDate anc1Date = today.plusDays(10);
        LocalDate anc2Date = today.plusDays(50);
        LocalDate anc3Date = today.plusMonths(7);
        LocalDate expectedReferenceDate = anc3Date.plusDays(30).plus(periodUtil.getScheduleOffset());
        LocalDate expectedStartDueDate = anc3Date.plusDays(30);
        LocalDate expectedStartLateDate = anc3Date.plusDays(30).plusDays(PeriodUtil.DAYS_IN_3RD_TRIMESTER);

        CareCase careCase=new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withANC1(null).withANC2(null).withANC3(null).withANC4(null).build();
        motherService.process(careCase);
        markScheduleForUnEnrollment(caseId, scheduleName);
        EnrollmentRecord enrollment = getEnrollmentRecord(scheduleName, caseId, EnrollmentStatus.ACTIVE);
        assertNull(enrollment);

        careCase=new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withANC1(anc1Date.toString()).withANC2(anc2Date.toString()).withANC3(anc3Date.toString()).withANC4(null).build();
        motherService.process(careCase);
        enrollment = getEnrollmentRecord(scheduleName, caseId, EnrollmentStatus.ACTIVE);
        assertEquals(MilestoneType.Anc4.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(DateUtil.newDateTime(expectedReferenceDate), enrollment.getReferenceDateTime().withTimeAtStartOfDay());
        assertEquals(DateUtil.newDateTime(expectedStartDueDate), enrollment.getStartOfDueWindow().withTimeAtStartOfDay());
        assertEquals(DateUtil.newDateTime(expectedStartLateDate), enrollment.getStartOfLateWindow().withTimeAtStartOfDay());
    }

    @Test
    public void shouldVerifyAnc4ScheduleFulfillmentWhenAnc4VisitIsOver() {
        LocalDate today = DateUtil.today();
        LocalDate edd = today.plusMonths(9);
        LocalDate anc1Date = today.plusDays(10);
        LocalDate anc2Date = today.plusDays(50);
        LocalDate anc3Date = today.plusMonths(7);
        LocalDate anc4Date = today.plusMonths(8);

        CareCase careCase=new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withANC1(anc1Date.toString()).withANC2(anc2Date.toString()).withANC3(anc3Date.toString()).withANC4(null).build();
        motherService.process(careCase);
        careCase=new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withANC1(anc1Date.toString()).withANC2(anc2Date.toString()).withANC3(anc3Date.toString()).withANC4(anc4Date.toString()).build();
        motherService.process(careCase);

        assertNull(trackingService.getEnrollment(caseId, scheduleName));
    }
}