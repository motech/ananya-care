package org.motechproject.care.schedule.service;

import org.joda.time.DateTime;
import org.motechproject.care.schedule.vaccinations.Vaccine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChildVaccinationProcessor {

    List<Vaccine> childVaccines;

    @Autowired
    public ChildVaccinationProcessor(List<Vaccine> childVaccines) {
        this.childVaccines = childVaccines;
    }

    public void enrollUpdateVaccines(String caseId, DateTime dob){
        for(Vaccine vaccine : childVaccines)
            vaccine.process(caseId,dob);

    }
}
