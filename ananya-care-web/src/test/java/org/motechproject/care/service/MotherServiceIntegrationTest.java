package org.motechproject.care.service;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.repository.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class MotherServiceIntegrationTest extends SpringIntegrationTest {
    @Autowired
    private MotherService motherService;

    @Autowired
    private AllMothers allMothers;

    @Test
    public void shouldSaveMotherCaseIfItDoesNotExists(){
        String caseId = "caseId";
        assertNull(allMothers.findByCaseId(caseId));
        Mother mother = new Mother(caseId);
        motherService.process(mother);
        markForDeletion(mother);
        Mother motherFromDb = allMothers.findByCaseId(caseId);
        assertEquals(mother.getId(),motherFromDb.getId());
    }

    @Test
    public void shouldUpdateMotherCaseIfItExists(){
        String caseId = "caseId";
        DateTime now = new DateTime(2012, 3, 4, 0, 0);
        Mother mother = new Mother(caseId);
        mother.setEdd(now);
        allMothers.add(mother);

        Mother motherToBeUpdated = new Mother(caseId);
        motherToBeUpdated.setFlwId("flwid");

        motherService.process(motherToBeUpdated);

        Mother motherFromDb = allMothers.findByCaseId(caseId);

        markForDeletion(motherFromDb);
        assertEquals(now, motherFromDb.getEdd());
        assertEquals(mother.getId(), motherFromDb.getId());
        assertEquals(mother.getCaseId(), motherFromDb.getCaseId());
        assertEquals("flwid", motherFromDb.getFlwId());

    }
}
