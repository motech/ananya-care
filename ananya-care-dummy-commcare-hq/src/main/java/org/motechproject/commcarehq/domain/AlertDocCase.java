package org.motechproject.commcarehq.domain;


import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type == 'AlertDocCase'")
public class AlertDocCase extends MotechBaseDataObject {

    @JsonProperty
    private String caseId;

    @JsonProperty
    private String xmlDocument;

    @JsonProperty
    private DateTime submittedAt;

    @JsonProperty
    private String name;

    public AlertDocCase(String caseId, String xmlDocument, DateTime submittedAt,String name) {
        this.caseId = caseId;
        this.xmlDocument = xmlDocument;
        this.submittedAt = submittedAt;
        this.name = name;
    }

    public AlertDocCase() {

    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getXmlDocument() {
        return xmlDocument;
    }

    public void setXmlDocument(String xmlDocument) {
        this.xmlDocument = xmlDocument;
    }

    public DateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(DateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}