package org.motechproject.care.schedule.service.integration;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.care.schedule.service.ChildVaccinationProcessor;
import org.motechproject.care.schedule.vaccinations.VaccinationSchedule;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:applicationContext-Scheduling.xml")
public class ChildVaccinationProcessorIntegrationTest {

    @Autowired
    ChildVaccinationProcessor childVaccinationProcessor;
    @Autowired
    private ScheduleTrackingService trackingService;


    @Test
    public void shouldProcessForMeaslesVaccine(){
        String caseId = UUID.randomUUID().toString();
        DateTime today = DateUtil.now();
        EnrollmentRecord enrollment = trackingService.getEnrollment(caseId, VaccinationSchedule.Measles.getName());
        Assert.assertNull(enrollment);

        childVaccinationProcessor.enrollUpdateVaccines(caseId, today);
        enrollment = trackingService.getEnrollment(caseId, VaccinationSchedule.Measles.getName());
        Assert.assertNotNull(enrollment);
    }
}
