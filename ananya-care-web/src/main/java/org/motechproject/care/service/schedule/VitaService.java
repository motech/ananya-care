package org.motechproject.care.service.schedule;

import org.motechproject.care.domain.Child;
import org.motechproject.care.domain.Client;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.service.ScheduleService;
import org.motechproject.care.schedule.vaccinations.ChildVaccinationSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VitaService extends VaccinationService{

    private final String scheduleName = ChildVaccinationSchedule.Vita.getName();

    @Autowired
    public VitaService(ScheduleService schedulerService) {
        super(schedulerService);
    }

    @Override
    public void process(Client client) {
        Child child = (Child) client;
        if(child.getDOB() != null){
            schedulerService.enroll(child.getCaseId(), child.getDOB(), scheduleName);
        }
        if(child.getVitamin1Date() != null){
            schedulerService.fulfillMileStone(child.getCaseId(), MilestoneType.VitaminA.toString(),  child.getVitamin1Date(), scheduleName);
        }
    }
}
