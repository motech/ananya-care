package org.motechproject.care.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.care.domain.Child;
import org.motechproject.care.service.schedule.MeaslesService;
import org.motechproject.care.service.schedule.VaccinationService;

import java.util.Arrays;

@RunWith(MockitoJUnitRunner.class)
public class ChildVaccinationProcessorTest{
    @Mock
    private MeaslesService measlesService;

    @Test
    public void shouldProcessForMeaslesVaccine(){
        ChildVaccinationProcessor processor = new ChildVaccinationProcessor(Arrays.<VaccinationService>asList(measlesService));
        Child child = new Child();
        processor.enrollUpdateVaccines(child);
        Mockito.verify(measlesService).process(child);
    }
}
