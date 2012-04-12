package org.motechproject.care.repository;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.care.domain.Child;
import org.motechproject.care.request.CaseType;
import org.motechproject.care.utils.CaseUtils;
import org.motechproject.care.utils.SpringIntegrationTest;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;

public class AllChildrenTest extends SpringIntegrationTest {

    @Autowired
    AllChildren allChildren;

    @Test
    public void shouldSaveChildToDb() {
        String caseId = CaseUtils.getUniqueCaseId();
        DateTime measlesDate = DateUtil.now().plusDays(10);
        DateTime bcgDate = DateUtil.now().plusWeeks(15);
        DateTime vitamin1Date = DateUtil.now().plusWeeks(20);
        DateTime dob = DateUtil.now().minusDays(20);
        Child child = new Child(caseId, DateUtil.now(),"flwID", "aragorn", "groupID", dob, measlesDate, bcgDate, vitamin1Date);
        allChildren.add(child);

        Child childFromDb = allChildren.findByCaseId(caseId);

        assertEquals(caseId, childFromDb.getCaseId());
        assertEquals(CaseType.Child.getType(), childFromDb.getCaseType());
        assertEquals("aragorn", childFromDb.getName());
        assertEquals("flwID", childFromDb.getFlwId());
        assertEquals("groupID", childFromDb.getGroupId());
        assertEquals(bcgDate, childFromDb.getBcgDate());
        assertEquals(dob, childFromDb.getDOB());
        assertEquals(measlesDate, childFromDb.getMeaslesDate());
        assertEquals(vitamin1Date, childFromDb.getVitamin1Date());
    }
}
