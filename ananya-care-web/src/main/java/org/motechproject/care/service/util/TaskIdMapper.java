package org.motechproject.care.service.util;

import org.motechproject.care.schedule.service.*;

import java.util.HashMap;
import java.util.Map;

public class TaskIdMapper {
    private Map<String,String> taskMap;

    public TaskIdMapper() {
        taskMap = new HashMap<String,String>();
        taskMap.put(MilestoneType.TT1.toString(),"tt_1");

        taskMap.put(MilestoneType.Measles.toString(),"measles");
        taskMap.put(MilestoneType.Bcg.toString(),"bcg");
        taskMap.put(MilestoneType.VitaminA.toString(),"vita_1");
    }

    public String getTaskId(String milestoneName) {
        return taskMap.get(milestoneName);
    }
}
