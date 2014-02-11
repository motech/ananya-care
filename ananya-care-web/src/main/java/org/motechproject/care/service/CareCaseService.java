package org.motechproject.care.service;

import org.apache.log4j.Logger;
import org.motechproject.care.request.CareCase;
import org.motechproject.care.request.CaseType;
import org.motechproject.care.service.mapper.ChildMapper;
import org.motechproject.care.service.mapper.MotherMapper;
import org.motechproject.care.service.exception.CaseException;
import org.motechproject.commons.date.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/care/**")
public class CareCaseService extends CaseService<CareCase>{

    private MotherService motherService;
    private ChildService childService;
    Logger logger = Logger.getLogger(CareCaseService.class);

    @Autowired
    public CareCaseService(MotherService motherService, ChildService childService) {
        super(CareCase.class);
        this.motherService = motherService;
        this.childService = childService;
    }

    @Override
    public void createCase(CareCase careCase) throws CaseException{
        validateCreateCase(careCase);
        String caseType = careCase.getCase_type();
        if(CaseType.Mother.getType().equals(caseType))
            motherService.process(MotherMapper.map(careCase));
        else if(CaseType.Child.getType().equals(caseType))
            childService.process(ChildMapper.map(careCase));
        else
            logger.info(String.format("Ignoring Case with type: %s", caseType));
    }

    @Override
    public void updateCase(CareCase careCase)  throws CaseException{
    }

    @Override
    public void closeCase(CareCase careCase) throws CaseException{
        validateCloseCase(careCase);
        boolean wasMotherClosed = motherService.closeCase(careCase.getCase_id());
        if(!wasMotherClosed)
            childService.closeCase(careCase.getCase_id());
    }

    private void validateCreateCase(CareCase careCase) throws CaseException {
        validateMandatory(careCase.getCase_id(), "Case Id");
        validateMandatory(careCase.getOwner_id(), "Owner Id");
    }

    private void validateCloseCase(CareCase careCase) throws CaseException {
        validateMandatory(careCase.getCase_id(), "Case Id");
    }

    private void validateMandatory(String fieldValue, String fieldName) throws CaseException {
        if(StringUtil.isNullOrEmpty(fieldValue)) {
            throw new CaseException(fieldName + " is a mandatory field.", HttpStatus.BAD_REQUEST);
        }
    }
}
