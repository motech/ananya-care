package org.motechproject.care.service.schedule;

import org.joda.time.DateTime;
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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class Hep0ServiceTest {

    @Mock
    private ScheduleService schedulerService;
    @Mock
    CareCaseTaskService careCaseTaskService;

    private String scheduleName = ChildVaccinationSchedule.Hepatitis0.getName();

    private Hep0Service hep0Service;


    @Before
    public void setUp(){
        hep0Service=new Hep0Service(schedulerService, careCaseTaskService);
    }

    @Test
    public void shouldNotEnrollChildForHep0ScheduleWhenDOBIsNull(){
        Child child = new Child();
        child.setCaseId("caseId");
        hep0Service.process(child);
        verify(schedulerService, never()).enroll(any(String.class), any(DateTime.class), anyString());
    }

    @Test
    public void shouldEnrollChildForHep0ScheduleWhenDOBIsAvailable(){
        Child child = new Child();
        child.setCaseId("caseId");
        child.setDOB(DateTime.now());
        hep0Service.process(child);
        verify(schedulerService).enroll(any(String.class), any(DateTime.class), anyString());
    }

    @Test
    public void shouldFulfillHep0MilestoneIfHep0DateAvailable(){
        Child child = new Child();
        String caseId = "caseId";
        child.setCaseId(caseId);
        child.setDOB(DateTime.now());
        DateTime hep0Date = DateTime.now().minusDays(1);
        child.setHep0Date(hep0Date);
        hep0Service.process(child);
        verify(schedulerService).fulfillMilestone(caseId, MilestoneType.Hep0.toString(), hep0Date, ChildVaccinationSchedule.Hepatitis0.getName());
        Mockito.verify(careCaseTaskService).close(caseId, MilestoneType.Hep0.toString());
    }

    @Test
    public void shouldUnenrollFromHep0Schedule(){
        String caseId = "caseId";

        Mother mother = new Mother();
        mother.setCaseId(caseId);

        hep0Service.close(mother);
        Mockito.verify(schedulerService).unenroll(caseId, scheduleName);

    }


}
