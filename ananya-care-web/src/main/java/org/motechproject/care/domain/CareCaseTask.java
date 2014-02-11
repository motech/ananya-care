package org.motechproject.care.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type == 'CareCaseTask'")
public class CareCaseTask extends MotechBaseDataObject{

    @JsonProperty
    private String caseType = "task";
    @JsonProperty
    private String milestoneName;
    @JsonProperty
    private String ownerId;
    @JsonProperty
    private String caseId;
    @JsonProperty
    private String motechUserId;
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
    @JsonProperty
    private Boolean isOpen = true;

    public CareCaseTask() {
    }

    public CareCaseTask(String milestoneName, String ownerId, String caseId, String motechUserId, String currentTime, String taskId, String dateEligible, String dateExpires, String clientCaseType, String clientCaseId) {
        this.milestoneName = milestoneName;
        this.ownerId = ownerId;
        this.caseId = caseId;
        this.motechUserId = motechUserId;
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
        caseTask.setCaseName(milestoneName);
        caseTask.setOwnerId(ownerId);
        caseTask.setCaseId(caseId);
        caseTask.setMotechUserId(motechUserId);
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

    public String getMilestoneName() {
        return milestoneName;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getCaseId() {
        return caseId;
    }

    public String getMotechUserId() {
        return motechUserId;
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

    public Boolean getOpen() {
        return isOpen;
    }

    public void setOpen(Boolean open) {
        isOpen = open;
    }
}

