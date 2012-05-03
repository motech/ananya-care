package org.motechproject.care.service.schedule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.care.domain.Client;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.schedule.service.ScheduleService;
import org.motechproject.care.service.CareCaseTaskService;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class VaccinationServiceTest {
    @Mock
    private ScheduleService schedulerService;
    @Mock
    CareCaseTaskService careCaseTaskService;


    private String scheduleName = "scheduleName";

    private VaccinationService vaccinationService;


    @Before
    public void setUp(){
        vaccinationService = new VaccinationService(schedulerService, scheduleName, careCaseTaskService) {
            @Override
            public void process(Client client) {

            }
        };
    }
    
    @Test
    public void shouldUnenrollAndReturnCurrentEnrollmentRecord() {
        String caseId = "caseId";
        String milestoneName = "milestoneName";
        Client client = new Mother();
        client.setCaseId(caseId);

        EnrollmentRecord expectedEnrollmentRecord = enrollmentRecordForMilestone(milestoneName);
        when(schedulerService.unenroll(caseId, scheduleName)).thenReturn(expectedEnrollmentRecord);
        
        vaccinationService.close(client);
        verify(schedulerService, only()).unenroll(caseId, scheduleName);
        verify(careCaseTaskService, only()).close(caseId, milestoneName);
    }
    
    private EnrollmentRecord enrollmentRecordForMilestone(String currentMilestoneName) {
        return new EnrollmentRecord(null, null, currentMilestoneName, null, null, null, null, null, null, null);
    }

}
