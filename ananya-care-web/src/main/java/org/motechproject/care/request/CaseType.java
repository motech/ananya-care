package org.motechproject.care.request;


public enum CaseType {


    Mother("cc_bihar_pregnancy"),
    Child("cc_bihar_newborn");
    private String caseType;


    CaseType(String case_type) {
        this.caseType=case_type;
    }

    public String getType() {
        return caseType;
    }
}
