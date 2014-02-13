package org.motechproject.care.gateway;

import org.motechproject.care.domain.CaseTask;
import org.motechproject.http.agent.service.HttpAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommcareCaseGateway{
    private CaseTaskXmlConverter caseTaskXmlConverter;
    private HttpAgent httpAgent;

    @Autowired
    public CommcareCaseGateway(CaseTaskXmlConverter caseTaskXmlConverter, HttpAgent httpAgent) {
        this.caseTaskXmlConverter = caseTaskXmlConverter;
        this.httpAgent = httpAgent;
    }

    public void submitCase(String commcareUrl, CaseTask task){
        String request = caseTaskXmlConverter.convertToCaseXml(task);
        httpAgent.execute(commcareUrl, request, null);
    }

    public void closeCase(String commcareUrl, CaseTask task) {
        String request = caseTaskXmlConverter.convertToCloseCaseXml(task);

        httpAgent.execute(commcareUrl, request, null);
    }
}
