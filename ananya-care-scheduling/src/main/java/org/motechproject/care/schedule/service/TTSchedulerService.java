package org.motechproject.care.schedule.service;

import org.motechproject.care.schedule.vaccinations.MotherVaccinationSchedule;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TTSchedulerService extends SchedulerService{

    public static String tt1Milestone = "TT 1";

    @Autowired
    public TTSchedulerService(ScheduleTrackingService trackingService) {
        super(trackingService, MotherVaccinationSchedule.TT.getName());
    }
}
