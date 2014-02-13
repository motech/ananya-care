package org.motechproject.care.service.router.action;

import org.joda.time.DateTime;
import org.motechproject.care.domain.Child;
import org.motechproject.care.domain.Window;
import org.motechproject.care.repository.AllCareCaseTasks;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.gateway.CommcareCaseGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class AlertChildAction extends AlertClientAction implements Action{

    private AllChildren allChildren;

    @Autowired
    public AlertChildAction(AllChildren allChildren, CommcareCaseGateway commcareCaseGateway, AllCareCaseTasks allCareCaseTasks, Properties ananyaCareProperties) {
        super(commcareCaseGateway, allCareCaseTasks, ananyaCareProperties);
        this.allChildren = allChildren;
    }

    @Override
    public void process(Window alertWindow, String externalId, String milestoneName) {
        Child child = allChildren.findByCaseId(externalId);
        if(!child.isActive()) {
            return;
        }
        alertWindow = alertWindow.resize(new Window(DateTime.now(), dateOf2ndYear(child)));
        if(!alertWindow.isValid()) {
            return;
        }
        postToCommCare(alertWindow, externalId, milestoneName, child);
    }

    private DateTime dateOf2ndYear(Child child) {
        return child.getDOB().plusYears(2);
    }
}
