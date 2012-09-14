package org.motechproject.care.service.builder;


import org.joda.time.DateTime;
import org.motechproject.care.domain.Mother;

public class MotherBuilder {

    private String caseId="6055b3ec-bec6-46cc-9e72-435ebc4eaec1";
    private String caseName="Vanaja";
    private DateTime dateModified=new DateTime(2012, 3, 4, 0, 0, 0);
    private DateTime add=null;
    private DateTime edd=new DateTime(2012, 10, 2, 0, 0, 0);
    private String groupId="112";
    private String userId="b823ea3d392a06f8b991e9e4933348bd";

    private DateTime tt1Date = new DateTime(2012, 1, 2, 0, 0, 0);
    private DateTime tt2Date = new DateTime(2012, 1, 2, 0, 0, 0);
    private boolean isLastPregTT = false;
    private DateTime anc1Date = new DateTime(2012, 1, 3, 0, 0, 0);
    private DateTime anc2Date = new DateTime(2012, 1, 4, 0, 0, 0);
    private DateTime anc3Date = new DateTime(2012, 1, 5, 0, 0, 0);
    private DateTime anc4Date = new DateTime(2012, 1, 6, 0, 0, 0);
    private DateTime ttBoosterDate = new DateTime(2012, 1, 7, 0, 0, 0);
    private boolean isAlive = true;
    private boolean expired;


    public Mother build(){
        Mother mother = new Mother();
        mother.setName(caseName);
        mother.setFlwId(userId);
        mother.setCaseId(caseId);
        mother.setGroupId(groupId);
        mother.setDateModified(dateModified);
        mother.setAdd(add);
        mother.setEdd(edd);
        mother.setAlive(isAlive);

        mother.setAnc1Date(anc1Date);
        mother.setAnc2Date(anc2Date);
        mother.setAnc3Date(anc3Date);
        mother.setAnc4Date(anc4Date);
        mother.setLastPregTt(isLastPregTT);
        mother.setTt1Date(tt1Date);
        mother.setTt2Date(tt2Date);
        mother.setTtBoosterDate(ttBoosterDate);
        mother.setExpired(expired);
        return mother;
    }

    public MotherBuilder withCaseId(String caseId){
        this.caseId=caseId;
        return this;
    }
    public MotherBuilder withName(String caseName){
        this.caseName=caseName;
        return this;
    }
    public MotherBuilder withDateModified(DateTime dateModified){
        this.dateModified = dateModified;
        return this;
    }
    public MotherBuilder withAdd(DateTime add){
        this.add = add;
        return this;
    }
    public MotherBuilder withEdd(DateTime edd){
        this.edd = edd;
        return this;
    }
    public MotherBuilder withGroupId(String groupId){
        this.groupId = groupId;
        return this;
    }
    public MotherBuilder withUserId(String userId){
        this.userId = userId;
        return this;
    }

    public MotherBuilder withTT1(DateTime tt1Date){
        this.tt1Date = tt1Date;
        return this;
    }
    public MotherBuilder withTT2(DateTime tt2Date){
        this.tt2Date = tt2Date;
        return this;
    }
    public MotherBuilder withANC1(DateTime anc1Date){
        this.anc1Date =anc1Date;
        return this;
    }
    public MotherBuilder withANC2(DateTime anc2Date){
        this.anc2Date = anc2Date;
        return this;
    }
    public MotherBuilder withANC3(DateTime anc3Date){
        this.anc3Date = anc3Date;
        return this;
    }
    public MotherBuilder withANC4(DateTime anc4Date){
        this.anc4Date = anc4Date;
        return this;
    }
    public MotherBuilder withLastPregTT(boolean isLastPregTT){
        this.isLastPregTT = isLastPregTT;
        return this;
    }
    public MotherBuilder withTTBooster(DateTime ttBoosterDate){
        this.ttBoosterDate = ttBoosterDate;
        return this;
    }
    public MotherBuilder withAlive(boolean isAlive){
        this.isAlive = isAlive;
        return this;
    }

    public MotherBuilder withExpired(boolean expired){
        this.expired = expired;
        return this;
    }
}
