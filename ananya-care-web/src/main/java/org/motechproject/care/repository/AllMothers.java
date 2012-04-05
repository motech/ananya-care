package org.motechproject.care.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.motechproject.care.domain.Mother;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllMothers extends MotechBaseRepository<Mother> {

    @Autowired
    public AllMothers(@Qualifier("ananyaCareDbConnector") CouchDbConnector dbCouchDbConnector) {
        super(Mother.class, dbCouchDbConnector);
    }

    @GenerateView
    public Mother findByCaseId(String caseId) {
        ViewQuery find_by_caseId = createQuery("by_caseId").key(caseId).includeDocs(true);
        List<Mother> mothers = db.queryView(find_by_caseId, Mother.class);
        if (mothers == null || mothers.isEmpty()) {
            return null;
        }
        return mothers.get(0);
    }
}
