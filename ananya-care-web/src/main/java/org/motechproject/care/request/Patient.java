package org.motechproject.care.request;

public class Patient {
    private String case_type;
    private String case_id;

    public Patient(String caseId, String caseType) {
        this.case_id = caseId;
        this.case_type = caseType;
    }

    public String getCase_type() {
        return case_type;
    }

    public String getCase_id() {
        return case_id;
    }
}
