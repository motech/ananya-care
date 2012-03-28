package org.motechproject.care.service;

import org.motechproject.care.domain.CareCase;
import org.motechproject.commcare.service.CaseService;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Created by IntelliJ IDEA.
 * User: pchandra
 * Date: 3/25/12
 * Time: 7:03 AM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Path("/case")
public class CareCaseService extends CaseService<CareCase> {

    public CareCaseService(){
       super(CareCase.class);
    }
   
    @Override
    protected void closeCase(CareCase ccCase) {
        String household_id = ccCase.getHousehold_id();
    }

    @Override
    protected void updateCase(CareCase ccCase) {
    }

    @Override
    protected void createCase(CareCase ccCase) {
        System.out.println("Case received: " +ccCase.getCaseName());
        System.out.println("primary contact name: " +ccCase.getPrimary_contact_name());
    }

    @GET
    public String test(){
        return  "Hello World";
    }
}
