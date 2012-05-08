package org.motechproject.care.integration;

import org.junit.Test;
import org.motechproject.care.service.MotherVaccinationProcessor;
import org.motechproject.care.service.schedule.*;
import org.motechproject.care.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MotherVaccinationProcessorIntegrationTest extends SpringIntegrationTest {

    @Autowired
    private MotherVaccinationProcessor motherVaccinationProcessor;

    @Test
    public void shouldInitializeChildServiceBeansCorrectly(){
        List<VaccinationService> vaccinationServices = motherVaccinationProcessor.getVaccinationServices();

        assertEquals(5, vaccinationServices.size());
        assertTrue(vaccinationServices.get(0) instanceof TTService);
        assertTrue(vaccinationServices.get(1) instanceof TTBoosterService);
        assertTrue(vaccinationServices.get(2) instanceof AncService);
        assertTrue(vaccinationServices.get(3) instanceof Anc4Service);
        assertTrue(vaccinationServices.get(4) instanceof MotherCareService);
    }
}
