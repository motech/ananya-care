package org.motechproject.care.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.care.service.NullAwareBeanUtilsBean;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.util.DateUtil;

@TypeDiscriminator("doc.type == 'Mother'")
public class Mother extends MotechBaseDataObject implements Client {
    @JsonProperty
    private String caseId;
    @JsonProperty
    private DateTime dateModified;
    @JsonProperty
    private String flwId;
    @JsonProperty
    private String name;
    @JsonProperty
    private String groupId;
    @JsonProperty
    private DateTime edd;
    @JsonProperty
    private DateTime add;
    @JsonProperty
    private DateTime tt1Date;
    @JsonProperty
    private DateTime tt2Date;
    @JsonProperty
    private boolean lastPregTt;
    @JsonProperty
    private DateTime anc1Date;
    @JsonProperty
    private DateTime anc2Date;
    @JsonProperty
    private DateTime anc3Date;
    @JsonProperty
    private DateTime anc4Date;
    @JsonProperty
    private DateTime ttBoosterDate;
    @JsonProperty
    private boolean isActive;
    @JsonProperty
    private String caseType;


    public Mother() {}

    public Mother(String caseId) {
        this.caseId = caseId;
    }

    public Mother(String caseId, String caseType,DateTime dateModified, String flwId, String name, String groupId, DateTime edd, DateTime add, DateTime tt1Date, DateTime tt2Date, boolean lastPregTt, DateTime anc1Date, DateTime anc2Date, DateTime anc3Date, DateTime anc4Date, DateTime ttBoosterDate,boolean isActive) {
        this.caseId = caseId;
        this.caseType = caseType;
        this.dateModified = dateModified;
        this.flwId = flwId;
        this.name = name;
        this.groupId = groupId;
        this.edd = edd;
        this.add = add;
        this.tt1Date = tt1Date;
        this.tt2Date = tt2Date;
        this.lastPregTt = lastPregTt;
        this.anc1Date = anc1Date;
        this.anc2Date = anc2Date;
        this.anc3Date = anc3Date;
        this.anc4Date = anc4Date;
        this.ttBoosterDate = ttBoosterDate;
        this.isActive =isActive;
    }

    public DateTime getTt1Date() {
        return DateUtil.setTimeZone(tt1Date);
    }

    public void setTt1Date(DateTime tt1Date) {
        this.tt1Date = tt1Date;
    }

    public DateTime getTt2Date() {
        return DateUtil.setTimeZone(tt2Date);
    }

    public void setTt2Date(DateTime tt2Date) {
        this.tt2Date = tt2Date;
    }

    public boolean isLastPregTt() {
        return lastPregTt;
    }

    public void setLastPregTt(boolean lastPregTt) {
        this.lastPregTt = lastPregTt;
    }

    public DateTime getAnc1Date() {
        return DateUtil.setTimeZone(anc1Date);
    }

    public void setAnc1Date(DateTime anc1Date) {
        this.anc1Date = anc1Date;
    }

    public DateTime getAnc2Date() {
        return DateUtil.setTimeZone(anc2Date);
    }

    public void setAnc2Date(DateTime anc2Date) {
        this.anc2Date = anc2Date;
    }

    public DateTime getAnc3Date() {
        return DateUtil.setTimeZone(anc3Date);
    }

    public void setAnc3Date(DateTime anc3Date) {
        this.anc3Date = anc3Date;
    }

    public DateTime getAnc4Date() {
        return DateUtil.setTimeZone(anc4Date);
    }

    public void setAnc4Date(DateTime anc4Date) {
        this.anc4Date = anc4Date;
    }

    public DateTime getTtBoosterDate() {
        return DateUtil.setTimeZone(ttBoosterDate);
    }

    public void setTtBoosterDate(DateTime ttBoosterDate) {
        this.ttBoosterDate = ttBoosterDate;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public DateTime getDateModified() {
        return DateUtil.setTimeZone(dateModified);
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

    @Override
    public String getCaseType() {
        return caseType;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public DateTime getEdd() {
        return DateUtil.setTimeZone(edd);
    }

    public void setEdd(DateTime edd) {
        this.edd = edd;
    }

    public DateTime getAdd() {
        return DateUtil.setTimeZone(add);
    }

    public void setAdd(DateTime add) {
        this.add = add;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public void setValuesFrom(Mother mother) {
        try{
            NullAwareBeanUtilsBean nullAwareBeanUtilsBean = new NullAwareBeanUtilsBean();
            nullAwareBeanUtilsBean.copyProperties(this, mother);
        }
        catch (Exception e){
            // do something
        }
    }
}