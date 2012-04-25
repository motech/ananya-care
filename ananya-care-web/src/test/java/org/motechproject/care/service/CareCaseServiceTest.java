package org.motechproject.care.service;

import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.care.request.CareCase;
import org.motechproject.casexml.service.exception.CaseValidationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;

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

        careCaseService.processCase(new HttpEntity<String>(xml));
        verify(motherService).process((CareCase) Matchers.any());

    }

    @Test
    public void shouldSetMotherAsNotActiveIfCaseIsClosedAndCaseIdIsAMother() throws IOException {
        String path = getClass().getResource("/sampleMotherCaseForClose.xml").getPath();
        File file = new File(path);
        String xml = FileUtils.readFileToString(file);

        careCaseService.processCase(new HttpEntity<String>(xml));
        verify(motherService).closeCase("caseId");
    }

    @Test
    public void shouldRedirectToChildServiceIfCaseTypeBelongsToChild() throws IOException {
        String path = getClass().getResource("/sampleChildCase.xml").getPath();
        File file = new File(path);
        String xml = FileUtils.readFileToString(file);

        careCaseService.processCase(new HttpEntity<String>(xml));
        verify(childService).process((CareCase) Matchers.any());

    }

    @Test
    public void shouldThrowExceptionWhenCaseIdIsEmptyForCreateCase() {
        CareCase careCase = new CareCase();
        careCase.setUser_id("userId");

        try{
            careCaseService.createCase(careCase);
            Assert.fail("Should have thrown a CaseValidationException");
        }catch (CaseValidationException exception){
            Assert.assertEquals(HttpStatus.valueOf(400), exception.getStatusCode());
            Assert.assertEquals("case_id is a mandatory field.",exception.getMessage());

        }
    }

    @Test
    public void shouldThrowExceptionWhenUserIdIsEmptyForCreateCase() {
        CareCase careCase = new CareCase();
        careCase.setCase_id("caseId");

        try{
            careCaseService.createCase(careCase);
            Assert.fail("Should have thrown a CaseValidationException");
        }catch (CaseValidationException exception){
            Assert.assertEquals(HttpStatus.valueOf(400), exception.getStatusCode());
            Assert.assertEquals("user_id is a mandatory field.",exception.getMessage());

        }
    }

    @Test
    public void shouldThrowExceptionWhenCaseIdIsEmptyForCloseCase() {
        CareCase careCase = new CareCase();
        careCase.setUser_id("userId");

        try{
            careCaseService.closeCase(careCase);
            Assert.fail("Should have thrown a CaseValidationException");
        }catch (CaseValidationException exception){
            Assert.assertEquals(HttpStatus.valueOf(400), exception.getStatusCode());
            Assert.assertEquals("case_id is a mandatory field.",exception.getMessage());

        }
    }

}
