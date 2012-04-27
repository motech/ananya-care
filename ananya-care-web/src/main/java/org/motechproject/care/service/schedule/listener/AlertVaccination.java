package org.motechproject.care.service.schedule.listener;

import org.apache.log4j.Logger;
import org.motechproject.care.domain.CareCaseTask;
import org.motechproject.care.domain.Client;
import org.motechproject.care.domain.Window;
import org.motechproject.care.repository.AllCareCaseTasks;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.casexml.gateway.CommcareCaseGateway;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.util.DateUtil;

import java.util.Properties;
import java.util.UUID;

public abstract class AlertVaccination {
    private CommcareCaseGateway commcareCaseGateway;
    private AllCareCaseTasks allCareCaseTasks;
    private Properties ananyaCareProperties;
    Logger logger = Logger.getLogger(AlertVaccination.class);

    public AlertVaccination(CommcareCaseGateway commcareCaseGateway, AllCareCaseTasks allCareCaseTasks, Properties ananyaCareProperties) {
        this.commcareCaseGateway = commcareCaseGateway;
        this.allCareCaseTasks = allCareCaseTasks;
        this.ananyaCareProperties = ananyaCareProperties;
    }

    public void invoke(MotechEvent event){
        MilestoneEvent msEvent = new MilestoneEvent(event);
        String externalId = msEvent.getExternalId();
        MilestoneAlert milestoneAlert = msEvent.getMilestoneAlert();
        String milestoneName = milestoneAlert.getMilestoneName();

        process(new Window(milestoneAlert.getDueDateTime(), milestoneAlert.getLateDateTime()), externalId, milestoneName);
    }

    public abstract void process(Window alertWindow, String externalId, String milestoneName);

    protected void postToCommCare(Window alertWindow, String externalId, String milestoneName, Client client, String clientElementTag) {
        String commcareUrl = ananyaCareProperties.getProperty("commcare.hq.url");
        CareCaseTask careCasetask = createCaseTask(alertWindow, externalId, milestoneName, client, clientElementTag);
        allCareCaseTasks.add(careCasetask);
        logger.info(String.format("Notifying commcare for vaccination due with task_id: %s, client_id: %s, eligible_date: %s, expiry_date: %s ",
                careCasetask.getTaskId(), careCasetask.getClientCaseId(), careCasetask.getDateEligible(), careCasetask.getDateExpires()));
        commcareCaseGateway.submitCase(commcareUrl, careCasetask.toCaseTask());
    }

    private CareCaseTask createCaseTask(Window alertWindow, String externalId, String milestoneName, Client client, String clientElementTag) {
        String motechUserId = ananyaCareProperties.getProperty("motech.user.id");
        String currentTime = DateUtil.now().toString();
        String taskId = MilestoneType.forType(milestoneName).getTaskId();
        String caseId = UUID.randomUUID().toString();
        return new CareCaseTask(milestoneName, client.getGroupId(), caseId, motechUserId, currentTime, taskId, alertWindow.getStart().toString("yyyy-MM-dd"), alertWindow.getEnd().toString("yyyy-MM-dd"), client.getCaseType(), externalId, clientElementTag);
    }
}
