package org.motechproject.care.service.schedule;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
public class Opv0ServiceTest {

    @Mock
    private ScheduleService schedulerService;
    private String scheduleName = ChildVaccinationSchedule.OPV0.getName();

    private Opv0Service Opv0Service;


    @Before
    public void setUp(){
        Opv0Service =new Opv0Service(schedulerService);
    }

    @Test
    public void shouldNotEnrollChildForOPV0ScheduleWhenDOBIsNull(){
        Child child = new Child();
        child.setCaseId("caseId");
        Opv0Service.process(child);
        verify(schedulerService, never()).enroll(any(String.class), any(DateTime.class), anyString());
    }

    @Test
    public void shouldEnrollChildForOPV0ScheduleWhenDOBIsAvailable(){
        Child child = new Child();
        child.setCaseId("caseId");
        child.setDOB(DateTime.now());
        Opv0Service.process(child);
        verify(schedulerService).enroll(any(String.class), any(DateTime.class), anyString());
    }

    @Test
    public void shouldFulfillOPV0MilestoneIfOPV0DateAvailable(){
        Child child = new Child();
        String caseId = "caseId";
        child.setCaseId(caseId);
        child.setDOB(DateTime.now());
        DateTime opv0Date = DateTime.now().minusDays(1);
        child.setOpv0Date(opv0Date);
        Opv0Service.process(child);
        verify(schedulerService).fulfillMileStone(caseId, MilestoneType.OPV0.toString(),opv0Date,ChildVaccinationSchedule.OPV0.getName());
    }


}
