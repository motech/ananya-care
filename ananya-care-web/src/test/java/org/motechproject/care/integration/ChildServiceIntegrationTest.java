package org.motechproject.care.integration;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.motechproject.care.domain.Child;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.request.CareCase;
import org.motechproject.care.service.ChildService;
import org.motechproject.care.service.builder.ChildCareCaseBuilder;
import org.motechproject.care.utils.CaseUtils;
import org.motechproject.care.utils.SpringIntegrationTest;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class ChildServiceIntegrationTest extends SpringIntegrationTest {

    @Autowired
    private AllChildren allChildren;
    @Autowired
    private ChildService childService;

    private final String caseId = CaseUtils.getUniqueCaseId();

    @After
    public void tearDown(){
        Child child = allChildren.findByCaseId(caseId);
        markForDeletion(child);
    }

    @Test
    public void shouldSetCreateDateOnCreatingAChild(){
        CareCase careCase = new ChildCareCaseBuilder().withDOB(DateUtil.now().toLocalDate().toString()).withCaseId(caseId).build();
        childService.process(careCase);
        Child child = allChildren.findByCaseId(caseId);
        Assert.assertEquals(DateUtil.today().toDate(), DateUtil.newDate(child.getDoc_create_time()).toDate());
    }

    @Test
    public void shouldNotChangeCreateDateOnUpdatingAChild() throws InterruptedException {
        CareCase careCase = new ChildCareCaseBuilder().withDOB(DateUtil.now().toLocalDate().toString()).withCaseId(caseId).build();
        childService.process(careCase);
        Child child = allChildren.findByCaseId(caseId);
        DateTime docCreateTime = child.getDoc_create_time();
        Assert.assertEquals(DateUtil.today().toDate(), DateUtil.newDate(docCreateTime).toDate());
        Thread.sleep(2000);
        careCase.setCase_name("Cullen");
        childService.process(careCase);

        child = allChildren.findByCaseId(caseId);
        Assert.assertEquals(DateUtil.today().toDate(), DateUtil.newDate(child.getDoc_create_time()).toDate());
    }
}
