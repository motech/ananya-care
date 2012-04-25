package org.motechproject.care.service.mapper;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.care.domain.Child;
import org.motechproject.care.request.CareCase;

public class ChildMapper {

    public static Child map(CareCase careCase){

        return new Child(
                careCase.getCase_id(), get_date_obj(careCase.getDate_modified()), careCase.getUser_id(), careCase.getCase_name(),
                careCase.getOwner_id(), get_date_obj(careCase.getDob())
                , get_date_obj(careCase.getBaby_measles()), get_date_obj(careCase.getBcg_date()), get_date_obj(careCase.getVit_a_1_date()),careCase.getMother_id(),get_date_obj(careCase.getHep_b_0_date()));
    }

    private static DateTime get_date_obj(String date_string) {
        return StringUtils.isNotEmpty(date_string)? DateTime.parse(date_string):null;
    }
}
