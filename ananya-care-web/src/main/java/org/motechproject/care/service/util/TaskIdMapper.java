package org.motechproject.care.service.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: pchandra
 * Date: 4/9/12
 * Time: 5:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class TaskIdMapper {
    private Map<String,String> taskMap;

    public TaskIdMapper() {
        taskMap = new HashMap<String,String>();
        taskMap.put("TT 1","tt1");
    }

    public String get(String caseName) {
        return taskMap.get(caseName);  //To change body of created methods use File | Settings | File Templates.
    }
}
