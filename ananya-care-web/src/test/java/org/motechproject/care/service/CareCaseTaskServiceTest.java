package org.motechproject.care.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.care.domain.CareCaseTask;
import org.motechproject.care.repository.AllCareCaseTasks;
import org.motechproject.care.domain.CaseTask;
import org.motechproject.care.gateway.CommcareCaseGateway;

import java.util.Properties;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

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
    public void shouldSendCloseUpdatesIfCareTaskExistsForItAndItIsOpen(){
        String clientCaseId = "clientCaseId";
        String milestoneName = "milestoneName";
        CareCaseTask careCaseTask = mock(CareCaseTask.class);
        when(careCaseTask.getOpen()).thenReturn(true);

        CaseTask caseTask = new CaseTask();
        String url = "someurl";
        
        when(ananyaCareProperties.getProperty("commcare.hq.url")).thenReturn(url);
        when(careCaseTask.toCaseTask()).thenReturn(caseTask);
        when(allCareCaseTasks.findByClientCaseIdAndMilestoneName(clientCaseId, milestoneName)).thenReturn(careCaseTask);

        careCaseTaskService.close(clientCaseId, milestoneName);

        verify(commcareCaseGateway).closeCase(url, caseTask);
    }

    @Test
    public void shouldNotSendCloseUpdatesIfCareTaskExistsForItAndItIsAlreadyClosed(){
        String clientCaseId = "clientCaseId";
        String milestoneName = "milestoneName";
        CareCaseTask careCaseTask = mock(CareCaseTask.class);
        when(careCaseTask.getOpen()).thenReturn(false);

        when(allCareCaseTasks.findByClientCaseIdAndMilestoneName(clientCaseId, milestoneName)).thenReturn(careCaseTask);

        careCaseTaskService.close(clientCaseId, milestoneName);
        verify(allCareCaseTasks,never()).update(Matchers.<CareCaseTask>any());
        verify(commcareCaseGateway, never()).closeCase(Matchers.<String>any(), Matchers.<CaseTask>any());
    }

    @Test
    public void shouldUpdateTheCaseTaskStatusToClose() {
        String clientCaseId = "clientCaseId";
        String milestoneName = "milestoneName";
        CareCaseTask careCaseTask = new CareCaseTask();
        careCaseTask.setOpen(true);
        when(allCareCaseTasks.findByClientCaseIdAndMilestoneName(clientCaseId, milestoneName)).thenReturn(careCaseTask);

        careCaseTaskService.close(clientCaseId, milestoneName);
        ArgumentCaptor<CareCaseTask> captor = ArgumentCaptor.forClass(CareCaseTask.class);
        verify(allCareCaseTasks).update(captor.capture());

        CareCaseTask value = captor.getValue();
        assertFalse(value.getOpen());
    }
}
