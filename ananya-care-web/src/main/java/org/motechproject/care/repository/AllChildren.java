package org.motechproject.care.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.joda.time.DateTime;
import org.motechproject.care.domain.Child;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllChildren extends MotechBaseRepository<Child> implements AllClients<Child> {

    @Autowired
    public AllChildren(@Qualifier("ananyaCareDbConnector") CouchDbConnector dbCouchDbConnector) {
        super(Child.class, dbCouchDbConnector);
    }

    @GenerateView
    public Child findByCaseId(String caseId) {
        ViewQuery find_by_caseId = createQuery("by_caseId").key(caseId).includeDocs(true);
        List<Child> children = db.queryView(find_by_caseId, Child.class);
        if (children == null || children.isEmpty()) {
            return null;
        }
        return children.get(0);
    }

    public void add(Child child) {
        child.setDocCreateTime(DateTime.now());
        super.add(child);
    }
}
