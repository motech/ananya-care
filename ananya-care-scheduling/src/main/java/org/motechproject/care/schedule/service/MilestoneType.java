package org.motechproject.care.schedule.service;

public enum MilestoneType {

    Measles("Measles", "measles"),
    Hep0("Hep 0", "hep_0"),
    Hep1("Hep 1", "hep_1"),
    Hep2("Hep 2", "hep_2"),
    Hep3("Hep 3", "hep_3"),
    DPT1("DPT 1", "dpt_1"),
    DPT2("DPT 2", "dpt_2"),
    DPT3("DPT 3", "dpt_3"),
    DPTBooster("DPT Booster", "dpt_booster"),
    OPV0("OPV 0", "opv_0"),
    OPV1("OPV 1", "opv_1"),
    OPV2("OPV 2", "opv_2"),
    OPV3("OPV 3", "opv_3"),
    OPVBooster("OPV Booster", "opv_booster"),
    ChildCare("Child Care", null),

    TT1("TT 1", "tt_1"),
    TT2("TT 2", "tt_2"),
    TTBooster("TT Booster", "tt_booster"),
    VitaminA("Vita", "vita_1"),
    Bcg("Bcg", "bcg"),
    Anc1("Anc 1", "anc_1"),
    Anc2("Anc 2", "anc_2"),
    Anc3("Anc 3", "anc_3"),
    Anc4("Anc 4", "anc_4"),
    MotherCare("Mother Care", null);

    private String name;
    private String taskId;

    MilestoneType(String name, String taskId) {
        this.name = name;
        this.taskId = taskId;
    }

    public String toString(){
        return name;
    }

    public static MilestoneType forType(String type) {
        MilestoneType[] values = MilestoneType.values();
        for(MilestoneType milestoneType: values) {
            if(milestoneType.name.equals(type)) {
                return milestoneType;
            }
        }
        return null;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getName() {
        return name;
    }
}
