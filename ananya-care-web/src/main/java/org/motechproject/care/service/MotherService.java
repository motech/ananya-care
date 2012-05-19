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
        Mother motherFromDb = allMothers.findByCaseId(mother.getCaseId());
        
        if(motherFromDb == null)
            processNew(mother);
        else
            processExisting(motherFromDb, mother);
    }
    
    private void processNew(Mother mother) {
        allMothers.add(mother);
        if(mother.isActive())
            motherVaccinationProcessor.enrollUpdateVaccines(mother);
    }
    
    private void processExisting(Mother motherFromDb, Mother mother) {
        motherFromDb.setValuesFrom(mother);
        allMothers.update(motherFromDb);

        if(motherFromDb.isActive())
            motherVaccinationProcessor.enrollUpdateVaccines(motherFromDb);
        else
            motherVaccinationProcessor.closeSchedules(motherFromDb);
    }

    public boolean closeCase(String caseId) {
        Mother mother = allMothers.findByCaseId(caseId);
        if(mother == null)
            return false;

        mother.setClosedByCommcare(true);
        allMothers.update(mother);
        motherVaccinationProcessor.closeSchedules(mother);
        return true;
    }

    public boolean expireCase(String caseId) {
        Mother mother = allMothers.findByCaseId(caseId);
        if(mother == null)
            return false;

        mother.setExpired(true);
        allMothers.update(mother);
        motherVaccinationProcessor.closeSchedules(mother);
        return true;
    }
}