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
import org.motechproject.care.utils.StringTemplateHelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class CommCareWrapper {

    public static void postXmlWithAttributes(HashMap<String, String> attributes, String templateFilePath) throws IOException {
        StringTemplate stringTemplate = StringTemplateHelper.getStringTemplate(templateFilePath);
        for(String attributeName: attributes.keySet()) {
            stringTemplate.setAttribute(attributeName, attributes.get(attributeName));
        }

        String final_xml = stringTemplate.toString();

        HttpResponse response = postToCommCare(final_xml);
        StatusLine statusLine = response.getStatusLine();
        Assert.assertEquals(201, statusLine.getStatusCode());
        Assert.assertEquals("CREATED",statusLine.getReasonPhrase());
    }

    private static HttpResponse postToCommCare(String final_xml) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("https://www.commcarehq.org/a/ananya-care/receiver/");

        InputStreamBody inputStreamBody = new InputStreamBody(new ByteArrayInputStream(final_xml.getBytes()), "file.xml");
        MultipartEntity reqEntity = new MultipartEntity();
        reqEntity.addPart("xml_submission_file", inputStreamBody);
        httpPost.setEntity(reqEntity);

        return httpclient.execute(httpPost);
    }


    public static HashMap<String, String> createAPregnantMotherCaseInCommCare() throws IOException {
        final String motherCaseId = UUID.randomUUID().toString();
        String motherInstanceId = UUID.randomUUID().toString();
        String motherName = "mother_test_gen" + Math.random();

        HashMap<String, String> motherAttributes = new HashMap<String, String>();
        motherAttributes.put("caseId", motherCaseId);
        motherAttributes.put("instanceId", motherInstanceId);
        motherAttributes.put("name", motherName);

        CommCareWrapper.postXmlWithAttributes(motherAttributes, "/commCareFormXmls/pregnantmother_new.st");
        return motherAttributes;
    }

}
