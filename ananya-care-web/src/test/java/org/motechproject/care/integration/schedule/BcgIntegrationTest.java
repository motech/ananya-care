package org.motechproject.care.integration.schedule;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.care.domain.Child;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.request.CareCase;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.vaccinations.ChildVaccinationSchedule;
import org.motechproject.care.service.ChildService;
import org.motechproject.care.service.ChildVaccinationProcessor;
import org.motechproject.care.service.builder.ChildCareCaseBuilder;
import org.motechproject.care.service.schedule.BcgService;
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
import static org.junit.Assert.assertNull;

public class BcgIntegrationTest extends SpringIntegrationTest {

    @Autowired
    private BcgService bcgService;
    @Autowired
    private ScheduleTrackingService scheduleTrackingService;
    @Autowired
    private AllChildren allChildren;

    private final String caseId = CaseUtils.getUniqueCaseId();
    private ChildService childService;

    @Before
    public void setUp(){
        List<VaccinationService> vaccinationServices = Arrays.asList((VaccinationService) bcgService);
        ChildVaccinationProcessor childVaccinationProcessor = new ChildVaccinationProcessor(vaccinationServices);
        childService = new ChildService(allChildren, childVaccinationProcessor);
    }

    @After
    public void tearDown() {
        allChildren.removeAll();
    }

    @Test
    public void shouldVerifyBcgScheduleCreationWhenChildIsRegistered() {
        String bcgScheduleName = ChildVaccinationSchedule.Bcg.getName();
        DateTime dob = DateUtil.newDateTime(DateUtil.today().minusMonths(4));

        String motherCaseId = "motherCaseId";
        CareCase careCase = new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withBcgDate(null).withMotherCaseId(motherCaseId).build();
        childService.process(careCase);

        markScheduleForUnEnrollment(caseId, bcgScheduleName);
        EnrollmentsQuery query = new EnrollmentsQuery()
                .havingExternalId(caseId)
                .havingState(EnrollmentStatus.ACTIVE)
                .havingSchedule(bcgScheduleName);

        EnrollmentRecord enrollment = scheduleTrackingService.searchWithWindowDates(query).get(0);

        assertEquals(MilestoneType.Bcg.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(dob, enrollment.getReferenceDateTime());
        assertEquals(dob, enrollment.getStartOfDueWindow());
        assertEquals(dob.plusMonths(12), enrollment.getStartOfLateWindow());

        Child child = allChildren.findByCaseId(caseId);
        assertEquals(dob , child.getDOB());
        assertNull(child.getBcgDate());
    }

    @Test
    public void shouldVerifyBcgScheduleFulfillmentWhenChildHasTakenBcg() {
        String bcgScheduleName = ChildVaccinationSchedule.Bcg.getName();
        DateTime dob = DateUtil.newDateTime(DateUtil.today().minusMonths(4));
        DateTime bcgTaken = DateUtil.newDateTime(DateUtil.today().plusMonths(1));
        String motherCaseId = "motherCaseId";

        CareCase careCase = new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withBcgDate(null).withMotherCaseId(motherCaseId).build();
        childService.process(careCase);
        careCase=new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withBcgDate( bcgTaken.toString()).withMotherCaseId(motherCaseId).build();
        childService.process(careCase);

        assertNull(scheduleTrackingService.getEnrollment(caseId, bcgScheduleName));

        Child child = allChildren.findByCaseId(caseId);
        assertEquals(dob, child.getDOB());
        assertEquals(bcgTaken, child.getBcgDate());
    }

}
