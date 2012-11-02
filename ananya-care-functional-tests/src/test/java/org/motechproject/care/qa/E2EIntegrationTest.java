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
import org.junit.After;
import org.junit.Before;
import org.motechproject.care.domain.Client;
import org.motechproject.care.utils.DbUtils;
import org.motechproject.care.utils.Pair;
import org.motechproject.care.utils.StringTemplateHelper;
import org.motechproject.commcarehq.domain.AlertDocCase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

public class E2EIntegrationTest {

    protected DbUtils dbUtils;
    private Properties ananyaCareProperties;

    private List<Client> clientsToDelete;
    private List<AlertDocCase> alertDocCasesToDelete;
    private List<Pair> schedulesToUnenroll;

    public E2EIntegrationTest(Properties ananyaCareProperties, DbUtils dbUtils) {
        this.ananyaCareProperties = ananyaCareProperties;
        this.dbUtils = dbUtils;
    }

    @After
    public void after() {
        dbUtils.deleteClients(clientsToDelete);
        dbUtils.deleteAlertDocCases(alertDocCasesToDelete);
        dbUtils.unenroll(schedulesToUnenroll);
    }

    @Before
    public void before() {
        clientsToDelete = new ArrayList<Client>();
        alertDocCasesToDelete = new ArrayList<AlertDocCase>();
        schedulesToUnenroll = new ArrayList<Pair>();
    }

    protected void markClientForDeletion(Client client) {
        clientsToDelete.add(client);
    }

    protected void markAlertDocCaseForDeletion(AlertDocCase alertDocCase) {
        alertDocCasesToDelete.add(alertDocCase);
    }

    protected void markScheduleForUnEnrollment(String externalId, String scheduleName) {
        schedulesToUnenroll.add(new Pair(externalId, scheduleName));
    }

    protected String getAppServerPort() {
        return ananyaCareProperties.getProperty("app.server.port");
    }

    protected String getAppServerHost() {
        return ananyaCareProperties.getProperty("app.server.host");
    }

    protected String getAppServerUrl() {
        return "http://" + getAppServerHost() + ":" + getAppServerPort()+"/ananya-care/care/process";
    }

    protected void postXmlWithAttributes(HashMap<String, String> attributes, String templateFilePath) {
        StringTemplate stringTemplate = StringTemplateHelper.getStringTemplate(templateFilePath);
        for(String attributeName: attributes.keySet()) {
            stringTemplate.setAttribute(attributeName, attributes.get(attributeName));
        }
        String final_xml = stringTemplate.toString();
        postToCommCare(final_xml);
    }

    protected void postToCommCare(String final_xml) {
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

    protected  HashMap<String, String> createAPregnantMotherCaseInCommCare(String userId, String ownerId) {
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
