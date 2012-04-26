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
import org.motechproject.care.service.schedule.TTService;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


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

        CareCase careCase=new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withTT1(null).withTT2(null).build();
        motherService.process(careCase);
        markScheduleForUnEnrollment(caseId, ttScheduleName);
        EnrollmentRecord enrollment = getEnrollmentRecord(ttScheduleName, caseId, EnrollmentStatus.ACTIVE);

        assertEquals(MilestoneType.TT1.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(DateUtil.newDateTime(edd.minusMonths(9)), enrollment.getReferenceDateTime());
        assertEquals(DateUtil.newDateTime(edd.minusMonths(9)), enrollment.getStartOfDueWindow());
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

        markScheduleForUnEnrollment(caseId, ttScheduleName);
        assertNull(scheduleTrackingService.getEnrollment(caseId, ttScheduleName));
    }

    private EnrollmentRecord getEnrollmentRecord(String ttScheduleName, String externalId, EnrollmentStatus status) {
        EnrollmentsQuery query = new EnrollmentsQuery()
                .havingExternalId(externalId)
                .havingState(status)
                .havingSchedule(ttScheduleName);

        return scheduleTrackingService.searchWithWindowDates(query).get(0);
    }
}
