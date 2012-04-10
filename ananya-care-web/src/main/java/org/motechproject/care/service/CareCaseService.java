package org.motechproject.care.service;

import org.motechproject.care.domain.Mother;
import org.motechproject.care.request.CareCase;
import org.motechproject.care.request.CaseType;
import org.motechproject.care.schedule.service.CareScheduleTrackingService;
import org.motechproject.care.service.mapper.MotherMapper;
import org.motechproject.commcare.service.CaseService;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/care/**")
public class CareCaseService extends CaseService<CareCase>{

    private MotherService motherService;

    @Autowired
    private CareScheduleTrackingService scheduleTrackingService;

    @Autowired
    public CareCaseService(MotherService motherService,CareScheduleTrackingService scheduleTrackingService) {
        super(CareCase.class);
        this.motherService = motherService;
        this.scheduleTrackingService = scheduleTrackingService;
    }

    @Override
    public void closeCase(CareCase careCase) {
        boolean wasClosed = motherService.closeCase(careCase.getCase_id());
         // if wasClosed is false
        // then
        //     Handle Child case close
        // end
    }

    @Override
    public void updateCase(CareCase careCase) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void createCase(CareCase careCase) {
        scheduleTrackingService.enroll(enrollmentRequest(careCase));
        if(careCase.getCase_type().equals(CaseType.Mother.getType())){
            Mother motherObj = MotherMapper.map(careCase);
            motherService.createUpdateCase(motherObj);
        }
    }

    private EnrollmentRequest enrollmentRequest(CareCase careCase) {
        String id = careCase.getCase_id();
        String case_type = careCase.getCase_type();
        String scheduleName ="";
        if(case_type == "pregnancy") 
            scheduleName = "TT" ;

        return new EnrollmentRequest(id,"TT Vaccination",DateUtil.time(DateUtil.now().plusMinutes(2)), DateUtil.today(),DateUtil.time(DateUtil.now().plusMinutes(1)),DateUtil.today(),DateUtil.time(DateUtil.now().plusMinutes(1)),null,null);
    }
}
