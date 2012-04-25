package org.motechproject.care.domain;

import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.care.request.CaseType;
import org.motechproject.care.service.util.NullAwareBeanUtilsBean;
import org.motechproject.util.DateUtil;

@TypeDiscriminator("doc.type == 'Child'")
public class Child extends Client {

    private DateTime DOB;
    private DateTime measlesDate;
    private DateTime bcgDate;
    private DateTime vitamin1Date;
    private DateTime hep0Date;

    private String caseType= CaseType.Child.getType();
    private String motherCaseId;

    public Child() {}

    public Child(String caseId) {
        this.caseId = caseId;
    }

    public Child(String caseId, DateTime dateModified, String flwId, String name, String groupId, DateTime DOB, DateTime measlesDate, DateTime bcgDate, DateTime vitamin1Date, String motherCaseId, DateTime hep0Date) {
        this.motherCaseId = motherCaseId;
        this.caseId = caseId;
        this.isActive = true;
        this.dateModified = dateModified;
        this.flwId = flwId;
        this.name = name;
        this.groupId = groupId;
        this.DOB = DOB;
        this.measlesDate = measlesDate;
        this.bcgDate = bcgDate;
        this.vitamin1Date = vitamin1Date;
        this.hep0Date = hep0Date;
    }

    public DateTime getDOB() {
        return DateUtil.setTimeZone(DOB);
    }

    public void setDOB(DateTime DOB) {
        this.DOB = DOB;
    }

    public DateTime getMeaslesDate() {
        return DateUtil.setTimeZone(measlesDate);
    }

    public void setMeaslesDate(DateTime measlesDate) {
        this.measlesDate = measlesDate;
    }

    public DateTime getBcgDate() {
        return DateUtil.setTimeZone(bcgDate);
    }

    public void setBcgDate(DateTime bcgDate) {
        this.bcgDate = bcgDate;
    }

    public DateTime getVitamin1Date() {
        return DateUtil.setTimeZone(vitamin1Date);
    }

    public void setVitamin1Date(DateTime vitamin1Date) {
        this.vitamin1Date = vitamin1Date;
    }

    public void setValuesFrom(Child child) {
        try{
            NullAwareBeanUtilsBean nullAwareBeanUtilsBean = new NullAwareBeanUtilsBean();
            nullAwareBeanUtilsBean.copyProperties(this, child);
        }
        catch (Exception e) {
            // do something
        }
    }

    @Override
    public String getCaseType() {
        return caseType;
    }

    @Override
    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public String getMotherCaseId() {
        return motherCaseId;
    }

    public void setMotherCaseId(String motherCaseId) {
        this.motherCaseId = motherCaseId;
    }

    public DateTime getHep0Date() {
        return DateUtil.setTimeZone(hep0Date);
    }

    public void setHep0Date(DateTime hep0Date) {
        this.hep0Date = hep0Date;
    }
}