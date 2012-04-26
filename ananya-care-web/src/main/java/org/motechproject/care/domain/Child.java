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
    private DateTime hep1Date;
    private DateTime hep2Date;
    private DateTime hep3Date;
    private DateTime dpt1Date;
    private DateTime dpt2Date;
    private DateTime dpt3Date;
    private DateTime dptBoosterDate;

    private String caseType= CaseType.Child.getType();
    private String motherCaseId;

    public Child() {}

    public Child(String caseId) {
        this.caseId = caseId;
    }

    public Child(String caseId, DateTime dateModified, String flwId, String name, String groupId, DateTime DOB, DateTime measlesDate, DateTime bcgDate, DateTime vitamin1Date, String motherCaseId,
                 DateTime hep0Date, DateTime hep1Date, DateTime hep2Date, DateTime hep3Date,
                 DateTime dpt1Date, DateTime dpt2Date, DateTime dpt3Date, DateTime dptBoosterDate) {
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
        this.hep1Date = hep1Date;
        this.hep2Date = hep2Date;
        this.hep3Date = hep3Date;
        this.dpt1Date = dpt1Date;
        this.dpt2Date = dpt2Date;
        this.dpt3Date = dpt3Date;
        this.dptBoosterDate = dptBoosterDate;
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

    public DateTime getHep1Date() {
        return DateUtil.setTimeZone(hep1Date);
    }

    public void setHep1Date(DateTime hep1Date) {
        this.hep1Date = hep1Date;
    }

    public DateTime getHep2Date() {
        return DateUtil.setTimeZone(hep2Date);
    }

    public void setHep2Date(DateTime hep2Date) {
        this.hep2Date = hep2Date;
    }

    public DateTime getHep3Date() {
        return DateUtil.setTimeZone(hep3Date);
    }

    public void setHep3Date(DateTime hep3Date) {
        this.hep3Date = hep3Date;
    }

    public DateTime getDpt1Date() {
        return DateUtil.setTimeZone(dpt1Date);
    }

    public void setDpt1Date(DateTime dpt1Date) {
        this.dpt1Date = dpt1Date;
    }

    public DateTime getDpt2Date() {
        return DateUtil.setTimeZone(dpt2Date);
    }

    public void setDpt2Date(DateTime dpt2Date) {
        this.dpt2Date = dpt2Date;
    }

    public DateTime getDpt3Date() {
        return DateUtil.setTimeZone(dpt3Date);
    }

    public void setDpt3Date(DateTime dpt3Date) {
        this.dpt3Date = dpt3Date;
    }

    public DateTime getDptBoosterDate() {
        return DateUtil.setTimeZone(dptBoosterDate);
    }

    public void setDptBoosterDate(DateTime dptBoosterDate) {
        this.dptBoosterDate = dptBoosterDate;
    }
}