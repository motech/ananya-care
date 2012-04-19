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
import org.motechproject.care.schedule.service.MeaslesSchedulerService;
import org.motechproject.care.schedule.vaccinations.ChildVaccinationSchedule;
import org.motechproject.care.service.ChildService;
import org.motechproject.care.service.ChildVaccinationProcessor;
import org.motechproject.care.service.builder.ChildCareCaseBuilder;
import org.motechproject.care.service.schedule.MeaslesService;
import org.motechproject.care.service.schedule.VaccinationService;
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

public class MeaslesIntegrationTest extends SpringIntegrationTest {

    @Autowired
    private MeaslesService measlesService;
    @Autowired
    private ScheduleTrackingService scheduleTrackingService;
    @Autowired
    private AllChildren allChildren;
    @Autowired
    private AllMothers allMothers;

    private final String caseId = CaseUtils.getUniqueCaseId();
    private ChildService childService;

    @Before
    public void setUp(){
        List<VaccinationService> vaccinationServices = Arrays.asList((VaccinationService) measlesService);
        ChildVaccinationProcessor childVaccinationProcessor = new ChildVaccinationProcessor(vaccinationServices);
        childService = new ChildService(allChildren, childVaccinationProcessor, allMothers);
    }

    @After
    public void tearDown() {
        allChildren.removeAll();
        allMothers.removeAll();
    }

    @Test
    public void shouldVerifyMeaslesScheduleCreationWhenChildIsRegistered() {
        String measlesScheduleName = ChildVaccinationSchedule.Measles.getName();
        DateTime add = DateUtil.newDateTime(DateUtil.today().minusMonths(4));

        String motherCaseId = "motherCaseId";
        Mother mother = new Mother(motherCaseId);
        mother.setAdd(add);
        allMothers.add(mother);

        CareCase careCase=new ChildCareCaseBuilder().withCaseId(caseId).withBabyMeaslesDate(null).withMotherCaseId(motherCaseId).build();
        childService.process(careCase);

        markScheduleForUnEnrollment(caseId,  measlesScheduleName);

        EnrollmentRecord enrollment = scheduleTrackingService.getEnrollment(caseId,  measlesScheduleName);
        assertEquals(MeaslesSchedulerService.milestone, enrollment.getCurrentMilestoneName());
        assertEquals(add, enrollment.getReferenceDateTime());

        Child child = allChildren.findByCaseId(caseId);
        assertEquals(add , child.getDOB());
        assertNull(child.getMeaslesDate());
    }

    @Test
    public void shouldVerifyMeaslesScheduleFulfillmentWhenChildHasTakenMeasles() {
        String measlesScheduleName = ChildVaccinationSchedule.Measles.getName();
        DateTime add = DateUtil.newDateTime(DateUtil.today().minusMonths(4));
        DateTime measlesTaken = DateUtil.newDateTime(DateUtil.today().plusMonths(1));
        String motherCaseId = "motherCaseId";

        Mother mother = new Mother(motherCaseId);
        mother.setAdd(add);
        allMothers.add(mother);

        CareCase careCase=new ChildCareCaseBuilder().withCaseId(caseId).withBabyMeaslesDate(null).withMotherCaseId(motherCaseId).build();
        childService.process(careCase);
        careCase=new ChildCareCaseBuilder().withCaseId(caseId).withBabyMeaslesDate( measlesTaken.toString()).withMotherCaseId(motherCaseId).build();
        childService.process(careCase);

        markScheduleForUnEnrollment(caseId, measlesScheduleName);

        assertNull(scheduleTrackingService.getEnrollment(caseId, measlesScheduleName));

        Child child = allChildren.findByCaseId(caseId);
        assertEquals(add , child.getDOB());
        assertEquals(measlesTaken, child.getMeaslesDate());
    }
}
