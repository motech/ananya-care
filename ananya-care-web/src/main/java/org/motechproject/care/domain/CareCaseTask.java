package org.motechproject.care.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.casexml.domain.CaseTask;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type == 'CareCaseTask'")
public class CareCaseTask extends MotechBaseDataObject{

    @JsonProperty
    private String caseType = "task";
    @JsonProperty
    private String caseName;
    @JsonProperty
    private String ownerId;
    @JsonProperty
    private String caseId;
    @JsonProperty
    private String userId;
    @JsonProperty
    private String currentTime;
    @JsonProperty
    private String taskId;
    @JsonProperty
    private String dateEligible;
    @JsonProperty
    private String dateExpires;
    @JsonProperty
    private String clientCaseType;
    @JsonProperty
    private String clientCaseId;

    public CareCaseTask(String caseName, String ownerId, String caseId, String userId, String currentTime, String taskId, String dateEligible, String dateExpires, String clientCaseType, String clientCaseId) {
        this.caseName = caseName;
        this.ownerId = ownerId;
        this.caseId = caseId;
        this.userId = userId;
        this.currentTime = currentTime;
        this.taskId = taskId;
        this.dateEligible = dateEligible;
        this.dateExpires = dateExpires;
        this.clientCaseType = clientCaseType;
        this.clientCaseId = clientCaseId;
    }

    public CaseTask toCaseTask() {
        CaseTask caseTask = new CaseTask();
        caseTask.setCaseType(caseType);
        caseTask.setCaseName(caseName);
        caseTask.setOwnerId(ownerId);
        caseTask.setCaseId(caseId);
        caseTask.setUserId(userId);
        caseTask.setCurrentTime(currentTime);
        caseTask.setTaskId(taskId);
        caseTask.setDateEligible(dateEligible);
        caseTask.setDateExpires(dateExpires);
        caseTask.setClientCaseType(clientCaseType);
        caseTask.setClientCaseId(clientCaseId);
        return caseTask;
    }

    public String getCaseType() {
        return caseType;
    }

    public String getCaseName() {
        return caseName;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getCaseId() {
        return caseId;
    }

    public String getUserId() {
        return userId;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getDateEligible() {
        return dateEligible;
    }

    public String getDateExpires() {
        return dateExpires;
    }

    public String getClientCaseType() {
        return clientCaseType;
    }

    public String getClientCaseId() {
        return clientCaseId;
    }

}

