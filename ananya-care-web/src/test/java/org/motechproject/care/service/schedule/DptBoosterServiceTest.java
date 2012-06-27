package org.motechproject.care.service.schedule;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.care.domain.Child;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.service.ScheduleService;
import org.motechproject.care.schedule.vaccinations.ChildVaccinationSchedule;
import org.motechproject.care.service.CareCaseTaskService;
import org.motechproject.care.service.util.PeriodUtil;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DptBoosterServiceTest {
    @Mock
    private ScheduleService schedulerService;
    @Mock
    private CareCaseTaskService careCaseTaskService;
    @Mock
    private PeriodUtil periodUtil;

    private DptBoosterService dptBoosterService;
    private String scheduleName = ChildVaccinationSchedule.DPTBooster.getName();
    private final Period periodOffset = Period.weeks(-2);

    @Before
    public void setUp(){
        dptBoosterService = new DptBoosterService(schedulerService, careCaseTaskService, periodUtil);
        when(periodUtil.getScheduleOffset()).thenReturn(periodOffset);
    }

    @Test
    public void shouldEnrollChildForDPTBoosterScheduleWhenDPT3IsTakenBetween16to24MonthsAge(){
        DateTime dob = new DateTime();
        DateTime dpt3Taken = dob.plusMonths(17);
        String caseId = "caseId";
        Child child = new Child();
        child.setDOB(dob);
        child.setDpt3Date(dpt3Taken);
        child.setCaseId(caseId);

        dptBoosterService.process(child);
        Mockito.verify(schedulerService).enroll(caseId, dpt3Taken.plusDays(180).plus(periodOffset), scheduleName);
    }

    @Test
    public void shouldEnrollChildForDPTBoosterScheduleWhenDPT3IsTaken180OrMoreDaysPriorTo16MonthsAge(){
        DateTime dob = new DateTime();
        DateTime dpt3Taken = dob.plusMonths(8);
        String caseId = "caseId";
        Child child = new Child();
        child.setDOB(dob);
        child.setDpt3Date(dpt3Taken);
        child.setCaseId(caseId);

        dptBoosterService.process(child);
        Mockito.verify(schedulerService).enroll(caseId, dob.plusMonths(16).plus(periodOffset), scheduleName);
    }

    @Test
    public void shouldEnrollChildForDPTBoosterScheduleWhenDPT3IsTakenLessThan180DaysPriorTo16MonthsAge(){
        DateTime dob = new DateTime();
        DateTime dpt3Taken = dob.plusMonths(11);
        String caseId = "caseId";
        Child child = new Child();
        child.setDOB(dob);
        child.setDpt3Date(dpt3Taken);
        child.setCaseId(caseId);

        dptBoosterService.process(child);
        Mockito.verify(schedulerService).enroll(caseId, dpt3Taken.plusDays(180).plus(periodOffset), scheduleName);
    }

    @Test
    public void shouldNotEnrollChildForDPTBoosterScheduleWhenDPT3IsNotTaken(){
        DateTime dob = new DateTime();
        String caseId = "caseId";
        Child child = new Child();
        child.setDOB(dob);
        child.setCaseId(caseId);

        dptBoosterService.process(child);
        Mockito.verify(schedulerService, never()).enroll(anyString(), any(DateTime.class), anyString());
    }

    @Test
    public void shouldNotEnrollChildForDPTBoosterScheduleWhenDPT3IsTakenAfter18MonthsOfAge(){
        DateTime dob = new DateTime();
        DateTime dpt3Taken = dob.plusMonths(19);
        String caseId = "caseId";
        Child child = new Child();
        child.setDOB(dob);
        child.setDpt3Date(dpt3Taken);
        child.setCaseId(caseId);

        dptBoosterService.process(child);
        Mockito.verify(schedulerService, never()).enroll(anyString(), any(DateTime.class), anyString());
    }

    @Test
    public void shouldFulfillDPTBoosterIfDPTBoosterDatePresentInChild(){
        DateTime dptBoosterDate = new DateTime();
        String caseId = "caseId";

        Child child = new Child();
        child.setDptBoosterDate(dptBoosterDate);
        child.setCaseId(caseId);

        dptBoosterService.process(child);
        Mockito.verify(schedulerService).fulfillMilestone(caseId, MilestoneType.DPTBooster.toString(), dptBoosterDate, scheduleName);
        Mockito.verify(careCaseTaskService).close(caseId, MilestoneType.DPTBooster.toString());
    }

    @Test
    public void shouldUnenrollFromDptBoosterSchedule(){
        String caseId = "caseId";
        Mother mother = new Mother();
        mother.setCaseId(caseId);

        dptBoosterService.close(mother);
        Mockito.verify(schedulerService).unenroll(caseId,scheduleName);
    }
}