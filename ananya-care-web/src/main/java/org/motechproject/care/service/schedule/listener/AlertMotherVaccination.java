package org.motechproject.care.service.schedule.listener;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.motechproject.care.domain.Mother;
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
    Logger logger = Logger.getLogger(AlertMotherVaccination.class);
    public static String clientElementTag = "mother_id";
    @Autowired
    public AlertMotherVaccination(AllMothers motherRepository, CommcareCaseGateway commcareCaseGateway, AllCareCaseTasks allCareCaseTasks, @Qualifier("ananyaCareProperties") Properties ananyaCareProperties) {
        super(commcareCaseGateway, allCareCaseTasks, ananyaCareProperties);
        this.allMothers = motherRepository;
    }

    @Override
    public void process(DateTime dueDateTime, DateTime lateDateTime) {
        Mother mother = allMothers.findByCaseId(externalId);
        DateTime now = DateTime.now();

        DateTime windowStart = getWindowStart(dueDateTime, now);
        DateTime windowEnd = getWindowEnd(lateDateTime, mother);

        if(windowStart.isAfter(mother.getEdd()) || windowEnd.isBefore(now) ) {
            return;
        }

        postToCommCare(windowStart, windowEnd, mother.getGroupId(), mother.getCaseType(),clientElementTag);
    }

    private DateTime getWindowEnd(DateTime lateDateTime, Mother mother) {
        return lateDateTime.isAfter(mother.getEdd()) ? mother.getEdd() : lateDateTime;
    }

    private DateTime getWindowStart(DateTime dueDateTime, DateTime now) {
        return dueDateTime.isBefore(now) ? now : dueDateTime;
    }
}

