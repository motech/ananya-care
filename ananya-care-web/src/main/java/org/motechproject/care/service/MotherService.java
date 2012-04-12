package org.motechproject.care.service;

import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.request.CareCase;
import org.motechproject.care.schedule.service.CareScheduleTrackingService;
import org.motechproject.care.service.mapper.MotherMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MotherService{

    private AllMothers allMothers;
    private CareScheduleTrackingService scheduleTrackingService;


    @Autowired
    public MotherService(AllMothers allMothers, CareScheduleTrackingService scheduleTrackingService) {
        this.allMothers = allMothers;
        this.scheduleTrackingService = scheduleTrackingService;
    }

    public void process(CareCase careCase) {
        Mother mother = MotherMapper.map(careCase);
        createUpdate(mother);
        scheduleTrackingService.enrollMother(mother.getCaseId(), mother.getEdd());

    }

    private void createUpdate(Mother mother) {
        Mother motherFromDb = allMothers.findByCaseId(mother.getCaseId());
        if(motherFromDb ==null){
           allMothers.add(mother);
            return;
        }
        motherFromDb.setValuesFrom(mother);
        allMothers.update(motherFromDb);
    }

    public boolean closeCase(String case_id) {
        Mother mother = allMothers.findByCaseId(case_id);
        if(mother==null)
            return false;
        mother.setActive(false);
        allMothers.update(mother);
        return true;
    }
}
