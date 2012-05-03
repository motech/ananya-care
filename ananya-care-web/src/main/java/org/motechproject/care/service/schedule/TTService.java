package org.motechproject.care.service.schedule;

import org.motechproject.care.domain.Client;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.service.ScheduleService;
import org.motechproject.care.schedule.vaccinations.MotherVaccinationSchedule;
import org.motechproject.care.service.CareCaseTaskService;
import org.motechproject.care.service.util.PeriodUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TTService extends VaccinationService{

    @Autowired
    public TTService(ScheduleService schedulerService, CareCaseTaskService careCaseTaskService) {
        super(schedulerService, MotherVaccinationSchedule.TT.getName(), careCaseTaskService);
    }

    @Override
    public void process(Client client) {
        Mother mother = (Mother) client;
        if(mother.getEdd() != null && isNotEligibleForBooster(mother)){
            schedulerService.enroll(mother.getCaseId(), mother.getEdd().minusDays(PeriodUtil.DAYS_IN_9_MONTHS), scheduleName);
        }
        if(mother.getTt1Date() != null){
            schedulerService.fulfillMileStone(mother.getCaseId(), MilestoneType.TT1.toString(),  mother.getTt1Date(), scheduleName);
        }
        if(mother.getTt2Date() != null){
            schedulerService.fulfillMileStone(mother.getCaseId(), MilestoneType.TT2.toString(),  mother.getTt2Date(), scheduleName);
        }
    }

    private boolean isNotEligibleForBooster(Mother mother) {
        return !mother.isLastPregTt();
    }
}
