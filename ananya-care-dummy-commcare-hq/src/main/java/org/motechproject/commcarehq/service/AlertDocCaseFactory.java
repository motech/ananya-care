package org.motechproject.commcarehq.service;

import org.joda.time.DateTime;
import org.motechproject.care.domain.Client;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.commcarehq.domain.AlertDocCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Component
public class AlertDocCaseFactory {

    AllMothers allMothers;
    AllChildren allChildren;

    @Autowired
    public AlertDocCaseFactory(AllMothers allMothers, AllChildren allChildren) {
        this.allMothers = allMothers;
        this.allChildren = allChildren;
    }

    public AlertDocCase getAlertDocCase(String xmlDocument, Document document){
        Client client = getClient(document);
        return new AlertDocCase(client.getCaseId(),xmlDocument, DateTime.now(),client.getName());

    }

    private Client getClient(Document document) {
        Element documentElement = document.getDocumentElement();
        NodeList caseList = documentElement.getElementsByTagName("mother_id");
        if(caseList.getLength() != 0) {
            return allMothers.findByCaseId(caseList.item(0).getTextContent());
        }
        caseList =  documentElement.getElementsByTagName("child_id");
        if(caseList.getLength() != 0) {
            return allChildren.findByCaseId(caseList.item(0).getTextContent());
        }
        throw new MalformedXmlException();
    }
}
