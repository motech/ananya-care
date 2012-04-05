package org.motechproject.care.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllMothers;

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
    public void shouldSaveMotherCaseIfItDoesNotExist(){
        Mother mother = new Mother("caseId");
        motherService.process(mother);

//        verify(allMothers).add(mother);
//        when(allMothers.)
    }
}
