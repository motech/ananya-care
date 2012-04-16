package org.motechproject.care.schedule.service;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.care.schedule.vaccinations.Measles;
import org.motechproject.care.schedule.vaccinations.Vaccine;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;

import java.util.Arrays;

@RunWith(MockitoJUnitRunner.class)
public class ChildVaccinationProcessorTest{

    @Mock
    private ScheduleTrackingService scheduleTrackingService;
    @Mock
    private Measles measles;

    @Test
    public void shouldProcessForMeaslesVaccine(){
        ChildVaccinationProcessor processor = new ChildVaccinationProcessor(Arrays.<Vaccine>asList(measles));
        String caseId = "caseId";
        DateTime today = DateUtil.now();
        processor.enrollUpdateVaccines(caseId, today);
        Mockito.verify(measles).process(caseId,today);
    }
}
