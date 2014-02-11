package org.motechproject.care;

import junit.framework.Assert;
import org.antlr.stringtemplate.StringTemplate;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.schedule.vaccinations.ExpirySchedule;
import org.motechproject.care.schedule.vaccinations.MotherVaccinationSchedule;
import org.motechproject.care.utils.StringTemplateHelper;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.commons.date.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.UUID;

public class MotherCaseFunctionalIT extends SpringIntegrationTest {
    @Autowired
    private AllMothers allMothers;
    String userId = "e819879aaf53a3787e0fd88993ac105d";
    String ownerId = "d823ea3d392a06f8b991e9e49394ce45";

    @After
    public void tearDown(){
        allMothers.removeAll();
    }

    @Test
    public void shouldCreateMother() throws IOException {
        String uniqueCaseId= UUID.randomUUID().toString();
        createAMother(uniqueCaseId);

        Mother motherFromDb = allMothers.findByCaseId(uniqueCaseId);

        Assert.assertEquals(ownerId, motherFromDb.getGroupId());
        Assert.assertEquals(userId,motherFromDb.getFlwId());
        Assert.assertEquals("NEERAJ",motherFromDb.getName());
        Assert.assertEquals(null,motherFromDb.getEdd());
        Assert.assertEquals(false,motherFromDb.isLastPregTt());
        Assert.assertTrue(motherFromDb.isActive());

        EnrollmentRecord ttEnrollment = trackingService.getEnrollment(uniqueCaseId, MotherVaccinationSchedule.TT.getName());
        Assert.assertNull(ttEnrollment);

        EnrollmentRecord ancEnrollment = trackingService.getEnrollment(uniqueCaseId, MotherVaccinationSchedule.Anc.getName());
        Assert.assertNull(ancEnrollment);

        EnrollmentRecord motherCareEnrollment = trackingService.getEnrollment(uniqueCaseId, ExpirySchedule.MotherCare.getName());
        Assert.assertNull(motherCareEnrollment);
    }

    @Test
    public void shouldUpdateMother() throws IOException {
        String uniqueCaseId = UUID.randomUUID().toString();

        createAMother(uniqueCaseId);

        StringTemplate stringTemplate = StringTemplateHelper.getStringTemplate("/caseXmls/pregnantMotherRegisterWithEddCaseXml.st");
        stringTemplate.setAttribute("caseId",uniqueCaseId);
        stringTemplate.setAttribute("userId",userId);
        stringTemplate.setAttribute("ownerId",ownerId);

        LocalDate edd = DateUtil.now().plusMonths(1).toLocalDate();
        stringTemplate.setAttribute("edd", edd.toString());
        postXmlToMotechCare(stringTemplate.toString());

        Mother motherFromDb = allMothers.findByCaseId(uniqueCaseId);

        markScheduleForUnEnrollment(uniqueCaseId, MotherVaccinationSchedule.TT.getName());

        Assert.assertEquals("d823ea3d392a06f8b991e9e49394ce45",motherFromDb.getGroupId());
        Assert.assertEquals("e819879aaf53a3787e0fd88993ac105d",motherFromDb.getFlwId());
        Assert.assertEquals("NEERAJ",motherFromDb.getName());
        Assert.assertEquals(DateUtil.newDateTime(edd), motherFromDb.getEdd());
        Assert.assertEquals(false,motherFromDb.isLastPregTt());
        Assert.assertTrue(motherFromDb.isActive());

        markScheduleForUnEnrollment(uniqueCaseId, MotherVaccinationSchedule.TT.getName());
        markScheduleForUnEnrollment(uniqueCaseId, MotherVaccinationSchedule.Anc.getName());
        markScheduleForUnEnrollment(uniqueCaseId, ExpirySchedule.MotherCare.getName());

        EnrollmentRecord ttEnrollment = trackingService.getEnrollment(uniqueCaseId, MotherVaccinationSchedule.TT.getName());
        Assert.assertEquals("TT 1", ttEnrollment.getCurrentMilestoneName());

        EnrollmentRecord ancEnrollment = trackingService.getEnrollment(uniqueCaseId, MotherVaccinationSchedule.Anc.getName());
        Assert.assertEquals("Anc 1", ancEnrollment.getCurrentMilestoneName());

        EnrollmentRecord motherCareEnrollment = trackingService.getEnrollment(uniqueCaseId, ExpirySchedule.MotherCare.getName());
        Assert.assertEquals("Mother Care", motherCareEnrollment.getCurrentMilestoneName());
    }

    @Test
    public void shouldCloseMother() throws IOException {
        String uniqueCaseId = UUID.randomUUID().toString();

        createAMother(uniqueCaseId);

        Mother motherFromDb = allMothers.findByCaseId(uniqueCaseId);

        Assert.assertTrue(motherFromDb.isActive());

        StringTemplate stringTemplate = StringTemplateHelper.getStringTemplate("/caseXmls/motherCloseCaseXml.st");
        stringTemplate.setAttribute("caseId",uniqueCaseId);
        stringTemplate.setAttribute("userId",userId);
        stringTemplate.setAttribute("ownerId",ownerId);
        postXmlToMotechCare(stringTemplate.toString());

        motherFromDb = allMothers.findByCaseId(uniqueCaseId);

        markScheduleForUnEnrollment(uniqueCaseId, MotherVaccinationSchedule.TT.getName());
        Assert.assertFalse(motherFromDb.isActive());
    }

    private void createAMother(String uniqueCaseId) throws IOException {
        StringTemplate stringTemplate = StringTemplateHelper.getStringTemplate("/caseXmls/pregnantMotherNewCaseXml.st");
        stringTemplate.setAttribute("caseId",uniqueCaseId);
        stringTemplate.setAttribute("userId",userId);
        stringTemplate.setAttribute("ownerId",ownerId);
        postXmlToMotechCare(stringTemplate.toString());
    }

    private void postXmlToMotechCare(String xmlBody) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForLocation(getAppServerUrl(), xmlBody);
    }
}