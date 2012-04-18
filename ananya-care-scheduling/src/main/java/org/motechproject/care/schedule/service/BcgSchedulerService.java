package org.motechproject.care.schedule.service;

import org.motechproject.care.schedule.vaccinations.VaccinationSchedule;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BcgSchedulerService extends SchedulerService {
    public static String milestone = "Bcg";

    @Autowired
    public BcgSchedulerService(ScheduleTrackingService trackingService) {
        super(trackingService, VaccinationSchedule.Bcg.getName());
    }
}

