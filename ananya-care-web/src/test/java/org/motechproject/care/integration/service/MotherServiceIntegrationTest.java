package org.motechproject.care.integration.service;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.request.CareCase;
import org.motechproject.care.request.CaseType;
import org.motechproject.care.schedule.vaccinations.MotherVaccinationSchedule;
import org.motechproject.care.service.MotherService;
import org.motechproject.care.service.builder.MotherCareCaseBuilder;
import org.motechproject.care.utils.CaseUtils;
import org.motechproject.care.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-Web.xml")
public class MotherServiceIntegrationTest extends SpringIntegrationTest {
    @Autowired
    private MotherService motherService;

    @Autowired
    private AllMothers allMothers;
    private final String caseId = CaseUtils.getUniqueCaseId();

    @After
    public void tearDown() {
        Mother mother = allMothers.findByCaseId(caseId);
        if(mother != null) {
            markForDeletion(mother);
        }
    }
    
    @Test
    public void shouldSaveMotherCaseIfItDoesNotExists(){
        CareCase careCase = new MotherCareCaseBuilder().withCaseId(caseId).build();
        assertNull(allMothers.findByCaseId(caseId));
        motherService.process(careCase);

        markScheduleForUnEnrollment(caseId, MotherVaccinationSchedule.TT.getName());
        Mother motherFromDb = allMothers.findByCaseId(caseId);
        assertNotNull(motherFromDb.getId());
    }

    @Test
    public void shouldUpdateMotherCaseIfItExists(){
        Mother mother = new Mother(caseId);
        mother.setEdd(new DateTime(2012, 3, 4, 0, 0));
        mother.setFlwId("flwid");
        mother.setName("Rajeshwari");
        allMothers.add(mother);

        CareCase careCase=new MotherCareCaseBuilder().withCaseId(caseId).withUserId("newFlwid").withCaseName("Heena").withEdd("2012-01-01").build();
        motherService.process(careCase);

        markScheduleForUnEnrollment(caseId, MotherVaccinationSchedule.TT.getName());
        Mother motherFromDb = allMothers.findByCaseId(caseId);

        assertEquals(DateTime.parse(careCase.getEdd()), motherFromDb.getEdd());
        assertEquals(mother.getCaseId(), motherFromDb.getCaseId());
        assertEquals(careCase.getUser_id(), motherFromDb.getFlwId());
        assertEquals(careCase.getCase_name(), motherFromDb.getName());
        assertEquals(CaseType.Mother.getType(),motherFromDb.getCaseType());
    }

    @Test
    public void shouldCloseMotherCase(){
        Mother mother = new Mother(caseId);
        allMothers.add(mother);
        motherService.closeCase(caseId);

        Mother motherFromDb = allMothers.findByCaseId(caseId);

        assertEquals(false, motherFromDb.isActive());
    }
}
