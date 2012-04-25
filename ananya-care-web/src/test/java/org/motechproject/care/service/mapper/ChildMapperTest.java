package org.motechproject.care.service.mapper;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.care.domain.Child;
import org.motechproject.care.request.CareCase;
import org.motechproject.care.service.builder.ChildCareCaseBuilder;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class ChildMapperTest {

    @Test
    public void shouldMapToAChildObject(){
        CareCase careCase = new ChildCareCaseBuilder().build();
        Child child = ChildMapper.map(careCase);
        assertEquals("6055b3ec-bec6-46cc-9e72-435ebc4eaec1", child.getCaseId());
        assertEquals(new DateTime(2012, 3, 4, 0, 0), child.getDateModified());
        assertEquals("b823ea3d392a06f8b991e9e4933348bd", child.getFlwId());
        assertEquals("Pinky", child.getName());
        assertEquals("112", child.getGroupId());
        assertEquals("motherCaseId", child.getMotherCaseId());
        assertEquals(new DateTime(2012, 1, 1, 0, 0), child.getBcgDate());
        assertEquals(new DateTime(2012, 1, 2, 0, 0), child.getVitamin1Date());
        assertEquals(new DateTime(2012, 1, 2, 0, 0), child.getMeaslesDate());
        assertEquals(new DateTime(2012, 1, 2, 0, 0), child.getHep0Date());

    }

    @Test
    public void shouldMapToAChildObjectWithEmptyFields(){
        CareCase careCase = new ChildCareCaseBuilder().withCaseId("").withCaseName("").withCaseType("").withDateModified("").withUserId("").withGroupId("").withBcgDate("").withBabyMeaslesDate("").withVitamin1Date("").build();
        Child child = ChildMapper.map(careCase);
        assertEquals("", child.getCaseId());
        assertEquals(null, child.getDateModified());
        assertEquals("", child.getFlwId());
        assertEquals("", child.getName());
        assertEquals("", child.getGroupId());
        assertEquals(null, child.getBcgDate());
        assertEquals(null, child.getMeaslesDate());
        assertEquals(null, child.getVitamin1Date());
    }

    @Test
    public void shouldMapToAMotherObjectWithNullFields(){
        CareCase careCase = new ChildCareCaseBuilder().withCaseId(null).withCaseName(null).withCaseType(null).withBcgDate(null).withBabyMeaslesDate(null).withDateModified(null).withUserId(null).withGroupId(null).withVitamin1Date(null).build();
        Child child = ChildMapper.map(careCase);
        assertNull(child.getCaseId());
        assertNull(child.getDateModified());
        assertNull(child.getFlwId());
        assertNull(child.getName());
        assertNull(child.getGroupId());
        assertNull(child.getBcgDate());
        assertNull(child.getMeaslesDate());
        assertNull(child.getVitamin1Date());
    }


}
