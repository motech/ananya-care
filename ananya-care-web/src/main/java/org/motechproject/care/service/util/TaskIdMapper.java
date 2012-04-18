package org.motechproject.care.service.util;

import org.motechproject.care.schedule.service.BcgSchedulerService;
import org.motechproject.care.schedule.service.MeaslesSchedulerService;
import org.motechproject.care.schedule.service.TTSchedulerService;
import org.motechproject.care.schedule.service.VitaSchedulerService;

import java.util.HashMap;
import java.util.Map;

public class TaskIdMapper {
    private Map<String,String> taskMap;

    public TaskIdMapper() {
        taskMap = new HashMap<String,String>();
        taskMap.put(TTSchedulerService.tt1Milestone,"tt_1");

        taskMap.put(MeaslesSchedulerService.milestone,"measles");
        taskMap.put(BcgSchedulerService.milestone,"bcg");
        taskMap.put(VitaSchedulerService.milestone,"vita_1");
    }

    public String getTaskId(String milestoneName) {
        return taskMap.get(milestoneName);
    }
}
