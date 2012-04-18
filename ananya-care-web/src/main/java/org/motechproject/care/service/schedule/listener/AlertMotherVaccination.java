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

    @Autowired
    public AlertMotherVaccination(AllMothers motherRepository, CommcareCaseGateway commcareCaseGateway, AllCareCaseTasks allCareCaseTasks, @Qualifier("ananyaCareProperties") Properties ananyaCareProperties) {
        super(commcareCaseGateway, allCareCaseTasks, ananyaCareProperties);
        this.allMothers = motherRepository;
    }

    @Override
    public void process(DateTime dueDateTime, DateTime lateDateTime) {
        Mother mother = allMothers.findByCaseId(externalId);

        DateTime dateEligible = dueDateTime;
        if(dateEligible.isAfter(mother.getEdd())) {
            return;
        }
        DateTime dateExpires = validExpiresDateTime(lateDateTime, mother);
        postToCommCare(dateEligible, dateExpires, mother.getGroupId(), mother.getCaseType());
    }

    private DateTime validExpiresDateTime(DateTime lateDateTime, Mother mother) {
        DateTime expiresDateTime = lateDateTime;
        DateTime edd = mother.getEdd();
        return expiresDateTime.isAfter(edd) ? edd : expiresDateTime;
    }

}

