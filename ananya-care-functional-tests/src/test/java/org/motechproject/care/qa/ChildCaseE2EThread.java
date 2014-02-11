package org.motechproject.care.qa;

import junit.framework.Assert;
import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.care.domain.Child;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.request.CaseType;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.utils.DbUtils;
import org.motechproject.commcarehq.domain.AlertDocCase;
import org.motechproject.commons.date.util.DateUtil;

import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;

@Ignore("This test should be run by E2ETestsRunner class which would run this test in parallel thread")
public class ChildCaseE2EThread extends E2EIntegrationTest {

    private final String userId;
    private final String ownerId;

    public ChildCaseE2EThread(Properties ananyaCareProperties, DbUtils dbUtils, String userId, String ownerId) {
        super(ananyaCareProperties, dbUtils);
        this.userId = userId;
        this.ownerId = ownerId;
    }

    @Test
    public void shouldSendBCGAlertForANewBornChild() {
        HashMap<String, String> caseAttributes = createAPregnantMotherCaseInCommCare(userId, ownerId);
        String motherCaseId = caseAttributes.get("caseId");


        Mother mother = dbUtils.getMotherWithRetry(motherCaseId);
        Assert.assertNotNull(mother);
        markClientForDeletion(mother);
        markScheduleForUnEnrollment(motherCaseId, MilestoneType.TT1.toString());

        final String childCaseId = UUID.randomUUID().toString();
        String childInstanceId = UUID.randomUUID().toString();
        String childName = "child_test_gen" + Math.random();
        LocalDate dob = DateUtil.now().minusDays(1).toLocalDate();

        HashMap<String, String> childAttributes = new HashMap<String, String>();
        childAttributes.put("userId", userId);
        childAttributes.put("ownerId", ownerId);
        childAttributes.put("caseId", childCaseId);
        childAttributes.put("instanceId", childInstanceId);
        childAttributes.put("name", childName);
        childAttributes.put("motherCaseId", motherCaseId);
        childAttributes.put("dob", dob.toString());

        postXmlWithAttributes(childAttributes, "/commCareFormXmls/newbornchild.st");

        Child child = dbUtils.getChildWithRetry(childCaseId);

        Assert.assertNotNull(child);
        Assert.assertEquals(childName, child.getName());
        Assert.assertEquals(userId, child.getFlwId());
        Assert.assertEquals(CaseType.Child.getType(), child.getCaseType());
        Assert.assertEquals(DateUtil.newDateTime(dob), child.getDOB());
        markClientForDeletion(child);
        markScheduleForUnEnrollment(childCaseId, MilestoneType.Bcg.toString());

        AlertDocCase alertDocCase = dbUtils.getAlertDocCaseWithRetry(childCaseId, "bcg");
        Assert.assertNotNull(alertDocCase);
        markAlertDocCaseForDeletion(alertDocCase);
    }
}