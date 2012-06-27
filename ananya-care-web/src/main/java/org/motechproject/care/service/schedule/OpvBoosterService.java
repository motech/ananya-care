package org.motechproject.care.service.schedule;

import org.joda.time.DateTime;
import org.motechproject.care.domain.Child;
import org.motechproject.care.domain.Client;
import org.motechproject.care.domain.Window;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.service.ScheduleService;
import org.motechproject.care.schedule.vaccinations.ChildVaccinationSchedule;
import org.motechproject.care.service.CareCaseTaskService;
import org.motechproject.care.service.util.PeriodUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpvBoosterService extends VaccinationService{

    private PeriodUtil periodUtil;

    @Autowired
    public OpvBoosterService(ScheduleService schedulerService, CareCaseTaskService careCaseTaskService, PeriodUtil periodUtil) {
        super(schedulerService, ChildVaccinationSchedule.OPVBooster.getName(), careCaseTaskService);
        this.periodUtil = periodUtil;
    }

    @Override
    public void process(Client client) {
        Child child = (Child) client;

        if(child.getOpv3Date() != null && child.getDOB() != null){
            Window opvBoosterWindow = getOPVBoosterWindow(child.getOpv3Date(), child.getDOB());
            if(opvBoosterWindow.isValid()) {
                DateTime referenceDate = opvBoosterWindow.getStart().plus(periodUtil.getScheduleOffset());
                schedulerService.enroll(child.getCaseId(), referenceDate, scheduleName);
            }
        }
        if(child.getOpvBoosterDate() != null) {
            fulfillMilestone(child.getCaseId(), MilestoneType.OPVBooster, child.getOpvBoosterDate());
        }
    }

    private Window getOPVBoosterWindow(DateTime opv3Date, DateTime dob) {
        DateTime nextPossibleOPVBoosterDate = opv3Date.plusDays(180);
        Window opvBoosterWindow = new Window(nextPossibleOPVBoosterDate, dob.plusMonths(24));

        Window last8MonthsWindow = new Window(dob.plusMonths(16), dob.plusMonths(24));

        return opvBoosterWindow.resize(last8MonthsWindow);
    }
}