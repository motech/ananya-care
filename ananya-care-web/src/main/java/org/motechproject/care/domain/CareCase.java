package org.motechproject.care.domain;

/**
 * Created by IntelliJ IDEA.
 * User: pchandra
 * Date: 3/25/12
 * Time: 7:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class CareCase {
    private String caseId;
    private String dateModified;
    private String action;
    private String caseTypeId;
    private String caseName;

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public String getCaseTypeId() {
        return caseTypeId;
    }

    public void setCaseTypeId(String caseTypeId) {
        this.caseTypeId = caseTypeId;
    }

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    private String household_id;
    private String primary_contact_name;
    private String visit_number;

    public String getHousehold_id() {
        return household_id;
    }

    public void setHousehold_id(String household_id) {
        this.household_id = household_id;
    }

    public String getPrimary_contact_name() {
        return primary_contact_name;
    }

    public void setPrimary_contact_name(String primary_contact_name) {
        this.primary_contact_name = primary_contact_name;
    }

    public String getVisit_number() {
        return visit_number;
    }

    public void setVisit_number(String visit_number) {
        this.visit_number = visit_number;
    }

}
