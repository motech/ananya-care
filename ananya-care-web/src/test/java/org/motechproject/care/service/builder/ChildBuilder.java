package org.motechproject.care.service.builder;


import org.joda.time.DateTime;
import org.motechproject.care.domain.Child;

public class ChildBuilder {

    private String caseId="6055b3ec-bec6-46cc-9e72-435ebc4eaec1";
    private String caseName="Pinky";
    private DateTime dateModified=new DateTime(2012, 3, 4, 0, 0, 0);
    private String groupId="112";
    private String motherCaseId="motherCaseId";

    private DateTime dob =new DateTime(2009, 1, 2, 0, 0, 0);
    private String userId="b823ea3d392a06f8b991e9e4933348bd";
    private DateTime bcgDate = new DateTime(2012, 1, 1, 0, 0, 0);
    private DateTime measlesDate = new DateTime(2012, 1, 2, 0, 0, 0);
    private DateTime vitamin1Date = new DateTime(2012, 1, 2, 0, 0, 0);
    private DateTime hep0Date=new DateTime(2012, 1, 2, 0, 0, 0);
    private DateTime hep1Date=new DateTime(2012, 2, 2, 0, 0, 0);
    private DateTime hep2Date=new DateTime(2012, 3, 2, 0, 0, 0);
    private DateTime hep3Date=new DateTime(2012, 4, 2, 0, 0, 0);

    private DateTime dpt1Date=new DateTime(2012, 8, 2, 0, 0, 0);
    private DateTime dpt2Date=new DateTime(2012, 9, 2, 0, 0, 0);
    private DateTime dpt3Date=new DateTime(2012, 10, 2, 0, 0, 0);
    private DateTime dptBoosterDate=new DateTime(2012, 11, 2, 0, 0, 0);

    private DateTime opv0Date=new DateTime(2012, 1, 2, 0, 0, 0);
    private DateTime opv1Date=new DateTime(2012, 2, 2, 0, 0, 0);
    private DateTime opv2Date=new DateTime(2012, 3, 2, 0, 0, 0);
    private DateTime opv3Date=new DateTime(2012, 4, 2, 0, 0, 0);
    private DateTime opvBoosterDate=new DateTime(2012, 5, 2, 0, 0, 0);

    private boolean isAlive = true;
    private boolean expired;

    public Child build(){

        Child child = new Child();
        child.setName(caseName);
        child.setFlwId(userId);
        child.setCaseId(caseId);
        child.setGroupId(groupId);
        child.setDOB(dob);
        child.setAlive(isAlive);
        child.setMotherCaseId(motherCaseId);
        child.setDateModified(dateModified);

        child.setDpt1Date(dpt1Date);
        child.setDpt2Date(dpt2Date);
        child.setDpt3Date(dpt3Date);
        child.setDptBoosterDate(dptBoosterDate);

        child.setHep0Date(hep0Date);
        child.setHep1Date(hep1Date);
        child.setHep2Date(hep2Date);
        child.setHep3Date(hep3Date);

        child.setBcgDate(bcgDate);
        child.setMeaslesDate(measlesDate);
        child.setVitamin1Date(vitamin1Date);

        child.setOpv0Date(opv0Date);
        child.setOpv1Date(opv1Date);
        child.setOpv2Date(opv2Date);
        child.setOpv3Date(opv3Date);
        child.setOpvBoosterDate(opvBoosterDate);
        child.setExpired(expired);

        return child;
    }

    public ChildBuilder withCaseId(String caseId){
        this.caseId=caseId;
        return this;
    }

    public ChildBuilder withCaseName(String caseName){
        this.caseName=caseName;
        return this;
    }

    public ChildBuilder withDateModified(DateTime dateModified){
        this.dateModified = dateModified;
        return this;
    }

    public ChildBuilder withGroupId(String groupId){
        this.groupId = groupId;
        return this;
    }

    public ChildBuilder withUserId(String userId){
        this.userId = userId;
        return this;
    }

    public ChildBuilder withBcgDate(DateTime bcg_date){
        this.bcgDate =bcg_date;
        return this;
    }
    public ChildBuilder withVitamin1Date(DateTime vita1Date){
        this.vitamin1Date =vita1Date;
        return this;
    }

    public ChildBuilder withMeaslesDate(DateTime measlesDate){
        this.measlesDate =measlesDate;
        return this;
    }

    public ChildBuilder withMotherCaseId(String motherCaseId){
        this.motherCaseId = motherCaseId;
        return this;
    }

    public ChildBuilder withDOB(DateTime dob){
        this.dob =dob;
        return this;
    }

    public ChildBuilder withHep0Date(DateTime hep0Date) {
        this.hep0Date = hep0Date;
        return this;
    }

    public ChildBuilder withHep1Date(DateTime hep1Date) {
        this.hep1Date = hep1Date;
        return this;
    }
    public ChildBuilder withHep2Date(DateTime hep2Date) {
        this.hep2Date = hep2Date;
        return this;
    }

    public ChildBuilder withHep3Date(DateTime hep3Date) {
        this.hep3Date = hep3Date;
        return this;
    }

    public ChildBuilder withDpt1Date(DateTime dpt1Date) {
        this.dpt1Date = dpt1Date;
        return this;
    }

    public ChildBuilder withDpt2Date(DateTime dpt2Date) {
        this.dpt2Date = dpt2Date;
        return this;
    }

    public ChildBuilder withDpt3Date(DateTime dpt3Date) {
        this.dpt3Date = dpt3Date;
        return this;
    }

    public ChildBuilder withDptBoosterDate(DateTime dptBoosterDate) {
        this.dptBoosterDate = dptBoosterDate;
        return this;
    }

    public ChildBuilder withOPV0Date(DateTime opv0Date) {
        this.opv0Date = opv0Date;
        return this;
    }

    public ChildBuilder withOPV1Date(DateTime opv1Date) {
        this.opv1Date = opv1Date;
        return this;
    }

    public ChildBuilder withOPV2Date(DateTime opv2Date) {
        this.opv2Date = opv2Date;
        return this;
    }

    public ChildBuilder withOPV3Date(DateTime opv3Date) {
        this.opv3Date = opv3Date;
        return this;
    }

    public ChildBuilder withOPVBoosterDate(DateTime opvBoosterDate) {
        this.opvBoosterDate = opvBoosterDate;
        return this;
    }

    public ChildBuilder withAlive(boolean isAlive){
        this.isAlive = isAlive;
        return this;
    }

    public ChildBuilder withExpired(boolean expired){
        this.expired = expired;
        return this;
    }

}
