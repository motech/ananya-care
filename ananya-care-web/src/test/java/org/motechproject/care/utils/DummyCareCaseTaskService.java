package org.motechproject.care.utils;

import org.motechproject.care.repository.AllCareCaseTasks;
import org.motechproject.care.service.CareCaseTaskService;
import org.motechproject.casexml.gateway.CommcareCaseGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class DummyCareCaseTaskService extends CareCaseTaskService {

    private String clientCaseId;
    private String milestoneName;

    @Autowired
    public DummyCareCaseTaskService(AllCareCaseTasks allCareCaseTasks, CommcareCaseGateway commcareCaseGateway, Properties ananyaCareProperties) {
        super(allCareCaseTasks, commcareCaseGateway, ananyaCareProperties);
    }

    public void close(String clientCaseId, String milestoneName) {
        this.clientCaseId = clientCaseId;
        this.milestoneName = milestoneName;
    }

    public String getClientCaseId() {
        return clientCaseId;
    }

    public String getMilestoneName() {
        return milestoneName;
    }
}
