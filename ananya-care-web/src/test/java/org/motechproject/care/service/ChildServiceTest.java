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
import org.motechproject.care.schedule.service.CareScheduleTrackingService;
import org.motechproject.care.service.builder.ChildCareCaseBuilder;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChildServiceTest {

    @Mock
    private AllChildren allChildren;
    @Mock
    private AllMothers allMothers;

    @Mock
    private CareScheduleTrackingService scheduleTrackingService;
    private ChildService childService;

    @Before
    public void setUp(){
        childService = new ChildService(allChildren, scheduleTrackingService, allMothers);

    }


    @Test
    public void shouldSaveChildIfDoesNotExist_WhenMotherExists() {
        String caseId = "caseId";
        CareCase careCase = new ChildCareCaseBuilder().withCaseId(caseId).withBabyMeaslesDate("2012-02-01").withVitamin1Date("2012-08-07").withCaseType(CaseType.Child.getType()).build();
        when(allChildren.findByCaseId(caseId)).thenReturn(null);
        when(allMothers.findByCaseId(careCase.getMother_id())).thenReturn(new Mother());
        ArgumentCaptor<Child> captor = ArgumentCaptor.forClass(Child.class);
        childService.process(careCase);
        verify(allChildren).add(captor.capture());
        Child child = captor.getValue();
        Assert.assertEquals(caseId,child.getCaseId());
        Assert.assertEquals(CaseType.Child.getType(),child.getCaseType());
        Assert.assertEquals(DateTime.parse("2012-02-01"),child.getMeaslesDate());
        Assert.assertEquals(DateTime.parse("2012-08-07"),child.getVitamin1Date());
    }

    @Test
    public void shouldNotSaveChildIfMotherDoesNotExist() {
        String caseId = "caseId";
        String motherId = "motherId";
        CareCase careCase = new ChildCareCaseBuilder().withCaseId(caseId).withBabyMeaslesDate("2012-02-01").withVitamin1Date("2012-08-07").withCaseType(CaseType.Child.getType()).withMotherCaseId(motherId).build();
        when(allChildren.findByCaseId(caseId)).thenReturn(null);
        when(allMothers.findByCaseId(motherId)).thenReturn(null);
        when(allChildren.findByCaseId(motherId)).thenReturn(null);
        childService.process(careCase);
        verify(allChildren,never()).add((Child) Matchers.any());
    }

}
