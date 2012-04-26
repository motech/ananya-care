package org.motechproject.care.service.builder;


import org.motechproject.care.request.CareCase;
import org.motechproject.care.request.CaseType;

public class ChildCareCaseBuilder {

    private String caseId="6055b3ec-bec6-46cc-9e72-435ebc4eaec1";
    private String caseName="Pinky";
    private String dateModified="2012-03-04";
    private String groupId="112";
    private String userId="b823ea3d392a06f8b991e9e4933348bd";
    private String case_type= CaseType.Child.getType();
    private String bcg_date = "2012-01-01";
    private String baby_measles = "2012-01-02";
    private String vit_a_1_date = "2012-01-02";
    private String motherCaseId="motherCaseId";
    private String DOB="2009-01-02";
    private String hep0Date="2012-01-02";
    private String hep1Date="2012-02-02";
    private String hep2Date="2012-03-02";
    private String hep3Date="2012-04-02";

    private String dpt1Date="2012-08-02";
    private String dpt2Date="2012-09-02";
    private String dpt3Date="2012-10-02";
    private String dptBoosterDate="2012-11-02";

    public CareCase build(){
        CareCase careCase = new CareCase();
        careCase.setCase_id(caseId);
        careCase.setCase_name(caseName);
        careCase.setDate_modified(dateModified);
        careCase.setOwner_id(groupId);
        careCase.setUser_id(userId);
        careCase.setCase_type(case_type);
        careCase.setBcg_date(bcg_date);
        careCase.setVit_a_1_date(vit_a_1_date);
        careCase.setBaby_measles(baby_measles);
        careCase.setMother_id(motherCaseId);
        careCase.setHep_b_0_date(hep0Date);
        careCase.setHep_b_1_date(hep1Date);
        careCase.setHep_b_2_date(hep2Date);
        careCase.setHep_b_3_date(hep3Date);
        careCase.setDpt_1_date(dpt1Date);
        careCase.setDpt_2_date(dpt2Date);
        careCase.setDpt_3_date(dpt3Date);
        careCase.setDpt_booster_date(dptBoosterDate);
        careCase.setDob(DOB);
        return careCase;
    }

    public ChildCareCaseBuilder withCaseId(String caseId){
        this.caseId=caseId;
        return this;
    }
    public ChildCareCaseBuilder withCaseName(String caseName){
        this.caseName=caseName;
        return this;
    }
    public ChildCareCaseBuilder withDateModified(String dateModified){
        this.dateModified = dateModified;
        return this;
    }

    public ChildCareCaseBuilder withGroupId(String groupId){
        this.groupId = groupId;
        return this;
    }
    public ChildCareCaseBuilder withUserId(String userId){
        this.userId = userId;
        return this;
    }

    public ChildCareCaseBuilder withCaseType(String caseType){
        this.case_type=caseType;
        return this;
    }
    public ChildCareCaseBuilder withBcgDate(String bcg_date){
        this.bcg_date =bcg_date;
        return this;
    }
    public ChildCareCaseBuilder withVitamin1Date(String vita1Date){
        this.vit_a_1_date =vita1Date;
        return this;
    }
    public ChildCareCaseBuilder withBabyMeaslesDate(String measlesDate){
        this.baby_measles =measlesDate;
        return this;
    }

    public ChildCareCaseBuilder withMotherCaseId(String motherCaseId){
        this.motherCaseId = motherCaseId;
        return this;
    }

    public ChildCareCaseBuilder withDOB(String dob){
        this.DOB =dob;
        return this;
    }

    public ChildCareCaseBuilder withHep0Date(String hep0Date) {
        this.hep0Date = hep0Date;
        return this;
    }
    public ChildCareCaseBuilder withHep1Date(String hep1Date) {
        this.hep1Date = hep1Date;
        return this;
    }
    public ChildCareCaseBuilder withHep2Date(String hep2Date) {
        this.hep2Date = hep2Date;
        return this;
    }
    public ChildCareCaseBuilder withHep3Date(String hep3Date) {
        this.hep3Date = hep3Date;
        return this;
    }
    public ChildCareCaseBuilder withDpt1Date(String dpt1Date) {
        this.dpt1Date = dpt1Date;
        return this;
    }
    public ChildCareCaseBuilder withDpt2Date(String dpt2Date) {
        this.dpt2Date = dpt2Date;
        return this;
    }
    public ChildCareCaseBuilder withDpt3Date(String dpt3Date) {
        this.dpt3Date = dpt3Date;
        return this;
    }
    public ChildCareCaseBuilder withDptBoosterDate(String dptBoosterDate) {
        this.dptBoosterDate = dptBoosterDate;
        return this;
    }
}
