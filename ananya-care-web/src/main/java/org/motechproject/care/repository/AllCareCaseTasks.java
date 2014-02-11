package org.motechproject.care.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.care.domain.CareCaseTask;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
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

    @View(name = "by_clientCaseId_and_milestoneName", map = "function(doc) {if(doc.type == 'CareCaseTask') {emit([doc.clientCaseId, doc.milestoneName]);}}")
    public CareCaseTask findByClientCaseIdAndMilestoneName(String clientCaseId, String milestoneName) {
        List<CareCaseTask> careCaseTasks = queryView("by_clientCaseId_and_milestoneName", ComplexKey.of(clientCaseId, milestoneName));
        return careCaseTasks.isEmpty() ? null : careCaseTasks.get(0);
    }

    @GenerateView
    public CareCaseTask findByCaseId(String caseId) {
        ViewQuery find_by_caseId = createQuery("by_caseId").key(caseId).includeDocs(true);
        List<CareCaseTask> careCaseTasks = db.queryView(find_by_caseId, CareCaseTask.class);
        if (careCaseTasks == null || careCaseTasks.isEmpty()) {
            return null;
        }
        return careCaseTasks.get(0);
    }
}
