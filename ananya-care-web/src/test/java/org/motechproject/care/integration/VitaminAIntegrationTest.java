package org.motechproject.care.integration;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.care.domain.Child;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.request.CareCase;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.vaccinations.ChildVaccinationSchedule;
import org.motechproject.care.service.ChildService;
import org.motechproject.care.service.ChildVaccinationProcessor;
import org.motechproject.care.service.builder.ChildCareCaseBuilder;
import org.motechproject.care.service.schedule.VaccinationService;
import org.motechproject.care.service.schedule.VitaService;
import org.motechproject.care.utils.CaseUtils;
import org.motechproject.care.utils.SpringIntegrationTest;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class VitaminAIntegrationTest extends SpringIntegrationTest {

    @Autowired
    private VitaService vitaService;
    @Autowired
    private ScheduleTrackingService scheduleTrackingService;
    @Autowired
    private AllChildren allChildren;
    @Autowired
    private AllMothers allMothers;

    private final String caseId = CaseUtils.getUniqueCaseId();
    private ChildService childService;

    @After
    public void tearDown() {
        allChildren.removeAll();
        allMothers.removeAll();
    }

    @Before
    public void setUp(){
        List<VaccinationService> vaccinationServices = Arrays.asList((VaccinationService) vitaService);
        ChildVaccinationProcessor childVaccinationProcessor = new ChildVaccinationProcessor(vaccinationServices);
        childService = new ChildService(allChildren, childVaccinationProcessor, allMothers);
    }
    
    @Test
    public void shouldVerifyVitaScheduleCreationWhenChildIsRegistered() {
        String vitaScheduleName = ChildVaccinationSchedule.Vita.getName();
        DateTime add = DateUtil.newDateTime(DateUtil.today().minusMonths(4));

        String motherCaseId = "motherCaseId";
        Mother mother = new Mother(motherCaseId);
        mother.setAdd(add);
        allMothers.add(mother);

        CareCase careCase=new ChildCareCaseBuilder().withCaseId(caseId).withVitamin1Date(null).withMotherCaseId(motherCaseId).build();
        childService.process(careCase);

        markScheduleForUnEnrollment(caseId,vitaScheduleName);

        EnrollmentRecord enrollment = scheduleTrackingService.getEnrollment(caseId,vitaScheduleName);
        assertEquals(MilestoneType.VitaminA.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(add, enrollment.getReferenceDateTime());

        Child child = allChildren.findByCaseId(caseId);
        assertEquals(add , child.getDOB());
        assertNull(child.getVitamin1Date());
    }

    @Test
    public void shouldVerifyVitaScheduleFulfillmentWhenChildHasTakenVita() {
        String vitaScheduleName = ChildVaccinationSchedule.Vita.getName();
        DateTime add = DateUtil.newDateTime(DateUtil.today().minusMonths(4));
        DateTime vitaTaken = DateUtil.newDateTime(DateUtil.today().plusMonths(1));
        String motherCaseId = "motherCaseId";

        Mother mother = new Mother(motherCaseId);
        mother.setAdd(add);
        allMothers.add(mother);

        CareCase careCase=new ChildCareCaseBuilder().withCaseId(caseId).withVitamin1Date(null).withMotherCaseId(motherCaseId).build();
        childService.process(careCase);
        careCase=new ChildCareCaseBuilder().withCaseId(caseId).withVitamin1Date(vitaTaken.toString()).withMotherCaseId(motherCaseId).build();
        childService.process(careCase);

        markScheduleForUnEnrollment(caseId, vitaScheduleName);

        assertNull(scheduleTrackingService.getEnrollment(caseId, vitaScheduleName));

        Child child = allChildren.findByCaseId(caseId);
        assertEquals(add , child.getDOB());
        assertEquals(vitaTaken, child.getVitamin1Date());
    }
}