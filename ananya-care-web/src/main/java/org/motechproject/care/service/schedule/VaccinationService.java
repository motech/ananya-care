package org.motechproject.care.service.schedule;

import org.joda.time.DateTime;
import org.motechproject.care.domain.Client;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.service.ScheduleService;
import org.motechproject.care.service.CareCaseTaskService;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;

public abstract class VaccinationService {

    protected ScheduleService schedulerService;
    protected String scheduleName;
    private CareCaseTaskService careCaseTaskService;

    public VaccinationService(ScheduleService schedulerService, String scheduleName, CareCaseTaskService careCaseTaskService) {
        this.schedulerService = schedulerService;
        this.scheduleName = scheduleName;
        this.careCaseTaskService = careCaseTaskService;
    }

    public abstract  void process(Client client);

    public void close(Client client) {
        EnrollmentRecord enrollmentRecord = schedulerService.unenroll(client.getCaseId(), scheduleName);
        if(enrollmentRecord == null)
            return;
        String currentMilestoneName = enrollmentRecord.getCurrentMilestoneName();
        careCaseTaskService.close(client.getCaseId(), currentMilestoneName);
    }

    protected void fulfillMilestone(String caseId, MilestoneType milestone, DateTime fulfillmentDate) {
        schedulerService.fulfillMilestone(caseId, milestone.toString(), fulfillmentDate, scheduleName);
        careCaseTaskService.close(caseId, milestone.toString());
    }
}
