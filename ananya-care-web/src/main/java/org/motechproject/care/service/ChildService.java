package org.motechproject.care.service;

import org.motechproject.care.domain.Child;
import org.motechproject.care.repository.AllClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ChildService extends BaseService<Child> {


    @Autowired
    public ChildService(AllClients<Child> allChildren, @Qualifier("childVaccinationProcessor") VaccinationProcessor vaccinationProcessor) {
        super(allChildren, vaccinationProcessor);
    }

    protected void onProcess(Child child) {
        Child childFromDb = allClients.findByCaseId(child.getCaseId());

        if(childFromDb == null)
            processNew(child);
        else if(childFromDb.isActive())
            processExisting(childFromDb, child);
    }

    private void processNew(Child child) {
        allClients.add(child);
        if(child.shouldEnrollForSchedules()) {
            vaccinationProcessor.enrollUpdateVaccines(child);
        }
    }

    private void processExisting(Child childFromDb, Child child) {
        childFromDb.setValuesFrom(child);
        allClients.update(childFromDb);

        if(childFromDb.isActive()) {
            vaccinationProcessor.enrollUpdateVaccines(childFromDb);
        }
        else {
            vaccinationProcessor.closeSchedules(childFromDb);
        }
    }
}
