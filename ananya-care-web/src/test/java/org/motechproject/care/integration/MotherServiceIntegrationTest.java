package org.motechproject.care.integration;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.request.CareCase;
import org.motechproject.care.service.MotherService;
import org.motechproject.care.service.builder.MotherCareCaseBuilder;
import org.motechproject.care.utils.CaseUtils;
import org.motechproject.care.utils.SpringIntegrationTest;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class MotherServiceIntegrationTest extends SpringIntegrationTest {

    @Autowired
    private AllMothers allMothers;
    @Autowired
    private MotherService motherService;
    private final String caseId = CaseUtils.getUniqueCaseId();

    @After
    public void tearDown(){
        Mother mother = allMothers.findByCaseId(caseId);
        markForDeletion(mother);
    }

    @Test
    public void shouldSetCreateDateOnCreatingAMother(){
        CareCase careCase = new MotherCareCaseBuilder().withCaseId(caseId).build();
        motherService.process(careCase);
        Mother mother = allMothers.findByCaseId(caseId);
        Assert.assertEquals(DateUtil.today().toDate(), DateUtil.newDate(mother.getDoc_create_time()).toDate());
    }

    @Test
    public void shouldNotChangeCreateDateOnUpdatingAMother() throws InterruptedException {
        CareCase careCase = new MotherCareCaseBuilder().withCaseId(caseId).build();
        motherService.process(careCase);
        Mother mother = allMothers.findByCaseId(caseId);
        DateTime docCreateTime = mother.getDoc_create_time();
        Assert.assertEquals(DateUtil.today().toDate(), DateUtil.newDate(docCreateTime).toDate());
        Thread.sleep(2000);
        careCase.setCase_name("Cullen");
        motherService.process(careCase);

        mother = allMothers.findByCaseId(caseId);
        Assert.assertEquals(DateUtil.today().toDate(), DateUtil.newDate(mother.getDoc_create_time()).toDate());
    }
}
