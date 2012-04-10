package org.motechproject.care.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.care.domain.CareCaseTask;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllCareCaseTasks extends MotechBaseRepository<CareCaseTask> {

    @Autowired
    public AllCareCaseTasks(@Qualifier("ananyaCareDbConnector") CouchDbConnector dbCouchDbConnector) {
        super(CareCaseTask.class, dbCouchDbConnector);
    }
}
