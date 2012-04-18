package org.motechproject.care.service.schedule;

import org.motechproject.care.domain.Client;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.schedule.service.TTSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;

public class TTService extends VaccinationService{

    @Autowired
    protected TTService(TTSchedulerService schedulerService) {
        super(schedulerService);
    }

    @Override
    public void process(Client client) {
        Mother mother = (Mother) client;
        if(mother.getEdd() != null){
            schedulerService.enroll(mother.getCaseId(), mother.getEdd().plusMonths(-9));
        }
        if(mother.getTt1Date() != null){
            schedulerService.fulfillMileStone(mother.getCaseId(), TTSchedulerService.tt1Milestone,  mother.getTt1Date());
        }
    }
}
