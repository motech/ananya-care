package org.motechproject.care.integration.service;

import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.request.CaseType;
import org.motechproject.care.service.CareCaseService;
import org.motechproject.care.utils.CaseUtils;
import org.motechproject.care.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-Web.xml")
public class CareCaseServiceIntegrationTest extends SpringIntegrationTest {

    @Autowired
    private AllMothers allMothers;

    @Autowired
    private CareCaseService careCaseService;
    private String caseId;

    @After
    public void tearDown() {
        Mother mother = allMothers.findByCaseId(caseId);
        if(mother != null) {
            markForDeletion(mother);
        }
    }

    @Test
    public void shouldAddAMother() throws IOException {
        caseId = CaseUtils.getUniqueCaseId();
        String path = getClass().getResource("/sampleMotherCase.xml").getPath();
        File file = new File(path);
        String xml = FileUtils.readFileToString(file);
        String modifiedXml = xml.replace("caseId", caseId);
        careCaseService.ProcessCase(modifiedXml, null);
        Mother mother = allMothers.findByCaseId(caseId);
        Assert.assertEquals("NEERAJ",mother.getName());
        Assert.assertEquals(CaseType.Mother.getType(),mother.getCaseType());
        Assert.assertEquals("d823ea3d392a06f8b991e9e4933348bd",mother.getFlwId());
        Assert.assertEquals("d823ea3d392a06f8b991e9e49394ce45",mother.getGroupId());
        Assert.assertEquals(DateTime.parse("2012-01-01"),mother.getTt1Date());
        Assert.assertFalse(mother.isLastPregTt());
        Assert.assertEquals(DateTime.parse("2012-10-20"),mother.getEdd());

    }

    @Test
    public void shouldSetMotherAsNotActiveIfCaseIsClosedAndCaseIdIsAMother() throws IOException {
        caseId = CaseUtils.getUniqueCaseId();

        Mother motherInDb = new Mother(caseId);
        motherInDb.setActive(true);
        allMothers.add(motherInDb);
        String path = getClass().getResource("/sampleMotherCaseForClose.xml").getPath();
        File file = new File(path);
        String xml = FileUtils.readFileToString(file);
        String modifiedXml = xml.replace("caseId", caseId);
        careCaseService.ProcessCase(modifiedXml, null);
        Mother mother = allMothers.findByCaseId(caseId);
        org.junit.Assert.assertFalse(mother.isActive());
    }

}
