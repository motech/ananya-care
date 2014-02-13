package org.motechproject.care.integration.schedule;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.care.domain.Child;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.vaccinations.ChildVaccinationSchedule;
import org.motechproject.care.service.ChildService;
import org.motechproject.care.service.VaccinationProcessor;
import org.motechproject.care.service.builder.ChildBuilder;
import org.motechproject.care.service.schedule.VaccinationService;
import org.motechproject.care.service.schedule.VitaService;
import org.motechproject.care.utils.CaseUtils;
import org.motechproject.care.utils.SpringIntegrationTest;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.commons.date.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class VitaminAIntegrationTest extends SpringIntegrationTest {

    @Autowired
    private VitaService vitaService;
    @Autowired
    private AllChildren allChildren;
    @Autowired
    private AllMothers allMothers;

    private String caseId;
    private ChildService childService;

    @After
    public void tearDown() {
        allChildren.removeAll();
        allMothers.removeAll();
    }

    @Before
    public void setUp(){
        caseId = CaseUtils.getUniqueCaseId();
        List<VaccinationService> vaccinationServices = Arrays.asList((VaccinationService) vitaService);
        VaccinationProcessor childVaccinationProcessor = new VaccinationProcessor(vaccinationServices);
        childService = new ChildService(allChildren, childVaccinationProcessor);
    }
    
    @Test
    public void shouldVerifyVitaScheduleCreationWhenChildIsRegistered() {
        String vitaScheduleName = ChildVaccinationSchedule.Vita.getName();
        DateTime dob = DateUtil.newDateTime(DateUtil.today().minusMonths(4));

        String motherCaseId = "motherCaseId";
        Child child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withVitamin1Date(null).withMotherCaseId(motherCaseId).build();
        childService.process(child);

        markScheduleForUnEnrollment(caseId,vitaScheduleName);
        EnrollmentsQuery query = new EnrollmentsQuery()
                .havingExternalId(caseId)
                .havingState(EnrollmentStatus.ACTIVE)
                .havingSchedule(vitaScheduleName);

        EnrollmentRecord enrollment = trackingService.searchWithWindowDates(query).get(0);

        assertEquals(MilestoneType.VitaminA.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(dob, enrollment.getReferenceDateTime().withTimeAtStartOfDay());
        assertEquals(dob.plusMonths(9), enrollment.getStartOfDueWindow().withTimeAtStartOfDay());
        assertEquals(dob.plusMonths(24), enrollment.getStartOfLateWindow().withTimeAtStartOfDay());

        Child childFromDb = allChildren.findByCaseId(caseId);
        assertEquals(dob, childFromDb.getDOB());
        assertNull(childFromDb.getVitamin1Date());
    }

    @Test
    public void shouldVerifyVitaScheduleFulfillmentWhenChildHasTakenVita() {
        String vitaScheduleName = ChildVaccinationSchedule.Vita.getName();
        DateTime dob = DateUtil.newDateTime(DateUtil.today().minusMonths(4));
        DateTime vitaTaken = DateUtil.newDateTime(DateUtil.today().plusMonths(1));
        String motherCaseId = "motherCaseId";

        Child child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withVitamin1Date(null).withMotherCaseId(motherCaseId).build();
        childService.process(child);
        child=new ChildBuilder().withCaseId(caseId).withDOB(dob).withVitamin1Date(vitaTaken).withMotherCaseId(motherCaseId).build();
        childService.process(child);

        assertNull(trackingService.getEnrollment(caseId, vitaScheduleName));

        Child childFromDb = allChildren.findByCaseId(caseId);
        assertEquals(dob, childFromDb.getDOB());
        assertEquals(vitaTaken, childFromDb.getVitamin1Date());
    }
}