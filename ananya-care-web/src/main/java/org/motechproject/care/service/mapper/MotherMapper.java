package org.motechproject.care.service.mapper;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.request.CareCase;

public class MotherMapper {

    public static Mother map(CareCase careCase){

        boolean last_preg_tt = careCase.getLast_preg_tt()!=null && careCase.getLast_preg_tt().equalsIgnoreCase("yes") ? true : false;
        boolean isMotherActive = careCase.getMother_alive()!=null && careCase.getMother_alive().equalsIgnoreCase("no") ? false : true;
        return new Mother(
                careCase.getCase_id(),  get_date_obj(careCase.getDate_modified()), careCase.getUser_id(), careCase.getCase_name(),
                careCase.getOwner_id(), get_date_obj(careCase.getEdd()), get_date_obj(careCase.getAdd())
                , get_date_obj(careCase.getTt_1_date()), get_date_obj(careCase.getTt_2_date()), last_preg_tt, get_date_obj(careCase.getAnc_1_date())
                , get_date_obj(careCase.getAnc_2_date()), get_date_obj(careCase.getAnc_3_date()), get_date_obj(careCase.getAnc_4_date()), get_date_obj(careCase.getTt_booster_date()),isMotherActive);
    }

    private static DateTime get_date_obj(String date_string) {
        return StringUtils.isNotEmpty(date_string)? DateTime.parse(date_string):null;
    }
}
