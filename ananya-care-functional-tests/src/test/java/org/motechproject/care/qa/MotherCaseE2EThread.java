package org.motechproject.care.qa;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.utils.DbUtils;
import org.motechproject.care.request.CaseType;
import org.motechproject.care.schedule.service.MilestoneType;
import org.motechproject.care.utils.E2EIntegrationTestUtil;
import org.motechproject.care.utils.TestCaseThread;
import org.motechproject.commcarehq.domain.AlertDocCase;
import org.motechproject.util.DateUtil;

import java.util.HashMap;
import java.util.UUID;

public class MotherCaseE2EThread {

    private E2EIntegrationTestUtil e2EIntegrationTestUtil;
    private DbUtils dbUtils;
    private final String userId;
    private final String ownerId;


    public MotherCaseE2EThread(E2EIntegrationTestUtil e2EIntegrationTestUtil, DbUtils dbUtils, String userId, String ownerId) {
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
    public void shouldSendATT1AlertForAPregnantMother() {
        final HashMap<String, String> caseAttributes = e2EIntegrationTestUtil.createAPregnantMotherCaseInCommCare(userId, ownerId);
        String caseId = caseAttributes.get("caseId");
        Mother mother = dbUtils.getMotherWithRetry(caseId);

        Assert.assertNotNull(mother);
        dbUtils.markForDeletion(mother);
        dbUtils.markScheduleForUnEnrollment(caseId, MilestoneType.TT1.toString());
        Assert.assertEquals(caseAttributes.get("name"), mother.getName());
        Assert.assertEquals(userId, mother.getFlwId());
        Assert.assertEquals(CaseType.Mother.getType(), mother.getCaseType());

        String instanceId = UUID.randomUUID().toString();
        String edd = DateUtil.now().plusMonths(1).toLocalDate().toString();
        caseAttributes.put("instanceId", instanceId);
        caseAttributes.put("edd", edd);
        e2EIntegrationTestUtil.postXmlWithAttributes(caseAttributes, "/commCareFormXmls/pregnantmother_register_with_edd.st");

        AlertDocCase alertDocCase = dbUtils.getAlertDocCaseWithRetry(caseId, "tt_1");

        Assert.assertNotNull(alertDocCase);
        dbUtils.markAlertDocCaseForDeletion(alertDocCase);
    }
}