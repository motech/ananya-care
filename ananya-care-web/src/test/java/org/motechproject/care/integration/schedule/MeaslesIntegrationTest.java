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
import org.motechproject.care.service.schedule.MeaslesService;
import org.motechproject.care.service.schedule.VaccinationService;
import org.motechproject.care.utils.CaseUtils;
import org.motechproject.care.utils.SpringIntegrationTest;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
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
    private AllChildren allChildren;

    private String caseId;
    private ChildService childService;

    @Before
    public void setUp(){
        caseId = CaseUtils.getUniqueCaseId();
        List<VaccinationService> vaccinationServices = Arrays.asList((VaccinationService) measlesService);
        ChildVaccinationProcessor childVaccinationProcessor = new ChildVaccinationProcessor(vaccinationServices);
        childService = new ChildService(allChildren, childVaccinationProcessor);
    }

    @After
    public void tearDown() {
        allChildren.removeAll();
    }

    @Test
    public void shouldVerifyMeaslesScheduleCreationWhenChildIsRegistered() {
        String measlesScheduleName = ChildVaccinationSchedule.Measles.getName();
        DateTime dob = DateUtil.newDateTime(DateUtil.today().minusMonths(4));

        CareCase careCase=new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withMeaslesDate(null).withMotherCaseId("motherCaseId").build();
        childService.process(careCase);

        markScheduleForUnEnrollment(caseId,  measlesScheduleName);
        EnrollmentsQuery query = new EnrollmentsQuery()
                .havingExternalId(caseId)
                .havingState(EnrollmentStatus.ACTIVE)
                .havingSchedule(measlesScheduleName);

        EnrollmentRecord enrollment = trackingService.searchWithWindowDates(query).get(0);

        assertEquals(MilestoneType.Measles.toString(), enrollment.getCurrentMilestoneName());
        assertEquals(dob, enrollment.getReferenceDateTime());
        assertEquals(dob.plusMonths(9), enrollment.getStartOfDueWindow());
        assertEquals(dob.plusMonths(24), enrollment.getStartOfLateWindow());

        Child child = allChildren.findByCaseId(caseId);
        assertEquals(dob , child.getDOB());
        assertNull(child.getMeaslesDate());
    }

    @Test
    public void shouldVerifyMeaslesScheduleFulfillmentWhenChildHasTakenMeasles() {
        String measlesScheduleName = ChildVaccinationSchedule.Measles.getName();
        DateTime dob = DateUtil.newDateTime(DateUtil.today().minusMonths(4));
        DateTime measlesTaken = DateUtil.newDateTime(DateUtil.today().plusMonths(1));
        String motherCaseId = "motherCaseId";

        CareCase careCase=new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withMeaslesDate(null).withMotherCaseId(motherCaseId).build();
        childService.process(careCase);
        careCase=new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withMeaslesDate(measlesTaken.toString()).withMotherCaseId(motherCaseId).build();
        childService.process(careCase);

        assertNull(trackingService.getEnrollment(caseId, measlesScheduleName));

        Child child = allChildren.findByCaseId(caseId);
        assertEquals(dob, child.getDOB());
        assertEquals(measlesTaken, child.getMeaslesDate());
    }
}
