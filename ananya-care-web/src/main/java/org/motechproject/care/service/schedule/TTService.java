package org.motechproject.care.service.schedule;

import org.motechproject.care.domain.Client;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.service.ScheduleService;
import org.motechproject.care.schedule.vaccinations.MotherVaccinationSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TTService extends VaccinationService{

    private final String scheduleName = MotherVaccinationSchedule.TT.getName();

    @Autowired
    public TTService(ScheduleService schedulerService) {
        super(schedulerService);
    }

    public TTService(){}

    @Override
    public void process(Client client) {
        Mother mother = (Mother) client;
        if(mother.getEdd() != null){
            schedulerService.enroll(mother.getCaseId(), mother.getEdd().plusMonths(-9), scheduleName);
        }
        if(mother.getTt1Date() != null){
            schedulerService.fulfillMileStone(mother.getCaseId(), MilestoneType.TT1.toString(),  mother.getTt1Date(), scheduleName);
        }
    }
}
