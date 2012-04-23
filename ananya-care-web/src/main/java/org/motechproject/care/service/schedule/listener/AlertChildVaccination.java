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

        DateTime windowStart = getWindowStart(dueDateTime, now);
        DateTime windowEnd = getWindowEnd(lateDateTime, child);
        if(windowStart.isAfter(dateOf2ndYear(child)) || windowEnd.isBefore(now)) {
            return;
        }

        postToCommCare(windowStart, windowEnd, child.getGroupId(), child.getCaseType(), clientElementTag);
    }

    private DateTime getWindowEnd(DateTime lateDateTime, Child child) {
        return lateDateTime.isAfter(dateOf2ndYear(child)) ? dateOf2ndYear(child) : lateDateTime;
    }

    private DateTime getWindowStart(DateTime dueDateTime, DateTime now) {
        return dueDateTime.isBefore(now) ? now : dueDateTime;
    }

    private DateTime dateOf2ndYear(Child child) {
        return child.getDOB().plusYears(2);
    }

}
