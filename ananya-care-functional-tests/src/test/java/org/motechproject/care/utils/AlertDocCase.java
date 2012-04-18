package org.motechproject.care.utils;


import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type == 'CareCase'")
public class AlertDocCase extends MotechBaseDataObject {

    @JsonProperty
    private String xmlDocument;

    @JsonProperty
    private DateTime submittedAt;

    public AlertDocCase(String xmlDocument, DateTime submittedAt) {
        this.xmlDocument = xmlDocument;
        this.submittedAt = submittedAt;
    }
}