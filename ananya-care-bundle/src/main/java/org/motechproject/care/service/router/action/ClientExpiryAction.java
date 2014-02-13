package org.motechproject.care.service.router.action;

import org.motechproject.care.service.ChildService;
import org.motechproject.care.service.MotherService;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientExpiryAction implements Action{

    private MotherService motherService;
    private ChildService childService;

    @Autowired
    public ClientExpiryAction(MotherService motherService, ChildService childService) {
        this.motherService = motherService;
        this.childService = childService;
    }

    @Override
    public void invoke(MilestoneEvent event) {
        String externalId = event.getExternalId();
        boolean didExpireMother = motherService.expireCase(externalId);
        if(!didExpireMother)
            childService.expireCase(externalId);
    }
}
