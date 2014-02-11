package org.motechproject.commcarehq.domain;


import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.care.domain.Client;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type == 'AlertDocCase'")
public class AlertDocCase extends MotechBaseDataObject {

    @JsonProperty
    private String clientCaseId;

    @JsonProperty
    private String xmlDocument;

    @JsonProperty
    private DateTime submittedAt;

    @JsonProperty
    private String name;

    @JsonProperty
    private String caseId;

    public AlertDocCase(String caseId, Client client, String xmlDocument, DateTime submittedAt) {
        this.caseId = caseId;
        this.clientCaseId = client.getCaseId();
        this.xmlDocument = xmlDocument;
        this.submittedAt = submittedAt;
        this.name = client.getName();
    }

    public AlertDocCase() {

    }

    public String getCaseId() {
        return caseId;
    }

    public String getXmlDocument() {
        return xmlDocument;
    }
}