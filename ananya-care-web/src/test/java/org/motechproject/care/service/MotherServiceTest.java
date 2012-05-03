package org.motechproject.care.service;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.request.CareCase;
import org.motechproject.care.service.builder.MotherCareCaseBuilder;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class MotherServiceTest {
    @Mock
    private AllMothers allMothers;
    @Mock
    private MotherVaccinationProcessor motherVaccinationProcessor;

    private MotherService motherService;
    private CareCase careCase;
    private String caseId="caseId";

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        motherService = new MotherService(allMothers, motherVaccinationProcessor);
        careCase = new MotherCareCaseBuilder().withCaseName("Aparna").withCaseId(caseId).withEdd("2012-01-02").withMotherAlive("true").build();
    }

    @Test
    public void shouldSaveMotherCaseIfItDoesNotExists() throws IOException {
        when(allMothers.findByCaseId(caseId)).thenReturn(null);

        motherService.process(careCase);

        ArgumentCaptor<Mother> captor=ArgumentCaptor.forClass(Mother.class);
        verify(allMothers).add(captor.capture());

        Mother motherInDb  = captor.getValue();

        Assert.assertEquals(caseId,motherInDb.getCaseId());
        Assert.assertEquals("Aparna",motherInDb.getName());
        DateTime expectedEdd = new DateTime(2012, 1, 2, 0, 0);
        Assert.assertEquals(expectedEdd,motherInDb.getEdd());
        Assert.assertTrue(motherInDb.isAlive());
        Assert.assertNull(motherInDb.getDocCreateTime());
        verify(motherVaccinationProcessor).enrollUpdateVaccines(motherInDb);
    }

    @Test
    public void shouldSaveMotherAsInactiveIfItDoesNotExistAndSheIsDead() throws IOException {
        careCase.setMother_alive("no");
        when(allMothers.findByCaseId(caseId)).thenReturn(null);

        motherService.process(careCase);

        ArgumentCaptor<Mother> captor=ArgumentCaptor.forClass(Mother.class);
        verify(allMothers).add(captor.capture());

        Mother motherInDb  = captor.getValue();


        Assert.assertFalse(motherInDb.isActive());
        Assert.assertFalse(motherInDb.isAlive());
        verify(motherVaccinationProcessor,never()).enrollUpdateVaccines(motherInDb);
    }

    @Test
    public void shouldUpdateMotherCaseIfItExists(){
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

        motherService.process(careCase);

        verify(allMothers, never()).add(motherInDb);
        ArgumentCaptor<Mother> captor = ArgumentCaptor.forClass(Mother.class);
        verify(allMothers).update(captor.capture());

        Mother motherToBeUpdated = captor.getValue();
        assertEquals(DateTime.parse("2012-01-02"), motherToBeUpdated.getEdd());
        assertEquals(motherToBeUpdated.getCaseId(), motherInDb.getCaseId());
        assertEquals(motherToBeUpdated.getId(), motherInDb.getId());
        assertEquals(motherToBeUpdated.getDocCreateTime(), docCreateTime);
        assertEquals(careCase.getCase_name(), motherToBeUpdated.getName());
        verify(motherVaccinationProcessor).enrollUpdateVaccines(motherToBeUpdated);
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
        verify(motherVaccinationProcessor).closeSchedules(motherFromDb);
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
    public void shouldCloseMotherCaseIfMotherIsDead(){
        CareCase motherCase = new MotherCareCaseBuilder().withMotherAlive("no").withCaseId(caseId).build();

        Mother mother = motherWithCaseId(caseId);
        mother.setClosedByCommcare(false);
        mother.setAdd(null);
        mother.setAlive(true);

        when(allMothers.findByCaseId(caseId)).thenReturn(mother);

        motherService.process(motherCase);

        verify(motherVaccinationProcessor).closeSchedules(mother);

        ArgumentCaptor<Mother> captor = ArgumentCaptor.forClass(Mother.class);
        verify(allMothers).update(captor.capture());
        Mother motherFromDb = captor.getValue();
        assertFalse(motherFromDb.isActive());
        assertFalse(motherFromDb.isAlive());
    }

    @Test
    public void shouldCloseMotherCaseIfADDIsGiven(){
        CareCase motherCase = new MotherCareCaseBuilder().withMotherAlive("yes").withCaseId(caseId).withAdd("2012-04-10").build();

        Mother mother = motherWithCaseId(caseId);
        mother.setClosedByCommcare(false);
        mother.setAdd(null);
        mother.setAlive(true);

        when(allMothers.findByCaseId(caseId)).thenReturn(mother);

        motherService.process(motherCase);

        verify(motherVaccinationProcessor).closeSchedules(mother);

        ArgumentCaptor<Mother> captor = ArgumentCaptor.forClass(Mother.class);
        verify(allMothers).update(captor.capture());
        Mother motherFromDb = captor.getValue();
        assertFalse(motherFromDb.isActive());
        assertNotNull(motherFromDb.getAdd());
    }
    
    private Mother motherWithCaseId(String caseId) {
        Mother mother = new Mother();
        mother.setCaseId(caseId);
        return mother;
    }



}