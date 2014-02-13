package org.motechproject.care.service;

import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class MotherService extends BaseService<Mother> {
    @Autowired
    public MotherService(AllClients<Mother> allMothers, @Qualifier("motherVaccinationProcessor") VaccinationProcessor vaccinationProcessor) {
        super(allMothers, vaccinationProcessor);
    }

    protected void onProcess(Mother mother) {
        Mother motherFromDb = allClients.findByCaseId(mother.getCaseId());
        
        if(motherFromDb == null)
            processNew(mother);
        else
            processExisting(motherFromDb, mother);
    }
    
    private void processNew(Mother mother) {
        allClients.add(mother);
        if(mother.isActive())
            vaccinationProcessor.enrollUpdateVaccines(mother);
    }
    
    private void processExisting(Mother motherFromDb, Mother mother) {
        motherFromDb.setValuesFrom(mother);
        allClients.update(motherFromDb);

        if(motherFromDb.isActive())
            vaccinationProcessor.enrollUpdateVaccines(motherFromDb);
        else
            vaccinationProcessor.closeSchedules(motherFromDb);
    }
}