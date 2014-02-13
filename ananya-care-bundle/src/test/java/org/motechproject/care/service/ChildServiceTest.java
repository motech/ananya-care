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
import org.motechproject.care.repository.AllClients;
import org.motechproject.care.request.CaseType;
import org.motechproject.care.service.builder.ChildBuilder;

import java.io.IOException;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class ChildServiceTest {

    @Mock
    private AllClients<Child> allChildren;

    @Mock
    private VaccinationProcessor vaccinationProcessor;
    private ChildService childService;
    private String caseId="caseId";

    @Before
    public void setUp(){
        initMocks(this);
        childService = new ChildService(allChildren, vaccinationProcessor);
    }

    @Test
    public void shouldSaveChildIfDoesNotExist_AgeLessThanAYear() {
        String caseId = "caseId";
        DateTime dobOfChild = DateTime.now().minusMonths(1);
        Child child = new ChildBuilder().withCaseId(caseId).withDOB(dobOfChild).withMeaslesDate(new DateTime(2012, 2, 1, 0, 0, 0)).withVitamin1Date(new DateTime(2012, 8, 7, 0, 0, 0)).build();
        when(allChildren.findByCaseId(caseId)).thenReturn(null);
        ArgumentCaptor<Child> captor = ArgumentCaptor.forClass(Child.class);
        childService.process(child);
        verify(allChildren).add(captor.capture());
        Child actualChild = captor.getValue();
        Assert.assertEquals(caseId, actualChild.getCaseId());
        Assert.assertEquals(CaseType.Child.getType(), actualChild.getCaseType());
        Assert.assertNull(actualChild.getDocCreateTime());
        Assert.assertEquals(DateTime.parse("2012-02-01"), actualChild.getMeaslesDate());
        Assert.assertEquals(DateTime.parse("2012-08-07"),actualChild.getVitamin1Date());
        verify(vaccinationProcessor).enrollUpdateVaccines(actualChild);
        verify(vaccinationProcessor, never()).closeSchedules(any(Child.class));
    }

    @Test
    public void shouldSaveChildIfAgeMoreThanAYearButShouldNotEnrollForAnySchedules() {
        String caseId = "caseId";
        DateTime dob = new DateTime(2011, 4, 13, 0, 0);
        Child child = new ChildBuilder().withCaseId(caseId).withDOB(dob).withCaseId(caseId).build();
        when(allChildren.findByCaseId(caseId)).thenReturn(null);
        childService.process(child);
        verify(allChildren).add((Child) Matchers.any());
        verify(vaccinationProcessor, never()).enrollUpdateVaccines(any(Child.class));
        verify(vaccinationProcessor, never()).closeSchedules(any(Child.class));
    }

    @Test
    public void shouldNotEnrollForAnySchedulesIfDOBIsNull() {
        String caseId = "caseId";
        Child child = new ChildBuilder().withCaseId(caseId).withDOB(null).withCaseId(caseId).build();
        when(allChildren.findByCaseId(caseId)).thenReturn(null);
        childService.process(child);
        verify(allChildren).add((Child) Matchers.any());
        verify(vaccinationProcessor, never()).enrollUpdateVaccines(any(Child.class));
        verify(vaccinationProcessor, never()).closeSchedules(any(Child.class));
    }

    @Test
    public void shouldUpdateChildIfAgeMoreThanAYear() {
        String oldName = "Aryan";
        DateTime newBcgDate = new DateTime(2012, 5, 4, 0, 0, 0);
        String newName = "Vijay";
        DateTime docCreateTime = DateTime.now().minus(1);
        DateTime dob = DateTime.now().plusMonths(5);
        Child child = new ChildBuilder().withCaseId(caseId).withDOB(dob).withCaseId(caseId).withCaseName(newName).withBcgDate(newBcgDate).build();
        Child childInDb = childWithCaseId(caseId);
        childInDb.setName(oldName);
        childInDb.setDocCreateTime(docCreateTime);
        childInDb.setAlive(true);
        ArgumentCaptor<Child> captor = ArgumentCaptor.forClass(Child.class);

        when(allChildren.findByCaseId(caseId)).thenReturn(childInDb);
        childService.process(child);

        verify(allChildren,never()).add((Child) Matchers.any());
        verify(allChildren).update(captor.capture());
        Child childUpdated = captor.getValue();
        Assert.assertEquals(newName,childUpdated.getName());
        Assert.assertEquals(docCreateTime,childUpdated.getDocCreateTime());
        Assert.assertEquals(newBcgDate,childUpdated.getBcgDate());
        Assert.assertTrue(childUpdated.isActive());
        verify(vaccinationProcessor).enrollUpdateVaccines(childInDb);
        verify(vaccinationProcessor, never()).closeSchedules(any(Child.class));
    }

    @Test
    public void shouldSaveChildAsInactiveIfItDoesNotExistAndIsDead() throws IOException {
        String caseId = "caseId";
        DateTime dobOfChild = DateTime.now().minusMonths(1);
        Child child = new ChildBuilder().withCaseId(caseId).withDOB(dobOfChild).withMeaslesDate(new DateTime(2012, 2, 1, 0, 0, 0)).withVitamin1Date(new DateTime(2012, 8, 7, 0, 0, 0)).withAlive(false).build();
        when(allChildren.findByCaseId(caseId)).thenReturn(null);

        childService.process(child);

        ArgumentCaptor<Child> captor=ArgumentCaptor.forClass(Child.class);
        verify(allChildren).add(captor.capture());

        Child childInDb  = captor.getValue();


        org.junit.Assert.assertFalse(childInDb.isActive());
        org.junit.Assert.assertFalse(childInDb.isAlive());
        verify(vaccinationProcessor, never()).enrollUpdateVaccines(any(Child.class));
        verify(vaccinationProcessor, never()).closeSchedules(any(Child.class));
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
        verify(vaccinationProcessor, never()).enrollUpdateVaccines(any(Child.class));
        verify(vaccinationProcessor).closeSchedules(childFromDb);

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
        verify(vaccinationProcessor).closeSchedules(childFromDb);

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

    @Test(expected = RuntimeException.class)
    public void testToCheckThatClientIsAlwaysSavedFirstBeforeSchedulingHerForVaccinations(){

        Child childFromDb = childWithCaseId(caseId);
        childFromDb.setClosedByCommcare(false);
        childFromDb.setAlive(true);

        when(allChildren.findByCaseId(caseId)).thenReturn(childFromDb);

        doThrow(new RuntimeException()).when(allChildren).update(Matchers.<Child>any());

        Child child = new ChildBuilder().withCaseId(caseId).build();
        childService.process(child);

        verify(allChildren).update(any(Child.class));
        verify(vaccinationProcessor,never()).enrollUpdateVaccines(Matchers.<Child>any());

    }

    @Test
    public void shouldCloseSchedulesEvenForAnExpiredClientToEnableActiveMqRetriesIfExceptionsOccur(){
        String caseId = "caseId";
        Child child = new ChildBuilder().withExpired(true).build();
        when(allChildren.findByCaseId(caseId)).thenReturn(child);
        childService.expireCase(caseId);
        verify(vaccinationProcessor).closeSchedules(child);
    }

    private Child childWithCaseId(String caseId) {
        Child child = new Child();
        child.setCaseId(caseId);
        return child;
    }

}
