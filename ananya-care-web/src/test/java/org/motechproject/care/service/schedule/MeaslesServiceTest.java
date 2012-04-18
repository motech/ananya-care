package org.motechproject.care.service.schedule;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.care.domain.Child;
import org.motechproject.care.schedule.service.MeaslesSchedulerService;
import org.motechproject.care.schedule.service.VitaSchedulerService;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MeaslesServiceTest {

    @Mock
    private MeaslesSchedulerService measlesSchedulerService;
    MeaslesService measlesService;

    @Before
    public void setUp(){
        measlesService = new MeaslesService(measlesSchedulerService);
    }

    @Test
    public void shouldEnrollChildForMeaslesSchedule(){
        DateTime dob = new DateTime();
        String caseId = "caseId";
        Child child = new Child();
        child.setDOB(dob);
        child.setCaseId(caseId);

        measlesService.process(child);
        Mockito.verify(measlesSchedulerService).enroll(caseId, dob);
    }

    @Test
    public void shouldNotEnrollChildForMeaslesScheduleWhenDOBIsNull(){
        Child child = new Child();
        child.setCaseId("caseId");

        measlesService.process(child);
        verify(measlesSchedulerService, never()).enroll(any(String.class), any(DateTime.class));
    }

    @Test
    public void shouldFulfilMeaslesIfMeaslesDatePresentInChild(){
        DateTime measlesDate = new DateTime();
        String caseId = "caseId";
        Child child = new Child();
        child.setMeaslesDate(measlesDate);
        child.setCaseId(caseId);

        measlesService.process(child);
        Mockito.verify(measlesSchedulerService).fulfillMileStone(caseId,MeaslesSchedulerService.milestone,  measlesDate);
    }

    @Test
    public void shouldNotFulfilMeaslesIfMeaslesDateNotPresentInChild(){
        Child child = new Child();
        child.setCaseId("caseId");

        measlesService.process(child);
        verify(measlesSchedulerService, never()).fulfillMileStone(any(String.class), any(String.class), any(DateTime.class));
    }
}

