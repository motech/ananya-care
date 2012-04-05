package org.motechproject.care.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllMothers;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

        motherService.process(mother);

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

        motherService.process(mother);

        verify(allMothers, never()).add(mother);
        ArgumentCaptor<Mother> captor = ArgumentCaptor.forClass(Mother.class);
        verify(allMothers).update(captor.capture());

        Mother mother1 = captor.getValue();
        assertEquals(now, mother1.getEdd());
        assertEquals(motherFromDb.getId(), mother1.getId());
        assertEquals(motherFromDb.getCaseId(), mother1.getCaseId());

    }
}
