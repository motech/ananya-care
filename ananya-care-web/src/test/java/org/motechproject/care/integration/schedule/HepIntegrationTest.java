package org.motechproject.care.integration.schedule;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.request.CareCase;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.vaccinations.ChildVaccinationSchedule;
import org.motechproject.care.service.ChildService;
import org.motechproject.care.service.ChildVaccinationProcessor;
import org.motechproject.care.service.builder.ChildCareCaseBuilder;
import org.motechproject.care.service.schedule.HepService;
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

public class HepIntegrationTest extends SpringIntegrationTest {

    @Autowired
    private HepService hepService;
    @Autowired
    private ScheduleTrackingService scheduleTrackingService;
    @Autowired
    private AllChildren allChildren;
    private String hepScheduleName = ChildVaccinationSchedule.Hepatitis.getName();

    private final String caseId = CaseUtils.getUniqueCaseId();
    private ChildService childService;

    @Before
    public void setUp(){
        List<VaccinationService> vaccinationServices = Arrays.asList((VaccinationService) hepService);
        ChildVaccinationProcessor childVaccinationProcessor = new ChildVaccinationProcessor(vaccinationServices);
        childService = new ChildService(allChildren, childVaccinationProcessor);
    }

    @After
    public void tearDown() {
        allChildren.removeAll();
    }

    @Test
    public void shouldVerifyHepScheduleCreationWhenChildIsRegistered() {
        LocalDate dob = DateUtil.today().plusMonths(4);

        CareCase careCase=new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withHep1Date(null).withHep2Date(null).build();
        childService.process(careCase);
        markScheduleForUnEnrollment(caseId, hepScheduleName);
        EnrollmentRecord enrollment = getEnrollmentRecord(hepScheduleName, caseId, EnrollmentStatus.ACTIVE);

        assertEquals(MilestoneType.Hep1.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(DateUtil.newDateTime(dob), enrollment.getReferenceDateTime());
        assertEquals(DateUtil.newDateTime(dob.plusWeeks(6)), enrollment.getStartOfDueWindow());
        assertEquals(DateUtil.newDateTime(dob.plusMonths(24)), enrollment.getStartOfLateWindow());
    }

    @Test
    public void shouldVerifyHep1ScheduleFulfillmentWhenHep1VisitIsOver() {
        LocalDate dob = DateUtil.today().minusMonths(4);
        LocalDate hep1Date = dob.plusMonths(2);

        CareCase careCase=new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withHep1Date(null).withHep2Date(null).build();
        childService.process(careCase);
        careCase=new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withHep1Date(hep1Date.toString()).withHep2Date(null).build();
        childService.process(careCase);

        markScheduleForUnEnrollment(caseId, hepScheduleName);
        EnrollmentRecord enrollment = getEnrollmentRecord(hepScheduleName, caseId, EnrollmentStatus.ACTIVE);

        assertEquals(MilestoneType.Hep2.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(DateUtil.newDateTime(hep1Date.plusWeeks(4)), enrollment.getStartOfDueWindow());
        assertEquals(DateUtil.newDateTime(hep1Date.plusMonths(24)), enrollment.getStartOfLateWindow());
    }

    @Test
    public void shouldVerifyHep2ScheduleFulfillmentWhenHep2VisitIsOver() {
        LocalDate dob = DateUtil.today().minusMonths(6);
        LocalDate hep1Date = dob.plusMonths(2);
        LocalDate hep2Date = dob.plusMonths(3);

        CareCase careCase=new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withHep1Date(hep1Date.toString()).withHep2Date(hep2Date.toString()).withHep3Date(null).build();
        childService.process(careCase);

        markScheduleForUnEnrollment(caseId, hepScheduleName);
        EnrollmentRecord enrollment = getEnrollmentRecord(hepScheduleName, caseId, EnrollmentStatus.ACTIVE);

        assertEquals(MilestoneType.Hep3.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(DateUtil.newDateTime(hep2Date.plusWeeks(4)), enrollment.getStartOfDueWindow());
        assertEquals(DateUtil.newDateTime(hep2Date.plusMonths(24)), enrollment.getStartOfLateWindow());
    }

    @Test
    public void shouldVerifyHep3ScheduleFulfillmentWhenHep3VisitIsOver() {
        LocalDate today = DateUtil.today();
        LocalDate dob = today.minusMonths(6);
        LocalDate hep1Date = dob.plusMonths(2);
        LocalDate hep2Date = dob.plusMonths(3);
        LocalDate hep3Date = today;

        CareCase careCase=new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString())
                .withHep1Date(hep1Date.toString()).withHep2Date(hep2Date.toString()).withHep3Date(hep3Date.toString()).build();
        childService.process(careCase);

        Assert.assertNull(scheduleTrackingService.getEnrollment(caseId, hepScheduleName));
    }

    private EnrollmentRecord getEnrollmentRecord(String scheduleName, String externalId, EnrollmentStatus status) {
        EnrollmentsQuery query = new EnrollmentsQuery()
                .havingExternalId(externalId)
                .havingState(status)
                .havingSchedule(scheduleName);

        return scheduleTrackingService.searchWithWindowDates(query).get(0);
    }
}
