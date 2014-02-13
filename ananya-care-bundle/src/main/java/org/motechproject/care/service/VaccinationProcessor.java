package org.motechproject.care.service;

import org.motechproject.care.domain.Client;
import org.motechproject.care.service.schedule.VaccinationService;

import java.util.List;

public class VaccinationProcessor {

    protected List<VaccinationService> vaccinationServices;

    public VaccinationProcessor(List<VaccinationService> vaccinationServices) {
        this.vaccinationServices = vaccinationServices;
    }


    public void enrollUpdateVaccines(Client client){
        for(VaccinationService vaccineService : vaccinationServices)
            vaccineService.process(client);
    }

    public List<VaccinationService> getVaccinationServices() {
        return vaccinationServices;
    }

    public void closeSchedules(Client client) {
        for(VaccinationService vaccineService : vaccinationServices)
            vaccineService.close(client);
    }
}

