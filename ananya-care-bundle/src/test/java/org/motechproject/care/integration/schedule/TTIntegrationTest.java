package org.motechproject.care.integration.schedule;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.vaccinations.MotherVaccinationSchedule;
import org.motechproject.care.service.CareCaseTaskService;
import org.motechproject.care.service.MotherService;
import org.motechproject.care.service.VaccinationProcessor;
import org.motechproject.care.service.builder.MotherBuilder;
import org.motechproject.care.service.schedule.TTService;
import org.motechproject.care.service.schedule.VaccinationService;
import org.motechproject.care.service.util.PeriodUtil;
import org.motechproject.care.utils.CaseUtils;
import org.motechproject.care.utils.DummyCareCaseTaskService;
import org.motechproject.care.utils.SpringIntegrationTest;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.commons.date.util.DateUtil;
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
        VaccinationProcessor motherVaccinationProcessor = new VaccinationProcessor(ttServices);
        motherService = new MotherService(allMothers, motherVaccinationProcessor);
    }

    @Test
    public void shouldVerifyTTScheduleCreationWhenMotherIsRegistered() {
        String ttScheduleName = MotherVaccinationSchedule.TT.getName();
        DateTime edd = DateUtil.newDateTime(DateUtil.today()).plusMonths(4);

        Mother mother=new MotherBuilder().withCaseId(caseId).withEdd(edd).withTT1(null).withTT2(null).build();
        motherService.process(mother);
        markScheduleForUnEnrollment(caseId, ttScheduleName);
        EnrollmentRecord enrollment = getEnrollmentRecord(ttScheduleName, caseId, EnrollmentStatus.ACTIVE);

        assertEquals(MilestoneType.TT1.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(edd.minusDays(PeriodUtil.DAYS_IN_9_MONTHS), enrollment.getReferenceDateTime().withTimeAtStartOfDay());
        assertEquals(edd.minusDays(PeriodUtil.DAYS_IN_9_MONTHS), enrollment.getStartOfDueWindow().withTimeAtStartOfDay());
        assertEquals(edd.plusWeeks(2), enrollment.getStartOfLateWindow().withTimeAtStartOfDay());
    }

    @Test
    public void shouldVerifyTT1ScheduleFulfillmentWhenMotherHasTakenTT1() {
        String ttScheduleName = MotherVaccinationSchedule.TT.getName();
        DateTime edd = DateUtil.newDateTime(DateUtil.today()).plusMonths(4);
        DateTime tt1Taken = DateUtil.newDateTime(DateUtil.today()).plusMonths(1);

        Mother mother=new MotherBuilder().withCaseId(caseId).withEdd(edd).withTT1(null).withTT2(null).build();
        motherService.process(mother);
        mother = new MotherBuilder().withCaseId(caseId).withEdd(edd).withTT1(tt1Taken).withTT2(null).build();
        motherService.process(mother);

        markScheduleForUnEnrollment(caseId, ttScheduleName);
        EnrollmentRecord enrollment = getEnrollmentRecord(ttScheduleName, caseId, EnrollmentStatus.ACTIVE);

        assertEquals(MilestoneType.TT2.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(tt1Taken.plusWeeks(4), enrollment.getStartOfDueWindow().withTimeAtStartOfDay());
        assertEquals(tt1Taken.plusDays(PeriodUtil.DAYS_IN_9_MONTHS).plusWeeks(2), enrollment.getStartOfLateWindow().withTimeAtStartOfDay());
    }

    @Test
    public void shouldVerifyTT2ScheduleFulfillmentWhenMotherHasTakenTT2() {
        String ttScheduleName = MotherVaccinationSchedule.TT.getName();
        DateTime edd = DateUtil.newDateTime(DateUtil.today()).plusMonths(4);
        DateTime tt1Taken = DateUtil.newDateTime(DateUtil.today()).plusMonths(1);
        DateTime tt2Taken = DateUtil.newDateTime(DateUtil.today()).plusMonths(3);

        Mother mother=new MotherBuilder().withCaseId(caseId).withEdd(edd).withTT1(null).withTT2(null).build();
        motherService.process(mother);
        mother=new MotherBuilder().withCaseId(caseId).withEdd(edd).withTT1(tt1Taken).withTT2(null).build();
        motherService.process(mother);
        mother=new MotherBuilder().withCaseId(caseId).withEdd(edd).withTT1(tt1Taken).withTT2(tt2Taken).build();
        motherService.process(mother);

        assertNull(trackingService.getEnrollment(caseId, ttScheduleName));
    }

    @Test
    public void shouldCloseTTScheduleWhenMotherIsDead() {
        String ttScheduleName = MotherVaccinationSchedule.TT.getName();
        DateTime edd = DateUtil.newDateTime(DateUtil.today()).plusMonths(4);

        Mother mother=new MotherBuilder().withCaseId(caseId).withEdd(edd).withTT1(null).withTT2(null).build();
        motherService.process(mother);
        markScheduleForUnEnrollment(caseId, ttScheduleName);
        EnrollmentRecord enrollment = getEnrollmentRecord(ttScheduleName, caseId, EnrollmentStatus.ACTIVE);

        assertEquals(MilestoneType.TT1.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(edd.minusDays(PeriodUtil.DAYS_IN_9_MONTHS), enrollment.getReferenceDateTime().withTimeAtStartOfDay());
        assertEquals(edd.minusDays(PeriodUtil.DAYS_IN_9_MONTHS), enrollment.getStartOfDueWindow().withTimeAtStartOfDay());
        assertEquals(edd.plusWeeks(2), enrollment.getStartOfLateWindow().withTimeAtStartOfDay());


        mother=new MotherBuilder().withCaseId(caseId).withEdd(edd).withTT1(null).withTT2(null).withAlive(false).build();
        motherService.process(mother);
        enrollment = getEnrollmentRecord(ttScheduleName, caseId, EnrollmentStatus.ACTIVE);
        assertNull(enrollment);

        DummyCareCaseTaskService dummyCareCaseTaskService = (DummyCareCaseTaskService) careCaseTaskService;
        assertEquals(caseId, dummyCareCaseTaskService.getClientCaseId());
        assertEquals(MilestoneType.TT1.toString(), dummyCareCaseTaskService.getMilestoneName());
    }
}
