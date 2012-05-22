package org.motechproject.care.qa;

import junit.framework.Assert;
import org.antlr.stringtemplate.StringTemplate;
import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.care.domain.CareCaseTask;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.schedule.vaccinations.MotherVaccinationSchedule;
import org.motechproject.care.utils.DbUtils;
import org.motechproject.care.utils.StringTemplateHelper;
import org.motechproject.commcarehq.domain.AlertDocCase;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.util.DateUtil;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;
import java.util.UUID;

@Ignore("This test should be run by E2ETestsRunner class which would run this test in parallel thread")
public class MotherCaseFunctionalThread extends  E2EIntegrationTest {

    private final String userId;
    private final String ownerId;

    public MotherCaseFunctionalThread(Properties ananyaCareProperties, DbUtils dbUtils, String userId, String ownerId) {
        super(ananyaCareProperties, dbUtils);
        this.userId = userId;
        this.ownerId = ownerId;
    }


    @Test
    public void shouldCreateAlertsForTt1AndTt2AndCloseCase() {
        String uniqueCaseId = UUID.randomUUID().toString();

        createAMother(uniqueCaseId);

        LocalDate edd = DateUtil.now().plusMonths(1).toLocalDate();
        postAndVerifyTt1AlertIsRaised(uniqueCaseId, edd);
        postAndVerifyTt2AlertIsRaised(uniqueCaseId, edd);
        postAndVerifyCloseAlertIsRaised(uniqueCaseId);
    }

    private void postAndVerifyCloseAlertIsRaised(String uniqueCaseId) {
        AlertDocCase alertForTT2 = dbUtils.getAlertDocCaseWithRetry(uniqueCaseId, MilestoneType.TT2.getTaskId());

        StringTemplate stringTemplate = fillBasicStringTemplateDetails(uniqueCaseId, "motherCloseCaseXml.st");
        postXmlToMotechCare(stringTemplate.toString());

        AlertDocCase alertForClose = dbUtils.getAlertDocCaseWithRetry(alertForTT2.getCaseId(), true);

        Assert.assertNotNull(alertForClose);
    }

    private void postAndVerifyTt2AlertIsRaised(String uniqueCaseId, LocalDate edd) {

        LocalDate tt1Date = DateUtil.now().minusMonths(2).toLocalDate();
        StringTemplate stringTemplate = fillBasicStringTemplateDetails(uniqueCaseId, "pregnantMotherRegisterWithEddAndTT1DateCaseXml.st");
        stringTemplate.setAttribute("edd", edd.toString());
        stringTemplate.setAttribute("tt1Date", tt1Date.toString());
        postXmlToMotechCare(stringTemplate.toString());

        AlertDocCase alertForTT2 = dbUtils.getAlertDocCaseWithRetry(uniqueCaseId, MilestoneType.TT2.getTaskId());
        Assert.assertNotNull(alertForTT2);
        CareCaseTask careCaseTask = dbUtils.getCareCaseTask(uniqueCaseId, MilestoneType.TT2.getName());
        Assert.assertNotNull(careCaseTask);
    }

    private void postAndVerifyTt1AlertIsRaised(String uniqueCaseId, LocalDate edd) {
        StringTemplate stringTemplate = fillBasicStringTemplateDetails(uniqueCaseId, "pregnantMotherRegisterWithEddCaseXml.st");
        stringTemplate.setAttribute("edd", edd.toString());
        postXmlToMotechCare(stringTemplate.toString());

        Mother motherFromDb = dbUtils.getMotherWithRetry(uniqueCaseId);

        markScheduleForUnEnrollment(uniqueCaseId, MotherVaccinationSchedule.TT.getName());

        Assert.assertEquals(ownerId, motherFromDb.getGroupId());
        Assert.assertEquals(userId, motherFromDb.getFlwId());
        Assert.assertEquals("NEERAJ",motherFromDb.getName());
        Assert.assertEquals(DateUtil.newDateTime(edd), motherFromDb.getEdd());
        Assert.assertEquals(false,motherFromDb.isLastPregTt());
        Assert.assertTrue(motherFromDb.isActive());

        EnrollmentRecord ttEnrollment = dbUtils.getEnrollment(uniqueCaseId, MotherVaccinationSchedule.TT.getName());
        Assert.assertEquals("TT 1", ttEnrollment.getCurrentMilestoneName());

        AlertDocCase alertForTT1 = dbUtils.getAlertDocCaseWithRetry(uniqueCaseId, MilestoneType.TT1.getTaskId());
        Assert.assertNotNull(alertForTT1);
    }

    private StringTemplate fillBasicStringTemplateDetails(String uniqueCaseId, String stringTemplateName) {
        StringTemplate stringTemplate = StringTemplateHelper.getStringTemplate("/caseXmls/" + stringTemplateName);
        stringTemplate.setAttribute("caseId",uniqueCaseId);
        stringTemplate.setAttribute("userId",userId);
        stringTemplate.setAttribute("ownerId",ownerId);
        return stringTemplate;
    }

    private void createAMother(String uniqueCaseId) {
        StringTemplate stringTemplate = StringTemplateHelper.getStringTemplate("/caseXmls/pregnantMotherNewCaseXml.st");
        stringTemplate.setAttribute("caseId",uniqueCaseId);
        stringTemplate.setAttribute("userId",userId);
        stringTemplate.setAttribute("ownerId",ownerId);
        postXmlToMotechCare(stringTemplate.toString());
    }

    protected void postXmlToMotechCare(String xmlBody) {
        Assert.assertFalse("All attributes are not replaced in the xml", xmlBody.contains("$"));
        RestTemplate restTemplate = new RestTemplate();
        int counter = 10;
        while(true) {
            counter--;
            try {
                restTemplate.postForLocation(getAppServerUrl(), xmlBody);
                break;
            } catch (RuntimeException ex) {
                if(counter == 1) {
                    throw ex;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }
            }
        }
    }
}