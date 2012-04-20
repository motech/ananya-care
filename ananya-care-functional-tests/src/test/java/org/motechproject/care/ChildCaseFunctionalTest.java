package org.motechproject.care;

import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.care.domain.Child;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.schedule.vaccinations.ChildVaccinationSchedule;
import org.motechproject.care.utils.CaseUtils;
import org.motechproject.care.utils.SpringIntegrationTest;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ChildCaseFunctionalTest extends SpringIntegrationTest {
    @Autowired
    private AllMothers allMothers;
    @Autowired
    private AllChildren allChildren;
    String motherCaseId;
    DateTime add;

    @Before
    public void setUp(){
        motherCaseId = CaseUtils.getUniqueCaseId();
        Mother mother = new Mother(motherCaseId);
        add = DateUtil.newDateTime(DateUtil.today());
        mother.setAdd(add);
        allMothers.add(mother);
    }

    @After
    public void tearDown(){
        allMothers.removeAll();
        allChildren.removeAll();
    }

    @Test
    public void shouldCreateChild() throws IOException {
        String uniqueCaseId= CaseUtils.getUniqueCaseId();

        postChildXmlToUrl(uniqueCaseId, "sampleChildCase.xml");
        Child childFromDb = allChildren.findByCaseId(uniqueCaseId);

        Assert.assertEquals("d823ea3d392a06f8b991e9e49394ce45", childFromDb.getGroupId());
        Assert.assertEquals("d823ea3d392a06f8b991e9e4933348bd", childFromDb.getFlwId());
        Assert.assertEquals("RAM",childFromDb.getName());
        Assert.assertEquals(add , childFromDb.getDOB());
        Assert.assertTrue(childFromDb.isActive());

        markScheduleForUnEnrollment(uniqueCaseId, ChildVaccinationSchedule.Bcg.getName());
        markScheduleForUnEnrollment(uniqueCaseId, ChildVaccinationSchedule.Measles.getName());
        markScheduleForUnEnrollment(uniqueCaseId, ChildVaccinationSchedule.Vita.getName());
        Assert.assertEquals("Bcg", trackingService.getEnrollment(uniqueCaseId, ChildVaccinationSchedule.Bcg.getName()).getCurrentMilestoneName());
        Assert.assertEquals("Vita", trackingService.getEnrollment(uniqueCaseId, ChildVaccinationSchedule.Vita.getName()).getCurrentMilestoneName());
        Assert.assertEquals("Measles", trackingService.getEnrollment(uniqueCaseId, ChildVaccinationSchedule.Measles.getName()).getCurrentMilestoneName());
    }

    @Test
    public void shouldUpdateChild() throws IOException {
        String uniqueCaseId= CaseUtils.getUniqueCaseId();

        postChildXmlToUrl(uniqueCaseId, "sampleChildCase.xml");
        postChildXmlToUrl(uniqueCaseId, "sampleUpdateChildCase.xml");
        Child childFromDb = allChildren.findByCaseId(uniqueCaseId);

        Assert.assertEquals("d823ea3d392a06f8b991e9e49394ce45", childFromDb.getGroupId());
        Assert.assertEquals("d823ea3d392a06f8b991e9e4933348bd", childFromDb.getFlwId());
        Assert.assertEquals("RAM",childFromDb.getName());
        Assert.assertEquals(add , childFromDb.getDOB());
        Assert.assertEquals(DateTime.parse("2012-12-04") , childFromDb.getBcgDate());
        Assert.assertEquals(DateTime.parse("2012-12-05") , childFromDb.getMeaslesDate());
        Assert.assertTrue(childFromDb.isActive());

        markScheduleForUnEnrollment(uniqueCaseId, ChildVaccinationSchedule.Bcg.getName());
        markScheduleForUnEnrollment(uniqueCaseId, ChildVaccinationSchedule.Measles.getName());
        markScheduleForUnEnrollment(uniqueCaseId, ChildVaccinationSchedule.Vita.getName());
        assertNull(trackingService.getEnrollment(uniqueCaseId, ChildVaccinationSchedule.Bcg.getName()));
        assertNull(trackingService.getEnrollment(uniqueCaseId, ChildVaccinationSchedule.Measles.getName()));
        assertEquals("Vita", trackingService.getEnrollment(uniqueCaseId, ChildVaccinationSchedule.Vita.getName()).getCurrentMilestoneName());
    }

    private void postChildXmlToUrl(String uniqueCaseId, String xmlFileName) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        File file = new File(getClass().getResource("/"+xmlFileName).getPath());
        String body = FileUtils.readFileToString(file);
        body = body.replace("caseId", uniqueCaseId);
        body = body.replace("motherCaseId", motherCaseId);

        restTemplate.postForLocation(getAppServerHostUrl() + "/ananya-care/care/process", body);
    }
}
