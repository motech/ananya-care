package org.motechproject.care.service;

import org.motechproject.care.request.CareCase;
import org.motechproject.care.request.CaseType;
import org.motechproject.casexml.service.CaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/care/**")
public class CareCaseService extends CaseService<CareCase>{

    private MotherService motherService;
    private ChildService childService;

    @Autowired
    public CareCaseService(MotherService motherService, ChildService childService) {
        super(CareCase.class);
        this.motherService = motherService;
        this.childService = childService;
    }

    @Override
    public void closeCase(CareCase careCase) {
        boolean wasClosed = motherService.closeCase(careCase.getCase_id());
         // if wasClosed is false
        // then
        //     Handle Child case close
        // end
    }

    @Override
    public void updateCase(CareCase careCase) {
    }

    @Override
    public void createCase(CareCase careCase) {
        System.out.println("Coming to the CReate");
        if(careCase.getCase_type().equals(CaseType.Mother.getType()))
            motherService.process(careCase);
        else
            childService.process(careCase);
    }
}
