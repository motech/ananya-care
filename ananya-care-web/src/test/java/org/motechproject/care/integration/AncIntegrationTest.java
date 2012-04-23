package org.motechproject.care.integration;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.request.CareCase;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.vaccinations.MotherVaccinationSchedule;
import org.motechproject.care.service.MotherService;
import org.motechproject.care.service.MotherVaccinationProcessor;
import org.motechproject.care.service.builder.MotherCareCaseBuilder;
import org.motechproject.care.service.schedule.AncService;
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


public class AncIntegrationTest extends SpringIntegrationTest {

    @Autowired
    private AncService ancService;
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
        List<VaccinationService> ancServices = Arrays.asList((VaccinationService) ancService);
        MotherVaccinationProcessor motherVaccinationProcessor = new MotherVaccinationProcessor(ancServices);
        motherService = new MotherService(allMothers, motherVaccinationProcessor);
    }
    
    @Test
    public void shouldVerifyAncScheduleCreationWhenMotherIsRegistered() {
        String ttScheduleName = MotherVaccinationSchedule.Anc.getName();
        LocalDate edd = DateUtil.today().plusMonths(4);

        CareCase careCase=new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withANC1(null).withANC2(null).withANC3(null).build();
        motherService.process(careCase);
        markScheduleForUnEnrollment(caseId, ttScheduleName);
        EnrollmentRecord enrollment = getEnrollmentRecord(ttScheduleName, caseId, EnrollmentStatus.ACTIVE);

        assertEquals(MilestoneType.Anc1.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(DateUtil.newDateTime(edd.minusMonths(9)), enrollment.getReferenceDateTime());
        assertEquals(DateUtil.newDateTime(edd.minusMonths(9)), enrollment.getStartOfDueWindow());
        assertEquals(DateUtil.newDateTime(edd), enrollment.getStartOfLateWindow());
    }

    @Test
    public void shouldVerifyAnc1ScheduleFulfillmentWhenAnc1VisitIsOver() {
        String ancScheduleName = MotherVaccinationSchedule.Anc.getName();
        LocalDate edd = DateUtil.today().plusMonths(4);
        LocalDate anc1Date = DateUtil.today().plusMonths(1);

        CareCase careCase=new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withANC1(null).withANC2(null).withANC3(null).build();
        motherService.process(careCase);
        careCase = new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withANC1(anc1Date.toString()).withANC2(null).withANC3(null).build();
        motherService.process(careCase);

        markScheduleForUnEnrollment(caseId, ancScheduleName);
        EnrollmentRecord enrollment = getEnrollmentRecord(ancScheduleName, caseId, EnrollmentStatus.ACTIVE);

        assertEquals(MilestoneType.Anc2.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(DateUtil.newDateTime(anc1Date.plusDays(30)), enrollment.getStartOfDueWindow());
        assertEquals(DateUtil.newDateTime(anc1Date.plusMonths(9)), enrollment.getStartOfLateWindow());

    }

    @Test
    public void shouldVerifyAnc2ScheduleFulfillmentWhenAnc2VisitHasHappened() {
        String ancScheduleName = MotherVaccinationSchedule.Anc.getName();
        LocalDate edd = DateUtil.today().plusMonths(4);
        LocalDate anc1FulfillmentDate = DateUtil.today().plusMonths(1);
        LocalDate anc2FulfillmentDate = DateUtil.today().plusMonths(3);

        CareCase careCase=new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withANC1(null).withANC2(null).withANC3(null).build();
        motherService.process(careCase);
        careCase=new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withANC1(anc1FulfillmentDate.toString()).withANC2(null).withANC3(null).build();
        motherService.process(careCase);
        careCase=new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withANC1((anc1FulfillmentDate.toString())).withANC2(anc2FulfillmentDate.toString()).withANC3(null).build();
        motherService.process(careCase);

        markScheduleForUnEnrollment(caseId, ancScheduleName);
        EnrollmentRecord enrollment = getEnrollmentRecord(ancScheduleName, caseId, EnrollmentStatus.ACTIVE);

        assertEquals(MilestoneType.Anc3.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(DateUtil.newDateTime(anc2FulfillmentDate.plusDays(30)), enrollment.getStartOfDueWindow());
        assertEquals(DateUtil.newDateTime(anc2FulfillmentDate.plusMonths(9)), enrollment.getStartOfLateWindow());
    }

    @Test
    public void shouldVerifyAnc3ScheduleFulfillmentWhenAnc3VisitHasHappened() {
        String ancScheduleName = MotherVaccinationSchedule.Anc.getName();
        LocalDate edd = DateUtil.today().plusMonths(4);
        LocalDate anc1FulfillmentDate = DateUtil.today().plusMonths(1);
        LocalDate anc2FulfillmentDate = DateUtil.today().plusMonths(3);
        LocalDate anc3FulfillmentDate = DateUtil.today().plusMonths(5);

        CareCase careCase=new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withANC1(null).withANC2(null).withANC3(null).build();
        motherService.process(careCase);
        careCase=new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withANC1(anc1FulfillmentDate.toString()).withANC2(null).withANC3(null).build();
        motherService.process(careCase);
        careCase=new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withANC1((anc1FulfillmentDate.toString())).withANC2(anc2FulfillmentDate.toString()).withANC3(null).build();
        motherService.process(careCase);
        careCase=new MotherCareCaseBuilder().withCaseId(caseId).withEdd(edd.toString()).withANC1((anc1FulfillmentDate.toString())).withANC2(anc2FulfillmentDate.toString()).withANC3(anc3FulfillmentDate.toString()).build();
        motherService.process(careCase);

        markScheduleForUnEnrollment(caseId, ancScheduleName);
        Assert.assertNull(scheduleTrackingService.getEnrollment(caseId, ancScheduleName));
    }


    private EnrollmentRecord getEnrollmentRecord(String ttScheduleName, String externalId, EnrollmentStatus status) {
        EnrollmentsQuery query = new EnrollmentsQuery()
                .havingExternalId(externalId)
                .havingState(status)
                .havingSchedule(ttScheduleName);

        return scheduleTrackingService.searchWithWindowDates(query).get(0);
    }
}
