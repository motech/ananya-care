package org.motechproject.care.service.schedule.listener;

import org.joda.time.DateTime;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.domain.Window;
import org.motechproject.care.repository.AllCareCaseTasks;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.casexml.gateway.CommcareCaseGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class AlertMotherVaccination extends AlertVaccination{
    private AllMothers allMothers;
    public static String clientElementTag = "mother_id";

    @Autowired
    public AlertMotherVaccination(AllMothers motherRepository, CommcareCaseGateway commcareCaseGateway, AllCareCaseTasks allCareCaseTasks, @Qualifier("ananyaCareProperties") Properties ananyaCareProperties) {
        super(commcareCaseGateway, allCareCaseTasks, ananyaCareProperties);
        this.allMothers = motherRepository;
    }

    @Override
    public void process(Window alertWindow) {
        Mother mother = allMothers.findByCaseId(externalId);
        alertWindow = alertWindow.resize(new Window(DateTime.now(), mother.getEdd()));
        if(!alertWindow.isValid()) {
            return;
        }
        postToCommCare(alertWindow, mother.getGroupId(), mother.getCaseType(),clientElementTag);
    }
}

