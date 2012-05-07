package org.motechproject.care.service.router.action;

import org.motechproject.care.domain.Child;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.service.schedule.BcgService;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BcgExpiryAction implements Action {

    private BcgService bcgService;
    private AllChildren allChildren;

    @Autowired
    public BcgExpiryAction(BcgService bcgService, AllChildren allChildren) {
        this.bcgService = bcgService;
        this.allChildren = allChildren;
    }

    @Override
    public void invoke(MilestoneEvent event) {
        String externalId = event.getExternalId();
        Child child = allChildren.findByCaseId(externalId);
        bcgService.close(child);
    }
}
