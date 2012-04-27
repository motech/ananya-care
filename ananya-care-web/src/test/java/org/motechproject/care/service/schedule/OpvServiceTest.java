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
public class OpvServiceTest {
    @Mock
    private ScheduleService schedulerService;
    OpvService opvService;
    private String scheduleName = ChildVaccinationSchedule.OPV.getName();

    @Before
    public void setUp(){
        opvService = new OpvService(schedulerService);
    }

    @Test
    public void shouldEnrollChildForOPVSchedule(){
        DateTime dob = new DateTime();
        String caseId = "caseId";
        Child child = new Child();
        child.setDOB(dob);
        child.setCaseId(caseId);

        opvService.process(child);
        Mockito.verify(schedulerService).enroll(caseId, dob, scheduleName);
    }

    @Test
    public void shouldNotEnrollChildForOPVScheduleWhenDOBIsNull(){
        Child child = new Child();
        child.setCaseId("caseId");

        opvService.process(child);
        verify(schedulerService, never()).enroll(any(String.class), any(DateTime.class), anyString());
    }

    @Test
    public void shouldFulfillOPV1IfOPV1DatePresentInChild(){
        DateTime OPV1Date = new DateTime();
        String caseId = "caseId";
        Child child = new Child();
        child.setOpv1Date(OPV1Date);
        child.setCaseId(caseId);

        opvService.process(child);
        Mockito.verify(schedulerService).fulfillMileStone(caseId, MilestoneType.OPV1.toString(),  OPV1Date, scheduleName);
    }

    @Test
    public void shouldFulfillOPV2IfOPV2DatePresentInChild(){
        DateTime OPV2Date = new DateTime();
        String caseId = "caseId";
        Child child = new Child();
        child.setOpv2Date(OPV2Date);
        child.setCaseId(caseId);

        opvService.process(child);
        Mockito.verify(schedulerService).fulfillMileStone(caseId, MilestoneType.OPV2.toString(),  OPV2Date, scheduleName);
    }

    @Test
    public void shouldFulfillOPV3IfOPV3DatePresentInChild(){
        DateTime OPV3Date = new DateTime();
        String caseId = "caseId";
        Child child = new Child();
        child.setOpv3Date(OPV3Date);
        child.setCaseId(caseId);

        opvService.process(child);
        Mockito.verify(schedulerService).fulfillMileStone(caseId, MilestoneType.OPV3.toString(),  OPV3Date, scheduleName);
    }

    @Test
    public void shouldNotFulfillOPV1OrOPV2OrOPV3IfNeitherOfTheTakenDatesArePresentInChild(){
        Child child = new Child();
        child.setCaseId("caseId");

        opvService.process(child);
        verify(schedulerService, never()).fulfillMileStone(any(String.class), any(String.class), any(DateTime.class), anyString());
    }

}
