package org.motechproject.care.qa;

import junit.framework.Assert;
import org.antlr.stringtemplate.StringTemplate;
import org.joda.time.LocalDate;
import org.motechproject.care.domain.CareCaseTask;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.vaccinations.MotherVaccinationSchedule;
import org.motechproject.care.utils.DbUtils;
import org.motechproject.care.utils.E2EIntegrationTestUtil;
import org.motechproject.care.utils.StringTemplateHelper;
import org.motechproject.care.utils.TestCaseThread;
import org.motechproject.commcarehq.domain.AlertDocCase;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.util.DateUtil;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

public class MotherCaseFunctionalThread extends TestCaseThread {


    private E2EIntegrationTestUtil e2EIntegrationTestUtil;
    private DbUtils dbUtils;


    public MotherCaseFunctionalThread(E2EIntegrationTestUtil e2EIntegrationTestUtil, DbUtils dbUtils) {
        this.e2EIntegrationTestUtil = e2EIntegrationTestUtil;
        this.dbUtils = dbUtils;
    }

    //shouldCreateAlertsForTt1AndTt2AndCloseCase
    @Override
    protected void test() {
        String uniqueCaseId = UUID.randomUUID().toString();

        createAMother(uniqueCaseId);

        StringTemplate stringTemplate = StringTemplateHelper.getStringTemplate("/caseXmls/pregnantMotherRegisterWithEddCaseXml.st");
        stringTemplate.setAttribute("caseId",uniqueCaseId);
        LocalDate edd = DateUtil.now().plusMonths(1).toLocalDate();
        stringTemplate.setAttribute("edd", edd.toString());
        postXmlToMotechCare(stringTemplate.toString());

        Mother motherFromDb = dbUtils.getMotherWithRetry(uniqueCaseId);

        dbUtils.markScheduleForUnEnrollment(uniqueCaseId, MotherVaccinationSchedule.TT.getName());

        Assert.assertEquals("d823ea3d392a06f8b991e9e49394ce45", motherFromDb.getGroupId());
        Assert.assertEquals("d823ea3d392a06f8b991e9e4933348bd",motherFromDb.getFlwId());
        Assert.assertEquals("NEERAJ",motherFromDb.getName());
        Assert.assertEquals(DateUtil.newDateTime(edd), motherFromDb.getEdd());
        Assert.assertEquals(false,motherFromDb.isLastPregTt());
        Assert.assertTrue(motherFromDb.isActive());

        EnrollmentRecord ttEnrollment = dbUtils.getEnrollment(uniqueCaseId, MotherVaccinationSchedule.TT.getName());
        Assert.assertEquals("TT 1", ttEnrollment.getCurrentMilestoneName());

        stringTemplate = StringTemplateHelper.getStringTemplate("/caseXmls/pregnantMotherRegisterWithEddAndTT1DateCaseXml.st");
        stringTemplate.setAttribute("caseId",uniqueCaseId);
        LocalDate tt1Date = DateUtil.now().minusMonths(2).toLocalDate();
        stringTemplate.setAttribute("edd", edd.toString());
        stringTemplate.setAttribute("tt1Date", tt1Date.toString());
        postXmlToMotechCare(stringTemplate.toString());

        AlertDocCase alertDocCase = dbUtils.getAlertDocCaseWithRetry(uniqueCaseId, MilestoneType.TT2.getTaskId());
        Assert.assertNotNull(alertDocCase);
        CareCaseTask careCaseTask = dbUtils.getCareCaseTask(uniqueCaseId, MilestoneType.TT2.getName());
        Assert.assertNotNull(careCaseTask);

        stringTemplate = StringTemplateHelper.getStringTemplate("/caseXmls/motherCloseCaseXml.st");
        stringTemplate.setAttribute("caseId",uniqueCaseId);
        postXmlToMotechCare(stringTemplate.toString());

        alertDocCase = dbUtils.getAlertDocCaseWithRetry(alertDocCase.getCaseId(), true);
        Assert.assertNotNull(alertDocCase);
    }

    private void createAMother(String uniqueCaseId) {
        StringTemplate stringTemplate = StringTemplateHelper.getStringTemplate("/caseXmls/pregnantMotherNewCaseXml.st");
        stringTemplate.setAttribute("caseId",uniqueCaseId);
        postXmlToMotechCare(stringTemplate.toString());
    }

    protected void postXmlToMotechCare(String xmlBody) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForLocation(e2EIntegrationTestUtil.getAppServerUrl(), xmlBody);
    }
}