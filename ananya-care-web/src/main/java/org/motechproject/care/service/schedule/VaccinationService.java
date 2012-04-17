package org.motechproject.care.service.schedule;

import org.motechproject.care.domain.Client;
import org.motechproject.care.schedule.service.SchedulerService;

public abstract class VaccinationService {

    protected SchedulerService schedulerService;

    protected VaccinationService(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public abstract void process(Client client);
}
