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
import org.motechproject.care.service.schedule.OpvService;
import org.motechproject.care.service.schedule.VaccinationService;
import org.motechproject.care.utils.CaseUtils;
import org.motechproject.care.utils.SpringIntegrationTest;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.util.DateUtil;
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
        ChildVaccinationProcessor childVaccinationProcessor = new ChildVaccinationProcessor(vaccinationServices);
        childService = new ChildService(allChildren, childVaccinationProcessor);
    }

    @After
    public void tearDown() {
        allChildren.removeAll();
    }

    @Test
    public void shouldVerifyOPVScheduleCreationWhenChildIsRegistered() {
        LocalDate dob = DateUtil.today().plusMonths(4);

        CareCase careCase=new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withOPV1Date(null).withOPV2Date(null).withOPV3Date(null).build();
        childService.process(careCase);
        markScheduleForUnEnrollment(caseId, opvScheduleName);
        EnrollmentRecord enrollment = getEnrollmentRecord(opvScheduleName, caseId, EnrollmentStatus.ACTIVE);

        assertEquals(MilestoneType.OPV1.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(DateUtil.newDateTime(dob), enrollment.getReferenceDateTime().withTimeAtStartOfDay());
        assertEquals(DateUtil.newDateTime(dob.plusWeeks(6)), enrollment.getStartOfDueWindow().withTimeAtStartOfDay());
        assertEquals(DateUtil.newDateTime(dob.plusMonths(24)), enrollment.getStartOfLateWindow().withTimeAtStartOfDay());
    }

    @Test
    public void shouldVerifyOPVScheduleFulfillmentWhenOPV1VaccinationIsTaken() {
        LocalDate dob = DateUtil.today().minusMonths(4);
        LocalDate opv1Date = dob.plusMonths(2);

        CareCase careCase=new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withOPV1Date(null).withOPV2Date(null).withOPV3Date(null).build();
        childService.process(careCase);
        careCase=new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withOPV1Date(opv1Date.toString()).withOPV2Date(null).withOPV3Date(null).build();
        childService.process(careCase);

        markScheduleForUnEnrollment(caseId, opvScheduleName);
        EnrollmentRecord enrollment = getEnrollmentRecord(opvScheduleName, caseId, EnrollmentStatus.ACTIVE);

        assertEquals(MilestoneType.OPV2.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(DateUtil.newDateTime(opv1Date.plusWeeks(4)), enrollment.getStartOfDueWindow().withTimeAtStartOfDay());
        assertEquals(DateUtil.newDateTime(opv1Date.plusMonths(24)), enrollment.getStartOfLateWindow().withTimeAtStartOfDay());
    }

    @Test
    public void shouldVerifyOPVScheduleFulfillmentWhenOPV2VaccinationIsTaken() {
        LocalDate dob = DateUtil.today().minusMonths(6);
        LocalDate opv1Date = dob.plusMonths(2);
        LocalDate opv2Date = dob.plusMonths(3);

        CareCase careCase=new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withOPV1Date(null).withOPV2Date(null).withOPV3Date(null).build();
        childService.process(careCase);
        careCase=new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withOPV1Date(opv1Date.toString()).withOPV2Date(null).withOPV3Date(null).build();
        childService.process(careCase);
        careCase=new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withOPV1Date(opv1Date.toString()).withOPV2Date(opv2Date.toString()).withOPV3Date(null).build();
        childService.process(careCase);

        markScheduleForUnEnrollment(caseId, opvScheduleName);
        EnrollmentRecord enrollment = getEnrollmentRecord(opvScheduleName, caseId, EnrollmentStatus.ACTIVE);

        assertEquals(MilestoneType.OPV3.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(DateUtil.newDateTime(opv2Date.plusWeeks(4)), enrollment.getStartOfDueWindow().withTimeAtStartOfDay());
        assertEquals(DateUtil.newDateTime(opv2Date.plusMonths(24)), enrollment.getStartOfLateWindow().withTimeAtStartOfDay());
    }

    @Test
    public void shouldVerifyOPVScheduleFulfillmentWhenOPV3VaccinationIsTaken() {
        LocalDate today = DateUtil.today();
        LocalDate dob = today.minusMonths(6);
        LocalDate opv1Date = dob.plusMonths(2);
        LocalDate opv2Date = dob.plusMonths(3);
        LocalDate opv3Date = today;

        CareCase careCase=new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withOPV1Date(null).withOPV2Date(null).withOPV3Date(null).build();
        childService.process(careCase);
        careCase=new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withOPV1Date(opv1Date.toString()).withOPV2Date(null).withOPV3Date(null).build();
        childService.process(careCase);
        careCase=new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withOPV1Date(opv1Date.toString()).withOPV2Date(opv2Date.toString()).withOPV3Date(null).build();
        childService.process(careCase);
        careCase=new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withOPV1Date(opv1Date.toString()).withOPV2Date(opv2Date.toString()).withOPV3Date(opv3Date.toString()).build();
        childService.process(careCase);

        Assert.assertNull(trackingService.getEnrollment(caseId, opvScheduleName));
    }
}
