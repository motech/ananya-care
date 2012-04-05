package org.motechproject.care.request;

public class CareCase{
    private String case_id;
    private String case_type;
    private String date_modified;
    private String user_id;
    private String case_name;
    private String owner_id;
    private String edd;
    private String add;

    public CareCase() {
    }

    public CareCase(String case_id, String case_type, String date_modified, String user_id, String case_name, String owner_id, String edd, String add) {
        this.case_id = case_id;
        this.case_type = case_type;
        this.date_modified = date_modified;
        this.user_id = user_id;
        this.case_name = case_name;
        this.owner_id = owner_id;
        this.edd = edd;
        this.add = add;
    }

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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCase_name() {
        return case_name;
    }

    public void setCase_name(String case_name) {
        this.case_name = case_name;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getEdd() {
        return edd;
    }

    public void setEdd(String edd) {
        this.edd = edd;
    }

    public String getAdd() {
        return add;
    }

    public void setAdd(String add) {
        this.add = add;
    }

    public String getCase_type() {
        return case_type;
    }

    public void setCase_type(String case_type) {
        this.case_type = case_type;
    }


}
