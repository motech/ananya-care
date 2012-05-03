package org.motechproject.care.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.care.domain.CareCaseTask;
import org.motechproject.care.repository.AllCareCaseTasks;
import org.motechproject.casexml.domain.CaseTask;
import org.motechproject.casexml.gateway.CommcareCaseGateway;

import java.util.Properties;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CareCaseTaskServiceTest {

    private CareCaseTaskService careCaseTaskService;
    @Mock
    AllCareCaseTasks allCareCaseTasks;
    @Mock
    CommcareCaseGateway commcareCaseGateway;

    @Mock
    Properties ananyaCareProperties;

    @Before
    public void setUp(){
        careCaseTaskService=new CareCaseTaskService(allCareCaseTasks,commcareCaseGateway,ananyaCareProperties);
    }
    
    @Test
    public void shouldNotCloseACaseIfNoCareTaskExistsForIt(){
        careCaseTaskService.close("clientCaseId", "milestoneName");
        verify(commcareCaseGateway, never()).closeCase(anyString(), any(CaseTask.class));
    }

    @Test
    public void shouldCloseACaseIfCareTaskExistsForIt(){
        String clientCaseId = "clientCaseId";
        String milestoneName = "milestoneName";
        CareCaseTask careCaseTask = mock(CareCaseTask.class);
        CaseTask caseTask = new CaseTask();
        String url = "someurl";
        
        when(ananyaCareProperties.getProperty("commcare.hq.url")).thenReturn(url);
        when(careCaseTask.toCaseTask()).thenReturn(caseTask);
        when(allCareCaseTasks.findByClientCaseIdAndMilestoneName(clientCaseId, milestoneName)).thenReturn(careCaseTask);

        careCaseTaskService.close(clientCaseId, milestoneName);

        verify(commcareCaseGateway).closeCase(url, caseTask);
    }
}
