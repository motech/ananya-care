package org.motechproject.care.service.schedule.listener;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.motechproject.care.domain.Child;
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
    public void process(DateTime dueDateTime, DateTime lateDateTime) {
        Child child = allChildren.findByCaseId(externalId);
        DateTime now = DateTime.now();

        if(dueDateTime.isAfter(dateOf2ndYear(child))) {
            return;
        }
        DateTime dateEligible = dueDateTime.isBefore(now) ? now : dueDateTime;
        DateTime dateExpires = lateDateTime.isAfter(dateOf2ndYear(child)) ? dateOf2ndYear(child) : lateDateTime;

        postToCommCare(dateEligible, dateExpires, child.getGroupId(), child.getCaseType(), clientElementTag);
    }

    private DateTime dateOf2ndYear(Child child) {
        return child.getDOB().plusYears(2);
    }

}
