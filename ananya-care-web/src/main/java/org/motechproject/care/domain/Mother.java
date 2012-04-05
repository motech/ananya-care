package org.motechproject.care.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.care.service.NullAwareBeanUtilsBean;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.util.DateUtil;

@TypeDiscriminator("doc.type == 'Mother'")
public class Mother extends MotechBaseDataObject {
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

    public Mother() {}

    public Mother(String caseId) {
        this.caseId = caseId;
    }

    public Mother(String caseId, DateTime dateModified, String flwId, String name, String groupId, DateTime edd, DateTime add) {
        this.caseId = caseId;
        this.dateModified = dateModified;
        this.flwId = flwId;
        this.name = name;
        this.groupId = groupId;
        this.edd = edd;
        this.add = add;
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