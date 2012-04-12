package org.motechproject.care.service;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.care.request.CareCase;
import org.motechproject.care.schedule.service.CareScheduleTrackingService;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class CareCaseServiceTest  {

    @Mock
    private MotherService motherService;
    @Mock
    private CareScheduleTrackingService careScheduleTrackingService;
    private CareCaseService careCaseService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        careCaseService = new CareCaseService(motherService);
    }

    @Test
    public void shouldRedirectToMotherService() throws IOException {
        String path = getClass().getResource("/sampleMotherCase.xml").getPath();
        File file = new File(path);
        String xml = FileUtils.readFileToString(file);

        careCaseService.ProcessCase(xml, null);
        verify(motherService).process((CareCase) Matchers.any());

    }

    @Test
    public void shouldSetMotherAsNotActiveIfCaseIsClosedAndCaseIdIsAMother() throws IOException {
        String path = getClass().getResource("/sampleMotherCaseForClose.xml").getPath();
        File file = new File(path);
        String xml = FileUtils.readFileToString(file);

        careCaseService.ProcessCase(xml, null);
        verify(motherService).closeCase("caseId");
    }
}
