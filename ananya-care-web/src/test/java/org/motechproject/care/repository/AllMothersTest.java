package org.motechproject.care.repository;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.care.domain.Mother;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.*;


public class AllMothersTest extends SpringIntegrationTest {

    @Autowired
    private AllMothers allMothers;

    @Test
    public void shouldSaveMother(){
        Mother mother = new Mother("caseId", new DateTime(123456), "flwId", "name", "gropuId", new DateTime(123123123)
                , new DateTime(3423), new DateTime(1123), new DateTime(1123), true, new DateTime(1123)
                , new DateTime(1123), new DateTime(1123), new DateTime(1123), new DateTime(1123));
        assertNull(mother.getId());
        allMothers.add(mother);
        assertNotNull(mother.getId());
        markForDeletion(mother);
    }

    @Test
    public void shouldQueryAMotherBasedOnCaseId(){
        String caseId = "case2";
        Mother mother = new Mother(caseId);
        allMothers.add(mother);
        markForDeletion(mother);
        Mother motherFromDb = allMothers.findByCaseId(caseId);
        assertEquals(caseId,motherFromDb.getCaseId());
        assertEquals(mother.getId(),motherFromDb.getId());
    }
}
