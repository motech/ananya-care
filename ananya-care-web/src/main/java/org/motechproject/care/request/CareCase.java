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
    private String tt_1_date;
    private String tt_2_date;
    private String last_preg_tt;
    private String anc_1_date;
    private String anc_2_date;
    private String anc_3_date;
    private String anc_4_date;
    private String tt_booster_date;

    public String getTt_1_date() {
        return tt_1_date;
    }

    public void setTt_1_date(String tt_1_date) {
        this.tt_1_date = tt_1_date;
    }

    public String getTt_2_date() {
        return tt_2_date;
    }

    public void setTt_2_date(String tt_2_date) {
        this.tt_2_date = tt_2_date;
    }

    public String getLast_preg_tt() {
        return last_preg_tt;
    }

    public void setLast_preg_tt(String last_preg_tt) {
        this.last_preg_tt = last_preg_tt;
    }

    public String getAnc_1_date() {
        return anc_1_date;
    }

    public void setAnc_1_date(String anc_1_date) {
        this.anc_1_date = anc_1_date;
    }

    public String getAnc_2_date() {
        return anc_2_date;
    }

    public void setAnc_2_date(String anc_2_date) {
        this.anc_2_date = anc_2_date;
    }

    public String getAnc_3_date() {
        return anc_3_date;
    }

    public void setAnc_3_date(String anc_3_date) {
        this.anc_3_date = anc_3_date;
    }

    public String getAnc_4_date() {
        return anc_4_date;
    }

    public void setAnc_4_date(String anc_4_date) {
        this.anc_4_date = anc_4_date;
    }

    public String getTt_booster_date() {
        return tt_booster_date;
    }

    public void setTt_booster_date(String tt_booster_date) {
        this.tt_booster_date = tt_booster_date;
    }

    public CareCase() {
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
