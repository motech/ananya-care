package org.motechproject.care.builder;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.motechproject.care.exception.CaseParserException;
import org.motechproject.care.service.exception.CaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.StringWriter;

@Component
public class ResponseMessageBuilder {

    private VelocityEngine velocityEngine;

    @Autowired
    public ResponseMessageBuilder(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    public String createResponseMessage(CaseException exception) {
        Template template = velocityEngine.getTemplate("/templates/responses/responseWithMultipleErrors.vm");
        VelocityContext context = new VelocityContext();
        context.put("message", exception.getMessage());
        context.put("status", "submit_error");
        context.put("errors", exception.getErrors());
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

    public String createResponseMessage(CaseParserException exception){
        return responseWithMessage("An unexpected exception occurred while while trying to parse xml ", "submit_error");
    }

    public String messageForRuntimeException() {
        return responseWithMessage("An unexpected exception occurred while processing. Please verify the message and try again.", "submit_error");
    }

    public String messageForSuccess() {
        return responseWithMessage("Request successfully processed.", "submit_success");
    }

    private String responseWithMessage(String message, String status) {
        Template template = velocityEngine.getTemplate("/templates/responses/simpleResponse.vm");
        VelocityContext context = new VelocityContext();
        context.put("message", message);
        context.put("status", status);
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }
}