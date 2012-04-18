package org.motechproject.care.service.schedule.listener;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
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

    @Autowired
    public AlertChildVaccination(AllChildren allChildren, CommcareCaseGateway commcareCaseGateway, AllCareCaseTasks allCareCaseTasks, Properties ananyaCareProperties) {
        super(commcareCaseGateway, allCareCaseTasks, ananyaCareProperties);
        this.allChildren = allChildren;
    }

    @Override
    public void process(DateTime dueDateTime, DateTime lateDateTime) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
