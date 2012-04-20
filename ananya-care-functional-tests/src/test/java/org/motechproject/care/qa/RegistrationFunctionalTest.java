package org.motechproject.care.qa;

import junit.framework.Assert;
import org.antlr.stringtemplate.StringTemplate;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.request.CaseType;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.utils.RetryTask;
import org.motechproject.care.utils.TextHelper;
import org.motechproject.commcarehq.domain.AlertDocCase;
import org.motechproject.commcarehq.repository.AllAlertDocCases;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
public class RegistrationFunctionalTest extends SpringQAIntegrationTest{

    @Autowired
    private AllMothers allMothers;

    @Autowired
    private AllAlertDocCases allAlertDocCases;

    @Autowired
    private ScheduleTrackingService trackingService;

    @Test
    public void shouldSendATT1AlertForAPregnantMother() throws IOException {

        final String caseId = UUID.randomUUID().toString();
        String instanceId = UUID.randomUUID().toString();
        String name = "test_gen" + Math.random();

        postXmlWithAttributes(caseId, instanceId, name, null, "/pregnantmother_new.st");

        RetryTask<Mother> task = new RetryTask<Mother>() {
            @Override
            protected Mother perform() {
                return allMothers.findByCaseId(caseId);
            }
        };

        Mother mother = task.execute(120, 1000);
        Assert.assertNotNull(mother);
        markForDeletion(mother);
        markScheduleForUnEnrollment(caseId, MilestoneType.TT1.toString());
        Assert.assertEquals(name, mother.getName());
        Assert.assertEquals("d823ea3d392a06f8b991e9e4933348bd", mother.getFlwId());
        Assert.assertEquals(CaseType.Mother.getType(), mother.getCaseType());

        instanceId = UUID.randomUUID().toString();
        String edd = DateUtil.now().plusMonths(1).toLocalDate().toString();
        postXmlWithAttributes(caseId, instanceId, name, edd, "/pregnantmother_register_with_edd.st");


        RetryTask<AlertDocCase> taskForGettingAlertDocCase = new RetryTask<AlertDocCase>() {
            @Override
            protected AlertDocCase perform() {
                return allAlertDocCases.findByCaseId(caseId);
            }
        };
        AlertDocCase alertDocCase = taskForGettingAlertDocCase.execute(300, 1000);
        Assert.assertNotNull(alertDocCase);
        markForDeletion(alertDocCase);
        Assert.assertTrue(alertDocCase.getXmlDocument().contains(caseId));

    }

    private void postXmlWithAttributes(String caseId, String instanceId, String name, String edd, String templateFilePath) throws IOException {
        StringTemplate stringTemplate = getStringTemplate(templateFilePath);
        stringTemplate.setAttribute("caseId",caseId);
        stringTemplate.setAttribute("instanceId",instanceId);
        stringTemplate.setAttribute("name", name);
        stringTemplate.setAttribute("edd", edd);
        String final_xml = stringTemplate.toString();

        HttpResponse response = postToCommCare(final_xml);
        StatusLine statusLine = response.getStatusLine();
        Assert.assertEquals(201, statusLine.getStatusCode());
        Assert.assertEquals("CREATED",statusLine.getReasonPhrase());
    }

    private HttpResponse postToCommCare(String final_xml) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("https://www.commcarehq.org/a/ananya-care/receiver/");

        InputStreamBody inputStreamBody = new InputStreamBody(new ByteArrayInputStream(final_xml.getBytes()), "file.xml");
        MultipartEntity reqEntity = new MultipartEntity();
        reqEntity.addPart("xml_submission_file", inputStreamBody);
        httpPost.setEntity(reqEntity);

        return httpclient.execute(httpPost);
    }

    private StringTemplate getStringTemplate(String templateFilePath) {
        InputStream resourceAsStream = getClass().getResourceAsStream(templateFilePath);
        String template = TextHelper.GetText(resourceAsStream);
        return new StringTemplate(template);
    }

}


