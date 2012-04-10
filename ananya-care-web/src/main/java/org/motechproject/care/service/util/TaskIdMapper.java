package org.motechproject.care.service.util;

import java.util.HashMap;
import java.util.Map;

public class TaskIdMapper {
    private Map<String,String> taskMap;

    public TaskIdMapper() {
        taskMap = new HashMap<String,String>();
        taskMap.put("TT 1","tt_1");
    }

    public String get(String caseName) {
        return taskMap.get(caseName);  //To change body of created methods use File | Settings | File Templates.
    }
}
