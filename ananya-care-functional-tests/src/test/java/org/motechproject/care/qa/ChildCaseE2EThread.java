package org.motechproject.care.qa;

import junit.framework.Assert;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.care.domain.Child;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.request.CaseType;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.utils.DbUtils;
import org.motechproject.care.utils.E2EIntegrationTestUtil;
import org.motechproject.commcarehq.domain.AlertDocCase;
import org.motechproject.util.DateUtil;

import java.util.HashMap;
import java.util.UUID;

@Ignore
public class ChildCaseE2EThread {

    private E2EIntegrationTestUtil e2EIntegrationTestUtil;
    private DbUtils dbUtils;
    private final String userId;
    private final String ownerId;

    public ChildCaseE2EThread(E2EIntegrationTestUtil e2EIntegrationTestUtil, DbUtils dbUtils, String userId, String ownerId) {
        this.e2EIntegrationTestUtil = e2EIntegrationTestUtil;
        this.dbUtils = dbUtils;
        this.userId = userId;
        this.ownerId = ownerId;
    }

    @After
    public void after() {
        dbUtils.after();
    }

    @Before
    public void before() {
        dbUtils.before();
    }

    @Test
    public void shouldSendBCGAlertForANewBornChild() {
        HashMap<String, String> caseAttributes = e2EIntegrationTestUtil.createAPregnantMotherCaseInCommCare(userId, ownerId);
        String motherCaseId = caseAttributes.get("caseId");


        Mother mother = dbUtils.getMotherWithRetry(motherCaseId);
        Assert.assertNotNull(mother);
        dbUtils.markForDeletion(mother);
        dbUtils.markScheduleForUnEnrollment(motherCaseId, MilestoneType.TT1.toString());

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

        e2EIntegrationTestUtil.postXmlWithAttributes(childAttributes, "/commCareFormXmls/newbornchild.st");

        Child child = dbUtils.getChildWithRetry(childCaseId);

        Assert.assertNotNull(child);
        Assert.assertEquals(childName, child.getName());
        Assert.assertEquals(userId, child.getFlwId());
        Assert.assertEquals(CaseType.Child.getType(), child.getCaseType());
        Assert.assertEquals(DateUtil.newDateTime(dob), child.getDOB());
        dbUtils.markForDeletion(child);
        dbUtils.markScheduleForUnEnrollment(childCaseId, MilestoneType.Bcg.toString());

        AlertDocCase alertDocCase = dbUtils.getAlertDocCaseWithRetry(childCaseId, "bcg");
        Assert.assertNotNull(alertDocCase);
        dbUtils.markAlertDocCaseForDeletion(alertDocCase);
    }
}