package org.motechproject.care.service.schedule.listener;

import org.joda.time.DateTime;
import org.motechproject.care.domain.CareCaseTask;
import org.motechproject.care.repository.AllCareCaseTasks;
import org.motechproject.care.service.util.TaskIdMapper;
import org.motechproject.casexml.gateway.CommcareCaseGateway;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.util.DateUtil;

import java.util.Properties;

public abstract class AlertVaccination {
    private CommcareCaseGateway commcareCaseGateway;
    private AllCareCaseTasks allCareCaseTasks;
    private Properties ananyaCareProperties;
    private static TaskIdMapper taskIdMapper = new TaskIdMapper();
    protected String externalId;
    protected String milestoneName;

    public AlertVaccination(CommcareCaseGateway commcareCaseGateway, AllCareCaseTasks allCareCaseTasks, Properties ananyaCareProperties) {
        this.commcareCaseGateway = commcareCaseGateway;
        this.allCareCaseTasks = allCareCaseTasks;
        this.ananyaCareProperties = ananyaCareProperties;
    }

    public void  invoke(MotechEvent event){
        MilestoneEvent msEvent = new MilestoneEvent(event);
        externalId = msEvent.getExternalId();
        MilestoneAlert milestoneAlert = msEvent.getMilestoneAlert();
        milestoneName = milestoneAlert.getMilestoneName();
        process(milestoneAlert.getDueDateTime(), milestoneAlert.getLateDateTime());
    }
    
    public abstract void process(DateTime dueDateTime, DateTime lateDateTime);

    protected void postToCommCare(DateTime dateEligible, DateTime dateExpires,String ownerId, String clientCaseType ) {
        String commcareUrl = ananyaCareProperties.getProperty("commcare.hq.url");
        CareCaseTask careCasetask = createCaseTask(dateEligible.toString("yyyy-MM-dd"), dateExpires.toString("yyyy-MM-dd"), ownerId, clientCaseType);
        allCareCaseTasks.add(careCasetask);
        commcareCaseGateway.submitCase(commcareUrl, careCasetask.toCaseTask());
    }

    private CareCaseTask createCaseTask(String dateEligible, String dateExpires, String ownerId, String caseType) {
        String userId = ananyaCareProperties.getProperty("motech.user.id");
        String currentTime = DateUtil.now().toString();
        String taskId = taskIdMapper.getTaskId(milestoneName);
        return new CareCaseTask(milestoneName, ownerId, externalId, userId, currentTime, taskId, dateEligible, dateExpires, caseType, externalId);
    }
}
