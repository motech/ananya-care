package org.motechproject.care.service;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.care.request.CareCase;
import org.motechproject.care.schedule.service.TTSchedulerService;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class CareCaseServiceTest  {

    @Mock
    private MotherService motherService;
    @Mock
    private ChildService childService;
    @Mock
    private TTSchedulerService TTSchedulerService;
    private CareCaseService careCaseService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        careCaseService = new CareCaseService(motherService,childService);
    }

    @Test
    public void shouldRedirectToMotherServiceIfCaseTypeBelongsToMother() throws IOException {
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

    @Test
    public void shouldRedirectToChildServiceIfCaseTypeBelongsToChild() throws IOException {
        String path = getClass().getResource("/sampleChildCase.xml").getPath();
        File file = new File(path);
        String xml = FileUtils.readFileToString(file);

        careCaseService.ProcessCase(xml, null);
        verify(childService).process((CareCase) Matchers.any());

    }

}
