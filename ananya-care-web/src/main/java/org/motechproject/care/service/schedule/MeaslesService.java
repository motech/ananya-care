package org.motechproject.care.service.schedule;

import org.motechproject.care.domain.Child;
import org.motechproject.care.domain.Client;
import org.motechproject.care.schedule.service.MeaslesSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;

public class MeaslesService extends VaccinationService{

    @Autowired
    protected MeaslesService(MeaslesSchedulerService schedulerService) {
        super(schedulerService);
    }

    @Override
    public void process(Client client) {
        Child child = (Child) client;
        MeaslesSchedulerService measlesSchedulerService = (MeaslesSchedulerService) schedulerService;
        if(child.getDOB() != null){
            measlesSchedulerService.enroll(child.getCaseId(), child.getDOB());
        }
        if(child.getMeaslesDate() != null){
            measlesSchedulerService.fulfilMeaslesTaken(child.getCaseId(), child.getMeaslesDate());
        }
    }
}
