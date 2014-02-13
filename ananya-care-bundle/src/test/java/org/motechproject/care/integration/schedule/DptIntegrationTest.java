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
import org.motechproject.care.service.schedule.DptService;
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

public class DptIntegrationTest extends SpringIntegrationTest {

    @Autowired
    private DptService dptService;
    @Autowired
    private AllChildren allChildren;
    private String dptScheduleName = ChildVaccinationSchedule.DPT.getName();
    private String caseId;
    private ChildService childService;

    @Before
    public void setUp() {
        caseId = CaseUtils.getUniqueCaseId();
        List<VaccinationService> vaccinationServices = Arrays.asList((VaccinationService) dptService);
        VaccinationProcessor childVaccinationProcessor = new VaccinationProcessor(vaccinationServices);
        childService = new ChildService(allChildren, childVaccinationProcessor);
    }

    @After
    public void tearDown() {
        allChildren.removeAll();
    }

    @Test
    public void shouldVerifyDptScheduleCreationWhenChildIsRegistered() {
        DateTime dob = DateUtil.newDateTime(DateUtil.today()).plusMonths(4);

        Child child = new ChildBuilder().withCaseId(caseId).withDOB(dob).withDpt1Date(null).withDpt2Date(null).build();
        childService.process(child);
        markScheduleForUnEnrollment(caseId, dptScheduleName);
        EnrollmentRecord enrollment = getEnrollmentRecord(dptScheduleName, caseId, EnrollmentStatus.ACTIVE);

        assertEquals(MilestoneType.DPT1.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(dob, enrollment.getReferenceDateTime().withTimeAtStartOfDay());
        assertEquals(dob.plusWeeks(6), enrollment.getStartOfDueWindow().withTimeAtStartOfDay());
        assertEquals(dob.plusMonths(24), enrollment.getStartOfLateWindow().withTimeAtStartOfDay());
    }

    @Test
    public void shouldVerifyDpt1ScheduleFulfillmentWhenDpt1VaccineIsOver() {
        DateTime dob = DateUtil.newDateTime(DateUtil.today()).minusMonths(4);
        DateTime dpt1Date = dob.plusMonths(2);

        Child child = new ChildBuilder().withCaseId(caseId).withDOB(dob).withDpt1Date(dpt1Date).withDpt2Date(null).build();
        childService.process(child);

        markScheduleForUnEnrollment(caseId, dptScheduleName);
        EnrollmentRecord enrollment = getEnrollmentRecord(dptScheduleName, caseId, EnrollmentStatus.ACTIVE);

        assertEquals(MilestoneType.DPT2.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(dpt1Date.plusWeeks(4), enrollment.getStartOfDueWindow().withTimeAtStartOfDay());
        assertEquals(dpt1Date.plusMonths(24), enrollment.getStartOfLateWindow().withTimeAtStartOfDay());
    }

    @Test
    public void shouldVerifyDpt2ScheduleFulfillmentWhenDpt2VaccineIsOver() {
        DateTime dob = DateUtil.newDateTime(DateUtil.today()).minusMonths(6);
        DateTime dpt1Date = dob.plusMonths(2);
        DateTime dpt2Date = dob.plusMonths(3);

        Child child = new ChildBuilder().withCaseId(caseId).withDOB(dob).withDpt1Date(null).withDpt2Date(null).withDpt3Date(null).build();
        childService.process(child);
        child = new ChildBuilder().withCaseId(caseId).withDOB(dob).withDpt1Date(dpt1Date).withDpt2Date(null).withDpt3Date(null).build();
        childService.process(child);
        child = new ChildBuilder().withCaseId(caseId).withDOB(dob).withDpt1Date(dpt1Date).withDpt2Date(dpt2Date).withDpt3Date(null).build();
        childService.process(child);

        markScheduleForUnEnrollment(caseId, dptScheduleName);
        EnrollmentRecord enrollment = getEnrollmentRecord(dptScheduleName, caseId, EnrollmentStatus.ACTIVE);

        assertEquals(MilestoneType.DPT3.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(dpt2Date.plusWeeks(4), enrollment.getStartOfDueWindow().withTimeAtStartOfDay());
        assertEquals(dpt2Date.plusMonths(24), enrollment.getStartOfLateWindow().withTimeAtStartOfDay());
    }

    @Test
    public void shouldVerifyDpt3ScheduleFulfillmentWhenDpt3VaccineIsOver() {
        DateTime today = DateUtil.newDateTime(DateUtil.today());
        DateTime dob = today.minusMonths(6);
        DateTime dpt1Date = dob.plusMonths(2);
        DateTime dpt2Date = dob.plusMonths(3);
        DateTime dpt3Date = today;

        Child child = new ChildBuilder().withCaseId(caseId).withDOB(dob)
                .withDpt1Date(dpt1Date).withDpt2Date(dpt2Date).withDpt3Date(dpt3Date).withDptBoosterDate(null).build();
        childService.process(child);

        Assert.assertNull(trackingService.getEnrollment(caseId, dptScheduleName));
    }
}
