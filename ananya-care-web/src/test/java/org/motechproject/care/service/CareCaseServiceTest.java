package org.motechproject.care.service;

import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.care.domain.Mother;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class CareCaseServiceTest  {

    @Mock
    private MotherService motherService;
    private CareCaseService careCaseService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        careCaseService = new CareCaseService(motherService,null);
    }

    @Test
    public void shouldRedirectToMotherServiceWithMotherObjectMapped() throws IOException {
        String path = getClass().getResource("/sampleMotherCaseXml.xml").getPath();
        File file = new File(path);
        String xml = FileUtils.readFileToString(file);

        careCaseService.ProcessCase(xml, null);
        ArgumentCaptor<Mother> captor = ArgumentCaptor.forClass(Mother.class);
        verify(motherService).createUpdateCase(captor.capture());

        Mother mother = captor.getValue();
        Assert.assertEquals("8055b3ec-bec6-46cc-9e72-435ebc4eaec1",mother.getCaseId());
        Assert.assertEquals("d823ea3d392a06f8b991e9e4933348bd",mother.getFlwId());
        Assert.assertEquals("d823ea3d392a06f8b991e9e49394ce45",mother.getGroupId());
        Assert.assertEquals("NEERAJ",mother.getName());
        Assert.assertNull(mother.getAdd());
        Assert.assertEquals(DateTime.parse("2012-10-20"),mother.getEdd());
        Assert.assertEquals(DateTime.parse("2012-04-03"),mother.getDateModified());
    }

    @Test
    public void shouldSetMotherAsNotActiveIfCaseIsClosedAndCaseIdIsAMother() throws IOException {
        String path = getClass().getResource("/sampleMotherCaseForClose.xml").getPath();
        File file = new File(path);
        String xml = FileUtils.readFileToString(file);

        careCaseService.ProcessCase(xml, null);
        verify(motherService).closeCase("8055b3ec-bec6-46cc-9e72-435ebc4eaec1");
    }
}
