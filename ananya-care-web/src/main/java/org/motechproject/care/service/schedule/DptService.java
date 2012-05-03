package org.motechproject.care.service.schedule;

import org.motechproject.care.domain.Child;
import org.motechproject.care.domain.Client;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.service.ScheduleService;
import org.motechproject.care.schedule.vaccinations.ChildVaccinationSchedule;
import org.motechproject.care.service.CareCaseTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DptService extends VaccinationService{

    @Autowired
    public DptService(ScheduleService schedulerService, CareCaseTaskService careCaseTaskService) {
        super(schedulerService, ChildVaccinationSchedule.DPT.getName(), careCaseTaskService);
    }

    @Override
    public void process(Client client) {
        Child child = (Child) client;
        if(child.getDOB() != null)
            schedulerService.enroll(child.getCaseId(), child.getDOB(), scheduleName);
        if(child.getDpt1Date()!=null)
            schedulerService.fulfillMileStone(child.getCaseId(), MilestoneType.DPT1.toString(),child.getDpt1Date(),scheduleName);
        if(child.getDpt2Date()!=null)
            schedulerService.fulfillMileStone(child.getCaseId(), MilestoneType.DPT2.toString(),child.getDpt2Date(),scheduleName);
        if(child.getDpt3Date()!=null)
            schedulerService.fulfillMileStone(child.getCaseId(), MilestoneType.DPT3.toString(),child.getDpt3Date(),scheduleName);
        if(child.getDptBoosterDate()!=null)
            schedulerService.fulfillMileStone(child.getCaseId(), MilestoneType.DPTBooster.toString(),child.getDptBoosterDate(),scheduleName);
    }
}
