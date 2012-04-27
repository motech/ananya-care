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

@RunWith(MockitoJUnitRunner.class)
public class OpvBoosterServiceTest {
    @Mock
    private ScheduleService schedulerService;
    OpvBoosterService opvBoosterService;
    private String scheduleName = ChildVaccinationSchedule.OPVBooster.getName();


    @Before
    public void setUp(){
        opvBoosterService = new OpvBoosterService(schedulerService);
    }

    @Test
    public void shouldEnrollChildForOPVBoosterScheduleWhenOPV3IsTakenBetween16to24MonthsAge(){
        DateTime dob = new DateTime();
        DateTime opv3Taken = dob.plusMonths(17);
        String caseId = "caseId";
        Child child = new Child();
        child.setDOB(dob);
        child.setOpv3Date(opv3Taken);
        child.setCaseId(caseId);

        opvBoosterService.process(child);
        Mockito.verify(schedulerService).enroll(caseId, opv3Taken.plusDays(180).minusWeeks(2), scheduleName);
    }

    @Test
    public void shouldEnrollChildForOPVBoosterScheduleWhenOPV3IsTaken180OrMoreDaysPriorTo16MonthsAge(){
        DateTime dob = new DateTime();
        DateTime opv3Taken = dob.plusMonths(8);
        String caseId = "caseId";
        Child child = new Child();
        child.setDOB(dob);
        child.setOpv3Date(opv3Taken);
        child.setCaseId(caseId);

        opvBoosterService.process(child);
        Mockito.verify(schedulerService).enroll(caseId, dob.plusMonths(16).minusWeeks(2), scheduleName);
    }

    @Test
    public void shouldEnrollChildForOPVBoosterScheduleWhenOPV3IsTakenLessThan180DaysPriorTo16MonthsAge(){
        DateTime dob = new DateTime();
        DateTime opv3Taken = dob.plusMonths(11);
        String caseId = "caseId";
        Child child = new Child();
        child.setDOB(dob);
        child.setOpv3Date(opv3Taken);
        child.setCaseId(caseId);

        opvBoosterService.process(child);
        Mockito.verify(schedulerService).enroll(caseId, opv3Taken.plusDays(180).minusWeeks(2), scheduleName);
    }

    @Test
    public void shouldNotEnrollChildForOPVBoosterScheduleWhenOPV3IsNotTaken(){
        DateTime dob = new DateTime();
        String caseId = "caseId";
        Child child = new Child();
        child.setDOB(dob);
        child.setCaseId(caseId);

        opvBoosterService.process(child);
        Mockito.verify(schedulerService, never()).enroll(anyString(), any(DateTime.class), anyString());
    }

    @Test
    public void shouldNotEnrollChildForOPVBoosterScheduleWhenOPV3IsTakenAfter18MonthsOfAge(){
        DateTime dob = new DateTime();
        DateTime opv3Taken = dob.plusMonths(19);
        String caseId = "caseId";
        Child child = new Child();
        child.setDOB(dob);
        child.setOpv3Date(opv3Taken);
        child.setCaseId(caseId);

        opvBoosterService.process(child);
        Mockito.verify(schedulerService, never()).enroll(anyString(), any(DateTime.class), anyString());
    }

    @Test
    public void shouldFulfillOPVBoosterIfOPVBoosterDatePresentInChild(){
        DateTime opvBoosterDate = new DateTime();
        String caseId = "caseId";

        Child child = new Child();
        child.setOpvBoosterDate(opvBoosterDate);
        child.setCaseId(caseId);

        opvBoosterService.process(child);
        Mockito.verify(schedulerService).fulfillMileStone(caseId, MilestoneType.OPVBooster.toString(),  opvBoosterDate, scheduleName);
    }
}
