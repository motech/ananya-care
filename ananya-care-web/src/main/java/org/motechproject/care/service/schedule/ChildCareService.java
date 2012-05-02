package org.motechproject.care.service.schedule;

import org.motechproject.care.domain.Child;
import org.motechproject.care.domain.Client;
import org.motechproject.care.schedule.service.ScheduleService;
import org.motechproject.care.schedule.vaccinations.ChildVaccinationSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChildCareService extends VaccinationService{

    private final String scheduleName = ChildVaccinationSchedule.ChildCare.getName();

    @Autowired
    public ChildCareService(ScheduleService schedulerService) {
        super(schedulerService);
    }

    @Override
    public void process(Client client) {
        Child child = (Child) client;
        if(child.getDOB() != null){
            schedulerService.enroll(child.getCaseId(), child.getDOB(), scheduleName);
        }
    }
}
