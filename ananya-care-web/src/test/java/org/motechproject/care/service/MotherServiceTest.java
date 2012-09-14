package org.motechproject.care.service;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllClients;
import org.motechproject.care.service.builder.MotherBuilder;

import java.io.IOException;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class MotherServiceTest {
    @Mock
    private AllClients<Mother> allMothers;
    @Mock
    private VaccinationProcessor vaccinationProcessor;

    private MotherService motherService;
    private String caseId="caseId";

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        motherService = new MotherService(allMothers, vaccinationProcessor);
    }

    @Test
    public void shouldSaveMotherCaseIfItDoesNotExists() throws IOException {
        Mother mother = new MotherBuilder().withName("Aparna").withCaseId(caseId).withEdd(new DateTime(2012, 1, 2, 0, 0, 0)).withAlive(true).build();
        when(allMothers.findByCaseId(caseId)).thenReturn(null);

        motherService.process(mother);

        ArgumentCaptor<Mother> captor=ArgumentCaptor.forClass(Mother.class);
        verify(allMothers).add(captor.capture());

        Mother motherInDb  = captor.getValue();

        Assert.assertEquals(caseId,motherInDb.getCaseId());
        Assert.assertEquals("Aparna",motherInDb.getName());
        DateTime expectedEdd = new DateTime(2012, 1, 2, 0, 0);
        Assert.assertEquals(expectedEdd,motherInDb.getEdd());
        Assert.assertTrue(motherInDb.isAlive());
        Assert.assertNull(motherInDb.getDocCreateTime());
        verify(vaccinationProcessor).enrollUpdateVaccines(motherInDb);
        verify(vaccinationProcessor, never()).closeSchedules(any(Mother.class));
    }

    @Test
    public void shouldSaveMotherAsInactiveIfItDoesNotExistAndSheIsDead() throws IOException {
        Mother mother = new MotherBuilder().withName("Aparna").withCaseId(caseId).withEdd(new DateTime(2012, 1, 2, 0, 0, 0)).withAlive(false).build();
        when(allMothers.findByCaseId(caseId)).thenReturn(null);

        motherService.process(mother);

        ArgumentCaptor<Mother> captor=ArgumentCaptor.forClass(Mother.class);
        verify(allMothers).add(captor.capture());

        Mother motherInDb  = captor.getValue();


        Assert.assertFalse(motherInDb.isActive());
        Assert.assertFalse(motherInDb.isAlive());
        verify(vaccinationProcessor, never()).enrollUpdateVaccines(any(Mother.class));
        verify(vaccinationProcessor, never()).closeSchedules(any(Mother.class));
    }

    @Test
    public void shouldCloseSchedulesEvenForAnExpiredClientToEnableActiveMqRetriesIfExceptionsOccur(){
        String caseId = "caseId";
        Mother mother = new MotherBuilder().withExpired(true).build();
        when(allMothers.findByCaseId(caseId)).thenReturn(mother);
        motherService.expireCase(caseId);
        verify(vaccinationProcessor).closeSchedules(mother);
    }

    @Test
    public void shouldUpdateMotherCaseIfItExists(){
        Mother mother = new MotherBuilder().withName("Aparna").withCaseId(caseId).withEdd(new DateTime(2012, 1, 2, 0, 0, 0)).withAlive(true).build();
        DateTime now = DateTime.now();
        DateTime docCreateTime = DateTime.now().minusDays(1);
        Mother motherInDb = motherWithCaseId(caseId);
        motherInDb.setEdd(now);
        motherInDb.setDocCreateTime(docCreateTime);
        motherInDb.setName("Seema");
        motherInDb.setAlive(true);
        motherInDb.setClosedByCommcare(false);
        motherInDb.setAdd(null);

        when(allMothers.findByCaseId(caseId)).thenReturn(motherInDb);

        motherService.process(mother);

        verify(allMothers, never()).add(motherInDb);
        ArgumentCaptor<Mother> captor = ArgumentCaptor.forClass(Mother.class);
        verify(allMothers).update(captor.capture());

        Mother motherToBeUpdated = captor.getValue();
        assertEquals(DateTime.parse("2012-01-02"), motherToBeUpdated.getEdd());
        assertEquals(motherToBeUpdated.getCaseId(), motherInDb.getCaseId());
        assertEquals(motherToBeUpdated.getId(), motherInDb.getId());
        assertEquals(motherToBeUpdated.getDocCreateTime(), docCreateTime);
        assertEquals(mother.getName(), motherToBeUpdated.getName());
        verify(vaccinationProcessor).enrollUpdateVaccines(motherToBeUpdated);
    }
   
    @Test
    public void shouldSetMotherCaseAsClosedByCommcareAndCloseSchedulesIfExists_WhenMotherCaseIsClosed(){
        Mother motherFromDb = motherWithCaseId(caseId);
        motherFromDb.setClosedByCommcare(false);
        motherFromDb.setAdd(null);
        motherFromDb.setAlive(true);

        when(allMothers.findByCaseId(caseId)).thenReturn(motherFromDb);
        boolean wasClosed = motherService.closeCase(caseId);

        Assert.assertTrue(wasClosed);

        verify(allMothers, times(1)).update(motherFromDb);
        verify(vaccinationProcessor).closeSchedules(motherFromDb);

        ArgumentCaptor<Mother> captor = ArgumentCaptor.forClass(Mother.class);
        verify(allMothers).update(captor.capture());
        Mother mother = captor.getValue();
        assertFalse(mother.isActive());
        assertTrue(mother.isClosedByCommcare());
    }

    @Test
    public void shouldReturnFalseIfMotherCaseDoesNotExists(){
        when(allMothers.findByCaseId(caseId)).thenReturn(null);
        boolean wasClosed = motherService.closeCase(caseId);

        Assert.assertFalse(wasClosed);
    }

    @Test
    public void shouldSetMotherCaseAsExpiredAndCloseSchedulesIfExists_WhenMotherCaseIsExpired(){
        Mother motherFromDb = motherWithCaseId(caseId);
        motherFromDb.setExpired(false);
        motherFromDb.setClosedByCommcare(false);
        motherFromDb.setAdd(null);
        motherFromDb.setAlive(true);

        when(allMothers.findByCaseId(caseId)).thenReturn(motherFromDb);
        boolean wasClosed = motherService.expireCase(caseId);

        Assert.assertTrue(wasClosed);

        verify(allMothers, times(1)).update(motherFromDb);
        verify(vaccinationProcessor).closeSchedules(motherFromDb);

        ArgumentCaptor<Mother> captor = ArgumentCaptor.forClass(Mother.class);
        verify(allMothers).update(captor.capture());
        Mother mother = captor.getValue();
        assertFalse(mother.isActive());
        assertTrue(mother.isExpired());
    }

    @Test
    public void shouldReturnTrueIfMotherInactiveWhileExpiringMother(){
        Mother motherFromDb = motherWithCaseId(caseId);
        motherFromDb.setExpired(true);
        when(allMothers.findByCaseId(caseId)).thenReturn(motherFromDb);
        boolean wasClosed = motherService.expireCase(caseId);
        Assert.assertTrue(wasClosed);
    }

    @Test
    public void shouldReturnFalseIfMotherCaseDoesNotExistsWhileExpiringCase(){
        when(allMothers.findByCaseId(caseId)).thenReturn(null);
        boolean wasClosed = motherService.expireCase(caseId);
        Assert.assertFalse(wasClosed);
    }

    @Test
    public void shouldCloseMotherCaseIfMotherIsDead(){
        Mother mother = new MotherBuilder().withAlive(false).withCaseId(caseId).build();

        Mother existingMother = motherWithCaseId(caseId);
        existingMother.setClosedByCommcare(false);
        existingMother.setAdd(null);
        existingMother.setAlive(true);

        when(allMothers.findByCaseId(caseId)).thenReturn(existingMother);

        motherService.process(mother);

        verify(allMothers, times(1)).update(existingMother);
        verify(vaccinationProcessor).closeSchedules(existingMother);

        ArgumentCaptor<Mother> captor = ArgumentCaptor.forClass(Mother.class);
        verify(allMothers).update(captor.capture());
        Mother motherFromDb = captor.getValue();
        assertFalse(motherFromDb.isActive());
        assertFalse(motherFromDb.isAlive());
    }

    @Test
    public void shouldCloseMotherSchedulesIfADDIsGiven(){
        Mother mother = new MotherBuilder().withAlive(true).withCaseId(caseId).withAdd(new DateTime(2012, 4, 10, 0, 0, 0)).build();

        Mother existingMother = motherWithCaseId(caseId);
        existingMother.setClosedByCommcare(false);
        existingMother.setAdd(null);
        existingMother.setAlive(true);

        when(allMothers.findByCaseId(caseId)).thenReturn(existingMother);

        motherService.process(mother);

        verify(allMothers, times(1)).update(existingMother);
        verify(vaccinationProcessor).closeSchedules(existingMother);

        ArgumentCaptor<Mother> captor = ArgumentCaptor.forClass(Mother.class);
        verify(allMothers).update(captor.capture());
        Mother motherFromDb = captor.getValue();
        assertFalse(motherFromDb.isActive());
        assertNotNull(motherFromDb.getAdd());
    }

    @Test
    public void shouldUpdateIrrespectiveOfWhetherMotherIsAlreadyInactiveOrNotButShouldNotScheduleVaccinations(){
        Mother mother = new MotherBuilder().withAlive(true).withCaseId(caseId).withAdd(new DateTime(2012, 4, 10, 0, 0, 0)).build();

        Mother existingMother = motherWithCaseId(caseId);
        existingMother.setClosedByCommcare(false);
        existingMother.setAdd(null);
        existingMother.setAlive(false);
        existingMother.setExpired(false);

        when(allMothers.findByCaseId(caseId)).thenReturn(existingMother);

        motherService.process(mother);

        verify(allMothers).update(any(Mother.class));
        verify(vaccinationProcessor).closeSchedules(any(Mother.class));
    }
    
    @Test(expected = RuntimeException.class)
    public void testToCheckThatClientIsAlwaysSavedFirstBeforeSchedulingHerForVaccinations(){

        Mother mother = new MotherBuilder().withAlive(true).withCaseId(caseId).withAdd(new DateTime(2012, 4, 10, 0, 0, 0)).build();

        Mother existingMother = motherWithCaseId(caseId);
        existingMother.setClosedByCommcare(false);
        existingMother.setAdd(null);
        existingMother.setAlive(true);
        existingMother.setExpired(false);
        existingMother.setName("Hannah Montana");

        when(allMothers.findByCaseId(caseId)).thenReturn(existingMother);
        doThrow(new RuntimeException()).when(allMothers).update(Matchers.<Mother>any());

        motherService.process(mother);

        verify(allMothers).update(any(Mother.class));
        verify(vaccinationProcessor,never()).enrollUpdateVaccines(Matchers.<Mother>any());

    }

    private Mother motherWithCaseId(String caseId) {
        Mother mother = new Mother();
        mother.setCaseId(caseId);
        return mother;
    }



}