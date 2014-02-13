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
public class Opv0Service extends VaccinationService{

    @Autowired
    public Opv0Service(ScheduleService schedulerService, CareCaseTaskService careCaseTaskService) {
        super(schedulerService, ChildVaccinationSchedule.OPV0.getName(), careCaseTaskService);
    }

    @Override
    public void process(Client client) {
        Child child = (Child) client;
        if(child.getDOB() != null)
            schedulerService.enroll(child.getCaseId(), child.getDOB(), scheduleName);
        if(child.getOpv0Date()!=null)
            fulfillMilestone(child.getCaseId(), MilestoneType.OPV0, child.getOpv0Date());
    }
}
