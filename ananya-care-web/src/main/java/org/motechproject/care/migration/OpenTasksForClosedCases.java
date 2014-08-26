package org.motechproject.care.migration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.motechproject.care.domain.CareCaseTask;
import org.motechproject.care.domain.Child;
import org.motechproject.care.domain.Client;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllCareCaseTasks;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.repository.AllMothers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*This will write all the taskids which are open whose respective mothers/childs are closed to a file
 * 
 * */

@Component
public class OpenTasksForClosedCases {
	
	Logger logger = Logger.getLogger(OpenTasksForClosedCases.class);
	
	@Autowired
	private AllCareCaseTasks allCareCaseTasks;
	@Autowired
	private AllMothers allMothers;
	@Autowired
	private AllChildren allChildren;
	@Autowired
	private Properties ananyaCareProperties;

	public void writeTaskIdsToFile() throws IOException {
		File file = new File(ananyaCareProperties.getProperty("file.path"));
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);

		List<Mother> mothersList = allMothers.getAll();
		
		List<Child> childrenList = allChildren.getAll();

		List<CareCaseTask> allTasksOpen = allCareCaseTasks
				.findAllTasksOpen(true);

		Map<String, Client> clientMap = new HashMap<String, Client>();

		for (Mother mother : mothersList) {
			if (!mother.isActive()) {
				clientMap.put(mother.getCaseId(), mother);
			}
		}
		
		for(Child child : childrenList){
			if(!child.isActive()){
				clientMap.put(child.getCaseId(), child);
			}
		}

		for (CareCaseTask task : allTasksOpen) {
			if (task.getOpen() && clientMap.containsKey(task.getClientCaseId())) {
				String content1 = String.format("%s,", task.getCaseId());
				bw.write(content1);
				logger.info("writing task id which is open to csv file.");
			}
		}
		bw.close();
	}
}
