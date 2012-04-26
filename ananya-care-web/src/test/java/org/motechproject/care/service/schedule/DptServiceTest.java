package org.motechproject.care.service.schedule;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.care.domain.Child;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.service.ScheduleService;
import org.motechproject.care.schedule.vaccinations.ChildVaccinationSchedule;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DptServiceTest {
    @Mock
    private ScheduleService schedulerService;
    DptService dptService;
    private String scheduleName = ChildVaccinationSchedule.DPT.getName();

    @Before
    public void setUp(){
        dptService = new DptService(schedulerService);
    }

    @Test
    public void shouldEnrollChildForDptSchedule(){
        DateTime dob = new DateTime();
        String caseId = "caseId";
        Child child = new Child();
        child.setDOB(dob);
        child.setCaseId(caseId);

        dptService.process(child);
        Mockito.verify(schedulerService).enroll(caseId, dob, scheduleName);
    }

    @Test
    public void shouldNotEnrollChildForDptScheduleWhenDOBIsNull(){
        Child child = new Child();
        child.setCaseId("caseId");

        dptService.process(child);
        verify(schedulerService, never()).enroll(any(String.class), any(DateTime.class), anyString());
    }

    @Test
    public void shouldFulfillDpt1IfDpt1DatePresentInChild(){
        DateTime dpt1Date = new DateTime();
        String caseId = "caseId";
        Child child = new Child();
        child.setDpt1Date(dpt1Date);
        child.setCaseId(caseId);

        dptService.process(child);
        Mockito.verify(schedulerService).fulfillMileStone(caseId, MilestoneType.DPT1.toString(),  dpt1Date, scheduleName);
    }

    @Test
    public void shouldFulfillDpt2IfDpt2DatePresentInChild(){
        DateTime dpt2Date = new DateTime();
        String caseId = "caseId";
        Child child = new Child();
        child.setDpt2Date(dpt2Date);
        child.setCaseId(caseId);

        dptService.process(child);
        Mockito.verify(schedulerService).fulfillMileStone(caseId, MilestoneType.DPT2.toString(),  dpt2Date, scheduleName);
    }

    @Test
    public void shouldFulfillDpt3IfDpt3DatePresentInChild(){
        DateTime dpt3Date = new DateTime();
        String caseId = "caseId";
        Child child = new Child();
        child.setDpt3Date(dpt3Date);
        child.setCaseId(caseId);

        dptService.process(child);
        Mockito.verify(schedulerService).fulfillMileStone(caseId, MilestoneType.DPT3.toString(),  dpt3Date, scheduleName);
    }

    @Test
    public void shouldFulfillDptBoosterIfDptBoosterDatePresentInChild(){
        DateTime dptBoosterDate = new DateTime();
        String caseId = "caseId";
        Child child = new Child();
        child.setDptBoosterDate(dptBoosterDate);
        child.setCaseId(caseId);

        dptService.process(child);
        Mockito.verify(schedulerService).fulfillMileStone(caseId, MilestoneType.DPTBooster.toString(),  dptBoosterDate, scheduleName);
    }

    @Test
    public void shouldNotFulfillAnyDptIfNoneOfTheTakenDatesArePresentInChild(){
        Child child = new Child();
        child.setCaseId("caseId");

        dptService.process(child);
        verify(schedulerService, never()).fulfillMileStone(any(String.class), any(String.class), any(DateTime.class), anyString());
    }

}
