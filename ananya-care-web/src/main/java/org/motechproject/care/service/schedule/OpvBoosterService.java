package org.motechproject.care.service.schedule;

import org.joda.time.DateTime;
import org.motechproject.care.domain.Child;
import org.motechproject.care.domain.Client;
import org.motechproject.care.domain.Window;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.service.ScheduleService;
import org.motechproject.care.schedule.vaccinations.ChildVaccinationSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpvBoosterService extends VaccinationService{

    private final String scheduleName = ChildVaccinationSchedule.OPVBooster.getName();

    @Autowired
    public OpvBoosterService(ScheduleService schedulerService) {
        super(schedulerService);
    }

    @Override
    public void process(Client client) {
        Child child = (Child) client;

        if(child.getOpv3Date() != null && child.getDOB() != null){
            Window opvBoosterWindow = getOPVBoosterWindow(child.getOpv3Date(), child.getDOB());
            if(opvBoosterWindow.isValid()) {
                DateTime referenceDate = opvBoosterWindow.getStart().minusWeeks(2);
                schedulerService.enroll(child.getCaseId(), referenceDate, scheduleName);
            }
        }
        if(child.getOpvBoosterDate() != null) {
            schedulerService.fulfillMileStone(child.getCaseId(), MilestoneType.OPVBooster.toString(),  child.getOpvBoosterDate(), scheduleName);
        }
    }

    private Window getOPVBoosterWindow(DateTime opv3Date, DateTime dob) {
        DateTime nextPossibleOPVBoosterDate = opv3Date.plusDays(180);
        Window opvBoosterWindow = new Window(nextPossibleOPVBoosterDate, dob.plusMonths(24));

        Window last8MonthsWindow = new Window(dob.plusMonths(16), dob.plusMonths(24));

        return opvBoosterWindow.resize(last8MonthsWindow);
    }
}
