package org.motechproject.care.service.schedule;

import org.motechproject.care.domain.Client;
import org.motechproject.care.schedule.service.ScheduleService;

public abstract class VaccinationService {

    protected ScheduleService schedulerService;

    protected VaccinationService() {
    }

    public VaccinationService(ScheduleService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public abstract void process(Client client);
}
