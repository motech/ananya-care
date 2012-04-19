package org.motechproject.care.schedule.service;

public enum MilestoneType {

    Measles("Measles"),
    TT1("TT 1"),
    TT2("TT 2"),
    VitaminA("Vita"),
    Bcg("Bcg");

    private String type;

    MilestoneType(String type) {

        this.type = type;
    }

    public String toString(){
        return type;
    }
}
