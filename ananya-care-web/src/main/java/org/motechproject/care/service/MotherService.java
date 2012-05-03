package org.motechproject.care.service;

import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.request.CareCase;
import org.motechproject.care.service.mapper.MotherMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MotherService {

    private AllMothers allMothers;
    private MotherVaccinationProcessor motherVaccinationProcessor;


    @Autowired
    public MotherService(AllMothers allMothers, MotherVaccinationProcessor motherVaccinationProcessor) {
        this.allMothers = allMothers;
        this.motherVaccinationProcessor = motherVaccinationProcessor;
    }

    public void process(CareCase careCase) {
        Mother mother = MotherMapper.map(careCase);
        Mother existingMother = allMothers.findByCaseId(mother.getCaseId());
        
        if(existingMother == null)
            processNew(mother);
        else
            processExisting(existingMother, mother);

    }
    
    private void processNew(Mother mother) {
        allMothers.add(mother);
        if(mother.isActive())
            motherVaccinationProcessor.enrollUpdateVaccines(mother);
    }
    
    private void processExisting(Mother existingMother, Mother newMother) {
        if(!existingMother.isActive()) {
            return;
        }

        existingMother.setValuesFrom(newMother);
        allMothers.update(existingMother);

        if(existingMother.isActive())
            motherVaccinationProcessor.enrollUpdateVaccines(existingMother);
        else
            motherVaccinationProcessor.closeSchedules(existingMother);
    }


    public boolean closeCase(String caseId) {
        Mother mother = allMothers.findByCaseId(caseId);
        if(mother == null)
            return false;

        if(!mother.isActive()) {
            return true;
        }
        mother.setClosedByCommcare(true);
        allMothers.update(mother);
        motherVaccinationProcessor.closeSchedules(mother);
        return true;
    }
}