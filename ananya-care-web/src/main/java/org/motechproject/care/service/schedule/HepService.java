package org.motechproject.care.service.schedule;

import org.motechproject.care.domain.Child;
import org.motechproject.care.domain.Client;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.service.ScheduleService;
import org.motechproject.care.schedule.vaccinations.ChildVaccinationSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HepService extends VaccinationService{

    private final String scheduleName = ChildVaccinationSchedule.Hepatitis.getName();

    @Autowired
    public HepService(ScheduleService schedulerService) {
        super(schedulerService);
    }

    @Override
    public void process(Client client) {
        Child child = (Child) client;
        if(child.getDOB() != null)
            schedulerService.enroll(child.getCaseId(), child.getDOB(), scheduleName);
        if(child.getHep1Date()!=null)
            schedulerService.fulfillMileStone(child.getCaseId(), MilestoneType.Hep1.toString(),child.getHep1Date(),scheduleName);
        if(child.getHep2Date()!=null)
            schedulerService.fulfillMileStone(child.getCaseId(), MilestoneType.Hep2.toString(),child.getHep2Date(),scheduleName);
        if(child.getHep3Date()!=null)
            schedulerService.fulfillMileStone(child.getCaseId(), MilestoneType.Hep3.toString(),child.getHep3Date(),scheduleName);
    }
}
