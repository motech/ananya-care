package org.motechproject.care.service.mapper;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.request.CareCase;

public class MotherMapper {

    public static Mother map(CareCase careCase){
        DateTime edd = StringUtils.isNotEmpty(careCase.getEdd())? DateTime.parse(careCase.getEdd()):null;
        DateTime add = StringUtils.isNotEmpty(careCase.getAdd())? DateTime.parse(careCase.getAdd()):null;
        DateTime date_modified = StringUtils.isNotEmpty(careCase.getDate_modified()) ?  DateTime.parse(careCase.getDate_modified()):null;

        return new Mother(careCase.getCase_id(),  date_modified, careCase.getUser_id(), careCase.getCase_name(), careCase.getOwner_id(), edd, add );
    }
}
