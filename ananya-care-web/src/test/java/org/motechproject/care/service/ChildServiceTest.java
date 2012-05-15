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

import java.io.IOException;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChildServiceTest {

    @Mock
    private AllChildren allChildren;

    @Mock
    private ChildVaccinationProcessor childVaccinationProcessor;
    private ChildService childService;
    private String caseId="caseId";

    @Before
    public void setUp(){
        childService = new ChildService(allChildren, childVaccinationProcessor);
    }

    @Test
    public void shouldSaveChildIfDoesNotExist_AgeLessThanAYear() {
        String caseId = "caseId";
        DateTime dobOfChild = DateTime.now().minusMonths(1);
        CareCase careCase = new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dobOfChild.toString()).withMeaslesDate("2012-02-01").withVitamin1Date("2012-08-07").withCaseType(CaseType.Child.getType()).build();
        when(allChildren.findByCaseId(caseId)).thenReturn(null);
        ArgumentCaptor<Child> captor = ArgumentCaptor.forClass(Child.class);
        childService.process(careCase);
        verify(allChildren).add(captor.capture());
        Child child = captor.getValue();
        Assert.assertEquals(caseId,child.getCaseId());
        Assert.assertEquals(CaseType.Child.getType(), child.getCaseType());
        Assert.assertNull(child.getDocCreateTime());
        Assert.assertEquals(DateTime.parse("2012-02-01"), child.getMeaslesDate());
        Assert.assertEquals(DateTime.parse("2012-08-07"),child.getVitamin1Date());
        verify(childVaccinationProcessor).enrollUpdateVaccines(child);
        verify(childVaccinationProcessor, never()).closeSchedules(any(Child.class));
    }

    @Test
    public void shouldSaveChildIfAgeMoreThanAYearButShouldNotEnrollForAnySchedules() {
        String caseId = "caseId";
        DateTime dob = new DateTime(2011, 4, 13, 0, 0);
        CareCase careCase = new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withCaseType(CaseType.Child.getType()).withCaseId(caseId).build();
        when(allChildren.findByCaseId(caseId)).thenReturn(null);
        childService.process(careCase);
        verify(allChildren).add((Child) Matchers.any());
        verify(childVaccinationProcessor, never()).enrollUpdateVaccines(any(Child.class));
        verify(childVaccinationProcessor, never()).closeSchedules(any(Child.class));
    }

    @Test
    public void shouldNotEnrollForAnySchedulesIfDOBIsNull() {
        String caseId = "caseId";
        CareCase careCase = new ChildCareCaseBuilder().withCaseId(caseId).withDOB(null).withCaseType(CaseType.Child.getType()).withCaseId(caseId).build();
        when(allChildren.findByCaseId(caseId)).thenReturn(null);
        childService.process(careCase);
        verify(allChildren).add((Child) Matchers.any());
        verify(childVaccinationProcessor, never()).enrollUpdateVaccines(any(Child.class));
        verify(childVaccinationProcessor, never()).closeSchedules(any(Child.class));
    }

    @Test
    public void shouldUpdateChildIfAgeMoreThanAYear() {
        String oldName = "Aryan";
        String newBcgDate = "2012-05-04";
        String newName = "Vijay";
        DateTime docCreateTime = DateTime.now().minus(1);
        DateTime dob = DateTime.now().plusMonths(5);
        CareCase careCase = new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dob.toString()).withCaseType(CaseType.Child.getType()).withCaseId(caseId).withCaseName(newName).withBcgDate(newBcgDate).build();
        Child childInDb = childWithCaseId(caseId);
        childInDb.setName(oldName);
        childInDb.setDocCreateTime(docCreateTime);
        childInDb.setAlive(true);
        ArgumentCaptor<Child> captor = ArgumentCaptor.forClass(Child.class);

        when(allChildren.findByCaseId(caseId)).thenReturn(childInDb);
        childService.process(careCase);

        verify(allChildren,never()).add((Child) Matchers.any());
        verify(allChildren).update(captor.capture());
        Child childUpdated = captor.getValue();
        Assert.assertEquals(newName,childUpdated.getName());
        Assert.assertEquals(docCreateTime,childUpdated.getDocCreateTime());
        Assert.assertEquals(DateTime.parse(newBcgDate),childUpdated.getBcgDate());
        Assert.assertTrue(childUpdated.isActive());
        verify(childVaccinationProcessor).enrollUpdateVaccines(childInDb);
        verify(childVaccinationProcessor, never()).closeSchedules(any(Child.class));
    }

    @Test
    public void shouldSaveChildAsInactiveIfItDoesNotExistAndIsDead() throws IOException {
        String caseId = "caseId";
        DateTime dobOfChild = DateTime.now().minusMonths(1);
        CareCase careCase = new ChildCareCaseBuilder().withCaseId(caseId).withDOB(dobOfChild.toString()).withMeaslesDate("2012-02-01").withVitamin1Date("2012-08-07").withCaseType(CaseType.Child.getType()).build();
        careCase.setChild_alive("no");
        when(allChildren.findByCaseId(caseId)).thenReturn(null);

        childService.process(careCase);

        ArgumentCaptor<Child> captor=ArgumentCaptor.forClass(Child.class);
        verify(allChildren).add(captor.capture());

        Child childInDb  = captor.getValue();


        org.junit.Assert.assertFalse(childInDb.isActive());
        org.junit.Assert.assertFalse(childInDb.isAlive());
        verify(childVaccinationProcessor, never()).enrollUpdateVaccines(any(Child.class));
        verify(childVaccinationProcessor, never()).closeSchedules(any(Child.class));
    }

    @Test
    public void shouldSetChildCaseAsExpiredAndCloseSchedulesIfExists_WhenChildCaseIsExpired(){
        Child childFromDb = childWithCaseId(caseId);
        childFromDb.setExpired(false);
        childFromDb.setAlive(true);
        childFromDb.setClosedByCommcare(false);

        when(allChildren.findByCaseId(caseId)).thenReturn(childFromDb);
        boolean wasClosed = childService.expireCase(caseId);

        assertTrue(wasClosed);

        verify(allChildren, times(1)).update(childFromDb);
        verify(childVaccinationProcessor, never()).enrollUpdateVaccines(any(Child.class));
        verify(childVaccinationProcessor).closeSchedules(childFromDb);

        ArgumentCaptor<Child> captor = ArgumentCaptor.forClass(Child.class);
        verify(allChildren).update(captor.capture());
        Child child = captor.getValue();
        assertFalse(child.isActive());
        assertTrue(child.isExpired());
    }
    
    @Test
    public void shouldSetChildCaseAsClosedByCommcareAndCloseSchedulesIfExists_WhenChildCaseIsClosed(){
        Child childFromDb = childWithCaseId(caseId);
        childFromDb.setClosedByCommcare(false);
        childFromDb.setAlive(true);

        when(allChildren.findByCaseId(caseId)).thenReturn(childFromDb);
        boolean wasClosed = childService.closeCase(caseId);

        org.junit.Assert.assertTrue(wasClosed);

        verify(allChildren, times(1)).update(childFromDb);
        verify(childVaccinationProcessor).closeSchedules(childFromDb);

        ArgumentCaptor<Child> captor = ArgumentCaptor.forClass(Child.class);
        verify(allChildren).update(captor.capture());
        Child child = captor.getValue();
        assertFalse(child.isActive());
        assertTrue(child.isClosedByCommcare());
    }

    @Test
    public void shouldReturnFalseIfMotherCaseDoesNotExists(){
        when(allChildren.findByCaseId(caseId)).thenReturn(null);
        boolean wasClosed = childService.closeCase(caseId);

        org.junit.Assert.assertFalse(wasClosed);
    }

    @Test
    public void shouldReturnTrueIfChildInactiveWhileExpiringChild(){
        Child childFromDb = childWithCaseId(caseId);
        childFromDb.setExpired(true);
        when(allChildren.findByCaseId(caseId)).thenReturn(childFromDb);
        boolean wasClosed = childService.expireCase(caseId);
        assertTrue(wasClosed);
    }

    @Test
    public void shouldReturnFalseIfChildCaseDoesNotExistsWhileExpiringCase(){
        when(allChildren.findByCaseId(caseId)).thenReturn(null);
        boolean wasClosed = childService.expireCase(caseId);
        assertFalse(wasClosed);
    }

    private Child childWithCaseId(String caseId) {
        Child child = new Child();
        child.setCaseId(caseId);
        return child;
    }

}
