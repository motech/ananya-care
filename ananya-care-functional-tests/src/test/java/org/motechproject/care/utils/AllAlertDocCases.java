package org.motechproject.care.utils;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllAlertDocCases extends MotechBaseRepository<AlertDocCase> {

    @Autowired
    public AllAlertDocCases(@Qualifier("ananyaCareDbConnector") CouchDbConnector dbCouchDbConnector) {
        super(AlertDocCase.class, dbCouchDbConnector);
    }

//    @GenerateView
//    public AlertDocCase findByCaseId(String caseId) {
//        ViewQuery find_by_caseId = createQuery("by_caseId").key(caseId).includeDocs(true);
//        List<AlertDocCase> alertDocCases = db.queryView(find_by_caseId, AlertDocCase.class);
//        if (alertDocCases == null || alertDocCases.isEmpty()) {
//            return null;
//        }
//        return alertDocCases.get(0);
//    }
}
