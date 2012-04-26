package org.motechproject.care.service;

import org.motechproject.care.domain.Mother;
import org.motechproject.care.service.schedule.VaccinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MotherVaccinationProcessor {

    List<VaccinationService> vaccinationServices;

    @Autowired
    public MotherVaccinationProcessor(List<VaccinationService> vaccinationServices) {
        this.vaccinationServices = vaccinationServices;
    }

    public void enrollUpdateVaccines(Mother mother){
        for(VaccinationService vaccineService : vaccinationServices)
            vaccineService.process(mother);
    }

    public List<VaccinationService> getVaccinationServices() {
        return vaccinationServices;
    }
}
