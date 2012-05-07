package org.motechproject.care.service.router.action;

import org.motechproject.care.domain.Child;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.service.schedule.Opv0Service;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Opv0ExpiryAction implements Action {


    private Opv0Service opv0Service;
    private AllChildren allChildren;

    @Autowired
    public Opv0ExpiryAction(Opv0Service opv0Service, AllChildren allChildren) {
        this.opv0Service = opv0Service;
        this.allChildren = allChildren;
    }

    @Override
    public void invoke(MilestoneEvent event) {
        String externalId = event.getExternalId();
        Child child = allChildren.findByCaseId(externalId);
        opv0Service.close(child);
    }
}
