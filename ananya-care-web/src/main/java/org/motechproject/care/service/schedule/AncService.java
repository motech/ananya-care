package org.motechproject.care.service.schedule;

import org.motechproject.care.domain.Client;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.service.ScheduleService;
import org.motechproject.care.schedule.vaccinations.MotherVaccinationSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AncService extends VaccinationService{

    private final String scheduleName = MotherVaccinationSchedule.Anc.getName();

    @Autowired
    public AncService(ScheduleService schedulerService) {
        super(schedulerService);
    }

    @Override
    public void process(Client client) {
        Mother mother = (Mother) client;
        if(mother.getEdd() != null){
            schedulerService.enroll(mother.getCaseId(), mother.getEdd().minusMonths(9), scheduleName);
        }
        if(mother.getAnc1Date() != null) {
            schedulerService.fulfillMileStone(mother.getCaseId(), MilestoneType.Anc1.toString(),  mother.getAnc1Date(), scheduleName);
        }
        if(mother.getAnc2Date() != null) {
            schedulerService.fulfillMileStone(mother.getCaseId(), MilestoneType.Anc2.toString(),  mother.getAnc2Date(), scheduleName);
        }
        if(mother.getAnc3Date() != null) {
            schedulerService.fulfillMileStone(mother.getCaseId(), MilestoneType.Anc3.toString(),  mother.getAnc3Date(), scheduleName);
        }
    }
}
