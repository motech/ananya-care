package org.motechproject.care.exception;

public class CaseParserException extends Exception{
    public CaseParserException(String message) {
        super(message);
    }

    public CaseParserException(Exception ex, String message) {
        super(message, ex);
    }
}
