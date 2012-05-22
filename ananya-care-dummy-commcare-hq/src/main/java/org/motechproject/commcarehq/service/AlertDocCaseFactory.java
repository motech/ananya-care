package org.motechproject.commcarehq.service;

import org.joda.time.DateTime;
import org.motechproject.care.domain.CareCaseTask;
import org.motechproject.care.domain.Client;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllCareCaseTasks;
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
    private AllCareCaseTasks allCareCaseTasks;

    @Autowired
    public AlertDocCaseFactory(AllMothers allMothers, AllChildren allChildren, AllCareCaseTasks allCareCaseTasks) {
        this.allMothers = allMothers;
        this.allChildren = allChildren;
        this.allCareCaseTasks = allCareCaseTasks;
    }

    public AlertDocCase getAlertDocCase(String xmlDocument, Document document){
        Client client = getClient(document);
        String caseId = getCaseId(document);
        return new AlertDocCase(caseId, client, xmlDocument, DateTime.now());
    }

    private Client getClient(Document document) {
        Element documentElement = document.getDocumentElement();
        
        NodeList caseList = documentElement.getElementsByTagName("person_id");
        if(caseList.getLength() != 0) {
            String caseId = caseList.item(0).getTextContent();
            Mother mother = allMothers.findByCaseId(caseId);
            if(mother != null) return mother;
            return allChildren.findByCaseId(caseId);
        }

        if(document.getElementsByTagName("close").getLength() == 0) {
            throw new MalformedXmlException(); 
        }

        return getClientFromCloseCase(document);
    }

    private String getCaseId(Document document) {
        Element caseElement = (Element) document.getElementsByTagName("case").item(0);
        return caseElement.getAttribute("case_id");
    }

    private Client getClientFromCloseCase(Document document) {
        String caseId = getCaseId(document);
        CareCaseTask careCaseTask = allCareCaseTasks.findByCaseId(caseId);
        if(careCaseTask == null) {
            throw new MalformedXmlException();
        }

        Client client = allMothers.findByCaseId(careCaseTask.getClientCaseId());
        if(client != null) {
            return client;
        }
        return allChildren.findByCaseId(careCaseTask.getClientCaseId());
    }
}
