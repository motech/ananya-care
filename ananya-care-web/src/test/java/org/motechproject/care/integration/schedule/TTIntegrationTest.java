package org.motechproject.care.integration.schedule;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.request.CareCase;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.vaccinations.MotherVaccinationSchedule;
import org.motechproject.care.service.CareCaseTaskService;
import org.motechproject.care.service.MotherService;
import org.motechproject.care.service.MotherVaccinationProcessor;
import org.motechproject.care.service.builder.MotherCareCaseBuilder;
import org.motechproject.care.service.schedule.TTService;
import org.motechproject.care.service.schedule.VaccinationService;
import org.motechproject.care.service.util.PeriodUtil;
import org.motechproject.care.utils.CaseUtils;
import org.motechproject.care.utils.DummyCareCaseTaskService;
import org.motechproject.care.utils.SpringIntegrationTest;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class TTIntegrationTest extends SpringIntegrationTest {

    @Autowired
    private TTService ttService;

    @Autowired
    private CareCaseTaskService careCaseTaskService;

    @Autowired
    private AllMothers allMothers;

    private String caseId;

    private MotherService motherService;

    @After
    public void tearDown() {
        allMothers.removeAll();
    }


    @Before
    public void setUp(){
        caseId = CaseUtils.getUniqueCaseId();
        List<VaccinationService> ttServices = Arrays.asList((VaccinationService) ttService);
        MotherVaccinationProcessor motherVaccinationProcessor = new MotherVaccinationProcessor(ttServices);
        motherService = new MotherService(allMothers, motherVaccinationProcessor);
    }

    @Test
    public void shouldVerifyTTScheduleCreationWhenMotherIsRegistered() {
        String ttScheduleName = MotherVaccinationSchedule.TT.getName();
        LocalDate edd = DateUtil.today().plusMonths(4);

        CareCase careCase=new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withTT1(null).withTT2(null).build();
        motherService.process(careCase);
        markScheduleForUnEnrollment(caseId, ttScheduleName);
        EnrollmentRecord enrollment = getEnrollmentRecord(ttScheduleName, caseId, EnrollmentStatus.ACTIVE);

        assertEquals(MilestoneType.TT1.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(DateUtil.newDateTime(edd.minusDays(PeriodUtil.DAYS_IN_9_MONTHS)), enrollment.getReferenceDateTime());
        assertEquals(DateUtil.newDateTime(edd.minusDays(PeriodUtil.DAYS_IN_9_MONTHS)), enrollment.getStartOfDueWindow());
        assertEquals(DateUtil.newDateTime(edd), enrollment.getStartOfLateWindow());
    }

    @Test
    public void shouldVerifyTT1ScheduleFulfillmentWhenMotherHasTakenTT1() {
        String ttScheduleName = MotherVaccinationSchedule.TT.getName();
        LocalDate edd = DateUtil.today().plusMonths(4);
        LocalDate tt1Taken = DateUtil.today().plusMonths(1);

        CareCase careCase=new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withTT1(null).withTT2(null).build();
        motherService.process(careCase);
        careCase = new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withTT1(tt1Taken.toString()).withTT2(null).build();
        motherService.process(careCase);

        markScheduleForUnEnrollment(caseId, ttScheduleName);
        EnrollmentRecord enrollment = getEnrollmentRecord(ttScheduleName, caseId, EnrollmentStatus.ACTIVE);

        assertEquals(MilestoneType.TT2.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(DateUtil.newDateTime(tt1Taken.plusWeeks(4)), enrollment.getStartOfDueWindow());
        assertEquals(DateUtil.newDateTime(tt1Taken.plusDays(PeriodUtil.DAYS_IN_9_MONTHS)), enrollment.getStartOfLateWindow());
    }

    @Test
    public void shouldVerifyTT2ScheduleFulfillmentWhenMotherHasTakenTT2() {
        String ttScheduleName = MotherVaccinationSchedule.TT.getName();
        LocalDate edd = DateUtil.today().plusMonths(4);
        LocalDate tt1Taken = DateUtil.today().plusMonths(1);
        LocalDate tt2Taken = DateUtil.today().plusMonths(3);

        CareCase careCase=new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withTT1(null).withTT2(null).build();
        motherService.process(careCase);
        careCase=new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withTT1(tt1Taken.toString()).withTT2(null).build();
        motherService.process(careCase);
        careCase=new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withTT1(tt1Taken.toString()).withTT2(tt2Taken.toString()).build();
        motherService.process(careCase);

        assertNull(trackingService.getEnrollment(caseId, ttScheduleName));
    }

    @Test
    public void shouldCloseTTScheduleWhenMotherIsDead() {
        String ttScheduleName = MotherVaccinationSchedule.TT.getName();
        LocalDate edd = DateUtil.today().plusMonths(4);

        CareCase careCase=new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withTT1(null).withTT2(null).build();
        motherService.process(careCase);
        markScheduleForUnEnrollment(caseId, ttScheduleName);
        EnrollmentRecord enrollment = getEnrollmentRecord(ttScheduleName, caseId, EnrollmentStatus.ACTIVE);

        assertEquals(MilestoneType.TT1.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(DateUtil.newDateTime(edd.minusDays(PeriodUtil.DAYS_IN_9_MONTHS)), enrollment.getReferenceDateTime());
        assertEquals(DateUtil.newDateTime(edd.minusDays(PeriodUtil.DAYS_IN_9_MONTHS)), enrollment.getStartOfDueWindow());
        assertEquals(DateUtil.newDateTime(edd), enrollment.getStartOfLateWindow());


        careCase=new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withTT1(null).withTT2(null).withMotherAlive("no").build();
        motherService.process(careCase);
        enrollment = getEnrollmentRecord(ttScheduleName, caseId, EnrollmentStatus.ACTIVE);
        assertNull(enrollment);

        DummyCareCaseTaskService dummyCareCaseTaskService = (DummyCareCaseTaskService) careCaseTaskService;
        assertEquals(caseId, dummyCareCaseTaskService.getClientCaseId());
        assertEquals(MilestoneType.TT1.toString(), dummyCareCaseTaskService.getMilestoneName());
    }
}
