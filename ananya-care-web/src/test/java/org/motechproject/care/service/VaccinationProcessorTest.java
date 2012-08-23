package org.motechproject.care.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.care.domain.Child;
import org.motechproject.care.service.schedule.BcgService;
import org.motechproject.care.service.schedule.MeaslesService;
import org.motechproject.care.service.schedule.VaccinationService;
import org.motechproject.care.service.schedule.VitaService;

import java.util.Arrays;

@RunWith(MockitoJUnitRunner.class)
public class VaccinationProcessorTest {
    @Mock
    private MeaslesService measlesService;
    @Mock
    private BcgService bcgService;
    @Mock
    private VitaService vitaService;

    @Test
    public void shouldProcessForEachVaccines(){
        VaccinationProcessor processor = new VaccinationProcessor(Arrays.<VaccinationService>asList(measlesService, bcgService, vitaService));
        Child child = new Child();
        processor.enrollUpdateVaccines(child);
        Mockito.verify(measlesService).process(child);
        Mockito.verify(bcgService).process(child);
        Mockito.verify(vitaService).process(child);
    }

    @Test
    public void shouldCloseAllSchedulesFor(){
        VaccinationProcessor processor = new VaccinationProcessor(Arrays.<VaccinationService>asList(measlesService,bcgService, vitaService));
        Child child = new Child();
        processor.closeSchedules(child);
        Mockito.verify(measlesService).close(child);
        Mockito.verify(bcgService).close(child);
        Mockito.verify(vitaService).close(child);
    }
}



