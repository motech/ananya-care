package org.motechproject.care.qa;

import junit.framework.Assert;
import org.antlr.stringtemplate.StringTemplate;
import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.care.repository.AllCareCaseTasks;
import org.motechproject.care.tools.QuartzWrapper;
import org.motechproject.care.utils.DbUtils;
import org.motechproject.care.utils.StringTemplateHelper;
import org.motechproject.commons.date.util.DateUtil;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;

@Ignore("This test should be run by E2ETestsRunner class which would run this test in parallel thread")
public class MotherCaseMultipleSimultaneousUpdatesThread extends E2EIntegrationTest {

    private final String userId;
    private final String ownerId;
    private String caseId;
    private LocalDate edd;

    public MotherCaseMultipleSimultaneousUpdatesThread(Properties ananyaCareProperties, DbUtils dbUtils, String userId, String ownerId, String caseId, QuartzWrapper quartzWrapper, AllCareCaseTasks allCareCaseTasks) {
        super(ananyaCareProperties, dbUtils);
        this.userId = userId;
        this.ownerId = ownerId;
        this.caseId = caseId;
        initMother();
    }

    private void initMother() {
        createAMother(caseId);
        edd = DateUtil.now().plusMonths(1).toLocalDate();
        StringTemplate stringTemplate = fillBasicStringTemplateDetails(caseId, "pregnantMotherRegisterWithEddCaseXml.st");
        stringTemplate.setAttribute("edd", edd.toString());
        postXmlToMotechCare(stringTemplate.toString());
    }

    @Test
    public void shouldHandleMultipleUpdatesForTheSameCaseId() {
        LocalDate tt1Date = DateUtil.now().minusDays(1).toLocalDate();
        StringTemplate stringTemplate = fillBasicStringTemplateDetails(caseId, "pregnantMotherRegisterWithEddAndTT1DateCaseXml.st");
        stringTemplate.setAttribute("edd", edd.toString());
        stringTemplate.setAttribute("tt1Date", tt1Date.toString());
        postXmlToMotechCare(stringTemplate.toString());
    }

    private void createAMother(String uniqueCaseId) {
        StringTemplate stringTemplate = StringTemplateHelper.getStringTemplate("/caseXmls/pregnantMotherNewCaseXml.st");
        stringTemplate.setAttribute("caseId",uniqueCaseId);
        stringTemplate.setAttribute("userId",userId);
        stringTemplate.setAttribute("ownerId",ownerId);
        postXmlToMotechCare(stringTemplate.toString());
    }

    private StringTemplate fillBasicStringTemplateDetails(String uniqueCaseId, String stringTemplateName) {
        StringTemplate stringTemplate = StringTemplateHelper.getStringTemplate("/caseXmls/" + stringTemplateName);
        stringTemplate.setAttribute("caseId",uniqueCaseId);
        stringTemplate.setAttribute("userId",userId);
        stringTemplate.setAttribute("ownerId",ownerId);
        return stringTemplate;
    }


    protected void postXmlToMotechCare(String xmlBody) {
        Assert.assertFalse("All attributes are not replaced in the xml", xmlBody.contains("$"));
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForLocation(getAppServerUrl(), xmlBody);
    }
}
