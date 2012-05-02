package org.motechproject.care.service.schedule;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.schedule.service.ScheduleService;
import org.motechproject.care.schedule.vaccinations.MotherVaccinationSchedule;
import org.motechproject.care.service.util.PeriodUtil;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MotherCareServiceTest {
    @Mock
    private ScheduleService schedulerService;
    MotherCareService motherCareService;
    private String scheduleName = MotherVaccinationSchedule.MotherCare.getName();


    @Before
    public void setUp(){
        motherCareService = new MotherCareService(schedulerService);
    }

    @Test
    public void shouldEnrollMotherForMotherCareSchedule(){
        DateTime edd = new DateTime();
        String caseId = "caseId";
        Mother mother = new Mother();
        mother.setEdd(edd);
        mother.setCaseId(caseId);

        motherCareService.process(mother);
        Mockito.verify(schedulerService).enroll(caseId, edd.minusDays(PeriodUtil.DAYS_IN_9_MONTHS), scheduleName);
    }

    @Test
    public void shouldNotEnrollMotherForMotherCareWhenEDDIsNull(){
        Mother mother = new Mother();
        mother.setCaseId("caseId");

        motherCareService.process(mother);
        verify(schedulerService, never()).enroll(any(String.class), any(DateTime.class), anyString());
    }
}