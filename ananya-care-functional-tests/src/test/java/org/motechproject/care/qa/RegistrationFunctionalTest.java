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
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.request.CaseType;
import org.motechproject.care.schedule.service.TTSchedulerService;
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
@Ignore
public class RegistrationFunctionalTest extends SpringQAIntegrationTest{

    @Autowired
    private AllMothers allMothers;

    @Autowired
    private AllAlertDocCases allAlertDocCases;

    @Autowired
    private ScheduleTrackingService trackingService;


    @Test
    public void shouldPreRegisterAPregnantMother() throws IOException {
        final String caseId = UUID.randomUUID().toString();
        String instanceId = UUID.randomUUID().toString();
        String name = "test_gen" + Math.random();

        StringTemplate stringTemplate = getStringTemplate("/preregister_pregnantmother.st");
        stringTemplate.setAttribute("caseId",caseId);
        stringTemplate.setAttribute("instanceId",instanceId);
        stringTemplate.setAttribute("name", name);
        String final_xml = stringTemplate.toString();


        HttpResponse response = postToCommCare(final_xml);
        StatusLine statusLine = response.getStatusLine();
        Assert.assertEquals(201,statusLine.getStatusCode());
        Assert.assertEquals("CREATED",statusLine.getReasonPhrase());

        RetryTask<Mother> task = new RetryTask<Mother>() {
            @Override
            protected Mother perform() {
               return allMothers.findByCaseId(caseId);
            }
        };

        Mother mother = task.execute(100, 1000);
        markForDeletion(mother);
        markScheduleForUnEnrollment(caseId, TTSchedulerService.tt1Milestone);
        Assert.assertEquals(name, mother.getName());
        Assert.assertEquals("d823ea3d392a06f8b991e9e4933348bd", mother.getFlwId());
        Assert.assertEquals("d823ea3d392a06f8b991e9e49394ce45", mother.getGroupId());
        Assert.assertEquals(CaseType.Mother.getType(), mother.getCaseType());
    }

    @Test
    //TO be fixed asap.
    public void shouldSendATT1AlertForAPregnantMother() throws IOException {

        final String caseId = UUID.randomUUID().toString();
        String instanceId = UUID.randomUUID().toString();
        String name = "test_gen" + Math.random();

        StringTemplate stringTemplate = getStringTemplate("/register_pregnantmother_with_edd.st");
        stringTemplate.setAttribute("caseId",caseId);
        stringTemplate.setAttribute("instanceId",instanceId);
        stringTemplate.setAttribute("edd", DateUtil.now().toLocalDate().toString());
        String final_xml = stringTemplate.toString();


        HttpResponse response = postToCommCare(final_xml);
        StatusLine statusLine = response.getStatusLine();
        Assert.assertEquals(201,statusLine.getStatusCode());
        Assert.assertEquals("CREATED",statusLine.getReasonPhrase());

        RetryTask<Mother> task = new RetryTask<Mother>() {
            @Override
            protected Mother perform() {
                return allMothers.findByCaseId(caseId);
            }
        };

        Mother mother = task.execute(100, 1000);
        markForDeletion(mother);
        markScheduleForUnEnrollment(caseId, TTSchedulerService.tt1Milestone);
        Assert.assertEquals(name, mother.getName());
        Assert.assertEquals("d823ea3d392a06f8b991e9e4933348bd", mother.getFlwId());
        Assert.assertEquals(CaseType.Mother.getType(), mother.getCaseType());


        RetryTask<AlertDocCase> taskForGettingAlertDocCase = new RetryTask<AlertDocCase>() {
            @Override
            protected AlertDocCase perform() {
                return allAlertDocCases.findByCaseId(caseId);
            }
        };

        AlertDocCase alertDocCase = taskForGettingAlertDocCase.execute(300, 1000);
        Assert.assertTrue(alertDocCase.getXmlDocument().contains("xcxfdxcxcx"));

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


