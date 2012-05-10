package org.motechproject.care.utils;

import junit.framework.Assert;
import org.antlr.stringtemplate.StringTemplate;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ektorp.BulkDeleteDocument;
import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.motechproject.care.repository.AllCareCaseTasks;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.utils.StringTemplateHelper;
import org.motechproject.commcarehq.domain.AlertDocCase;
import org.motechproject.commcarehq.repository.AllAlertDocCases;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.quartz.utils.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;

@Component
public class E2EIntegrationTestUtil {


    @Qualifier("ananyaCareProperties")
    @Autowired
    private Properties ananyaCareProperties;

    public String getAppServerPort() {
        return ananyaCareProperties.getProperty("app.server.port");
    }

    public String getAppServerHost() {
        return ananyaCareProperties.getProperty("app.server.host");
    }

    public String getAppServerUrl() {
        return "http://" + getAppServerHost() + ":" + getAppServerPort()+"/ananya-care/care/process";
    }

    public void postXmlWithAttributes(HashMap<String, String> attributes, String templateFilePath) {
        StringTemplate stringTemplate = StringTemplateHelper.getStringTemplate(templateFilePath);
        for(String attributeName: attributes.keySet()) {
            stringTemplate.setAttribute(attributeName, attributes.get(attributeName));
        }

        String final_xml = stringTemplate.toString();
        postToCommCare(final_xml);
    }

    public void postToCommCare(String final_xml) {

        Assert.assertFalse("All attributes are not replaced in the xml", final_xml.contains("$"));
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("https://www.commcarehq.org/a/ananya-care/receiver/");

        InputStreamBody inputStreamBody = new InputStreamBody(new ByteArrayInputStream(final_xml.getBytes()), "file.xml");
        MultipartEntity reqEntity = new MultipartEntity();
        reqEntity.addPart("xml_submission_file", inputStreamBody);
        httpPost.setEntity(reqEntity);

        try {
            HttpResponse response = httpclient.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            Assert.assertEquals(201, statusLine.getStatusCode());
            Assert.assertEquals("CREATED",statusLine.getReasonPhrase());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public  HashMap<String, String> createAPregnantMotherCaseInCommCare(String userId, String ownerId) {
        final String motherCaseId = UUID.randomUUID().toString();
        String motherInstanceId = UUID.randomUUID().toString();
        String motherName = "mother_test_gen" + Math.random();

        HashMap<String, String> motherAttributes = new HashMap<String, String>();
        motherAttributes.put("userId", userId);
        motherAttributes.put("ownerId", ownerId);
        motherAttributes.put("caseId", motherCaseId);
        motherAttributes.put("instanceId", motherInstanceId);
        motherAttributes.put("name", motherName);

        this.postXmlWithAttributes(motherAttributes, "/commCareFormXmls/pregnantmother_new.st");
        return motherAttributes;
    }



}
