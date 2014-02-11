package org.motechproject.care.gateway;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.motechproject.care.domain.CaseTask;
import org.motechproject.care.request.CaseRequest;
import org.motechproject.care.request.CloseElement;
import org.motechproject.care.request.CommcareRequestData;
import org.motechproject.care.request.CreateElement;
import org.motechproject.care.request.MetaElement;
import org.motechproject.care.request.Patient;
import org.motechproject.care.request.UpdateElement;
import org.motechproject.care.request.converter.PatientConverter;
import org.motechproject.care.request.Index;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CaseTaskXmlConverter {

    public CaseTaskXmlConverter() {
    }

    public String convertToCaseXml(CaseTask task) {
        CaseRequest caseRequest = mapToCase(task);
        CommcareRequestData request = createRequestWithEnvelope(caseRequest);

        return convertToXml(request);
    }

    public String convertToCloseCaseXml(CaseTask task) {
        CaseRequest caseRequest = mapToCloseCase(task);
        CommcareRequestData request = createRequestWithEnvelope(caseRequest);
        return convertToCloseXml(request);
    }

    private CaseRequest mapToCloseCase(CaseTask task) {
        CaseRequest ccCase = createCase(task);
        CloseElement close = new CloseElement();
        ccCase.setCloseElement(close);
        return ccCase;
    }

    private CaseRequest mapToCase(CaseTask task) {
        CaseRequest ccCase = createCase(task);

        CreateElement create = new CreateElement(task.getCaseType(), task.getCaseName(), task.getOwnerId());
        ccCase.setCreateElement(create);
        UpdateElement update = new UpdateElement(task.getTaskId(), task.getDateEligible(), task.getDateExpires());
        ccCase.setUpdateElement(update);

        Patient patient = new Patient(task.getClientCaseId(), task.getClientCaseType());
        Index index = new Index(patient);
        ccCase.setIndex(index);

        return ccCase;
    }

    private CaseRequest createCase(CaseTask task) {
        return new CaseRequest(task.getCaseId(),task.getMotechUserId(),task.getCurrentTime());
    }

    private String convertToCloseXml(CommcareRequestData request) {
        XStream xstream = mapEnvelope();
        xstream.aliasField("close", CaseRequest.class, "closeElement");

        return xstream.toXML(request);
    }

    private String convertToXml(CommcareRequestData request) {

        XStream xstream = mapEnvelope();

        xstream.aliasField("create", CaseRequest.class, "createElement");
        xstream.aliasField("update", CaseRequest.class, "updateElement");
        xstream.alias("index", Index.class);
        xstream.omitField(Index.class, "patientTagName");

        String patientCaseType = request.getCcCase().getIndex().getPatient().getCase_type();
        xstream.registerConverter(new PatientConverter(patientCaseType));
        xstream.aliasField("person_id", Index.class, "patient");

        return xstream.toXML(request);
    }

    private XStream mapEnvelope() {
        XStream xstream = new XStream(new DomDriver("UTF-8", new NoNameCoder()));

        xstream.alias("data", CommcareRequestData.class);
        xstream.useAttributeFor(CommcareRequestData.class, "xmlns");
        xstream.aliasField("case", CommcareRequestData.class, "ccCase");

        xstream.alias("meta", MetaElement.class);
        xstream.useAttributeFor(MetaElement.class, "xmlns");
        xstream.useAttributeFor(CaseRequest.class, "case_id");
        xstream.useAttributeFor(CaseRequest.class, "user_id");
        xstream.useAttributeFor(CaseRequest.class, "xmlns");
        xstream.useAttributeFor(CaseRequest.class, "date_modified");
        return xstream;
    }

    private CommcareRequestData createRequestWithEnvelope(CaseRequest caseRequest) {
        MetaElement metaElement = new MetaElement("http://openrosa.org/jr/xforms", UUID.randomUUID().toString(), caseRequest.getDate_modified(), caseRequest.getDate_modified(), caseRequest.getUser_id());
        return new CommcareRequestData("http://bihar.commcarehq.org/pregnancy/task", metaElement,caseRequest);    }

}