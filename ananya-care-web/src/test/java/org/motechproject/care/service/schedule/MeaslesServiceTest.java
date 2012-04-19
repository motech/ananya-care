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
public class MeaslesServiceTest {

    @Mock
    private ScheduleService schedulerService;
    MeaslesService measlesService;
    private String scheduleName = ChildVaccinationSchedule.Measles.getName();


    @Before
    public void setUp(){
        measlesService = new MeaslesService(schedulerService);
    }

    @Test
    public void shouldEnrollChildForMeaslesSchedule(){
        DateTime dob = new DateTime();
        String caseId = "caseId";
        Child child = new Child();
        child.setDOB(dob);
        child.setCaseId(caseId);

        measlesService.process(child);
        Mockito.verify(schedulerService).enroll(caseId, dob, scheduleName);
    }

    @Test
    public void shouldNotEnrollChildForMeaslesScheduleWhenDOBIsNull(){
        Child child = new Child();
        child.setCaseId("caseId");

        measlesService.process(child);
        verify(schedulerService, never()).enroll(any(String.class), any(DateTime.class), anyString());
    }

    @Test
    public void shouldFulfilMeaslesIfMeaslesDatePresentInChild(){
        DateTime measlesDate = new DateTime();
        String caseId = "caseId";
        Child child = new Child();
        child.setMeaslesDate(measlesDate);
        child.setCaseId(caseId);

        measlesService.process(child);
        Mockito.verify(schedulerService).fulfillMileStone(caseId, MilestoneType.Measles.toString(),  measlesDate, scheduleName);
    }

    @Test
    public void shouldNotFulfilMeaslesIfMeaslesDateNotPresentInChild(){
        Child child = new Child();
        child.setCaseId("caseId");

        measlesService.process(child);
        verify(schedulerService, never()).fulfillMileStone(any(String.class), any(String.class), any(DateTime.class), anyString());
    }
}

