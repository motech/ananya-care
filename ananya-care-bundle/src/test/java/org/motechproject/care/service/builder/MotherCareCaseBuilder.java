package org.motechproject.care.service.builder;


import org.motechproject.care.request.CareCase;
import org.motechproject.care.request.CaseType;

public class MotherCareCaseBuilder {

    private String caseId="6055b3ec-bec6-46cc-9e72-435ebc4eaec1";
    private String caseName="Vanaja";
    private String dateModified="2012-03-04";
    private String add=null;
    private String edd="2012-10-02";
    private String groupId="112";
    private String userId="b823ea3d392a06f8b991e9e4933348bd";
    private String case_type= CaseType.Mother.getType();
    private String tt_1_date = "2012-01-01";
    private String tt_2_date = "2012-01-02";
    private String last_preg_tt = "no";
    private String anc_1_date = "2012-01-03";
    private String anc_2_date = "2012-01-04";
    private String anc_3_date = "2012-01-05";
    private String anc_4_date = "2012-01-06";
    private String tt_booster_date = "2012-01-07";
    private String mother_alive = "yes";


    public CareCase build(){
        CareCase careCase = new CareCase();
        careCase.setCase_id(caseId);
        careCase.setCase_name(caseName);
        careCase.setDate_modified(dateModified);
        careCase.setAdd(add);
        careCase.setEdd(edd);
        careCase.setOwner_id(groupId);
        careCase.setUser_id(userId);
        careCase.setCase_type(case_type);
        careCase.setAnc_1_date(anc_1_date);
        careCase.setAnc_2_date(anc_2_date);
        careCase.setAnc_3_date(anc_3_date);
        careCase.setAnc_4_date(anc_4_date);
        careCase.setTt_1_date(tt_1_date);
        careCase.setTt_2_date(tt_2_date);
        careCase.setTt_booster_date(tt_booster_date);
        careCase.setLast_preg_tt(last_preg_tt);
        careCase.setMother_alive(mother_alive);
        return careCase;
    }

    public MotherCareCaseBuilder withCaseId(String caseId){
        this.caseId=caseId;
        return this;
    }
    public MotherCareCaseBuilder withCaseName(String caseName){
        this.caseName=caseName;
        return this;
    }
    public MotherCareCaseBuilder withDateModified(String dateModified){
        this.dateModified = dateModified;
        return this;
    }
    public MotherCareCaseBuilder withAdd(String add){
        this.add = add;
        return this;
    }
    public MotherCareCaseBuilder withEdd(String edd){
        this.edd = edd;
        return this;
    }
    public MotherCareCaseBuilder withGroupId(String groupId){
        this.groupId = groupId;
        return this;
    }
    public MotherCareCaseBuilder withUserId(String userId){
        this.userId = userId;
        return this;
    }

    public MotherCareCaseBuilder withCaseType(String caseType){
        this.case_type=caseType;
        return this;
    }
    public MotherCareCaseBuilder withTT1(String tt1_date){
        this.tt_1_date=tt1_date;
        return this;
    }
    public MotherCareCaseBuilder withTT2(String tt2_date){
        this.tt_2_date=tt2_date;
        return this;
    }
    public MotherCareCaseBuilder withANC2(String anc2_date){
        this.anc_2_date=anc2_date;
        return this;
    }
    public MotherCareCaseBuilder withANC3(String anc3_date){
        this.anc_3_date=anc3_date;
        return this;
    }
    public MotherCareCaseBuilder withANC4(String anc4_date){
        this.anc_4_date=anc4_date;
        return this;
    }
    public MotherCareCaseBuilder withANC1(String anc1_date){
        this.anc_1_date=anc1_date;
        return this;
    }
    public MotherCareCaseBuilder withLastPregTT(String lastPregTTTaken){
        this.last_preg_tt=lastPregTTTaken;
        return this;
    }
    public MotherCareCaseBuilder withTTBooster(String ttBooster_date){
        this.tt_booster_date=ttBooster_date;
        return this;
    }
    public MotherCareCaseBuilder withMotherAlive(String mother_alive){
        this.mother_alive = mother_alive;
        return this;
    }
}
