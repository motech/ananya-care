package org.motechproject.care.service.schedule;

import org.joda.time.DateTime;
import org.motechproject.care.domain.Client;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.domain.Window;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.service.ScheduleService;
import org.motechproject.care.schedule.vaccinations.MotherVaccinationSchedule;
import org.motechproject.care.service.CareCaseTaskService;
import org.motechproject.care.service.util.PeriodUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Anc4Service extends VaccinationService{

    PeriodUtil periodUtil;

    @Autowired
    public Anc4Service(ScheduleService schedulerService, CareCaseTaskService careCaseTaskService, PeriodUtil periodUtil) {
        super(schedulerService, MotherVaccinationSchedule.Anc4.getName(), careCaseTaskService);
        this.periodUtil = periodUtil;
    }

    @Override
    public void process(Client client) {
        Mother mother = (Mother) client;

        if(mother.getAnc3Date() != null && mother.getEdd() != null){
            Window anc4Window = getAnc4Window(mother.getAnc3Date(), mother.getEdd());
            if(anc4Window.isValid()) {
                DateTime referenceDate = anc4Window.getStart().plus(periodUtil.getScheduleOffset());
                schedulerService.enroll(mother.getCaseId(), referenceDate, scheduleName);
            }
        }
        if(mother.getAnc4Date() != null) {
            fulfillMilestone(mother.getCaseId(), MilestoneType.Anc4, mother.getAnc4Date());
        }
    }

    private Window getAnc4Window(DateTime anc3Date, DateTime edd) {
        DateTime nextPossibleAnc4Date = anc3Date.plusDays(30);
        DateTime trimesterStartDate = edd.minusDays(PeriodUtil.DAYS_IN_3RD_TRIMESTER);

        Window anc4Window = new Window(nextPossibleAnc4Date, edd);
        Window trimesterWindow = new Window(trimesterStartDate, edd);

        return anc4Window.resize(trimesterWindow);
    }
}