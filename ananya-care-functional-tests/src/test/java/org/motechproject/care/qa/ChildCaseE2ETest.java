package org.motechproject.care.qa;

import junit.framework.Assert;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.care.domain.Child;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.utils.DbUtils;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.request.CaseType;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.commcarehq.domain.AlertDocCase;
import org.motechproject.commcarehq.repository.AllAlertDocCases;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class ChildCaseE2ETest extends SpringQAIntegrationTest {

    @Autowired
    private AllMothers allMothers;

    @Autowired
    private AllChildren allChildren;

    @Autowired
    private AllAlertDocCases allAlertDocCases;

    @Autowired
    private ScheduleTrackingService trackingService;

    @Autowired
    private DbUtils dbUtils;

    @Test
    public void shouldSendBCGAlertForANewBornChild() throws IOException {

        HashMap<String, String> caseAttributes = CommCareWrapper.createAPregnantMotherCaseInCommCare();
        String motherCaseId = caseAttributes.get("caseId");

        Mother mother = dbUtils.getMotherFromDb(motherCaseId);
        Assert.assertNotNull(mother);
        markForDeletion(mother);
        markScheduleForUnEnrollment(motherCaseId, MilestoneType.TT1.toString());

        final String childCaseId = UUID.randomUUID().toString();
        String childInstanceId = UUID.randomUUID().toString();
        String childName = "child_test_gen" + Math.random();
        LocalDate dob = DateUtil.now().minusDays(1).toLocalDate();

        HashMap<String, String> childAttributes = new HashMap<String, String>();
        childAttributes.put("caseId", childCaseId);
        childAttributes.put("instanceId", childInstanceId);
        childAttributes.put("name", childName);
        childAttributes.put("motherCaseId", motherCaseId);
        childAttributes.put("dob", dob.toString());

        CommCareWrapper.postXmlWithAttributes(childAttributes, "/commCareFormXmls/newbornchild.st");

        Child child = dbUtils.getChildFromDb(childCaseId);

        Assert.assertNotNull(child);
        Assert.assertEquals(childName, child.getName());
        Assert.assertEquals("d823ea3d392a06f8b991e9e4933348bd", child.getFlwId());
        Assert.assertEquals(CaseType.Child.getType(), child.getCaseType());
        Assert.assertEquals(DateUtil.newDateTime(dob), child.getDOB());
        markForDeletion(child);
        markScheduleForUnEnrollment(childCaseId, MilestoneType.Bcg.toString());

        AlertDocCase alertDocCase = dbUtils.getAlertDocFromDb(childCaseId, "bcg");
        Assert.assertNotNull(alertDocCase);
        markAlertDocCaseForDeletion(alertDocCase);

    }




}


