package org.motechproject.care.integration.schedule;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.care.domain.Child;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.vaccinations.ChildVaccinationSchedule;
import org.motechproject.care.service.ChildService;
import org.motechproject.care.service.VaccinationProcessor;
import org.motechproject.care.service.builder.ChildBuilder;
import org.motechproject.care.service.schedule.HepService;
import org.motechproject.care.service.schedule.VaccinationService;
import org.motechproject.care.utils.CaseUtils;
import org.motechproject.care.utils.SpringIntegrationTest;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.commons.date.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class HepIntegrationTest extends SpringIntegrationTest {

    @Autowired
    private HepService hepService;
    @Autowired
    private AllChildren allChildren;
    private String hepScheduleName = ChildVaccinationSchedule.Hepatitis.getName();


    private String caseId;
    private ChildService childService;

    @Before
    public void setUp(){
        caseId = CaseUtils.getUniqueCaseId();
    List<VaccinationService> vaccinationServices = Arrays.asList((VaccinationService) hepService);
        VaccinationProcessor childVaccinationProcessor = new VaccinationProcessor(vaccinationServices);
        childService = new ChildService(allChildren, childVaccinationProcessor);
    }

    @After
    public void tearDown() {
        allChildren.removeAll();
    }

    @Test
    public void shouldVerifyHepScheduleCreationWhenChildIsRegistered() {
        DateTime dob = DateUtil.newDateTime(DateUtil.today()).plusMonths(4);

        Child child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withHep1Date(null).withHep2Date(null).withHep3Date(null).build();
        childService.process(child);
        markScheduleForUnEnrollment(caseId, hepScheduleName);
        EnrollmentRecord enrollment = getEnrollmentRecord(hepScheduleName, caseId, EnrollmentStatus.ACTIVE);

        assertEquals(MilestoneType.Hep1.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(dob, enrollment.getReferenceDateTime().withTimeAtStartOfDay());
        assertEquals(dob.plusWeeks(6), enrollment.getStartOfDueWindow().withTimeAtStartOfDay());
        assertEquals(dob.plusMonths(24), enrollment.getStartOfLateWindow().withTimeAtStartOfDay());
    }

    @Test
    public void shouldVerifyHep1ScheduleFulfillmentWhenHep1VaccinationIsTaken() {
        DateTime dob = DateUtil.newDateTime(DateUtil.today()).minusMonths(4);
        DateTime hep1Date = dob.plusMonths(2);

        Child child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withHep1Date(null).withHep2Date(null).withHep3Date(null).build();
        childService.process(child);
        child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withHep1Date(hep1Date).withHep2Date(null).withHep3Date(null).build();
        childService.process(child);

        markScheduleForUnEnrollment(caseId, hepScheduleName);
        EnrollmentRecord enrollment = getEnrollmentRecord(hepScheduleName, caseId, EnrollmentStatus.ACTIVE);

        assertEquals(MilestoneType.Hep2.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(hep1Date.plusWeeks(4), enrollment.getStartOfDueWindow().withTimeAtStartOfDay());
        assertEquals(hep1Date.plusMonths(24), enrollment.getStartOfLateWindow().withTimeAtStartOfDay());
    }

    @Test
    public void shouldVerifyHep2ScheduleFulfillmentWhenHep2VaccinationIsTaken() {
        DateTime dob = DateUtil.newDateTime(DateUtil.today()).minusMonths(6);
        DateTime hep1Date = dob.plusMonths(2);
        DateTime hep2Date = dob.plusMonths(3);

        Child child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withHep1Date(null).withHep2Date(null).withHep3Date(null).build();
        childService.process(child);
        child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withHep1Date(hep1Date).withHep2Date(null).withHep3Date(null).build();
        childService.process(child);
        child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withHep1Date(hep1Date).withHep2Date(hep2Date).withHep3Date(null).build();
        childService.process(child);

        markScheduleForUnEnrollment(caseId, hepScheduleName);
        EnrollmentRecord enrollment = getEnrollmentRecord(hepScheduleName, caseId, EnrollmentStatus.ACTIVE);

        assertEquals(MilestoneType.Hep3.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(hep2Date.plusWeeks(4), enrollment.getStartOfDueWindow().withTimeAtStartOfDay());
        assertEquals(hep2Date.plusMonths(24), enrollment.getStartOfLateWindow().withTimeAtStartOfDay().withTimeAtStartOfDay());
    }

    @Test
    public void shouldVerifyHep3ScheduleFulfillmentWhenHep3VaccinationIsTaken() {
        DateTime today = DateUtil.newDateTime(DateUtil.today());
        DateTime dob = today.minusMonths(6);
        DateTime hep1Date = dob.plusMonths(2);
        DateTime hep2Date = dob.plusMonths(3);
        DateTime hep3Date = today;

        Child child = new ChildBuilder().withCaseId(caseId).withDOB(dob).withHep1Date(hep1Date).withHep2Date(hep2Date).withHep3Date(hep3Date).build();
        childService.process(child);

        Assert.assertNull(trackingService.getEnrollment(caseId, hepScheduleName));
    }
}
