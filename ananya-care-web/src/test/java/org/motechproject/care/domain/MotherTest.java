package org.motechproject.care.domain;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Test;

public class MotherTest {

    @Test
    public void shouldNotCopyNullPropertiesFromAnotherMotherObject()  {
        Mother mother = new Mother("caseId",null,"flwid","name",null,null, DateTime.parse("2010-04-03"),null,null,false,null,null,null,null,null);
        Mother motherFromDb = new Mother("caseId", DateTime.parse("2010-01-01"), null, "name2", "groupid2", null, null,null,null,false,null,null,null,null,null);
        motherFromDb.setValuesFrom(mother);
        Assert.assertEquals(DateTime.parse("2010-01-01"), motherFromDb.getDateModified());
        Assert.assertEquals("flwid", motherFromDb.getFlwId());
        Assert.assertEquals("name", motherFromDb.getName());
        Assert.assertEquals("groupid2", motherFromDb.getGroupId());
        Assert.assertEquals(DateTime.parse("2010-04-03"), motherFromDb.getAdd());
    }

    @Test
    public void shouldNotCopyEmptyPropertiesFromAnotherMotherObject()  {
        Mother mother = new Mother("caseId",null,"","arpan","groupid",null, DateTime.parse("2010-04-03"),null,null,false,null,null,null,null,null);
        Mother motherFromDb = new Mother("caseId", DateTime.parse("2010-01-01"), "flwid", "arpana", "", null, null,null,null,false,null,null,null,null,null);
        motherFromDb.setValuesFrom(mother);
        Assert.assertEquals(DateTime.parse("2010-01-01"), motherFromDb.getDateModified());
        Assert.assertEquals("flwid", motherFromDb.getFlwId());
        Assert.assertEquals("arpan", motherFromDb.getName());
        Assert.assertEquals("groupid", motherFromDb.getGroupId());
        Assert.assertEquals(DateTime.parse("2010-04-03"), motherFromDb.getAdd());
    }
}
