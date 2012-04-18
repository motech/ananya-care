package org.motechproject.care.schedule.service;

import org.motechproject.care.schedule.vaccinations.VaccinationSchedule;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MeaslesSchedulerService extends SchedulerService {
    
    public static String milestone = "Measles";

    @Autowired
    public MeaslesSchedulerService(ScheduleTrackingService trackingService) {
        super(trackingService, VaccinationSchedule.Measles.getName());
    }

}
