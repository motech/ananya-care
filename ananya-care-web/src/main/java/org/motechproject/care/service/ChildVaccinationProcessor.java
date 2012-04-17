package org.motechproject.care.service;

import org.motechproject.care.domain.Child;
import org.motechproject.care.service.schedule.VaccinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChildVaccinationProcessor {

    List<VaccinationService> vaccinationServices;

    @Autowired
    public ChildVaccinationProcessor(List<VaccinationService> vaccinationServices) {
        this.vaccinationServices = vaccinationServices;
    }

    public void enrollUpdateVaccines(Child child){
        for(VaccinationService vaccineService : vaccinationServices)
            vaccineService.process(child);
    }
}
