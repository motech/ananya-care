package org.motechproject.care.service.schedule;

import org.motechproject.care.domain.Child;
import org.motechproject.care.domain.Client;
import org.motechproject.care.schedule.service.BcgSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;

public class BcgService extends VaccinationService{

    @Autowired
    protected BcgService(BcgSchedulerService schedulerService) {
        super(schedulerService);
    }

    @Override
    public void process(Client client) {
        Child child = (Child) client;
        if(child.getDOB() != null){
            schedulerService.enroll(child.getCaseId(), child.getDOB());
        }
        if(child.getBcgDate() != null){
            schedulerService.fulfillMileStone(child.getCaseId(), BcgSchedulerService.milestone,  child.getBcgDate());
        }
    }
}
