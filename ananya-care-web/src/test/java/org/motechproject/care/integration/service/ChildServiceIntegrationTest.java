package org.motechproject.care.integration.service;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.care.domain.Child;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.request.CareCase;
import org.motechproject.care.request.CaseType;
import org.motechproject.care.service.ChildService;
import org.motechproject.care.service.builder.ChildCareCaseBuilder;
import org.motechproject.care.utils.CaseUtils;
import org.motechproject.care.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class ChildServiceIntegrationTest extends SpringIntegrationTest {
    @Autowired
    private ChildService childService;

    @Autowired
    private AllChildren allChildren;
    @Autowired
    private AllMothers allMothers;
    private final String caseId = CaseUtils.getUniqueCaseId();
    private final String motherCaseId = CaseUtils.getUniqueCaseId();

    @After
    public void tearDown() {
        Child child = allChildren.findByCaseId(caseId);
        if(child != null) {
            markForDeletion(child);
        }
        Mother mother = allMothers.findByCaseId(motherCaseId);
        if(mother != null) {
            markForDeletion(mother);
        }
    }

    @Test
    public void shouldSaveChildIfDoesNotExist_WhenMotherExistsAndAgeLessThanAYear() {
        CareCase careCase = new ChildCareCaseBuilder().withCaseId(caseId).withBabyMeaslesDate("2012-02-01").withVitamin1Date("2012-08-07").withCaseType(CaseType.Child.getType()).withMotherCaseId(motherCaseId).build();
        Mother mother = new Mother(motherCaseId);
        DateTime dobOfChild = DateTime.now().minusMonths(1);
        mother.setAdd(dobOfChild);
        allMothers.add(mother);

        childService.process(careCase);
        Child child = allChildren.findByCaseId(caseId);

        Assert.assertEquals(caseId, child.getCaseId());
        Assert.assertEquals(CaseType.Child.getType(),child.getCaseType());
        Assert.assertEquals(DateTime.parse("2012-02-01"),child.getMeaslesDate());
        Assert.assertEquals(DateTime.parse("2012-08-07"),child.getVitamin1Date());
        Assert.assertEquals(dobOfChild,child.getDOB());
    }
    
   
}
