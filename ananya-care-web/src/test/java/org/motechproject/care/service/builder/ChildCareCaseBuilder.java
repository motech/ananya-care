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
    public ChildCareCaseBuilder withVita1Date(String vita1Date){
        this.vit_a_1_date =vita1Date;
        return this;
    }
    public ChildCareCaseBuilder withBabyMeasles(String measlesDate){
        this.baby_measles =measlesDate;
        return this;
    }
}
