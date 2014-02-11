package org.motechproject.care.qa;

import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
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
public class MotherCaseE2EThread extends E2EIntegrationTest {

    private final String userId;
    private final String ownerId;


    public MotherCaseE2EThread(Properties ananyaCareProperties, DbUtils dbUtils, String userId, String ownerId) {
        super(ananyaCareProperties, dbUtils);
        this.userId = userId;
        this.ownerId = ownerId;
    }

    @Test
    public void shouldSendATT1AlertForAPregnantMother() {
        final HashMap<String, String> caseAttributes = createAPregnantMotherCaseInCommCare(userId, ownerId);
        String caseId = caseAttributes.get("caseId");
        Mother mother = dbUtils.getMotherWithRetry(caseId);

        Assert.assertNotNull(mother);
        markClientForDeletion(mother);
        markScheduleForUnEnrollment(caseId, MilestoneType.TT1.toString());
        Assert.assertEquals(caseAttributes.get("name"), mother.getName());
        Assert.assertEquals(userId, mother.getFlwId());
        Assert.assertEquals(CaseType.Mother.getType(), mother.getCaseType());

        String instanceId = UUID.randomUUID().toString();
        String edd = DateUtil.now().plusMonths(1).toLocalDate().toString();
        caseAttributes.put("instanceId", instanceId);
        caseAttributes.put("edd", edd);
        postXmlWithAttributes(caseAttributes, "/commCareFormXmls/pregnantmother_register_with_edd.st");

        AlertDocCase alertDocCase = dbUtils.getAlertDocCaseWithRetry(caseId, "tt_1");

        Assert.assertNotNull(alertDocCase);
        markAlertDocCaseForDeletion(alertDocCase);
    }
}