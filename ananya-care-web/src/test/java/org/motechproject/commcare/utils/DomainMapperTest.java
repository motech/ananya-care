package org.motechproject.commcare.utils;

/**
 * Created by IntelliJ IDEA.
 * User: pchandra
 * Date: 3/25/12
 * Time: 1:02 PM
 * To change this template use File | Settings | File Templates.
 */
import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;
import org.motechproject.care.domain.CareCase;
import org.motechproject.commcare.domain.Case;

import java.util.HashMap;

public class DomainMapperTest extends TestCase{

    public void testShouldMapValuesToDomainObjectCorrectly(){
        Case ccCase = createCase();
        DomainMapper<CareCase> mapper = new DomainMapper<CareCase>(CareCase.class);
        CareCase careCase = mapper.mapToDomainObject(ccCase);

        Assert.assertEquals("test",careCase.getCaseId());
        Assert.assertEquals("Smith",careCase.getCaseName());
        Assert.assertEquals("11/10/09 21:23:43",careCase.getDateModified());
        Assert.assertEquals("houshold_rollout_ONICAF",careCase.getCaseTypeId());
        Assert.assertEquals("24/F23/3",careCase.getHousehold_id());
        Assert.assertEquals("Tom Smith",careCase.getPrimary_contact_name());
        Assert.assertEquals("1",careCase.getVisit_number());

    }

   /* public void testShouldMapValuesFromDomainObjectCorrectly(){
        CareCase careCase = createCareCase();
        DomainMapper<CareCase> mapper = new DomainMapper<CareCase>(CareCase.class);
        Case ccCase = mapper.mapFromDomainObject(careCase);
    }*/



    public Case createCase(){
        Case ccCase = new Case();
        ccCase.setCaseId("test");
        ccCase.setCaseName("Smith");
        ccCase.setDateModified("11/10/09 21:23:43");
        ccCase.setCaseTypeId("houshold_rollout_ONICAF");

        HashMap<String,String> fieldValues = new HashMap<String,String>();
        fieldValues.put("household_id","24/F23/3");
        fieldValues.put("primary_contact_name","Tom Smith");
        fieldValues.put("visit_number","1");

        ccCase.setFieldValues(fieldValues);

        return ccCase;
    }

    private CareCase createCareCase() {
        CareCase ccCase = new CareCase();
        ccCase.setCaseId("test");
        ccCase.setCaseName("Smith");
        ccCase.setDateModified("11/10/09 21:23:43");
        ccCase.setCaseTypeId("houshold_rollout_ONICAF");

        ccCase.setHousehold_id("24/F23/3");
        ccCase.setPrimary_contact_name("Tom Smith");
        ccCase.setVisit_number("1");


        return ccCase;
    }
}
