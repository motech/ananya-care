package org.motechproject.care.service.schedule;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.service.ScheduleService;
import org.motechproject.care.schedule.vaccinations.MotherVaccinationSchedule;
import org.motechproject.care.service.CareCaseTaskService;
import org.motechproject.care.service.util.PeriodUtil;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class Anc4ServiceTest {
    @Mock
    private ScheduleService schedulerService;
    @Mock
    private CareCaseTaskService careCaseTaskService;
    @Mock
    private PeriodUtil periodUtil;

    private Anc4Service anc4Service;
    private String scheduleName = MotherVaccinationSchedule.Anc4.getName();
    private final Period periodOffset = Period.weeks(-2);

    @Before
    public void setUp(){
        anc4Service = new Anc4Service(schedulerService, careCaseTaskService, periodUtil);
        when(periodUtil.getScheduleOffset()).thenReturn(periodOffset);
    }

    @Test
    public void shouldEnrollMotherForAnc4ScheduleWhenAnc3TakenInTrimester(){
        DateTime edd = new DateTime();
        DateTime anc3Taken = edd.minusMonths(2);
        String caseId = "caseId";
        Mother mother = new Mother();
        mother.setEdd(edd);
        mother.setAnc3Date(anc3Taken);
        mother.setCaseId(caseId);

        anc4Service.process(mother);
        Mockito.verify(schedulerService).enroll(caseId, anc3Taken.plusDays(30).plus(periodOffset), scheduleName);
    }

    @Test
    public void shouldEnrollMotherForAnc4ScheduleWhenAnc3TakenMoreThan30DaysBeforeTrimester(){
        DateTime edd = new DateTime();
        DateTime anc3Taken = edd.minusMonths(5);
        String caseId = "caseId";
        Mother mother = new Mother();
        mother.setEdd(edd);
        mother.setAnc3Date(anc3Taken);
        mother.setCaseId(caseId);

        anc4Service.process(mother);
        Mockito.verify(schedulerService).enroll(caseId, edd.minusDays(PeriodUtil.DAYS_IN_3RD_TRIMESTER).plus(periodOffset), scheduleName);
    }

    @Test
    public void shouldEnrollMotherForAnc4ScheduleWhenAnc3TakenLessThan30DaysBeforeTrimester(){
        DateTime edd = new DateTime();
        DateTime anc3Taken = edd.minusMonths(3).minusDays(12);
        String caseId = "caseId";
        Mother mother = new Mother();
        mother.setEdd(edd);
        mother.setAnc3Date(anc3Taken);
        mother.setCaseId(caseId);

        anc4Service.process(mother);
        Mockito.verify(schedulerService).enroll(caseId, anc3Taken.plusDays(30).plus(periodOffset), scheduleName);
    }

    @Test
    public void shouldNotEnrollMotherForAnc4ScheduleWhenAnc3NotTaken(){
        DateTime edd = new DateTime();
        String caseId = "caseId";
        Mother mother = new Mother();
        mother.setEdd(edd);
        mother.setAnc3Date(null);
        mother.setCaseId(caseId);

        anc4Service.process(mother);
        Mockito.verify(schedulerService, never()).enroll(anyString(), any(DateTime.class), anyString());
    }

    @Test
    public void shouldNotEnrollMotherForAnc4ScheduleWhenAnc3IsTakenInTheLastMonthOfPregnancy(){
        DateTime edd = new DateTime();
        DateTime anc3Taken = edd.minusDays(10);
        String caseId = "caseId";
        Mother mother = new Mother();
        mother.setEdd(edd);
        mother.setAnc3Date(anc3Taken);
        mother.setCaseId(caseId);

        anc4Service.process(mother);
        Mockito.verify(schedulerService, never()).enroll(anyString(), any(DateTime.class), anyString());
    }

    @Test
    public void shouldFulfillAnc4IfAnc4DatePresentInMother(){
        DateTime anc4Date = new DateTime();
        String caseId = "caseId";
        Mother mother = new Mother();
        mother.setAnc4Date(anc4Date);
        mother.setCaseId(caseId);

        anc4Service.process(mother);
        Mockito.verify(schedulerService).fulfillMilestone(caseId, MilestoneType.Anc4.toString(), anc4Date, scheduleName);
        Mockito.verify(careCaseTaskService).close(caseId, MilestoneType.Anc4.toString());
    }

    @Test
    public void shouldUnenrollFromAnc4Schedule(){
        String caseId = "caseId";
        Mother mother = new Mother();
        mother.setCaseId(caseId);
        anc4Service.close(mother);
        Mockito.verify(schedulerService).unenroll(caseId,scheduleName);
    }
}
