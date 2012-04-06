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

    public void createUpdateCase(Mother mother) {
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
