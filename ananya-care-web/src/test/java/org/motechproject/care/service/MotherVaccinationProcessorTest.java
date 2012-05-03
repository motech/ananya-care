package org.motechproject.care.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.service.schedule.VaccinationService;

import java.util.Arrays;

@RunWith(MockitoJUnitRunner.class)
public class MotherVaccinationProcessorTest{
    @Mock
    private VaccinationService ttService;
    @Mock
    private VaccinationService ancService;

    @Test
    public void shouldProcessForMotherVaccines(){
        MotherVaccinationProcessor processor = new MotherVaccinationProcessor(Arrays.<VaccinationService>asList(ttService,ancService));
        Mother mother = new Mother();
        processor.enrollUpdateVaccines(mother);
        Mockito.verify(ttService).process(mother);
        Mockito.verify(ancService).process(mother);
    }

    @Test
    public void shouldCloseAllSchedulesForAMother(){
        MotherVaccinationProcessor processor = new MotherVaccinationProcessor(Arrays.<VaccinationService>asList(ttService,ancService));
        Mother mother = new Mother();
        processor.closeSchedules(mother);
        Mockito.verify(ttService).close(mother);
        Mockito.verify(ancService).close(mother);
    }
}
