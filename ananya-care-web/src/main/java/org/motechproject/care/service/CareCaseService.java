package org.motechproject.care.service;

import org.motechproject.care.domain.Mother;
import org.motechproject.care.request.CareCase;
import org.motechproject.care.request.CaseType;
import org.motechproject.care.service.mapper.MotherMapper;
import org.motechproject.commcare.service.CaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/case/**")
public class CareCaseService extends CaseService<CareCase>{

    private MotherService motherService;

    @Autowired
    public CareCaseService(MotherService motherService) {
        super(CareCase.class);
        this.motherService = motherService;
    }

    @Override
    public void closeCase(CareCase careCase) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateCase(CareCase careCase) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void createCase(CareCase careCase) {
        if(careCase.getCase_type().equals(CaseType.Mother.getType())){
            Mother motherObj = MotherMapper.map(careCase);
            motherService.process(motherObj);
        }
    }
}
