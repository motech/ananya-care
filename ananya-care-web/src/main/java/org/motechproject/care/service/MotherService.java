package org.motechproject.care.service;

import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllMothers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MotherService{

    AllMothers allMothers;

    @Autowired
    public MotherService(AllMothers allMothers) {
        this.allMothers = allMothers;
    }

    public void process(Mother mother) {
        Mother motherFromDb = allMothers.findByCaseId(mother.getCaseId());
        if(motherFromDb ==null){
           allMothers.add(mother);
            return;
        }
        motherFromDb.setValuesFrom(mother);
        allMothers.update(motherFromDb);
    }
}
