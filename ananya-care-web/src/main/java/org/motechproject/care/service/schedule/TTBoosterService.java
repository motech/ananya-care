package org.motechproject.care.service.schedule;

import org.motechproject.care.domain.Client;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.service.ScheduleService;
import org.motechproject.care.schedule.vaccinations.MotherVaccinationSchedule;
import org.motechproject.care.service.util.PeriodUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TTBoosterService extends VaccinationService{

    private final String scheduleName = MotherVaccinationSchedule.TTBooster.getName();

    @Autowired
    public TTBoosterService(ScheduleService schedulerService) {
        super(schedulerService);
    }

    public TTBoosterService(){}

    @Override
    public void process(Client client) {
        Mother mother = (Mother) client;
        if(mother.getEdd() != null && mother.isLastPregTt()){
            schedulerService.enroll(mother.getCaseId(), mother.getEdd().minusDays(PeriodUtil.DAYS_IN_9_MONTHS), scheduleName);
        }
        if(mother.getTtBoosterDate() != null){
            schedulerService.fulfillMileStone(mother.getCaseId(), MilestoneType.TTBooster.toString(),  mother.getTtBoosterDate(), scheduleName);
        }
    }
}
