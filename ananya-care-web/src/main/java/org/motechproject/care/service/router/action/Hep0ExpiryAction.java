package org.motechproject.care.service.router.action;

import org.motechproject.care.domain.Child;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.service.schedule.Hep0Service;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Hep0ExpiryAction implements Action {

    private Hep0Service hep0Service;
    private AllChildren allChildren;

    @Autowired
    public Hep0ExpiryAction(Hep0Service hep0Service, AllChildren allChildren) {
        this.hep0Service = hep0Service;
        this.allChildren = allChildren;
    }

    @Override
    public void invoke(MilestoneEvent event) {
        String externalId = event.getExternalId();
        Child child = allChildren.findByCaseId(externalId);
        hep0Service.close(child);
    }
}
