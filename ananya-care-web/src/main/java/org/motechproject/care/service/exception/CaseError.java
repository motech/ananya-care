package org.motechproject.care.service.exception;

public class CaseError {

    private String code;
    private String message;

    public CaseError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
