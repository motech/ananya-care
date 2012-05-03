package org.motechproject.care.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.care.domain.CareCaseTask;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllCareCaseTasks extends MotechBaseRepository<CareCaseTask> {

    @Autowired
    public AllCareCaseTasks(@Qualifier("ananyaCareDbConnector") CouchDbConnector dbCouchDbConnector) {
        super(CareCaseTask.class, dbCouchDbConnector);
    }

    @View(name = "find_by_clientCaseId_and_milestoneName", map = "function(doc) {{emit([doc.clientCaseId, doc.milestoneName]);}}")
    public CareCaseTask findByClientCaseIdAndMilestoneName(String clientCaseId, String milestoneName) {
        List<CareCaseTask> careCaseTasks = queryView("find_by_clientCaseId_and_milestoneName", ComplexKey.of(clientCaseId, milestoneName));
        return careCaseTasks.isEmpty() ? null : careCaseTasks.get(0);
    }
}
