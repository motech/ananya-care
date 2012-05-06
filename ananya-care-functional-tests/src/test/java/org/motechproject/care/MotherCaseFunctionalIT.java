package org.motechproject.care;

import junit.framework.Assert;
import org.antlr.stringtemplate.StringTemplate;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.schedule.vaccinations.MotherVaccinationSchedule;
import org.motechproject.care.utils.StringTemplateHelper;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.UUID;

public class MotherCaseFunctionalIT extends SpringIntegrationTest {
    @Autowired
    private AllMothers allMothers;

    @After
    public void tearDown(){
        allMothers.removeAll();
    }

    @Test
    public void shouldCreateMother() throws IOException {
        String uniqueCaseId= UUID.randomUUID().toString();
        createAMother(uniqueCaseId);

        Mother motherFromDb = allMothers.findByCaseId(uniqueCaseId);

        Assert.assertEquals("d823ea3d392a06f8b991e9e49394ce45", motherFromDb.getGroupId());
        Assert.assertEquals("d823ea3d392a06f8b991e9e4933348bd",motherFromDb.getFlwId());
        Assert.assertEquals("NEERAJ",motherFromDb.getName());
        Assert.assertEquals(DateTime.parse("2012-10-20"),motherFromDb.getEdd());
        Assert.assertEquals(false,motherFromDb.isLastPregTt());
        Assert.assertTrue(motherFromDb.isActive());

        markScheduleForUnEnrollment(uniqueCaseId, MotherVaccinationSchedule.TT.getName());
        EnrollmentRecord ttEnrollment = trackingService.getEnrollment(uniqueCaseId, MotherVaccinationSchedule.TT.getName());
        Assert.assertEquals("TT 1", ttEnrollment.getCurrentMilestoneName());
    }

    @Test
    public void shouldUpdateMother() throws IOException {
        String uniqueCaseId = UUID.randomUUID().toString();

        createAMother(uniqueCaseId);

        StringTemplate stringTemplate = StringTemplateHelper.getStringTemplate("/caseXmls/pregnantMotherRegisterWithEddCaseXml.st");
        stringTemplate.setAttribute("caseId",uniqueCaseId);
        postXmlToLocalHost(stringTemplate.toString());

        Mother motherFromDb = allMothers.findByCaseId(uniqueCaseId);

        markScheduleForUnEnrollment(uniqueCaseId, MotherVaccinationSchedule.TT.getName());
        Assert.assertEquals("d823ea3d392a06f8b991e9e49394ce45",motherFromDb.getGroupId());
        Assert.assertEquals("d823ea3d392a06f8b991e9e4933348bd",motherFromDb.getFlwId());
        Assert.assertEquals("NEERAJ",motherFromDb.getName());
        Assert.assertEquals(DateTime.parse("2012-10-20"),motherFromDb.getEdd());
        Assert.assertEquals(DateTime.parse("2012-10-21"),motherFromDb.getAdd());
        Assert.assertEquals(false,motherFromDb.isLastPregTt());
        Assert.assertEquals(DateTime.parse("2012-01-02"),motherFromDb.getTt1Date());
        Assert.assertFalse(motherFromDb.isActive());
    }

    @Test
    public void shouldCloseMother() throws IOException {
        String uniqueCaseId = UUID.randomUUID().toString();

        createAMother(uniqueCaseId);

        Mother motherFromDb = allMothers.findByCaseId(uniqueCaseId);

        Assert.assertTrue(motherFromDb.isActive());


        StringTemplate stringTemplate = StringTemplateHelper.getStringTemplate("/caseXmls/motherCloseCaseXml.st");
        stringTemplate.setAttribute("caseId",uniqueCaseId);
        postXmlToLocalHost(stringTemplate.toString());

        motherFromDb = allMothers.findByCaseId(uniqueCaseId);

        markScheduleForUnEnrollment(uniqueCaseId, MotherVaccinationSchedule.TT.getName());
        Assert.assertFalse(motherFromDb.isActive());
    }

    private void createAMother(String uniqueCaseId) throws IOException {
        StringTemplate stringTemplate = StringTemplateHelper.getStringTemplate("/caseXmls/pregnantMotherNewCaseXml.st");
        stringTemplate.setAttribute("caseId",uniqueCaseId);
        postXmlToLocalHost(stringTemplate.toString());
    }

    private void postXmlToLocalHost(String xmlBody) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForLocation(getAppServerHostUrl() + "/ananya-care/care/process", xmlBody);
    }
}
