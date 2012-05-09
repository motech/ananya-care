package org.motechproject.care.service;

import org.motechproject.care.domain.Child;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.request.CareCase;
import org.motechproject.care.service.mapper.ChildMapper;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChildService {

    private AllChildren allChildren;
    private ChildVaccinationProcessor childVaccinationProcessor;


    @Autowired
    public ChildService(AllChildren allChildren, ChildVaccinationProcessor childVaccinationProcessor) {
        this.allChildren = allChildren;
        this.childVaccinationProcessor = childVaccinationProcessor;
    }

    public void process(CareCase careCase) {
        Child child = ChildMapper.map(careCase);
        Child childFromDb = allChildren.findByCaseId(child.getCaseId());

        if(childFromDb == null)
            processNew(child);
        else if(childFromDb.isActive())
            processExisting(childFromDb, child);
    }

    private void processNew(Child child) {
        if(isOlderThanAYear(child))
            return;

        allChildren.add(child);
        if(child.isActive())
            childVaccinationProcessor.enrollUpdateVaccines(child);
    }

    private void processExisting(Child childFromDb, Child child) {
        childFromDb.setValuesFrom(child);
        allChildren.update(childFromDb);

        if(childFromDb.isActive())
            childVaccinationProcessor.enrollUpdateVaccines(childFromDb);
        else
            childVaccinationProcessor.closeSchedules(childFromDb);
    }

    public boolean closeCase(String caseId) {
        Child child = allChildren.findByCaseId(caseId);
        if(child == null)
            return false;

        if(!child.isActive())
            return true;

        child.setClosedByCommcare(true);
        allChildren.update(child);
        childVaccinationProcessor.closeSchedules(child);
        return true;
    }


    public boolean expireCase(String caseId) {
        Child child = allChildren.findByCaseId(caseId);
        if(child == null)
            return false;

        if(!child.isActive()) {
            return true;
        }
        child.setExpired(true);
        allChildren.update(child);
        childVaccinationProcessor.closeSchedules(child);
        return true;
    }

    private boolean isOlderThanAYear(Child child) {
        return !DateUtil.today().minusYears(1).isBefore(child.getDOB().toLocalDate());
    }
}
