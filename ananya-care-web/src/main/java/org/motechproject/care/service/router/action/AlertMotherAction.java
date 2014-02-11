package org.motechproject.care.service.router.action;

import org.joda.time.DateTime;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.domain.Window;
import org.motechproject.care.repository.AllCareCaseTasks;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.gateway.CommcareCaseGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class AlertMotherAction extends AlertClientAction implements Action{
    private AllMothers allMothers;

    @Autowired
    public AlertMotherAction(AllMothers motherRepository, CommcareCaseGateway commcareCaseGateway, AllCareCaseTasks allCareCaseTasks, @Qualifier("ananyaCareProperties") Properties ananyaCareProperties) {
        super(commcareCaseGateway, allCareCaseTasks, ananyaCareProperties);
        this.allMothers = motherRepository;
    }

    @Override
    public void process(Window alertWindow, String externalId, String milestoneName) {
        Mother mother = allMothers.findByCaseId(externalId);
        if(!mother.isActive()) {
            return;
        }
        alertWindow = alertWindow.resize(new Window(DateTime.now(), mother.getEdd()));
        if(!alertWindow.isValid()) {
            return;
        }
        postToCommCare(alertWindow, externalId, milestoneName, mother);
    }
}

