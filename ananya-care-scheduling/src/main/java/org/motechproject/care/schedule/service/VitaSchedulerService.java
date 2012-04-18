package org.motechproject.care.schedule.service;

import org.motechproject.care.schedule.vaccinations.ChildVaccinationSchedule;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VitaSchedulerService extends SchedulerService {
    public static String milestone = "Vita";

    @Autowired
    public VitaSchedulerService(ScheduleTrackingService trackingService) {
        super(trackingService, ChildVaccinationSchedule.Vita.getName());
    }
}
