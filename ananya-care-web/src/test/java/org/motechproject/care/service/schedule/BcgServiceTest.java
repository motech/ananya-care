package org.motechproject.care.service.schedule;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.care.domain.Child;
import org.motechproject.care.schedule.service.BcgSchedulerService;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BcgServiceTest {

    @Mock
    private BcgSchedulerService bcgSchedulerService;
    BcgService bcgService;

    @Before
    public void setUp(){
        bcgService = new BcgService(bcgSchedulerService);
    }

    @Test
    public void shouldEnrollChildForBcgSchedule(){
        DateTime dob = new DateTime();
        String caseId = "caseId";
        Child child = new Child();
        child.setDOB(dob);
        child.setCaseId(caseId);

        bcgService.process(child);
        Mockito.verify(bcgSchedulerService).enroll(caseId, dob);
    }

    @Test
    public void shouldNotEnrollChildForBcgScheduleWhenDOBIsNull(){
        Child child = new Child();
        child.setCaseId("caseId");

        bcgService.process(child);
        verify(bcgSchedulerService, never()).enroll(any(String.class), any(DateTime.class));
    }

    @Test
    public void shouldFulfilBcgIfBcgDatePresentInChild(){
        DateTime bcgDate = new DateTime();
        String caseId = "caseId";
        Child child = new Child();
        child.setBcgDate(bcgDate);
        child.setCaseId(caseId);

        bcgService.process(child);
        Mockito.verify(bcgSchedulerService).fulfillMileStone(caseId, BcgSchedulerService.milestone,  bcgDate);
    }

    @Test
    public void shouldNotFulfilBcgIfBcgDateNotPresentInChild(){
        Child child = new Child();
        child.setCaseId("caseId");

        bcgService.process(child);
        verify(bcgSchedulerService, never()).fulfillMileStone(any(String.class), any(String.class), any(DateTime.class));
    }

}
