package org.motechproject.care.service.schedule;

import org.motechproject.care.domain.Client;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.schedule.service.ScheduleService;
import org.motechproject.care.schedule.vaccinations.MotherVaccinationSchedule;
import org.motechproject.care.service.util.PeriodUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MotherCareService extends VaccinationService{

    private final String scheduleName = MotherVaccinationSchedule.MotherCare.getName();

    @Autowired
    public MotherCareService(ScheduleService schedulerService) {
        super(schedulerService);
    }

    @Override
    public void process(Client client) {
        Mother mother = (Mother) client;
        if(mother.getEdd() != null){
            schedulerService.enroll(mother.getCaseId(), mother.getEdd().minusDays(PeriodUtil.DAYS_IN_9_MONTHS), scheduleName);
        }
    }
}
