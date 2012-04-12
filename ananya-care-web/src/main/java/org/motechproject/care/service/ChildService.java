package org.motechproject.care.service;

import org.motechproject.care.domain.Child;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.request.CareCase;
import org.motechproject.care.schedule.service.CareScheduleTrackingService;
import org.motechproject.care.service.mapper.ChildMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChildService {

    private AllChildren allChildren;
    private CareScheduleTrackingService scheduleTrackingService;
    private AllMothers allMothers;


    @Autowired
    public ChildService(AllChildren allChildren, CareScheduleTrackingService scheduleTrackingService, AllMothers allMothers) {
        this.allChildren = allChildren;
        this.scheduleTrackingService = scheduleTrackingService;
        this.allMothers = allMothers;
    }

    public void process(CareCase careCase) {
        Child child = ChildMapper.map(careCase);
        createUpdate(child);
    }

    private void createUpdate(Child child) {
        if (doesMotherNotExist(child)) return;
        Child childFromDb = allChildren.findByCaseId(child.getCaseId());
        if(childFromDb ==null){
           allChildren.add(child);
            return;
        }
        childFromDb.setValuesFrom(child);
        allChildren.update(childFromDb);
    }

    private boolean doesMotherNotExist(Child child) {
        return child.getMotherCaseId() == null || allMothers.findByCaseId(child.getMotherCaseId()) == null;
    }

}
