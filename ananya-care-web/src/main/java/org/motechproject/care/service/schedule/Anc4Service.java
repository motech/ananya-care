package org.motechproject.care.service.schedule;

import org.joda.time.DateTime;
import org.motechproject.care.domain.Client;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.domain.Window;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.service.ScheduleService;
import org.motechproject.care.schedule.vaccinations.MotherVaccinationSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Anc4Service extends VaccinationService{

    private final String scheduleName = MotherVaccinationSchedule.Anc4.getName();

    @Autowired
    public Anc4Service(ScheduleService schedulerService) {
        super(schedulerService);
    }

    @Override
    public void process(Client client) {
        Mother mother = (Mother) client;

        if(mother.getAnc3Date() != null && mother.getEdd() != null){
            Window anc4Window = getAnc4Window(mother.getAnc3Date(), mother.getEdd());
            if(anc4Window.isValid()) {
                DateTime referenceDate = anc4Window.getStart().minusWeeks(2);
                schedulerService.enroll(mother.getCaseId(), referenceDate, scheduleName);
            }
        }
        if(mother.getAnc4Date() != null) {
            schedulerService.fulfillMileStone(mother.getCaseId(), MilestoneType.Anc4.toString(),  mother.getAnc4Date(), scheduleName);
        }
    }

    private Window getAnc4Window(DateTime anc3Date, DateTime edd) {
        DateTime nextPossibleAnc4Date = anc3Date.plusDays(30);
        DateTime trimesterStartDate = edd.minusMonths(3);

        Window anc4Window = new Window(nextPossibleAnc4Date, edd);
        Window trimesterWindow = new Window(trimesterStartDate, edd);

        return anc4Window.resize(trimesterWindow);
    }
}
