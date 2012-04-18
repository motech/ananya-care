package org.motechproject.care.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.service.schedule.TTService;
import org.motechproject.care.service.schedule.VaccinationService;

import java.util.Arrays;

@RunWith(MockitoJUnitRunner.class)
public class MotherVaccinationProcessorTest{
    @Mock
    private TTService ttService;

    @Test
    public void shouldProcessForChildVaccines(){
        MotherVaccinationProcessor processor = new MotherVaccinationProcessor(Arrays.<VaccinationService>asList(ttService));
        Mother mother = new Mother();
        processor.enrollUpdateVaccines(mother);
        Mockito.verify(ttService).process(mother);
    }
}
