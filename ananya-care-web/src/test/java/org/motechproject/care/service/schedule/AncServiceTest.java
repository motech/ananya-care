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
public class AncServiceTest {
    @Mock
    private ScheduleService schedulerService;
    @Mock
    CareCaseTaskService careCaseTaskService;

    AncService ancService;
    private String scheduleName = MotherVaccinationSchedule.Anc.getName();


    @Before
    public void setUp(){
        ancService = new AncService(schedulerService, careCaseTaskService);
    }

    @Test
    public void shouldEnrollMotherForAncSchedule(){
        DateTime edd = new DateTime();
        String caseId = "caseId";
        Mother mother = new Mother();
        mother.setEdd(edd);
        mother.setCaseId(caseId);

        ancService.process(mother);
        Mockito.verify(schedulerService).enroll(caseId, edd.minusDays(PeriodUtil.DAYS_IN_9_MONTHS), scheduleName);
    }

    @Test
    public void shouldNotEnrollMotherForAncScheduleWhenEDDIsNull(){
        Mother mother = new Mother();
        mother.setCaseId("caseId");

        ancService.process(mother);
        verify(schedulerService, never()).enroll(any(String.class), any(DateTime.class), anyString());
    }

    @Test
    public void shouldFulfillAnc1IfAnc1DatePresentInMother(){
        DateTime anc1Date = new DateTime();
        String caseId = "caseId";
        Mother mother = new Mother();
        mother.setAnc1Date(anc1Date);
        mother.setCaseId(caseId);

        ancService.process(mother);
        Mockito.verify(schedulerService).fulfillMilestone(caseId, MilestoneType.Anc1.toString(), anc1Date, scheduleName);
        Mockito.verify(careCaseTaskService).close(caseId, MilestoneType.Anc1.toString());
    }

    @Test
    public void shouldFulfillAnc2IfAnc2DatePresentInMother(){
        DateTime anc2Date = new DateTime();
        String caseId = "caseId";
        Mother mother = new Mother();
        mother.setAnc2Date(anc2Date);
        mother.setCaseId(caseId);

        ancService.process(mother);
        Mockito.verify(schedulerService).fulfillMilestone(caseId, MilestoneType.Anc2.toString(), anc2Date, scheduleName);
        Mockito.verify(careCaseTaskService).close(caseId, MilestoneType.Anc2.toString());
    }

    @Test
    public void shouldFulfillAnc3IfAnc3DatePresentInMother(){
        DateTime anc3Date = new DateTime();
        String caseId = "caseId";
        Mother mother = new Mother();
        mother.setAnc3Date(anc3Date);
        mother.setCaseId(caseId);

        ancService.process(mother);
        Mockito.verify(schedulerService).fulfillMilestone(caseId, MilestoneType.Anc3.toString(), anc3Date, scheduleName);
        Mockito.verify(careCaseTaskService).close(caseId, MilestoneType.Anc3.toString());
    }

    @Test
    public void shouldNotFulfillAnc1OrAnc2OrAnc3IfNeitherOfTheTakenDatesArePresentInMother(){
        Mother mother = new Mother();
        mother.setCaseId("caseId");

        ancService.process(mother);
        verify(schedulerService, never()).fulfillMilestone(any(String.class), any(String.class), any(DateTime.class), anyString());
        Mockito.verify(careCaseTaskService, never()).close(any(String.class), any(String.class));
    }

    @Test
    public void shouldUnenrollFromAncSchedule(){
        String caseId = "caseId";

        Mother mother = new Mother();
        mother.setCaseId(caseId);
        ancService.close(mother);
        Mockito.verify(schedulerService).unenroll(caseId, scheduleName);

    }

}
