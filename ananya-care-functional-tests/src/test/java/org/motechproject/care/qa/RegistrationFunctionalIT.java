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
import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.care.domain.Child;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllChildren;
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
import java.util.HashMap;
import java.util.UUID;

public class RegistrationFunctionalIT extends SpringQAIntegrationTest {

    @Autowired
    private AllMothers allMothers;

    @Autowired
    private AllChildren allChildren;

    @Autowired
    private AllAlertDocCases allAlertDocCases;

    @Autowired
    private ScheduleTrackingService trackingService;

    @Test
    public void shouldSendBCGAlertForANewBornChild() throws IOException {
        final String motherCaseId = UUID.randomUUID().toString();
        String motherInstanceId = UUID.randomUUID().toString();
        double random = Math.random();
        String motherName = "mother_test_gen" + random;

        HashMap<String, String> motherAttributes = new HashMap<String, String>();
        motherAttributes.put("caseId", motherCaseId);
        motherAttributes.put("instanceId", motherInstanceId);
        motherAttributes.put("name", motherName);

        postXmlWithAttributes(motherAttributes, "/pregnantmother_new.st");

        RetryTask<Mother> taskToFetchMother = new RetryTask<Mother>() {
            @Override
            protected Mother perform() {
                return allMothers.findByCaseId(motherCaseId);
            }
        };

        Mother mother = taskToFetchMother.execute(120, 1000);
        Assert.assertNotNull(mother);
        markForDeletion(mother);
        markScheduleForUnEnrollment(motherCaseId, MilestoneType.TT1.toString());

        final String childCaseId = UUID.randomUUID().toString();
        String childInstanceId = UUID.randomUUID().toString();
        String childName = "child_test_gen" + random;
        LocalDate dob = DateUtil.now().minusDays(1).toLocalDate();

        HashMap<String, String> childAttributes = new HashMap<String, String>();
        childAttributes.put("caseId", childCaseId);
        childAttributes.put("instanceId", childInstanceId);
        childAttributes.put("name", childName);
        childAttributes.put("motherCaseId", motherCaseId);
        childAttributes.put("dob", dob.toString());

        postXmlWithAttributes(childAttributes, "/newbornchild.st");
        RetryTask<Child> taskToFetchChild = new RetryTask<Child>() {
            @Override
            protected Child perform() {
                return allChildren.findByCaseId(childCaseId);
            }
        };

        Child child = taskToFetchChild.execute(120, 1000);
        Assert.assertNotNull(child);
        Assert.assertEquals(childName, child.getName());
        Assert.assertEquals("d823ea3d392a06f8b991e9e4933348bd", child.getFlwId());
        Assert.assertEquals(CaseType.Child.getType(), child.getCaseType());
        Assert.assertEquals(DateUtil.newDateTime(dob), child.getDOB());
        markForDeletion(child);
        markScheduleForUnEnrollment(childCaseId, MilestoneType.Bcg.toString());

        RetryTask<AlertDocCase> taskToFetchAlertDocCase = new RetryTask<AlertDocCase>() {
            @Override
            protected AlertDocCase perform() {
                return allAlertDocCases.findByCaseId(childCaseId);
            }
        };
        AlertDocCase alertDocCase = taskToFetchAlertDocCase.execute(300, 1000);
        Assert.assertNotNull(alertDocCase);
        markAlertDocCaseForDeletion(alertDocCase);
        Assert.assertTrue(alertDocCase.getXmlDocument().contains(childCaseId));
        Assert.assertTrue(alertDocCase.getXmlDocument().contains("<task_id>bcg</task_id>"));
    }

    @Test
    public void shouldSendATT1AlertForAPregnantMother() throws IOException {

        final String caseId = UUID.randomUUID().toString();
        String instanceId = UUID.randomUUID().toString();
        String name = "mother_test_gen" + Math.random();
        HashMap<String, String> attributes = new HashMap<String, String>();
        attributes.put("caseId", caseId);
        attributes.put("instanceId", instanceId);
        attributes.put("name", name);

        postXmlWithAttributes(attributes, "/pregnantmother_new.st");

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
        attributes.put("instanceId", instanceId);
        attributes.put("edd", edd);
        postXmlWithAttributes(attributes, "/pregnantmother_register_with_edd.st");


        RetryTask<AlertDocCase> taskToFetchAlertDocCase = new RetryTask<AlertDocCase>() {
            @Override
            protected AlertDocCase perform() {
                return allAlertDocCases.findByCaseId(caseId);
            }
        };
        AlertDocCase alertDocCase = taskToFetchAlertDocCase.execute(300, 1000);
        Assert.assertNotNull(alertDocCase);
        markAlertDocCaseForDeletion(alertDocCase);
        Assert.assertTrue(alertDocCase.getXmlDocument().contains(caseId));
        Assert.assertTrue(alertDocCase.getXmlDocument().contains("<task_id>tt_1</task_id>"));
    }

    private void postXmlWithAttributes(HashMap<String, String> attributes, String templateFilePath) throws IOException {
        StringTemplate stringTemplate = getStringTemplate(templateFilePath);
        for(String attributeName: attributes.keySet()) {
            stringTemplate.setAttribute(attributeName, attributes.get(attributeName));
        }

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


