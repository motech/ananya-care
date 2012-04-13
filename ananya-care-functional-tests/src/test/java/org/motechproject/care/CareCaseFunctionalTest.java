package org.motechproject.care;

import org.junit.After;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.motechproject.care.repository.AllMothers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-FunctionalTests.xml")
@Ignore
public class CareCaseFunctionalTest {
    @Autowired
    private AllMothers allMothers;

    @After
    public void after(){
        allMothers.removeAll();
    }

//    @Test
//    public void shouldCreateMother() throws IOException {
//        String uniqueCaseId=CaseUtils.getUniqueCaseId();
//
//        postXmlToUrl(uniqueCaseId, "sampleMotherCase.xml");
//        Mother motherFromDb = allMothers.findByCaseId(uniqueCaseId);
//
//        Assert.assertEquals("d823ea3d392a06f8b991e9e49394ce45",motherFromDb.getGroupId());
//        Assert.assertEquals("d823ea3d392a06f8b991e9e4933348bd",motherFromDb.getFlwId());
//        Assert.assertEquals("NEERAJ",motherFromDb.getName());
//        Assert.assertEquals(DateTime.parse("2012-10-20"),motherFromDb.getEdd());
//        Assert.assertEquals(DateTime.parse("2012-01-01"),motherFromDb.getTt1Date());
//        Assert.assertEquals(false,motherFromDb.isLastPregTt());
//        Assert.assertTrue(motherFromDb.isActive());
//    }
//
//    @Test
//    public void shouldUpdateMother() throws IOException {
//        String uniqueCaseId = CaseUtils.getUniqueCaseId();
//
//        postXmlToUrl(uniqueCaseId, "sampleMotherCase.xml");
//
//        postXmlToUrl(uniqueCaseId, "sampleMotherCaseForUpdate.xml");
//        Mother motherFromDb = allMothers.findByCaseId(uniqueCaseId);
//
//        Assert.assertEquals("d823ea3d392a06f8b991e9e49394ce45",motherFromDb.getGroupId());
//        Assert.assertEquals("d823ea3d392a06f8b991e9e4933348bd",motherFromDb.getFlwId());
//        Assert.assertEquals("NEERAJ",motherFromDb.getName());
//        Assert.assertEquals(DateTime.parse("2012-10-20"),motherFromDb.getEdd());
//        Assert.assertEquals(DateTime.parse("2012-10-21"),motherFromDb.getAdd());
//        Assert.assertEquals(DateTime.parse("2012-01-01"),motherFromDb.getTt1Date());
//        Assert.assertEquals(false,motherFromDb.isLastPregTt());
//        Assert.assertEquals(DateTime.parse("2012-01-02"),motherFromDb.getTt2Date());
//        Assert.assertFalse(motherFromDb.isActive());
//    }
//
//    private void postXmlToUrl(String uniqueCaseId, String xmlFileName) throws IOException {
//        RestTemplate restTemplate = new RestTemplate();
//        File file = new File(getClass().getResource("/"+xmlFileName).getPath());
//        String body = FileUtils.readFileToString(file);
//        String modifiedXml = body.replace("caseId", uniqueCaseId);
//
//        restTemplate.postForLocation(getAppServerHostUrl() + "/ananya-care/care/process", modifiedXml);
//    }
//
//    @Test
//    public void shouldCloseMother() throws IOException {
//        String uniqueCaseId = CaseUtils.getUniqueCaseId();
//
//        postXmlToUrl(uniqueCaseId, "sampleMotherCase.xml");
//
//        Mother motherFromDb = allMothers.findByCaseId(uniqueCaseId);
//
//        Assert.assertTrue(motherFromDb.isActive());
//
//        postXmlToUrl(uniqueCaseId, "sampleMotherCaseForClose.xml");
//        motherFromDb = allMothers.findByCaseId(uniqueCaseId);
//
//        Assert.assertFalse(motherFromDb.isActive());
//    }
}
