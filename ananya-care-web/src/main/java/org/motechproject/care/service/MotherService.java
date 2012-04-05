package org.motechproject.care.service;

import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllMothers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MotherService{

    AllMothers allMothers;

    @Autowired
    public MotherService(AllMothers allMothers) {
        this.allMothers = allMothers;
    }

    public void process(Mother mother) {
        //To change body of created methods use File | Settings | File Templates.
    }
}
