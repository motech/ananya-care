package org.motechproject.care.qa.utils;

import junit.framework.Assert;
import org.antlr.stringtemplate.StringTemplate;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class HttpUtils {

    public static void postXmlWithAttributes(HashMap<String, String> attributes, String templateFilePath) throws IOException {
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

    private static HttpResponse postToCommCare(String final_xml) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("https://www.commcarehq.org/a/ananya-care/receiver/");

        InputStreamBody inputStreamBody = new InputStreamBody(new ByteArrayInputStream(final_xml.getBytes()), "file.xml");
        MultipartEntity reqEntity = new MultipartEntity();
        reqEntity.addPart("xml_submission_file", inputStreamBody);
        httpPost.setEntity(reqEntity);

        return httpclient.execute(httpPost);
    }


    public static StringTemplate getStringTemplate(String templateFilePath) {
        InputStream resourceAsStream = HttpUtils.class.getResourceAsStream(templateFilePath);
        String template = TextHelper.getText(resourceAsStream);
        return new StringTemplate(template);
    }

}
