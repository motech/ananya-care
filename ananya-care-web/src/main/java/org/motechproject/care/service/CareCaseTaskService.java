package org.motechproject.care.service;

import org.motechproject.care.domain.CareCaseTask;
import org.motechproject.care.repository.AllCareCaseTasks;
import org.motechproject.casexml.gateway.CommcareCaseGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class CareCaseTaskService {

    private AllCareCaseTasks allCareCaseTasks;
    private CommcareCaseGateway commcareCaseGateway;
    private Properties ananyaCareProperties;


    @Autowired
    public CareCaseTaskService(AllCareCaseTasks allCareCaseTasks, CommcareCaseGateway commcareCaseGateway, Properties ananyaCareProperties) {
        this.allCareCaseTasks = allCareCaseTasks;
        this.commcareCaseGateway = commcareCaseGateway;
        this.ananyaCareProperties = ananyaCareProperties;
    }

    public void close(String clientCaseId, String milestoneName) {
        CareCaseTask careCaseTask = allCareCaseTasks.findByClientCaseIdAndMilestoneName(clientCaseId, milestoneName);
        if(careCaseTask == null) {
            return;
        }
        careCaseTask.setOpen(false);
        allCareCaseTasks.update(careCaseTask);
        String commcareUrl = ananyaCareProperties.getProperty("commcare.hq.url");
        commcareCaseGateway.closeCase(commcareUrl, careCaseTask.toCaseTask());
    }
}
