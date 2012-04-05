package org.motechproject.care.service.mapper;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.request.CareCase;

public class MotherMapperTest {

    @Test
    public void shouldMapToAMotherObject(){
        CareCase careCase = new CareCase("caseId","pregnancy","2012-04-01","11","girija","group_id","2012-03-04","2012-02-03");
        Mother mother = MotherMapper.map(careCase);
        Assert.assertEquals("caseId",mother.getCaseId());
        Assert.assertEquals(new DateTime(2012, 4, 1, 0, 0),mother.getDateModified());
        Assert.assertEquals("11",mother.getFlwId());
        Assert.assertEquals("girija",mother.getName());
        Assert.assertEquals("group_id",mother.getGroupId());
        Assert.assertEquals(new DateTime(2012,3,4,0,0),mother.getEdd());
        Assert.assertEquals(new DateTime(2012,2,3,0,0),mother.getAdd());
    }

    @Test
    public void shouldMapToAMotherObjectWithEmptyFields(){
        CareCase careCase = new CareCase("","","","","","","","");
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
        CareCase careCase = new CareCase(null,null,null,null,null,null,null,null);
        Mother mother = MotherMapper.map(careCase);
        Assert.assertNull(mother.getCaseId());
        Assert.assertNull(mother.getDateModified());
        Assert.assertNull(mother.getFlwId());
        Assert.assertNull(mother.getName());
        Assert.assertNull(mother.getGroupId());
        Assert.assertNull(mother.getEdd());
        Assert.assertNull(mother.getAdd());
    }
}
