package org.motechproject.care.service.mapper;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.request.CareCase;
import org.motechproject.care.service.builder.MotherCareCaseBuilder;

import static junit.framework.Assert.*;

public class MotherMapperTest {

    @Test
    public void shouldMapToAMotherObject(){
        CareCase careCase = new MotherCareCaseBuilder().withLastPregTT("yes").withAdd("2012-10-04").build();
        Mother mother = MotherMapper.map(careCase);
        assertEquals("6055b3ec-bec6-46cc-9e72-435ebc4eaec1", mother.getCaseId());
        assertEquals(new DateTime(2012, 3, 4, 0, 0), mother.getDateModified());
        assertEquals("b823ea3d392a06f8b991e9e4933348bd", mother.getFlwId());
        assertEquals("Vanaja", mother.getName());
        assertEquals("112", mother.getGroupId());
        assertEquals(new DateTime(2012, 10, 2, 0, 0), mother.getEdd());
        assertEquals(new DateTime(2012, 10, 4, 0, 0), mother.getAdd());
        assertEquals(new DateTime(2012, 1, 1, 0, 0), mother.getTt1Date());
        assertEquals(new DateTime(2012, 1, 2, 0, 0), mother.getTt2Date());
        assertEquals(new DateTime(2012, 1, 3, 0, 0), mother.getAnc1Date());
        assertEquals(new DateTime(2012, 1, 4, 0, 0), mother.getAnc2Date());
        assertEquals(new DateTime(2012, 1, 5, 0, 0), mother.getAnc3Date());
        assertEquals(new DateTime(2012, 1, 6, 0, 0), mother.getAnc4Date());
        assertEquals(new DateTime(2012, 1, 7, 0, 0), mother.getTtBoosterDate());
        assertTrue(mother.isLastPregTt());

    }

    @Test
    public void shouldMapToAMotherObjectWithEmptyFields(){
        CareCase careCase = new MotherCareCaseBuilder().withCaseId("").withCaseName("").withCaseType("").withAdd("").withEdd("").withDateModified("").withUserId("").withGroupId("").build();
        Mother mother = MotherMapper.map(careCase);
        Assert.assertEquals("",mother.getCaseId());
        Assert.assertEquals(null,mother.getDateModified());
        Assert.assertEquals("",mother.getFlwId());
        Assert.assertEquals("",mother.getName());
        Assert.assertEquals("",mother.getGroupId());
        Assert.assertEquals(null,mother.getEdd());
        Assert.assertEquals(null,mother.getAdd());
    }

    @Test
    public void shouldMapToAMotherObjectWithNullFields(){
        CareCase careCase = new MotherCareCaseBuilder().withCaseId(null).withCaseName(null).withCaseType(null).withAdd(null).withEdd(null).withDateModified(null).withUserId(null).withGroupId(null).build();
        Mother mother = MotherMapper.map(careCase);
        Assert.assertNull(mother.getCaseId());
        Assert.assertNull(mother.getDateModified());
        Assert.assertNull(mother.getFlwId());
        Assert.assertNull(mother.getName());
        Assert.assertNull(mother.getGroupId());
        Assert.assertNull(mother.getEdd());
        Assert.assertNull(mother.getAdd());
    }

    @Test
    public void shouldInferMotherAliveCorrectly(){
        Mother mother = MotherMapper.map(new MotherCareCaseBuilder().withMotherAlive("").build());
        assertTrue(mother.isAlive());

        mother = MotherMapper.map(new MotherCareCaseBuilder().withMotherAlive(null).build());
        assertTrue(mother.isAlive());

        mother = MotherMapper.map(new MotherCareCaseBuilder().withMotherAlive("yes").build());
        assertTrue(mother.isAlive());

        mother = MotherMapper.map(new MotherCareCaseBuilder().withMotherAlive("random").build());
        assertTrue(mother.isAlive());

        mother = MotherMapper.map(new MotherCareCaseBuilder().withMotherAlive("no").build());
        assertFalse(mother.isAlive());
    }

    @Test
    public void shouldInferLastPregnancyCorrectly(){
        Mother mother = MotherMapper.map(new MotherCareCaseBuilder().withLastPregTT("").build());
        assertFalse(mother.isLastPregTt());

        mother = MotherMapper.map(new MotherCareCaseBuilder().withLastPregTT(null).build());
        assertFalse(mother.isLastPregTt());

        mother = MotherMapper.map(new MotherCareCaseBuilder().withLastPregTT("no").build());
        assertFalse(mother.isLastPregTt());

        mother = MotherMapper.map(new MotherCareCaseBuilder().withLastPregTT("random").build());
        assertFalse(mother.isLastPregTt());

        mother = MotherMapper.map(new MotherCareCaseBuilder().withLastPregTT("yes").build());
        assertTrue(mother.isLastPregTt());
    }
}
