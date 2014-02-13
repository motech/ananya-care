package org.motechproject.care.service.router.action;

import org.apache.log4j.Logger;
import org.motechproject.care.domain.CareCaseTask;
import org.motechproject.care.domain.Client;
import org.motechproject.care.domain.Window;
import org.motechproject.care.repository.AllCareCaseTasks;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.gateway.CommcareCaseGateway;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.commons.date.util.DateUtil;

import java.util.Properties;
import java.util.UUID;

public abstract class AlertClientAction {
    private CommcareCaseGateway commcareCaseGateway;
    private AllCareCaseTasks allCareCaseTasks;
    private Properties ananyaCareProperties;
    Logger logger = Logger.getLogger(AlertClientAction.class);

    public AlertClientAction(CommcareCaseGateway commcareCaseGateway, AllCareCaseTasks allCareCaseTasks, Properties ananyaCareProperties) {
        this.commcareCaseGateway = commcareCaseGateway;
        this.allCareCaseTasks = allCareCaseTasks;
        this.ananyaCareProperties = ananyaCareProperties;
    }

    public void invoke(MilestoneEvent event){
        String externalId = event.getExternalId();
        MilestoneAlert milestoneAlert = event.getMilestoneAlert();
        String milestoneName = milestoneAlert.getMilestoneName();

        process(new Window(milestoneAlert.getDueDateTime(), milestoneAlert.getLateDateTime()), externalId, milestoneName);
    }

    public abstract void process(Window alertWindow, String externalId, String milestoneName);

    protected void postToCommCare(Window alertWindow, String externalId, String milestoneName, Client client) {
        String commcareUrl = ananyaCareProperties.getProperty("commcare.hq.url");
        CareCaseTask careCaseTask = createCaseTask(alertWindow, externalId, milestoneName, client);
        allCareCaseTasks.add(careCaseTask);
        logger.info(String.format("Notifying commcare -- TaskId: %s, ExternalId: %s, EligibleDate: %s, ExpiryDate: %s ",
                careCaseTask.getTaskId(), careCaseTask.getClientCaseId(), careCaseTask.getDateEligible(), careCaseTask.getDateExpires()));
        commcareCaseGateway.submitCase(commcareUrl, careCaseTask.toCaseTask());
    }

    private CareCaseTask createCaseTask(Window alertWindow, String externalId, String milestoneName, Client client) {
        String motechUserId = ananyaCareProperties.getProperty("motech.user.id");
        String currentTime = DateUtil.now().toString();
        String taskId = MilestoneType.forType(milestoneName).getTaskId();
        String caseId = UUID.randomUUID().toString();
        return new CareCaseTask(milestoneName, client.getGroupId(), caseId, motechUserId, currentTime, taskId, alertWindow.getStart().toString("yyyy-MM-dd"), alertWindow.getEnd().toString("yyyy-MM-dd"), client.getCaseType(), externalId);
    }
}
