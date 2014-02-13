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
public class HepService extends VaccinationService{

    @Autowired
    public HepService(ScheduleService schedulerService, CareCaseTaskService careCaseTaskService) {
        super(schedulerService, ChildVaccinationSchedule.Hepatitis.getName(), careCaseTaskService);
    }

    @Override
    public void process(Client client) {
        Child child = (Child) client;
        if(child.getDOB() != null)
            schedulerService.enroll(child.getCaseId(), child.getDOB(), scheduleName);
        if(child.getHep1Date()!=null)
            fulfillMilestone(child.getCaseId(), MilestoneType.Hep1, child.getHep1Date());
        if(child.getHep2Date()!=null)
            fulfillMilestone(child.getCaseId(), MilestoneType.Hep2, child.getHep2Date());
        if(child.getHep3Date()!=null)
            fulfillMilestone(child.getCaseId(), MilestoneType.Hep3, child.getHep3Date());
    }
}
