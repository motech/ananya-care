package org.motechproject.care.repository;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.motechproject.care.domain.Child;
import org.motechproject.care.request.CaseType;
import org.motechproject.care.utils.CaseUtils;
import org.motechproject.care.utils.SpringIntegrationTest;
import org.motechproject.commons.date.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.*;

public class AllChildrenTest extends SpringIntegrationTest {

    @Autowired
    AllChildren allChildren;

    @Test
    public void shouldSaveChildToDb() {
        DateTime testStartTime = DateTime.now();
        String caseId = CaseUtils.getUniqueCaseId();
        DateTime measlesDate = DateUtil.now().plusDays(10);
        DateTime bcgDate = DateUtil.now().plusWeeks(15);
        DateTime vitamin1Date = DateUtil.now().plusWeeks(20);
        DateTime hep0Date = DateUtil.now().plusMonths(1);
        DateTime hep1Date = DateUtil.now().plusMonths(2);
        DateTime hep2Date = DateUtil.now().plusMonths(3);
        DateTime hep3Date = DateUtil.now().plusMonths(4);
        DateTime dpt1Date = DateUtil.now().plusMonths(5);
        DateTime dpt2Date = DateUtil.now().plusMonths(6);
        DateTime dpt3Date = DateUtil.now().plusMonths(7);
        DateTime dptBoosterDate = DateUtil.now().plusMonths(8);
        DateTime opv0Date = DateUtil.now().plusDays(5);
        DateTime opv1Date = DateUtil.now().plusMonths(2);
        DateTime opv2Date = DateUtil.now().plusMonths(3);
        DateTime opv3Date = DateUtil.now().plusMonths(4);
        DateTime opvBoosterDate = DateUtil.now().plusMonths(5);
        DateTime dob = DateUtil.now().minusDays(20);
        String motherCaseId = "MotherCaseId";
        Child child = new Child(caseId, DateUtil.now(),"flwID", "aragorn", "groupID", dob, measlesDate, bcgDate, vitamin1Date, motherCaseId, hep0Date, hep1Date, hep2Date, hep3Date,dpt1Date,dpt2Date,dpt3Date,dptBoosterDate,opv0Date,opv1Date,opv2Date,opv3Date,opvBoosterDate, true);
        allChildren.add(child);

        Child childFromDb = allChildren.findByCaseId(caseId);
        assertNotNull(childFromDb);
        markForDeletion(childFromDb);

        assertEquals(caseId, childFromDb.getCaseId());
        assertEquals(CaseType.Child.getType(), childFromDb.getCaseType());
        assertEquals("aragorn", childFromDb.getName());
        assertEquals("flwID", childFromDb.getFlwId());
        assertEquals("groupID", childFromDb.getGroupId());
        assertEquals(bcgDate, childFromDb.getBcgDate());
        assertEquals(dob, childFromDb.getDOB());
        assertEquals(measlesDate, childFromDb.getMeaslesDate());
        assertEquals(hep0Date, childFromDb.getHep0Date());
        assertEquals(hep1Date, childFromDb.getHep1Date());
        assertEquals(hep2Date, childFromDb.getHep2Date());
        assertEquals(hep3Date, childFromDb.getHep3Date());
        assertEquals(dpt1Date, childFromDb.getDpt1Date());
        assertEquals(dpt2Date, childFromDb.getDpt2Date());
        assertEquals(dpt3Date, childFromDb.getDpt3Date());
        assertEquals(dptBoosterDate, childFromDb.getDptBoosterDate());
        assertEquals(opv0Date, childFromDb.getOpv0Date());
        assertEquals(opv1Date, childFromDb.getOpv1Date());
        assertEquals(opv2Date, childFromDb.getOpv2Date());
        assertEquals(opv3Date, childFromDb.getOpv3Date());
        assertEquals(opvBoosterDate, childFromDb.getOpvBoosterDate());
        assertEquals(vitamin1Date, childFromDb.getVitamin1Date());
        assertEquals(motherCaseId, childFromDb.getMotherCaseId());
        assertTrue(child.isAlive());


        DateTime create_time = childFromDb.getDocCreateTime();
        Assert.assertTrue(create_time.isAfter(testStartTime) || create_time.isEqual(testStartTime));
        DateTime testEndTime = DateTime.now();
        Assert.assertTrue(create_time.isBefore(testEndTime) || create_time.isEqual(testEndTime));
    }

}
