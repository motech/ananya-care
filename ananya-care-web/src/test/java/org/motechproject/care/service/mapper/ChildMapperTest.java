package org.motechproject.care.service.mapper;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.care.domain.Child;
import org.motechproject.care.request.CareCase;
import org.motechproject.care.service.builder.ChildCareCaseBuilder;

import static junit.framework.Assert.*;

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
        assertEquals(new DateTime(2012, 2, 2, 0, 0), child.getHep1Date());
        assertEquals(new DateTime(2012, 3, 2, 0, 0), child.getHep2Date());
        assertEquals(new DateTime(2012, 4, 2, 0, 0), child.getHep3Date());
        assertEquals(new DateTime(2012, 8, 2, 0, 0), child.getDpt1Date());
        assertEquals(new DateTime(2012, 9, 2, 0, 0), child.getDpt2Date());
        assertEquals(new DateTime(2012, 10, 2, 0, 0), child.getDpt3Date());
        assertEquals(new DateTime(2012, 11, 2, 0, 0), child.getDptBoosterDate());
        assertEquals(new DateTime(2012, 1, 2, 0, 0), child.getOpv0Date());
        assertEquals(new DateTime(2012, 2, 2, 0, 0), child.getOpv1Date());
        assertEquals(new DateTime(2012, 3, 2, 0, 0), child.getOpv2Date());
        assertEquals(new DateTime(2012, 4, 2, 0, 0), child.getOpv3Date());
        assertEquals(new DateTime(2012, 5, 2, 0, 0), child.getOpvBoosterDate());

    }

    @Test
    public void shouldMapToAChildObjectWithEmptyFields(){
        CareCase careCase = new ChildCareCaseBuilder().withCaseId("").withCaseName("").withCaseType("").withDateModified("").withUserId("").withGroupId("").withBcgDate("").withMeaslesDate("").withVitamin1Date("")
                .withHep0Date("").withHep1Date("").withHep2Date("").withHep3Date("")
                .withDpt1Date("").withDpt2Date("").withDpt3Date("").withDptBoosterDate("").withOPV0Date("").withOPV1Date("").withOPV2Date("").withOPV3Date("").withOPVBoosterDate("").build();
        Child child = ChildMapper.map(careCase);
        assertEquals("", child.getCaseId());
        assertEquals(null, child.getDateModified());
        assertEquals("", child.getFlwId());
        assertEquals("", child.getName());
        assertEquals("", child.getGroupId());
        assertEquals(null, child.getBcgDate());
        assertEquals(null, child.getMeaslesDate());
        assertEquals(null, child.getVitamin1Date());
        assertEquals(null, child.getHep0Date());
        assertEquals(null, child.getHep1Date());
        assertEquals(null, child.getHep2Date());
        assertEquals(null, child.getHep3Date());
        assertEquals(null, child.getDpt1Date());
        assertEquals(null, child.getDpt2Date());
        assertEquals(null, child.getDpt3Date());
        assertEquals(null, child.getDptBoosterDate());
        assertEquals(null, child.getOpv0Date());
        assertEquals(null, child.getOpv1Date());
        assertEquals(null, child.getOpv2Date());
        assertEquals(null, child.getOpv3Date());
        assertEquals(null, child.getOpvBoosterDate());
    }

    @Test
    public void shouldMapToAMotherObjectWithNullFields(){
        CareCase careCase = new ChildCareCaseBuilder().withCaseId(null).withCaseName(null).withCaseType(null).withBcgDate(null).withMeaslesDate(null).withDateModified(null).withUserId(null).withGroupId(null).withVitamin1Date(null)
                .withHep0Date(null).withHep1Date(null).withHep2Date(null).withHep3Date(null)
                .withDpt1Date(null).withDpt2Date(null).withDpt3Date(null).withDptBoosterDate(null).withOPV0Date(null).withOPV1Date(null).withOPV2Date(null).withOPV3Date(null).withOPVBoosterDate(null).build();

        Child child = ChildMapper.map(careCase);
        assertNull(child.getCaseId());
        assertNull(child.getDateModified());
        assertNull(child.getFlwId());
        assertNull(child.getName());
        assertNull(child.getGroupId());
        assertNull(child.getBcgDate());
        assertNull(child.getMeaslesDate());
        assertNull(child.getVitamin1Date());
        assertNull(child.getHep0Date());
        assertNull(child.getHep1Date());
        assertNull(child.getHep2Date());
        assertNull(child.getHep3Date());
        assertNull(child.getDpt1Date());
        assertNull(child.getDpt2Date());
        assertNull(child.getDpt3Date());
        assertNull(child.getDptBoosterDate());
        assertNull(child.getOpv0Date());
        assertNull(child.getOpv1Date());
        assertNull(child.getOpv2Date());
        assertNull(child.getOpv3Date());
        assertNull(child.getOpvBoosterDate());
    }

    @Test
    public void shouldInferChildAliveCorrectly(){
        Child child = ChildMapper.map(new ChildCareCaseBuilder().withChildAlive("").build());
        assertTrue(child.isAlive());

        child =ChildMapper.map(new ChildCareCaseBuilder().withChildAlive(null).build());
        assertTrue(child.isAlive());

        child =ChildMapper.map(new ChildCareCaseBuilder().withChildAlive("yes").build());
        assertTrue(child.isAlive());

        child =ChildMapper.map(new ChildCareCaseBuilder().withChildAlive("random").build());
        assertTrue(child.isAlive());

        child =ChildMapper.map(new ChildCareCaseBuilder().withChildAlive("no").build());
        assertFalse(child.isAlive());
    }


}
