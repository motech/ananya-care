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
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.request.CareCase;
import org.motechproject.care.request.CaseType;
import org.motechproject.care.service.builder.ChildCareCaseBuilder;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChildServiceTest {

    @Mock
    private AllChildren allChildren;
    @Mock
    private AllMothers allMothers;

    @Mock
    private ChildVaccinationProcessor childVaccinationProcessor;
    private ChildService childService;

    @Before
    public void setUp(){
        childService = new ChildService(allChildren, childVaccinationProcessor, allMothers);
    }

    @Test
    public void shouldSaveChildIfDoesNotExist_WhenMotherExistsAndAgeLessThanAYear() {
        String caseId = "caseId";
        CareCase careCase = new ChildCareCaseBuilder().withCaseId(caseId).withBabyMeaslesDate("2012-02-01").withVitamin1Date("2012-08-07").withCaseType(CaseType.Child.getType()).build();
        when(allChildren.findByCaseId(caseId)).thenReturn(null);
        Mother mother = new Mother();
        DateTime dobOfChild = DateTime.now().minusMonths(1);
        mother.setAdd(dobOfChild);
        when(allMothers.findByCaseId(careCase.getMother_id())).thenReturn(mother);
        ArgumentCaptor<Child> captor = ArgumentCaptor.forClass(Child.class);
        childService.process(careCase);
        verify(allChildren).add(captor.capture());
        Child child = captor.getValue();
        Assert.assertEquals(caseId,child.getCaseId());
        Assert.assertEquals(CaseType.Child.getType(), child.getCaseType());
        Assert.assertEquals(DateTime.parse("2012-02-01"), child.getMeaslesDate());
        Assert.assertEquals(DateTime.parse("2012-08-07"),child.getVitamin1Date());
        verify(childVaccinationProcessor).enrollUpdateVaccines(child);
        //Todo: fix it
    }

    @Test
    public void shouldNotSaveChildIfMotherDoesNotExist() {
        String caseId = "caseId";
        String motherId = "motherId";
        CareCase careCase = new ChildCareCaseBuilder().withCaseId(caseId).withDOB("2010-09-12").withBabyMeaslesDate("2012-02-01").withVitamin1Date("2012-08-07").withCaseType(CaseType.Child.getType()).withMotherCaseId(motherId).build();
        when(allChildren.findByCaseId(caseId)).thenReturn(null);
        when(allMothers.findByCaseId(motherId)).thenReturn(null);
        when(allChildren.findByCaseId(motherId)).thenReturn(null);
        childService.process(careCase);
        verify(allChildren,never()).add((Child) Matchers.any());
        verify(childVaccinationProcessor, never()).enrollUpdateVaccines(any(Child.class));
    }

    @Test
    public void shouldNotSaveChildIfAgeMoreThanAYear() {
        String caseId = "caseId";
        String motherId = "motherId";
        DateTime motherAdd = new DateTime(2011, 4, 13, 0, 0);
        Mother mother = new Mother(motherId);
        mother.setAdd(motherAdd);
        CareCase careCase = new ChildCareCaseBuilder().withCaseId(caseId).withCaseType(CaseType.Child.getType()).withMotherCaseId(motherId).build();
        when(allChildren.findByCaseId(caseId)).thenReturn(null);
        when(allMothers.findByCaseId(motherId)).thenReturn(mother);
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
        DateTime motherAdd = new DateTime(2011, 4, 12, 0, 0);
        Mother mother = new Mother(motherId);
        mother.setAdd(motherAdd);
        CareCase careCase = new ChildCareCaseBuilder().withCaseId(caseId).withCaseType(CaseType.Child.getType()).withMotherCaseId(motherId).withCaseName(newName).withBcgDate(newBcgDate).build();
        Child childInDb = new Child(caseId);
        childInDb.setName(oldName);
        ArgumentCaptor<Child> captor = ArgumentCaptor.forClass(Child.class);

        when(allChildren.findByCaseId(caseId)).thenReturn(childInDb);
        when(allMothers.findByCaseId(motherId)).thenReturn(mother);
        childService.process(careCase);

        verify(allChildren,never()).add((Child) Matchers.any());
        verify(allChildren).update(captor.capture());
        Child child = captor.getValue();
        Assert.assertEquals(newName,child.getName());
        Assert.assertEquals(DateTime.parse(newBcgDate),child.getBcgDate());
        verify(childVaccinationProcessor).enrollUpdateVaccines(childInDb);
    }
}
