package org.motechproject.care.service.schedule;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.service.ScheduleService;
import org.motechproject.care.schedule.vaccinations.MotherVaccinationSchedule;
import org.motechproject.care.service.CareCaseTaskService;
import org.motechproject.care.service.util.PeriodUtil;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TTServiceTest {
    @Mock
    private ScheduleService schedulerService;
    @Mock
    CareCaseTaskService careCaseTaskService;

    TTService ttService;
    private String scheduleName = MotherVaccinationSchedule.TT.getName();


    @Before
    public void setUp(){
        ttService = new TTService(schedulerService, careCaseTaskService);
    }

    @Test
    public void shouldEnrollMotherForTTSchedule(){
        DateTime edd = new DateTime();
        String caseId = "caseId";
        Mother mother = new Mother();
        mother.setEdd(edd);
        mother.setCaseId(caseId);

        ttService.process(mother);
        Mockito.verify(schedulerService).enroll(caseId, edd.minusDays(PeriodUtil.DAYS_IN_9_MONTHS), scheduleName);
    }

    @Test
    public void shouldNotEnrollMotherForTTScheduleWhenEDDIsNull(){
        Mother mother = new Mother();
        mother.setCaseId("caseId");

        ttService.process(mother);
        verify(schedulerService, never()).enroll(any(String.class), any(DateTime.class), anyString());
    }

    @Test
    public void shouldNotEnrollMotherForTTScheduleWhenEDDPresentAndLastPregFlagSetToTrue(){
        DateTime edd = new DateTime();
        Mother mother = new Mother();
        mother.setEdd(edd);
        mother.setLastPregTt(true);
        mother.setCaseId("caseId");

        ttService.process(mother);
        verify(schedulerService, never()).enroll(any(String.class), any(DateTime.class), anyString());
    }

    @Test
    public void shouldFulfillTT1IfTT1DatePresentInMother(){
        DateTime tt1Date = new DateTime();
        String caseId = "caseId";
        Mother mother = new Mother();
        mother.setTt1Date(tt1Date);
        mother.setCaseId(caseId);

        ttService.process(mother);
        Mockito.verify(schedulerService).fulfillMilestone(caseId, MilestoneType.TT1.toString(), tt1Date, scheduleName);
        Mockito.verify(careCaseTaskService).close(caseId, MilestoneType.TT1.toString());
    }

    @Test
    public void shouldFulfillTT2IfTT2DatePresentInMother(){
        DateTime tt2Date = new DateTime();
        String caseId = "caseId";
        Mother mother = new Mother();
        mother.setTt2Date(tt2Date);
        mother.setCaseId(caseId);

        ttService.process(mother);
        Mockito.verify(schedulerService).fulfillMilestone(caseId, MilestoneType.TT2.toString(), tt2Date, scheduleName);
        Mockito.verify(careCaseTaskService).close(caseId, MilestoneType.TT2.toString());
    }

    @Test
    public void shouldNotFulfillTTIOrTT2IfTTTakenDateNotPresentInMother(){
        Mother mother = new Mother();
        mother.setCaseId("caseId");

        ttService.process(mother);
        verify(schedulerService, never()).fulfillMilestone(any(String.class), any(String.class), any(DateTime.class), anyString());
        Mockito.verify(careCaseTaskService, never()).close(any(String.class), any(String.class));
    }

    @Test
    public void shouldUnenrollFromTTSchedule(){
        String caseId = "caseId";

        Mother mother = new Mother();
        mother.setCaseId(caseId);

        ttService.close(mother);
        Mockito.verify(schedulerService).unenroll(caseId, scheduleName);

    }
}
