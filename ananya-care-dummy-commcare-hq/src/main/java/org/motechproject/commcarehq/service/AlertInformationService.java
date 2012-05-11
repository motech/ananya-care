package org.motechproject.commcarehq.service;


import org.joda.time.DateTime;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.service.MilestoneAlerts;
import org.motechproject.scheduletracking.api.service.impl.EnrollmentAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@Controller
@RequestMapping("/**")
public class AlertInformationService {

    private EnrollmentAlertService enrollmentAlertService;
    private AllEnrollments allEnrollments;

    @Autowired
    public AlertInformationService(EnrollmentAlertService enrollmentAlertService,AllEnrollments allEnrollments) {
        this.enrollmentAlertService = enrollmentAlertService;
        this.allEnrollments = allEnrollments;
    }

    @RequestMapping(value="/alert",method= RequestMethod.GET)
    public void captureAlertsFor(@RequestParam("externalId")String externalId, @RequestParam("scheduleName")String scheduleName, HttpServletResponse response) throws IOException {
        String result = null;

        List<Enrollment> enrollments = allEnrollments.findByExternalId(externalId);
        for(Enrollment enrollment : enrollments){
            if(enrollment.getScheduleName().equals(scheduleName)) {
                MilestoneAlerts milestoneAlerts = enrollmentAlertService.getAlertTimings(enrollment);
                List<DateTime> dueWindowAlertTimings = milestoneAlerts.getDueWindowAlertTimings();
                if(!dueWindowAlertTimings.isEmpty())
                     result = dueWindowAlertTimings.get(0).toString();
            }
        }
        if(result == null) result = "No Match";
        response.getOutputStream().print(result);
    }

    @RequestMapping(value="/alerts",method= RequestMethod.GET)
    public void captureAlertsFor(@RequestParam("externalId") String externalId, HttpServletResponse response) throws IOException {
        String result = "";

        List<Enrollment> enrollments = allEnrollments.findByExternalId(externalId);
        for(Enrollment enrollment : enrollments){
                MilestoneAlerts milestoneAlerts = enrollmentAlertService.getAlertTimings(enrollment);
                List<DateTime> dueWindowAlertTimings = milestoneAlerts.getDueWindowAlertTimings();

                if( dueWindowAlertTimings != null && !dueWindowAlertTimings.isEmpty()){
                     result = result.concat(enrollment.getScheduleName() + "\n");
                     result = result.concat(enrollment.getCurrentMilestoneName() + " : " + dueWindowAlertTimings.get(0).toString() + "\n");
                     result = result.concat("----------------------------------------------------------"+"\n");
                }
        }
        if(result.isEmpty()) result = "No Match";
        response.getOutputStream().print(result);
    }
}
