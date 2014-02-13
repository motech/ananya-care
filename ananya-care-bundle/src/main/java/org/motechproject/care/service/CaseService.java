package org.motechproject.care.service;

import org.apache.log4j.Logger;
import org.motechproject.care.builder.ResponseMessageBuilder;
import org.motechproject.care.exception.CaseParserException;
import org.motechproject.care.parser.CommcareCaseParser;
import org.motechproject.care.service.exception.CaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

public abstract class CaseService<T> {

    private ResponseMessageBuilder responseMessageBuilder;

    private static Logger logger = Logger.getLogger(CaseService.class);

    private Class<T> clazz;

    public CaseService(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Autowired
    public void setResponseMessageBuilder(ResponseMessageBuilder responseMessageBuilder) {
        this.responseMessageBuilder = responseMessageBuilder;
    }

    @RequestMapping(value = "/process", method = RequestMethod.POST)
    public ResponseEntity<String> processCase(HttpEntity<String> requestEntity) throws IOException {
        logger.info(requestEntity.getBody());
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.TEXT_XML);

        try {
            CommcareCaseParser<T> caseParser = new CommcareCaseParser<T>(clazz, requestEntity.getBody());
            T object = caseParser.parseCase();

            processCaseAction(caseParser, object);

        } catch (CaseParserException exception) {
            logError(exception);
            return loggedResponse(new ResponseEntity<String>(responseMessageBuilder.createResponseMessage(exception), responseHeaders, HttpStatus.BAD_REQUEST));

        } catch (CaseException exception) {
            logError(exception);
            return loggedResponse(new ResponseEntity<String>(responseMessageBuilder.createResponseMessage(exception), responseHeaders, exception.getHttpStatusCode()));

        } catch (RuntimeException exception) {
            logError(exception);
            return loggedResponse(new ResponseEntity<String>(responseMessageBuilder.messageForRuntimeException(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR));
        }
        return loggedResponse(new ResponseEntity<String>(responseMessageBuilder.messageForSuccess(), responseHeaders, HttpStatus.OK));
    }

    private void logError(Throwable exception) {
        logger.error("Exception encountered while processing case xml" + exception.getMessage() + exception.getStackTrace(), exception);
    }

    private ResponseEntity<String> loggedResponse(ResponseEntity<String> responseEntity) {
        logger.info("Sending case xml Response: Status Code: " + responseEntity.getStatusCode() + "; Body: " + responseEntity.getBody());
        return responseEntity;
    }

    private void processCaseAction(CommcareCaseParser<T> caseParser, T object) throws CaseException {
        if ("CREATE".equals(caseParser.getCaseAction()))
            createCase(object);
        else if ("UPDATE".equals(caseParser.getCaseAction()))
            updateCase(object);
        else if ("CLOSE".equals(caseParser.getCaseAction()))
            closeCase(object);
    }


    public abstract void closeCase(T ccCase) throws CaseException;

    public abstract void updateCase(T ccCase) throws CaseException;

    public abstract void createCase(T ccCase) throws CaseException;

}
