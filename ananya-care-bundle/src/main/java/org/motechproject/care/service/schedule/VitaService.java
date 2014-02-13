package org.motechproject.care.service.schedule;

import org.motechproject.care.domain.Child;
import org.motechproject.care.domain.Client;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.service.ScheduleService;
import org.motechproject.care.schedule.vaccinations.ChildVaccinationSchedule;
import org.motechproject.care.service.CareCaseTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VitaService extends VaccinationService{


    @Autowired
    public VitaService(ScheduleService schedulerService, CareCaseTaskService careCaseTaskService) {
        super(schedulerService, ChildVaccinationSchedule.Vita.getName(), careCaseTaskService);
    }

    @Override
    public void process(Client client) {
        Child child = (Child) client;
        if(child.getDOB() != null){
            schedulerService.enroll(child.getCaseId(), child.getDOB(), scheduleName);
        }
        if(child.getVitamin1Date() != null){
            fulfillMilestone(child.getCaseId(), MilestoneType.VitaminA, child.getVitamin1Date());
        }
    }
}
