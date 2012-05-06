package org.motechproject.care;

import junit.framework.Assert;
import org.antlr.stringtemplate.StringTemplate;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.care.domain.Child;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.schedule.vaccinations.ChildVaccinationSchedule;
import org.motechproject.care.utils.StringTemplateHelper;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ChildCaseFunctionalIT extends SpringIntegrationTest {
    @Autowired
    private AllChildren allChildren;
    String motherCaseId;
    DateTime dob;

    @Before
    public void setUp(){
        motherCaseId = UUID.randomUUID().toString();
        dob = DateUtil.newDateTime(DateUtil.today());
    }

    @After
    public void tearDown(){
        allChildren.removeAll();
    }

    @Test
    public void shouldCreateChild() throws IOException {
        String uniqueCaseId= UUID.randomUUID().toString();

        postChildXmlToLocalHost(uniqueCaseId, "/caseXmls/childRegistrationCaseXml.st");
        Child childFromDb = allChildren.findByCaseId(uniqueCaseId);

        Assert.assertEquals("d823ea3d392a06f8b991e9e49394ce45", childFromDb.getGroupId());
        Assert.assertEquals("d823ea3d392a06f8b991e9e4933348bd", childFromDb.getFlwId());
        Assert.assertEquals("RAM",childFromDb.getName());
        Assert.assertEquals(dob, childFromDb.getDOB());
        Assert.assertEquals(motherCaseId, childFromDb.getMotherCaseId());
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
        String uniqueCaseId= UUID.randomUUID().toString();

        postChildXmlToLocalHost(uniqueCaseId, "/caseXmls/childRegistrationCaseXml.st");
        postChildXmlToLocalHost(uniqueCaseId, "/caseXmls/childUpdateCaseXml.st");
        Child childFromDb = allChildren.findByCaseId(uniqueCaseId);

        Assert.assertEquals("d823ea3d392a06f8b991e9e49394ce45", childFromDb.getGroupId());
        Assert.assertEquals("d823ea3d392a06f8b991e9e4933348bd", childFromDb.getFlwId());
        Assert.assertEquals("RAM",childFromDb.getName());
        Assert.assertEquals(dob, childFromDb.getDOB());
        Assert.assertEquals(motherCaseId, childFromDb.getMotherCaseId());
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

    private void postChildXmlToLocalHost(String uniqueCaseId, String xmlFileName) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        StringTemplate stringTemplate = StringTemplateHelper.getStringTemplate(xmlFileName);
        stringTemplate.setAttribute("caseId",uniqueCaseId);
        stringTemplate.setAttribute("dobDate",DateUtil.newDate(dob).toString());
        stringTemplate.setAttribute("motherCaseId",motherCaseId);

        restTemplate.postForLocation(getAppServerHostUrl() + "/ananya-care/care/process", stringTemplate.toString());
    }
}
