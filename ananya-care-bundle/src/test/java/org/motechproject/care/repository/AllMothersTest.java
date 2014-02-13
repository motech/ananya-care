package org.motechproject.care.repository;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.request.CaseType;
import org.motechproject.care.utils.CaseUtils;
import org.motechproject.care.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;


public class AllMothersTest extends SpringIntegrationTest {

    @Autowired
    private AllMothers allMothers;
    private Mother mother;

    @After
    public void tearDown(){
        markForDeletion(mother);
    }

    @Test
    public void shouldSaveMother(){
        DateTime testStartTime = DateTime.now();
        String caseId = CaseUtils.getUniqueCaseId();
        mother = new Mother(caseId, new DateTime(123456), "flwId", "name", "gropuId", new DateTime(123123123)
                , new DateTime(3423), new DateTime(1123), new DateTime(1123), true, new DateTime(1123)
                , new DateTime(1123), new DateTime(1123), new DateTime(1123), new DateTime(1123),true);
        allMothers.add(mother);
        Mother motherFromDb = allMothers.findByCaseId(caseId);
        assertNotNull(motherFromDb);


        assertEquals(caseId,motherFromDb.getCaseId());
        assertEquals(CaseType.Mother.getType(),motherFromDb.getCaseType());
        assertNotNull(motherFromDb.getId());

        DateTime create_time = motherFromDb.getDocCreateTime();
        Assert.assertTrue(create_time.isAfter(testStartTime) || create_time.isEqual(testStartTime));
        DateTime testEndTime = DateTime.now();
        Assert.assertTrue(create_time.isBefore(testEndTime) || create_time.isEqual(testEndTime));
    }
}
