package org.motechproject.care.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.DateTime;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.commons.date.util.DateUtil;

public abstract class Client extends MotechBaseDataObject{

    protected String caseId;
    protected DateTime dateModified;
    protected String flwId;
    protected String name;
    protected String groupId;
    private DateTime docCreateTime;
    private boolean closedByCommcare;
    private boolean expired;
    protected boolean isAlive;

    public Client() {
    }

    public Client(boolean isAlive) {
        this.isAlive = isAlive;
    }

    @JsonIgnore
    public boolean isActive() {
        return isAlive && !closedByCommcare && !expired;
    }

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

    public DateTime getDocCreateTime() {
        return DateUtil.setTimeZone(docCreateTime);
    }

    public void setDocCreateTime(DateTime docCreateTime) {
        this.docCreateTime = docCreateTime;
    }

    public abstract String getCaseType();

    public abstract void setCaseType(String caseType);

    public boolean isClosedByCommcare() {
        return closedByCommcare;
    }

    public void setClosedByCommcare(boolean closedByCommcare) {
        this.closedByCommcare = closedByCommcare;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }
}