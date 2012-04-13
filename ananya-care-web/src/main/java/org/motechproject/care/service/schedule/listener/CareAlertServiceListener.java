package org.motechproject.care.service.schedule.listener;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.motechproject.care.domain.CareCaseTask;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllCareCaseTasks;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.service.util.TaskIdMapper;
import org.motechproject.casexml.gateway.CommcareCaseGateway;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class CareAlertServiceListener {

    private CommcareCaseGateway commcareCaseGateway;
    private AllCareCaseTasks allCareCaseTasks;
    private Properties ananyaCareProperties;
    private AllMothers allMothers;

    Logger logger = Logger.getLogger(CareAlertServiceListener.class);
    private static TaskIdMapper taskIdMapper = new TaskIdMapper();

    @Autowired
    public CareAlertServiceListener(CommcareCaseGateway commcareCaseGateway, AllMothers motherRepository, AllCareCaseTasks allCareCaseTasks, @Qualifier("ananyaCareProperties") Properties ananyaCareProperties) {
        this.commcareCaseGateway = commcareCaseGateway;
        this.allMothers = motherRepository;
        this.allCareCaseTasks = allCareCaseTasks;
        this.ananyaCareProperties = ananyaCareProperties;
    }

    @MotechListener(subjects = {EventSubjects.MILESTONE_ALERT})
    public void handleEvent(MotechEvent event){
        MilestoneEvent msEvent = new MilestoneEvent(event);
        String externalId = msEvent.getExternalId();
        MilestoneAlert milestoneAlert = msEvent.getMilestoneAlert();
        Mother mother = allMothers.findByCaseId(externalId);

        DateTime dateEligible = milestoneAlert.getDueDateTime();
        if(dateEligible.isAfter(mother.getEdd())) {
            return;
        }

        String userId = ananyaCareProperties.getProperty("motech.user.id");
        String commcareUrl = ananyaCareProperties.getProperty("commcare.hq.url");


        DateTime dateExpires = validExpiresDateTime(milestoneAlert, mother);
        CareCaseTask careCasetask = createCasetask(externalId, milestoneAlert.getMilestoneName(), dateEligible.toString("yyyy-MM-dd"), dateExpires.toString("yyyy-MM-dd"), mother.getGroupId(),userId, mother.getCaseType());
        allCareCaseTasks.add(careCasetask);
        commcareCaseGateway.submitCase(commcareUrl, careCasetask.toCaseTask());

    }

    private DateTime validExpiresDateTime(MilestoneAlert milestoneAlert, Mother mother) {
        DateTime expiresDateTime = milestoneAlert.getLateDateTime();
        DateTime edd = mother.getEdd();
        return expiresDateTime.isAfter(edd) ? edd : expiresDateTime;
    }


    private CareCaseTask createCasetask(String caseId, String caseName, String dateEligible, String dateExpires, String ownerId,String userId, String caseType) {
        String taskId = taskIdMapper.get(caseName);
        String currentTime = DateUtil.now().toString();
        return new CareCaseTask(caseName, ownerId, caseId, userId, currentTime, taskId, dateEligible, dateExpires, caseType, caseId);
    }
}
