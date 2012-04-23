package org.motechproject.care.service.schedule.listener;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.motechproject.care.domain.Child;
import org.motechproject.care.domain.Window;
import org.motechproject.care.repository.AllCareCaseTasks;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.casexml.gateway.CommcareCaseGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class AlertChildVaccination extends AlertVaccination{

    private AllChildren allChildren;
    Logger logger = Logger.getLogger(AlertChildVaccination.class);
    public static String clientElementTag = "child_id";

    @Autowired
    public AlertChildVaccination(AllChildren allChildren, CommcareCaseGateway commcareCaseGateway, AllCareCaseTasks allCareCaseTasks, Properties ananyaCareProperties) {
        super(commcareCaseGateway, allCareCaseTasks, ananyaCareProperties);
        this.allChildren = allChildren;
    }

    @Override
    public void process(Window alertWindow) {
        Child child = allChildren.findByCaseId(externalId);

        alertWindow = alertWindow.resize(new Window(DateTime.now(), dateOf2ndYear(child)));
        if(!alertWindow.isValid()) {
            return;
        }

        postToCommCare(alertWindow, child.getGroupId(), child.getCaseType(), clientElementTag);
    }


    private DateTime dateOf2ndYear(Child child) {
        return child.getDOB().plusYears(2);
    }

}
