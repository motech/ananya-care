package org.motechproject.care.functional;

import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.care.MyWebClient;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.repository.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;

public class CareCaseFunctionalTest extends SpringIntegrationTest{
    private MyWebClient myWebClient;

    @Autowired
    private AllMothers allMothers;

    @Before
    public void setUp() throws Exception {
        myWebClient = new MyWebClient();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void shouldCreateMother() throws IOException {
        String path = getClass().getResource("/sampleMotherCaseXml.xml").getPath();
        File file = new File(path);
        String xml = FileUtils.readFileToString(file);

        myWebClient.post(getAppServerHostUrl() + "/ananya-care/care/process", xml);
        Mother motherFromDb = allMothers.findByCaseId("8055b3ec-bec6-46cc-9e72-435ebc4eaec1");
        Assert.assertEquals("d823ea3d392a06f8b991e9e49394ce45",motherFromDb.getGroupId());
        Assert.assertEquals("d823ea3d392a06f8b991e9e4933348bd",motherFromDb.getFlwId());
        Assert.assertEquals("NEERAJ",motherFromDb.getName());
        Assert.assertEquals(DateTime.parse("2012-10-20"),motherFromDb.getEdd());

    }
}
