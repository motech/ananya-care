package org.motechproject.care.service;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllMothers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class MotherServiceTest {
    @Mock
    private AllMothers allMothers;
    private MotherService motherService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        motherService = new MotherService(allMothers);
    }

    @Test
    public void shouldSaveMotherCaseIfItDoesNotExists(){
        String caseId = "caseId";
        Mother mother = new Mother(caseId);
        when(allMothers.findByCaseId(caseId)).thenReturn(null);

        motherService.createUpdateCase(mother);

        verify(allMothers).add(mother);
    }

    @Test
    public void shouldUpdateMotherCaseIfItExists(){
        String caseId = "caseId";
        DateTime now = DateTime.now();
        Mother mother = new Mother(caseId);
        mother.setEdd(now);

        Mother motherFromDb = new Mother(caseId);
        motherFromDb.setId("1");
        when(allMothers.findByCaseId(caseId)).thenReturn(motherFromDb);

        motherService.createUpdateCase(mother);

        verify(allMothers, never()).add(mother);
        ArgumentCaptor<Mother> captor = ArgumentCaptor.forClass(Mother.class);
        verify(allMothers).update(captor.capture());

        Mother mother1 = captor.getValue();
        assertEquals(now, mother1.getEdd());
        assertEquals(motherFromDb.getId(), mother1.getId());
        assertEquals(motherFromDb.getCaseId(), mother1.getCaseId());
    }
    
    @Test
    public void shouldSetMotherCaseAsInActiveIfExistsAndReturnTrue(){
        String caseId = "caseId";
        when(allMothers.findByCaseId(caseId)).thenReturn(new Mother(caseId));

        boolean wasClosed = motherService.closeCase(caseId);

        Assert.assertTrue(wasClosed);
        ArgumentCaptor<Mother> captor = ArgumentCaptor.forClass(Mother.class);
        verify(allMothers).update(captor.capture());
        assertFalse(captor.getValue().isActive());
    }

    @Test
    public void shouldReturnFalseIfMotherCaseDoesNotExists(){
        String caseId = "caseId";
        when(allMothers.findByCaseId(caseId)).thenReturn(null);
        boolean wasClosed = motherService.closeCase(caseId);
        Assert.assertFalse(wasClosed);
    }
}
