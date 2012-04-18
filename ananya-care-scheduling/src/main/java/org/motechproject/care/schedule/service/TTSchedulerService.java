package org.motechproject.care.schedule.service;

import org.motechproject.care.schedule.vaccinations.VaccinationSchedule;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TTSchedulerService extends SchedulerService{

    public static String tt1Milestone = "TT1";

    @Autowired
    public TTSchedulerService(ScheduleTrackingService trackingService) {
        super(trackingService, VaccinationSchedule.TT.getName());
    }
}
