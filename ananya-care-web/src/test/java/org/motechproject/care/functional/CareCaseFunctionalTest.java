package org.motechproject.care.functional;

import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.repository.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;

public class CareCaseFunctionalTest extends SpringIntegrationTest{
    @Autowired
    private AllMothers allMothers;

    @After
    public void after(){
        allMothers.removeAll();
    }

    @Test
    public void shouldCreateMother() throws IOException {
        String path = getClass().getResource("/sampleMotherCaseXml.xml").getPath();
        File file = new File(path);
        String body = FileUtils.readFileToString(file);

        new RestTemplate().postForLocation(getAppServerHostUrl() + "/ananya-care/care/process", body);

        Mother motherFromDb = allMothers.findByCaseId("8055b3ec-bec6-46cc-9e72-435ebc4eaec1");
        Assert.assertEquals("d823ea3d392a06f8b991e9e49394ce45",motherFromDb.getGroupId());
        Assert.assertEquals("d823ea3d392a06f8b991e9e4933348bd",motherFromDb.getFlwId());
        Assert.assertEquals("NEERAJ",motherFromDb.getName());
        Assert.assertEquals(DateTime.parse("2012-10-20"),motherFromDb.getEdd());

    }
}
