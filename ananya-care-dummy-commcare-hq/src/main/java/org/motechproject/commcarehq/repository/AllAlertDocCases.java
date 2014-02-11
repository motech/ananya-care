package org.motechproject.commcarehq.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.commcarehq.domain.AlertDocCase;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AllAlertDocCases extends MotechBaseRepository<AlertDocCase> {

    @Autowired
    public AllAlertDocCases(@Qualifier("ananyaCareDummyAppDbConnector") CouchDbConnector dbCouchDbConnector) {
        super(AlertDocCase.class, dbCouchDbConnector);
    }

    @View(name = "by_caseId", map = "function(doc) {if(doc.type == 'AlertDocCase') {emit(doc.caseId);}}")
    public List<AlertDocCase> findAllByCaseId(String caseId) {
        ViewQuery find_by_caseId = createQuery("by_caseId").key(caseId).includeDocs(true);
        List<AlertDocCase> alertDocCases = db.queryView(find_by_caseId, AlertDocCase.class);
        if (alertDocCases == null) {
            return new ArrayList<AlertDocCase>();
        }
        return alertDocCases;
    }

    @View(name = "by_clientCaseId", map = "function(doc) {if(doc.type == 'AlertDocCase') {emit(doc.clientCaseId);}}")
    public List<AlertDocCase> findAllByClientCaseId(String clientCaseId) {
        ViewQuery find_by_clientCaseId = createQuery("by_clientCaseId").key(clientCaseId).includeDocs(true);
        List<AlertDocCase> alertDocCases = db.queryView(find_by_clientCaseId, AlertDocCase.class);
        if (alertDocCases == null) {
            return new ArrayList<AlertDocCase>();
        }
        return alertDocCases;
    }
}
