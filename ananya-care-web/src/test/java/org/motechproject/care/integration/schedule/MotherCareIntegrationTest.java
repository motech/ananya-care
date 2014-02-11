package org.motechproject.care.integration.schedule;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.vaccinations.ExpirySchedule;
import org.motechproject.care.service.MotherService;
import org.motechproject.care.service.VaccinationProcessor;
import org.motechproject.care.service.builder.MotherBuilder;
import org.motechproject.care.service.schedule.MotherCareService;
import org.motechproject.care.service.schedule.VaccinationService;
import org.motechproject.care.service.util.PeriodUtil;
import org.motechproject.care.utils.CaseUtils;
import org.motechproject.care.utils.SpringIntegrationTest;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.commons.date.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class MotherCareIntegrationTest extends SpringIntegrationTest {

    @Autowired
    private MotherCareService motherCareService;
    @Autowired
    private ScheduleTrackingService scheduleTrackingService;
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
        List<VaccinationService> ttServices = Arrays.asList((VaccinationService) motherCareService);
        VaccinationProcessor motherVaccinationProcessor = new VaccinationProcessor(ttServices);
        motherService = new MotherService(allMothers, motherVaccinationProcessor);
    }

    @Test
    public void shouldVerifyMotherCareScheduleCreationWhenMotherIsRegistered() {
        String motherCareScheduleName = ExpirySchedule.MotherCare.getName();
        DateTime edd = DateUtil.newDateTime(DateUtil.today()).plusMonths(4);

        Mother mother=new MotherBuilder().withCaseId(caseId).withEdd(edd).build();
        motherService.process(mother);
        markScheduleForUnEnrollment(caseId, motherCareScheduleName);
        EnrollmentsQuery query = new EnrollmentsQuery()
                .havingExternalId(caseId)
                .havingState(EnrollmentStatus.ACTIVE)
                .havingSchedule(motherCareScheduleName);

        EnrollmentRecord enrollment = scheduleTrackingService.searchWithWindowDates(query).get(0);

        assertEquals(MilestoneType.MotherCare.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(edd.minusDays(PeriodUtil.DAYS_IN_9_MONTHS), enrollment.getReferenceDateTime().withTimeAtStartOfDay());
        assertEquals(edd.minusDays(PeriodUtil.DAYS_IN_9_MONTHS), enrollment.getStartOfDueWindow().withTimeAtStartOfDay());
        assertEquals(edd.plusWeeks(2).plusDays(1), enrollment.getStartOfLateWindow().withTimeAtStartOfDay());
    }
}
