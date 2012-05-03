package org.motechproject.care.service;

import org.motechproject.care.domain.Child;
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
        Child savedChild = createUpdate(child);
        if(savedChild != null)
            childVaccinationProcessor.enrollUpdateVaccines(savedChild);
    }

    private Child createUpdate(Child child) {
        Child childFromDb = allChildren.findByCaseId(child.getCaseId());
        if(childFromDb == null){
            if(isOlderThanAYear(child))
                return null;
            allChildren.add(child);
            return child;
        }

        childFromDb.setValuesFrom(child);

        allChildren.update(childFromDb);
        return childFromDb;
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
