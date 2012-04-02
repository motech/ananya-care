package org.motechproject.care.domain;

/**
 * Created by IntelliJ IDEA.
 * User: pchandra
 * Date: 3/25/12
 * Time: 7:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class CareCase {
    private String case_id;
    private String date_modified;
    private String action;
    private String case_type_id;
    private String case_name;
    private String household_id;
    private String primary_contact_name;
    private String visit_number;


    public String getCase_id() {
        return case_id;
    }

    public void setCase_id(String case_id) {
        this.case_id = case_id;
    }

    public String getDate_modified() {
        return date_modified;
    }

    public void setDate_modified(String date_modified) {
        this.date_modified = date_modified;
    }

    public String getCase_type_id() {
        return case_type_id;
    }

    public void setCase_type_id(String case_type_id) {
        this.case_type_id = case_type_id;
    }

    public String getCase_name() {
        return case_name;
    }

    public void setCase_name(String case_name) {
        this.case_name = case_name;
    }


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
