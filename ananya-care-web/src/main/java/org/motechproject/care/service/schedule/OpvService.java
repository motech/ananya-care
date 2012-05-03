package org.motechproject.care.service.schedule;

import org.motechproject.care.domain.Child;
import org.motechproject.care.domain.Client;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.service.ScheduleService;
import org.motechproject.care.schedule.vaccinations.ChildVaccinationSchedule;
import org.motechproject.care.service.CareCaseTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpvService extends VaccinationService{

    @Autowired
    public OpvService(ScheduleService schedulerService, CareCaseTaskService careCaseTaskService) {
        super(schedulerService, ChildVaccinationSchedule.OPV.getName(), careCaseTaskService);
    }

    @Override
    public void process(Client client) {
        Child child = (Child) client;
        if(child.getDOB() != null)
            schedulerService.enroll(child.getCaseId(), child.getDOB(), scheduleName);
        if(child.getOpv1Date()!=null)
            schedulerService.fulfillMileStone(child.getCaseId(), MilestoneType.OPV1.toString(),child.getOpv1Date(),scheduleName);
        if(child.getOpv2Date()!=null)
            schedulerService.fulfillMileStone(child.getCaseId(), MilestoneType.OPV2.toString(),child.getOpv2Date(),scheduleName);
        if(child.getOpv3Date()!=null)
            schedulerService.fulfillMileStone(child.getCaseId(), MilestoneType.OPV3.toString(),child.getOpv3Date(),scheduleName);
    }
}
