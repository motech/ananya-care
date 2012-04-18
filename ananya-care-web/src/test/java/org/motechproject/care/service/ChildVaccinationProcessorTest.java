package org.motechproject.care.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.care.domain.Child;
import org.motechproject.care.service.schedule.*;

import java.util.Arrays;

@RunWith(MockitoJUnitRunner.class)
public class ChildVaccinationProcessorTest{
    @Mock
    private MeaslesService measlesService;
    @Mock
    private BcgService bcgService;
    @Mock
    private VitaService vitaService;

    @Test
    public void shouldProcessForChildVaccines(){
        ChildVaccinationProcessor processor = new ChildVaccinationProcessor(Arrays.<VaccinationService>asList(measlesService, bcgService, vitaService));
        Child child = new Child();
        processor.enrollUpdateVaccines(child);
        Mockito.verify(measlesService).process(child);
        Mockito.verify(bcgService).process(child);
        Mockito.verify(vitaService).process(child);
    }
}

