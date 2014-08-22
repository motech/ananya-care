package org.motechproject.care.service;

import org.apache.log4j.Logger;
import org.motechproject.care.domain.CareCaseTask;
import org.motechproject.care.repository.AllCareCaseTasks;
import org.motechproject.casexml.domain.CaseTask;
import org.motechproject.casexml.gateway.CommcareCaseGateway;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class CareCaseTaskService {

    private AllCareCaseTasks allCareCaseTasks;
    private CommcareCaseGateway commcareCaseGateway;
    private Properties ananyaCareProperties;

    Logger logger = Logger.getLogger(CareCaseTaskService.class);

    @Autowired
    public CareCaseTaskService(AllCareCaseTasks allCareCaseTasks, CommcareCaseGateway commcareCaseGateway, Properties ananyaCareProperties) {
        this.allCareCaseTasks = allCareCaseTasks;
        this.commcareCaseGateway = commcareCaseGateway;
        this.ananyaCareProperties = ananyaCareProperties;
    }

    public void close(String clientCaseId, String milestoneName) {
        logger.info(String.format("Closing case for Client Case Id: %s; Milestone Name: %s", clientCaseId, milestoneName));
        int retryCount = Integer.parseInt(ananyaCareProperties.getProperty("commcare.case.retry.count", "5"));
        CareCaseTask careCaseTask = allCareCaseTasks.findByClientCaseIdAndMilestoneName(clientCaseId, milestoneName);
        if(careCaseTask == null|| !careCaseTask.getOpen()) {
            logger.info(String.format("Valid care case not found for Client Case Id: %s; Milestone Name: %s", clientCaseId, milestoneName));
            return;
        }
        logger.info(String.format("Sending close case to Commcare for Client Case Id: %s; Milestone Name: %s", clientCaseId, milestoneName));
        careCaseTask.setOpen(false);
        careCaseTask.setCurrentTime(DateUtil.now().toString());
        allCareCaseTasks.update(careCaseTask);
        String commcareUrl = ananyaCareProperties.getProperty("commcare.hq.url");
        postToCommCareWithRetry(commcareUrl, careCaseTask.toCaseTask(), retryCount);
    }
    
    private void postToCommCareWithRetry(String commcareUrl, CaseTask caseTask, int retryCount){
    	try {
    		commcareCaseGateway.closeCase(commcareUrl, caseTask);
    	}catch(Exception e){
    		if (retryCount != 0){
    			retryCount--;
    			try {
					wait(10000);
				} catch (InterruptedException e1) {
					logger.error(e1.getMessage());
				}
    			postToCommCareWithRetry(commcareUrl, caseTask, retryCount);
    		}else {
    			logger.error(String.format("Close Case Request to Commcare failed for the CaseId: %s\n", caseTask.getCaseId()), e);
    		}
    	}
    }
}
