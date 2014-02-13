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
import org.motechproject.care.service.schedule.OpvService;
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

public class OpvIntegrationTest extends SpringIntegrationTest {

    @Autowired
    private OpvService opvService;
    @Autowired
    private AllChildren allChildren;
    private String opvScheduleName = ChildVaccinationSchedule.OPV.getName();

    private String caseId;
    private ChildService childService;

    @Before
    public void setUp(){
        caseId = CaseUtils.getUniqueCaseId();
        List<VaccinationService> vaccinationServices = Arrays.asList((VaccinationService) opvService);
        VaccinationProcessor childVaccinationProcessor = new VaccinationProcessor(vaccinationServices);
        childService = new ChildService(allChildren, childVaccinationProcessor);
    }

    @After
    public void tearDown() {
        allChildren.removeAll();
    }

    @Test
    public void shouldVerifyOPVScheduleCreationWhenChildIsRegistered() {
        DateTime dob = DateUtil.newDateTime(DateUtil.today()).plusMonths(4);

        Child child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withOPV1Date(null).withOPV2Date(null).withOPV3Date(null).build();
        childService.process(child);
        markScheduleForUnEnrollment(caseId, opvScheduleName);
        EnrollmentRecord enrollment = getEnrollmentRecord(opvScheduleName, caseId, EnrollmentStatus.ACTIVE);

        assertEquals(MilestoneType.OPV1.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(dob, enrollment.getReferenceDateTime().withTimeAtStartOfDay());
        assertEquals(dob.plusWeeks(6), enrollment.getStartOfDueWindow().withTimeAtStartOfDay());
        assertEquals(dob.plusMonths(24), enrollment.getStartOfLateWindow().withTimeAtStartOfDay());
    }

    @Test
    public void shouldVerifyOPVScheduleFulfillmentWhenOPV1VaccinationIsTaken() {
        DateTime dob = DateUtil.newDateTime(DateUtil.today()).minusMonths(4);
        DateTime opv1Date = dob.plusMonths(2);

        Child child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withOPV1Date(null).withOPV2Date(null).withOPV3Date(null).build();
        childService.process(child);
        child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withOPV1Date(opv1Date).withOPV2Date(null).withOPV3Date(null).build();
        childService.process(child);

        markScheduleForUnEnrollment(caseId, opvScheduleName);
        EnrollmentRecord enrollment = getEnrollmentRecord(opvScheduleName, caseId, EnrollmentStatus.ACTIVE);

        assertEquals(MilestoneType.OPV2.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(opv1Date.plusWeeks(4), enrollment.getStartOfDueWindow().withTimeAtStartOfDay());
        assertEquals(opv1Date.plusMonths(24), enrollment.getStartOfLateWindow().withTimeAtStartOfDay());
    }

    @Test
    public void shouldVerifyOPVScheduleFulfillmentWhenOPV2VaccinationIsTaken() {
        DateTime dob = DateUtil.newDateTime(DateUtil.today()).minusMonths(6);
        DateTime opv1Date = dob.plusMonths(2);
        DateTime opv2Date = dob.plusMonths(3);

        Child child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withOPV1Date(null).withOPV2Date(null).withOPV3Date(null).build();
        childService.process(child);
        child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withOPV1Date(opv1Date).withOPV2Date(null).withOPV3Date(null).build();
        childService.process(child);
        child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withOPV1Date(opv1Date).withOPV2Date(opv2Date).withOPV3Date(null).build();
        childService.process(child);

        markScheduleForUnEnrollment(caseId, opvScheduleName);
        EnrollmentRecord enrollment = getEnrollmentRecord(opvScheduleName, caseId, EnrollmentStatus.ACTIVE);

        assertEquals(MilestoneType.OPV3.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(opv2Date.plusWeeks(4), enrollment.getStartOfDueWindow().withTimeAtStartOfDay());
        assertEquals(opv2Date.plusMonths(24), enrollment.getStartOfLateWindow().withTimeAtStartOfDay());
    }

    @Test
    public void shouldVerifyOPVScheduleFulfillmentWhenOPV3VaccinationIsTaken() {
        DateTime today = DateUtil.newDateTime(DateUtil.today());
        DateTime dob = today.minusMonths(6);
        DateTime opv1Date = dob.plusMonths(2);
        DateTime opv2Date = dob.plusMonths(3);
        DateTime opv3Date = today;

        Child child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withOPV1Date(null).withOPV2Date(null).withOPV3Date(null).build();
        childService.process(child);
        child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withOPV1Date(opv1Date).withOPV2Date(null).withOPV3Date(null).build();
        childService.process(child);
        child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withOPV1Date(opv1Date).withOPV2Date(opv2Date).withOPV3Date(null).build();
        childService.process(child);
        child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withOPV1Date(opv1Date).withOPV2Date(opv2Date).withOPV3Date(opv3Date).build();
        childService.process(child);

        Assert.assertNull(trackingService.getEnrollment(caseId, opvScheduleName));
    }
}
