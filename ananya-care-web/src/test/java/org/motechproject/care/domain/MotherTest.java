package org.motechproject.care.domain;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Test;

public class MotherTest {

    @Test
    public void shouldBeSetToActiveIfAliveAndNoADDIsPresentAndNotClosedByCommcare() {
        Mother mother = new Mother();
        mother.setAlive(true);
        Assert.assertTrue(mother.isActive());
    }

    @Test
    public void shouldBeSetToInActiveIfNotAlive() {
        Mother mother = new Mother();
        mother.setAlive(false);
        Assert.assertFalse(mother.isActive());
    }

    @Test
    public void shouldBeSetToInActiveIfNoADDIsPresent() {
        Mother mother = new Mother();
        mother.setAlive(true);
        mother.setAdd(new DateTime());
        Assert.assertFalse(mother.isActive());
    }

    @Test
    public void shouldBeSetToInActiveIfCaseClosedByCommCare() {
        Mother mother = new Mother();
        mother.setAlive(true);
        mother.setClosedByCommcare(true);
        Assert.assertFalse(mother.isActive());
    }

    @Test
    public void shouldBeSetToInActiveIfExpired() {
        Mother mother = new Mother();
        mother.setAlive(true);
        mother.setExpired(true);
        Assert.assertFalse(mother.isActive());
    }
    
    @Test
    public void shouldNotCopyNullPropertiesFromAnotherMotherObject()  {
        Mother mother = new Mother("caseId", null,"flwid","name",null,null, DateTime.parse("2010-04-03"),null,null,false,null,null,null,null,null,true);
        Mother motherFromDb = new Mother("caseId", DateTime.parse("2010-01-01"), null, "name2", "groupid2", null, null,null,null,false,null,null,null,null,null,false);
        motherFromDb.setValuesFrom(mother);
        Assert.assertEquals(DateTime.parse("2010-01-01"), motherFromDb.getDateModified());
        Assert.assertEquals("flwid", motherFromDb.getFlwId());
        Assert.assertEquals("name", motherFromDb.getName());
        Assert.assertEquals("groupid2", motherFromDb.getGroupId());
        Assert.assertEquals(DateTime.parse("2010-04-03"), motherFromDb.getAdd());
    }

    @Test
    public void shouldNotCopyEmptyPropertiesFromAnotherMotherObject()  {
        Mother mother = new Mother("caseId", null,"","arpan","groupid",null, DateTime.parse("2010-04-03"),null,null,false,null,null,null,null,null,false);
        Mother motherFromDb = new Mother("caseId", DateTime.parse("2010-01-01"), "flwid", "arpana", "", null, null,null,null,false,null,null,null,null,null,true);
        motherFromDb.setValuesFrom(mother);
        Assert.assertEquals(DateTime.parse("2010-01-01"), motherFromDb.getDateModified());
        Assert.assertEquals("flwid", motherFromDb.getFlwId());
        Assert.assertEquals("arpan", motherFromDb.getName());
        Assert.assertEquals("groupid", motherFromDb.getGroupId());
        Assert.assertEquals(DateTime.parse("2010-04-03"), motherFromDb.getAdd());
    }
}
