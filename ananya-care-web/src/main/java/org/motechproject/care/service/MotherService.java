package org.motechproject.care.service;

import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.request.CareCase;
import org.motechproject.care.service.mapper.MotherMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MotherService{

    private AllMothers allMothers;
    private MotherVaccinationProcessor motherVaccinationProcessor;


    @Autowired
    public MotherService(AllMothers allMothers, MotherVaccinationProcessor motherVaccinationProcessor) {
        this.allMothers = allMothers;
        this.motherVaccinationProcessor = motherVaccinationProcessor;
    }

    public void process(CareCase careCase) {
        Mother mother = MotherMapper.map(careCase);
        Mother updatedMother = createUpdate(mother);

        if(updatedMother.isActive())
            motherVaccinationProcessor.enrollUpdateVaccines(updatedMother);
        else
            closeSchedules(updatedMother);
    }

    private Mother createUpdate(Mother mother) {
        Mother motherFromDb = allMothers.findByCaseId(mother.getCaseId());
        
        if(motherFromDb ==null){
            allMothers.add(mother);
            return mother;
        }

        motherFromDb.setValuesFrom(mother);

        allMothers.update(motherFromDb);
        return motherFromDb;
    }

    public boolean closeCase(String caseId) {
        Mother mother = allMothers.findByCaseId(caseId);
        if(mother==null)
            return false;
        mother.setClosedByCommcare(true);
        allMothers.update(mother);
        closeSchedules(mother);
        return true;
    }

    private void closeSchedules(Mother mother) {
        motherVaccinationProcessor.closeSchedules(mother);
    }
}
