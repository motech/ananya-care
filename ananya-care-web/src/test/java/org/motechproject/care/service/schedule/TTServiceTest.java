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
import org.motechproject.care.service.util.PeriodUtil;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TTServiceTest {
    @Mock
    private ScheduleService schedulerService;
    TTService ttService;
    private String scheduleName = MotherVaccinationSchedule.TT.getName();


    @Before
    public void setUp(){
        ttService = new TTService(schedulerService);
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
    public void shouldFulfillTT1IfTT1DatePresentInMother(){
        DateTime tt1Date = new DateTime();
        String caseId = "caseId";
        Mother mother = new Mother();
        mother.setTt1Date(tt1Date);
        mother.setCaseId(caseId);

        ttService.process(mother);
        Mockito.verify(schedulerService).fulfillMileStone(caseId, MilestoneType.TT1.toString(),  tt1Date, scheduleName);
    }

    @Test
    public void shouldFulfillTT2IfTT2DatePresentInMother(){
        DateTime tt1Date = new DateTime();
        String caseId = "caseId";
        Mother mother = new Mother();
        mother.setTt2Date(tt1Date);
        mother.setCaseId(caseId);

        ttService.process(mother);
        Mockito.verify(schedulerService).fulfillMileStone(caseId, MilestoneType.TT2.toString(),  tt1Date, scheduleName);
    }

    @Test
    public void shouldNotFulfillTTIOrTT2IfTTTakenDateNotPresentInMother(){
        Mother mother = new Mother();
        mother.setCaseId("caseId");

        ttService.process(mother);
        verify(schedulerService, never()).fulfillMileStone(any(String.class), any(String.class), any(DateTime.class), anyString());
    }
}
