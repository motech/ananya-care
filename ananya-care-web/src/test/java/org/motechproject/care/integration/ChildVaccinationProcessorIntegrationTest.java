package org.motechproject.care.integration;

import org.junit.Test;
import org.motechproject.care.service.ChildVaccinationProcessor;
import org.motechproject.care.service.schedule.*;
import org.motechproject.care.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChildVaccinationProcessorIntegrationTest extends SpringIntegrationTest{

    @Autowired
    private ChildVaccinationProcessor childVaccinationProcessor;

    @Test
    public void shouldInitializeChildServiceBeansCorrectly(){
        List<VaccinationService> vaccinationServices = childVaccinationProcessor.getVaccinationServices();

        assertEquals(10, vaccinationServices.size());
        assertTrue(vaccinationServices.get(0) instanceof MeaslesService);
        assertTrue(vaccinationServices.get(1) instanceof BcgService);
        assertTrue(vaccinationServices.get(2) instanceof VitaService);
        assertTrue(vaccinationServices.get(3) instanceof Hep0Service);
        assertTrue(vaccinationServices.get(4) instanceof HepService);
        assertTrue(vaccinationServices.get(5) instanceof DptService);
        assertTrue(vaccinationServices.get(6) instanceof Opv0Service);
        assertTrue(vaccinationServices.get(7) instanceof OpvService);
        assertTrue(vaccinationServices.get(8) instanceof OpvBoosterService);
        assertTrue(vaccinationServices.get(9) instanceof ChildCareService);
    }
}

