package org.motechproject.care.schedule.service;

public enum MilestoneType {

    Measles("Measles"),
    Hep0("Hep 0"),
    Hep1("Hep 1"),
    Hep2("Hep 2"),
    Hep3("Hep 3"),
    DPT1("DPT 1"),
    DPT2("DPT 2"),
    DPT3("DPT 3"),
    DPTBooster("DPT Booster"),
    TT1("TT 1"),
    TT2("TT 2"),
    VitaminA("Vita"),
    Bcg("Bcg"),
    Anc1("Anc 1"),
    Anc2("Anc 2"),
    Anc3("Anc 3"),
    Anc4("Anc 4");

    private String type;

    MilestoneType(String type) {

        this.type = type;
    }

    public String toString(){
        return type;
    }
}
