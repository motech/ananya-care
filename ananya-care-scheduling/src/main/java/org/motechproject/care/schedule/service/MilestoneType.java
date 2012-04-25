package org.motechproject.care.schedule.service;

public enum MilestoneType {

    Measles("Measles"),
    Hep0("Hep 0"),
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
