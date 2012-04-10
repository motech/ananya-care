package org.motechproject.care.service.schedule.listener;

import org.apache.log4j.Logger;
import org.motechproject.care.domain.Client;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.service.util.TaskIdMapper;
import org.motechproject.commcare.gateway.CommcareCaseGateway;
import org.motechproject.commcare.request.CaseTask;
import org.motechproject.commcare.request.Pregnancy;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: pchandra
 * Date: 4/5/12
 * Time: 11:27 AM
 * To change this template use File | Settings | File Templates.
 */

@Component
public class CareAlertServiceListener {

    private CommcareCaseGateway commcareCaseGateway;
    private AllMothers allMothers;

    Logger logger = Logger.getLogger(CareAlertServiceListener.class);
    private static TaskIdMapper taskIdMapper = new TaskIdMapper();

    @Autowired
    public CareAlertServiceListener(CommcareCaseGateway commcareCaseGateway,AllMothers motherRepository) {
        this.commcareCaseGateway = commcareCaseGateway;
        this.allMothers = motherRepository;
    }

    @MotechListener(subjects = {EventSubjects.MILESTONE_ALERT})
    public void handleEvent(MotechEvent event){
        MilestoneEvent msEvent = new MilestoneEvent(event);
        String externalId = msEvent.getExternalId();
        MilestoneAlert milestoneAlert = msEvent.getMilestoneAlert();
        Client client = allMothers.findByCaseId(externalId);

        CaseTask casetask = createCasetask(externalId, milestoneAlert.getMilestoneName(), client.getCaseType(),milestoneAlert.getDueDateTime().toString("yyyy-MM-dd"), milestoneAlert.getLateDateTime().toString("yyyy-MM-dd"), client.getGroupId(),client.getFlwId());

        commcareCaseGateway.submitCase(casetask);

    }

    private CaseTask createCasetask(String caseId, String caseName,String caseType, String dateEligible, String dateExpires, String ownerId,String userId) {
        CaseTask task = new CaseTask();
        task.setCaseName(caseName);
        task.setTaskId(taskIdMapper.get(caseName));
        task.setCaseId(UUID.randomUUID().toString());
        task.setDateEligible(dateEligible);
        task.setDateExpires(dateExpires);
        task.setDateModified(DateUtil.now().toString());
        task.setOwnerId(ownerId);
        task.setUserId(userId);
        task.setPregnancy(pregnancyObject(caseId,caseType));
        return task;
    }

    private Pregnancy pregnancyObject(String caseId, String caseType) {
        return new Pregnancy(caseId,caseType);
    }
}
