package org.motechproject.care.service.schedule;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.care.domain.Child;
import org.motechproject.care.schedule.service.VitaSchedulerService;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class VitaServiceTest {

    @Mock
    private VitaSchedulerService vitaSchedulerService;
    VitaService vitaService;

    @Before
    public void setUp(){
        vitaService = new VitaService(vitaSchedulerService);
    }

    @Test
    public void shouldEnrollChildForVitaSchedule(){
        DateTime dob = new DateTime();
        String caseId = "caseId";
        Child child = new Child();
        child.setDOB(dob);
        child.setCaseId(caseId);

        vitaService.process(child);
        Mockito.verify(vitaSchedulerService).enroll(caseId, dob);
    }

    @Test
    public void shouldNotEnrollChildForVitaScheduleWhenDOBIsNull(){
        Child child = new Child();
        child.setCaseId("caseId");

        vitaService.process(child);
        verify(vitaSchedulerService, never()).enroll(any(String.class), any(DateTime.class));
    }

    @Test
    public void shouldFulfilVitaIfVitaDatePresentInChild(){
        DateTime vitaDate = new DateTime();
        String caseId = "caseId";
        Child child = new Child();
        child.setVitamin1Date(vitaDate);
        child.setCaseId(caseId);

        vitaService.process(child);
        Mockito.verify(vitaSchedulerService).fulfillMileStone(caseId,VitaSchedulerService.milestone,  vitaDate);
    }

    @Test
    public void shouldNotFulfilVitaIfVitaDateNotPresentInChild(){
        Child child = new Child();
        child.setCaseId("caseId");

        vitaService.process(child);
        verify(vitaSchedulerService, never()).fulfillMileStone(any(String.class), any(String.class), any(DateTime.class));
    }
}
