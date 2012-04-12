package org.motechproject.care.domain;

import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;

public abstract class Client extends MotechBaseDataObject{

    protected String caseId;
    protected DateTime dateModified;
    protected String flwId;
    protected String name;
    protected String groupId;
    protected boolean isActive;

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public DateTime getDateModified() {
        return dateModified;
    }

    
    public void setDateModified(DateTime dateModified) {
        this.dateModified = dateModified;
    }

    public String getFlwId() {
        return flwId;
    }

    public void setFlwId(String flwId) {
        this.flwId = flwId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public abstract String getCaseType();

    public abstract void setCaseType(String caseType);
}