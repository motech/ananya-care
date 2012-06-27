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
public class HepServiceTest {
    @Mock
    private ScheduleService schedulerService;
    @Mock
    CareCaseTaskService careCaseTaskService;

    HepService hepService;
    private String scheduleName = ChildVaccinationSchedule.Hepatitis.getName();

    @Before
    public void setUp(){
        hepService = new HepService(schedulerService, careCaseTaskService);
    }

    @Test
    public void shouldEnrollChildForHepSchedule(){
        DateTime dob = new DateTime();
        String caseId = "caseId";
        Child child = new Child();
        child.setDOB(dob);
        child.setCaseId(caseId);

        hepService.process(child);
        Mockito.verify(schedulerService).enroll(caseId, dob, scheduleName);
    }

    @Test
    public void shouldNotEnrollChildForHepScheduleWhenDOBIsNull(){
        Child child = new Child();
        child.setCaseId("caseId");

        hepService.process(child);
        verify(schedulerService, never()).enroll(any(String.class), any(DateTime.class), anyString());
    }

    @Test
    public void shouldFulfillHep1IfHep1DatePresentInChild(){
        DateTime hep1Date = new DateTime();
        String caseId = "caseId";
        Child child = new Child();
        child.setHep1Date(hep1Date);
        child.setCaseId(caseId);

        hepService.process(child);
        Mockito.verify(schedulerService).fulfillMilestone(caseId, MilestoneType.Hep1.toString(), hep1Date, scheduleName);
        Mockito.verify(careCaseTaskService).close(caseId, MilestoneType.Hep1.toString());
    }

    @Test
    public void shouldFulfillHep2IfHep2DatePresentInChild(){
        DateTime hep2Date = new DateTime();
        String caseId = "caseId";
        Child child = new Child();
        child.setHep2Date(hep2Date);
        child.setCaseId(caseId);

        hepService.process(child);
        Mockito.verify(schedulerService).fulfillMilestone(caseId, MilestoneType.Hep2.toString(), hep2Date, scheduleName);
        Mockito.verify(careCaseTaskService).close(caseId, MilestoneType.Hep2.toString());
    }

    @Test
    public void shouldFulfillHep3IfHep3DatePresentInChild(){
        DateTime hep3Date = new DateTime();
        String caseId = "caseId";
        Child child = new Child();
        child.setHep3Date(hep3Date);
        child.setCaseId(caseId);

        hepService.process(child);
        Mockito.verify(schedulerService).fulfillMilestone(caseId, MilestoneType.Hep3.toString(), hep3Date, scheduleName);
        Mockito.verify(careCaseTaskService).close(caseId, MilestoneType.Hep3.toString());
    }

    @Test
    public void shouldNotFulfillHep1OrHep2OrHep3IfNeitherOfTheTakenDatesArePresentInChild(){
        Child child = new Child();
        child.setCaseId("caseId");

        hepService.process(child);
        verify(schedulerService, never()).fulfillMilestone(any(String.class), any(String.class), any(DateTime.class), anyString());
        Mockito.verify(careCaseTaskService, never()).close(any(String.class), any(String.class));
    }


    @Test
    public void shouldUnenrollFromHepSchedule(){
        String caseId = "caseId";

        Mother mother = new Mother();
        mother.setCaseId(caseId);

        hepService.close(mother);
        Mockito.verify(schedulerService).unenroll(caseId, scheduleName);

    }

}
