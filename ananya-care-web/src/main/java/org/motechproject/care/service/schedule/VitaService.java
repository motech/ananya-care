package org.motechproject.care.service.schedule;

import org.motechproject.care.domain.Child;
import org.motechproject.care.domain.Client;
import org.motechproject.care.schedule.service.VitaSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;

public class VitaService extends VaccinationService{

    @Autowired
    protected VitaService(VitaSchedulerService schedulerService) {
        super(schedulerService);
    }

    @Override
    public void process(Client client) {
        Child child = (Child) client;
        if(child.getDOB() != null){
            schedulerService.enroll(child.getCaseId(), child.getDOB());
        }
        if(child.getVitamin1Date() != null){
            schedulerService.fulfillMileStone(child.getCaseId(), VitaSchedulerService.milestone,  child.getVitamin1Date());
        }
    }
}
