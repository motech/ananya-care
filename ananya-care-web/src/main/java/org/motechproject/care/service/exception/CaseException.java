package org.motechproject.care.service.exception;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

public class CaseException extends RuntimeException {

    private HttpStatus httpStatusCode;
    private String message;
    private List<CaseError> errors = new ArrayList<CaseError>();

    public CaseException(Exception cause, HttpStatus httpStatusCode) {
        super(cause);
        this.message = cause.getMessage();
        this.httpStatusCode = httpStatusCode;
    }

    public CaseException(Exception cause, String message, HttpStatus httpStatusCode) {
        super(message, cause);
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }

    public CaseException(String message, HttpStatus httpStatusCode) {
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }

    public CaseException(Exception cause, String message, HttpStatus httpStatusCode, List<CaseError> errors) {
        super(message, cause);
        this.message = message;
        this.httpStatusCode = httpStatusCode;
        this.errors = errors;
    }

    public CaseException(String message, HttpStatus httpStatusCode, List<CaseError> errors) {
        this.message = message;
        this.httpStatusCode = httpStatusCode;
        this.errors = errors;
    }

    public HttpStatus getHttpStatusCode() {
        return httpStatusCode;
    }

    public CaseException addError(CaseError error) {
        this.errors.add(error);
        return this;
    }

    public String getMessage() {
        return message;
    }

    public List<CaseError> getErrors() {
        return errors;
    }
}