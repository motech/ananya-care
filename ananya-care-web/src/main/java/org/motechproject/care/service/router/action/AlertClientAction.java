package org.motechproject.care.service.router.action;

import java.util.Properties;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.motechproject.care.domain.CareCaseTask;
import org.motechproject.care.domain.Client;
import org.motechproject.care.domain.Window;
import org.motechproject.care.repository.AllCareCaseTasks;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.casexml.domain.CaseTask;
import org.motechproject.casexml.gateway.CommcareCaseGateway;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.util.DateUtil;

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
        int retryCount = Integer.parseInt(ananyaCareProperties.getProperty("commcare.case.retry.count", "5"));
        CareCaseTask careCaseTask = createCaseTask(alertWindow, externalId, milestoneName, client);
        allCareCaseTasks.add(careCaseTask);
        logger.info(String.format("Notifying commcare -- TaskId: %s, ExternalId: %s, EligibleDate: %s, ExpiryDate: %s ",
                careCaseTask.getTaskId(), careCaseTask.getClientCaseId(), careCaseTask.getDateEligible(), careCaseTask.getDateExpires()));
        postToCommCareWithRetry(commcareUrl, careCaseTask.toCaseTask(), retryCount);
    }

    private CareCaseTask createCaseTask(Window alertWindow, String externalId, String milestoneName, Client client) {
        String motechUserId = ananyaCareProperties.getProperty("motech.user.id");
        String currentTime = DateUtil.now().toString();
        String taskId = MilestoneType.forType(milestoneName).getTaskId();
        String caseId = UUID.randomUUID().toString();
        return new CareCaseTask(milestoneName, client.getGroupId(), caseId, motechUserId, currentTime, taskId, alertWindow.getStart().toString("yyyy-MM-dd"), alertWindow.getEnd().toString("yyyy-MM-dd"), client.getCaseType(), externalId);
    }
    
    private void postToCommCareWithRetry(String commcareUrl, CaseTask caseTask, int retryCount){
    	try {
    		commcareCaseGateway.submitCase(commcareUrl, caseTask);
    	}catch(Exception e){
    		if (retryCount != 0){
    			retryCount--;
    			try {
					wait(10000);
				} catch (InterruptedException e1) {
					logger.error(e1.getMessage());
				}
    			postToCommCareWithRetry(commcareUrl, caseTask, retryCount);
    		}else {
    			logger.error(String.format("Submit Case Request to Commcare failed for the CaseId: %s\n", caseTask.getCaseId()), e);
    		}
    	}
    }
}
