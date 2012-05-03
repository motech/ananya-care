package org.motechproject.care.service;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.care.domain.Child;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.request.CareCase;
import org.motechproject.care.request.CaseType;
import org.motechproject.care.service.builder.ChildCareCaseBuilder;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChildServiceTest {

    @Mock
    private AllChildren allChildren;

    @Mock
    private ChildVaccinationProcessor childVaccinationProcessor;
    private ChildService childService;

    @Before
    public void setUp(){
        childService = new ChildService(allChildren, childVaccinationProcessor);
    }

    @Test
    public void shouldSaveChildIfDoesNotExist_AgeLessThanAYear() {
        String caseId = "caseId";
        DateTime dobOfChild = DateTime.now().minusMonths(1);
        CareCase careCase = new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dobOfChild.toString()).withBabyMeaslesDate("2012-02-01").withVitamin1Date("2012-08-07").withCaseType(CaseType.Child.getType()).build();
        when(allChildren.findByCaseId(caseId)).thenReturn(null);
        ArgumentCaptor<Child> captor = ArgumentCaptor.forClass(Child.class);
        childService.process(careCase);
        verify(allChildren).add(captor.capture());
        Child child = captor.getValue();
        Assert.assertEquals(caseId,child.getCaseId());
        Assert.assertEquals(CaseType.Child.getType(), child.getCaseType());
        Assert.assertNull(child.getDoc_create_time());
        Assert.assertEquals(DateTime.parse("2012-02-01"), child.getMeaslesDate());
        Assert.assertEquals(DateTime.parse("2012-08-07"),child.getVitamin1Date());
        verify(childVaccinationProcessor).enrollUpdateVaccines(child);
    }

    @Test
    public void shouldNotSaveChildIfAgeMoreThanAYear() {
        String caseId = "caseId";
        String motherId = "motherId";
        DateTime dob = new DateTime(2011, 4, 13, 0, 0);
        CareCase careCase = new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withCaseType(CaseType.Child.getType()).withMotherCaseId(motherId).build();
        when(allChildren.findByCaseId(caseId)).thenReturn(null);
        childService.process(careCase);
        verify(allChildren,never()).add((Child) Matchers.any());
        verify(childVaccinationProcessor, never()).enrollUpdateVaccines(any(Child.class));
    }

    @Test
    public void shouldUpdateChildIfAgeMoreThanAYear() {
        String caseId = "caseId";
        String motherId = "motherId";
        String oldName = "Aryan";
        String newBcgDate = "2012-05-04";
        String newName = "Vijay";
        DateTime docCreateTime = DateTime.now().minus(1);
        DateTime dob = new DateTime(2011, 4, 12, 0, 0);
        CareCase careCase = new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withCaseType(CaseType.Child.getType()).withMotherCaseId(motherId).withCaseName(newName).withBcgDate(newBcgDate).build();
        Child child = new Child();
        child.setCaseId(caseId);
        Child childInDb = child;
        childInDb.setName(oldName);
        childInDb.setDoc_create_time(docCreateTime);
        ArgumentCaptor<Child> captor = ArgumentCaptor.forClass(Child.class);

        when(allChildren.findByCaseId(caseId)).thenReturn(childInDb);
        childService.process(careCase);

        verify(allChildren,never()).add((Child) Matchers.any());
        verify(allChildren).update(captor.capture());
        Child childUpdated = captor.getValue();
        Assert.assertEquals(newName,childUpdated.getName());
        Assert.assertEquals(docCreateTime,childUpdated.getDoc_create_time());
        Assert.assertEquals(DateTime.parse(newBcgDate),childUpdated.getBcgDate());
        Assert.assertTrue(childUpdated.isActive());
        verify(childVaccinationProcessor).enrollUpdateVaccines(childInDb);
    }

    @Test
    public void shouldNotUpdateChildIsActiveIfDbHasInactive() {
        String caseId = "caseId";
        String motherId = "motherId";
        String oldName = "Aryan";
        String newBcgDate = "2012-05-04";
        String newName = "Vijay";
        DateTime docCreateTime = DateTime.now().minus(1);
        DateTime dob = new DateTime(2011, 4, 12, 0, 0);
        CareCase careCase = new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withCaseType(CaseType.Child.getType()).withMotherCaseId(motherId).withCaseName(newName).withBcgDate(newBcgDate).build();

        Child childInDb = new Child();
        childInDb.setCaseId(caseId);
        childInDb.setActive(false);
        childInDb.setName(oldName);
        childInDb.setDoc_create_time(docCreateTime);

        ArgumentCaptor<Child> captor = ArgumentCaptor.forClass(Child.class);

        when(allChildren.findByCaseId(caseId)).thenReturn(childInDb);
        childService.process(careCase);

        verify(allChildren,never()).add((Child) Matchers.any());
        verify(allChildren).update(captor.capture());
        Child childUpdated = captor.getValue();
        Assert.assertEquals(newName,childUpdated.getName());
        Assert.assertEquals(docCreateTime,childUpdated.getDoc_create_time());
        Assert.assertEquals(DateTime.parse(newBcgDate),childUpdated.getBcgDate());
        Assert.assertFalse(childUpdated.isActive());
        verify(childVaccinationProcessor).enrollUpdateVaccines(childInDb);
    }
}
