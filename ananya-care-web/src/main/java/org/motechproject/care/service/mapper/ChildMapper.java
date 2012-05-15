package org.motechproject.care.service.mapper;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.care.domain.Child;
import org.motechproject.care.request.CareCase;

public class ChildMapper {

    public static Child map(CareCase careCase){
        boolean isChildAlive = careCase.getChild_alive() == null || !careCase.getChild_alive().equalsIgnoreCase("no");
        return new Child(
                careCase.getCase_id(), get_date_obj(careCase.getDate_modified()), careCase.getUser_id(), careCase.getCase_name(),
                careCase.getOwner_id(), get_date_obj(careCase.getDob()),
                get_date_obj(careCase.getMeasles_date()), get_date_obj(careCase.getBcg_date()), get_date_obj(careCase.getVit_a_1_date()),careCase.getMother_id(),
                get_date_obj(careCase.getHep_b_0_date()),get_date_obj(careCase.getHep_b_1_date()),get_date_obj(careCase.getHep_b_2_date()),get_date_obj(careCase.getHep_b_3_date()),
                get_date_obj(careCase.getDpt_1_date()),get_date_obj(careCase.getDpt_2_date()),get_date_obj(careCase.getDpt_3_date()),get_date_obj(careCase.getDpt_booster_date()), get_date_obj(careCase.getOpv_0_date()), get_date_obj(careCase.getOpv_1_date()), get_date_obj(careCase.getOpv_2_date()), get_date_obj(careCase.getOpv_3_date()), get_date_obj(careCase.getOpv_booster_date()), isChildAlive);
    }

    private static DateTime get_date_obj(String date_string) {
        return StringUtils.isNotEmpty(date_string)? DateTime.parse(date_string):null;
    }
}
