package org.motechproject.care.integration.service;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.request.CareCase;
import org.motechproject.care.schedule.service.TTSchedulerService;
import org.motechproject.care.schedule.vaccinations.MotherVaccinationSchedule;
import org.motechproject.care.service.MotherService;
import org.motechproject.care.service.MotherVaccinationProcessor;
import org.motechproject.care.service.builder.MotherCareCaseBuilder;
import org.motechproject.care.service.schedule.TTService;
import org.motechproject.care.service.schedule.VaccinationService;
import org.motechproject.care.utils.CaseUtils;
import org.motechproject.care.utils.SpringIntegrationTest;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-Web.xml")
public class TTIntegrationTest extends SpringIntegrationTest {

    @Autowired
    private TTService ttService;
    @Autowired
    private ScheduleTrackingService scheduleTrackingService;
    @Autowired

    private AllMothers allMothers;
    private final String caseId = CaseUtils.getUniqueCaseId();
    private MotherService motherService;

    @After
    public void tearDown() {
        allMothers.removeAll();
    }

    @Before
    public void setUp(){
        List<VaccinationService> ttServices = Arrays.asList((VaccinationService) ttService);
        MotherVaccinationProcessor motherVaccinationProcessor = new MotherVaccinationProcessor(ttServices);
        motherService = new MotherService(allMothers, motherVaccinationProcessor);
    }
    
    @Test
    public void shouldVerifyTTScheduleCreationWhenMotherIsRegistered() {
        String ttScheduleName = MotherVaccinationSchedule.TT.getName();
        LocalDate edd = DateUtil.today().plusMonths(4);

        CareCase careCase=new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withTT1(null).build();
        motherService.process(careCase);
        markScheduleForUnEnrollment(caseId, ttScheduleName);
        EnrollmentRecord enrollment = scheduleTrackingService.getEnrollment(caseId, ttScheduleName);

        assertEquals(TTSchedulerService.tt1Milestone, enrollment.getCurrentMilestoneName());
        assertEquals(DateUtil.newDateTime(edd.minusMonths(9)), enrollment.getReferenceDateTime());
    }

    @Test
    public void shouldVerifyTTScheduleFulfillmentWhenMotherHasTakenTT1() {
        String ttScheduleName = MotherVaccinationSchedule.TT.getName();
        LocalDate edd = DateUtil.today().plusMonths(4);
        LocalDate tt1Taken = DateUtil.today().plusMonths(1);

        CareCase careCase=new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withTT1(null).build();
        motherService.process(careCase);
        careCase=new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withTT1(tt1Taken.toString()).build();
        motherService.process(careCase);

        markScheduleForUnEnrollment(caseId, ttScheduleName);
        EnrollmentRecord enrollment = scheduleTrackingService.getEnrollment(caseId, ttScheduleName);

        assertEquals(TTSchedulerService.tt2Milestone, enrollment.getCurrentMilestoneName());
    }
}
