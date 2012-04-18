package org.motechproject.care.service.schedule;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.schedule.service.TTSchedulerService;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TTServiceTest {
    @Mock
    private TTSchedulerService ttSchedulerService;
    TTService ttService;

    @Before
    public void setUp(){
        ttService = new TTService(ttSchedulerService);
    }

    @Test
    public void shouldEnrollMotherForTTSchedule(){
        DateTime edd = new DateTime();
        String caseId = "caseId";
        Mother mother = new Mother();
        mother.setEdd(edd);
        mother.setCaseId(caseId);

        ttService.process(mother);
        Mockito.verify(ttSchedulerService).enroll(caseId, edd.minusMonths(9));
    }

    @Test
    public void shouldNotEnrollMotherForTTScheduleWhenEDDIsNull(){
        Mother mother = new Mother();
        mother.setCaseId("caseId");

        ttService.process(mother);
        verify(ttSchedulerService, never()).enroll(any(String.class), any(DateTime.class));
    }

    @Test
    public void shouldFulfillTTIfTTDatePresentInMother(){
        DateTime tt1Date = new DateTime();
        String caseId = "caseId";
        Mother mother = new Mother();
        mother.setTt1Date(tt1Date);
        mother.setCaseId(caseId);

        ttService.process(mother);
        Mockito.verify(ttSchedulerService).fulfillMileStone(caseId,TTSchedulerService.tt1Milestone,  tt1Date);
    }

    @Test
    public void shouldNotFulfillTTIfTTDateNotPresentInMother(){
        Mother mother = new Mother();
        mother.setCaseId("caseId");

        ttService.process(mother);
        verify(ttSchedulerService, never()).fulfillMileStone(any(String.class), any(String.class), any(DateTime.class));
    }
}
