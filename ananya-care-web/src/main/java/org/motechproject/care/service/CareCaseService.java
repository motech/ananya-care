package org.motechproject.care.service;

import org.motechproject.care.domain.CareCase;
import org.motechproject.commcare.service.CaseService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by IntelliJ IDEA.
 * User: pchandra
 * Date: 3/25/12
 * Time: 7:03 AM
 * To change this template use File | Settings | File Templates.
 */
//@Component
//@Path("/case")
@Controller
@RequestMapping("/case/**")
public class CareCaseService extends CaseService<CareCase> {

    public CareCaseService(){
       super(CareCase.class);
    }
   
    @Override
    public void closeCase(CareCase ccCase) {
        String household_id = ccCase.getHousehold_id();
    }

    @Override
    public void updateCase(CareCase ccCase) {
    }

    @Override
    public void createCase(CareCase ccCase) {
        System.out.println("Case received: " +ccCase.getCase_name());
        System.out.println("primary contact name: " +ccCase.getPrimary_contact_name());
    }

   // @GET
    @RequestMapping(value="/test",method = RequestMethod.GET)
    public String test(){
        return  "Hello World";
    }
}
