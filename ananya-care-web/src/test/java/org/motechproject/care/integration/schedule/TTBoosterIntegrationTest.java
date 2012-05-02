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
import org.motechproject.care.service.schedule.TTBoosterService;
import org.motechproject.care.service.schedule.VaccinationService;
import org.motechproject.care.service.util.PeriodUtil;
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

public class TTBoosterIntegrationTest extends SpringIntegrationTest {

    @Autowired
    private TTBoosterService ttBoosterService;
    @Autowired
    private ScheduleTrackingService scheduleTrackingService;
    @Autowired

    private AllMothers allMothers;
    private String caseId;
    private MotherService motherService;
    String scheduleName = MotherVaccinationSchedule.TTBooster.getName();

    @After
    public void tearDown() {
        allMothers.removeAll();
    }

    @Before
    public void setUp(){
        caseId = CaseUtils.getUniqueCaseId();
        List<VaccinationService> ttServices = Arrays.asList((VaccinationService) ttBoosterService);
        MotherVaccinationProcessor motherVaccinationProcessor = new MotherVaccinationProcessor(ttServices);
        motherService = new MotherService(allMothers, motherVaccinationProcessor);
    }

    @Test
    public void shouldVerifyTTBoosterScheduleCreationWhenMotherIsRegisteredWithLastPregSetToTrue() {

        LocalDate edd = DateUtil.today().plusMonths(4);

        CareCase careCase=new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withLastPregTT("yes").withTTBooster(null).build();
        motherService.process(careCase);
        markScheduleForUnEnrollment(caseId, scheduleName);
        EnrollmentRecord enrollment = getEnrollmentRecord(scheduleName, caseId, EnrollmentStatus.ACTIVE);

        assertEquals(MilestoneType.TTBooster.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(DateUtil.newDateTime(edd.minusDays(PeriodUtil.DAYS_IN_9_MONTHS)), enrollment.getReferenceDateTime());
        assertEquals(DateUtil.newDateTime(edd.minusDays(PeriodUtil.DAYS_IN_9_MONTHS)), enrollment.getStartOfDueWindow());
        assertEquals(DateUtil.newDateTime(edd), enrollment.getStartOfLateWindow());
    }

    @Test
    public void shouldVerifyTTBoosterScheduleFulfillmentWhenMotherHasTakenTTBooster() {

        LocalDate today = DateUtil.today();
        LocalDate edd = today.plusDays(PeriodUtil.DAYS_IN_9_MONTHS);
        LocalDate ttBoosterDate = today.plusWeeks(4);

        CareCase careCase=new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withLastPregTT("yes").withTTBooster(ttBoosterDate.toString()).build();
        motherService.process(careCase);

        assertNull(scheduleTrackingService.getEnrollment(caseId, scheduleName));
    }

    private EnrollmentRecord getEnrollmentRecord(String ttScheduleName, String externalId, EnrollmentStatus status) {
        EnrollmentsQuery query = new EnrollmentsQuery()
                .havingExternalId(externalId)
                .havingState(status)
                .havingSchedule(ttScheduleName);

        return scheduleTrackingService.searchWithWindowDates(query).get(0);
    }
}
