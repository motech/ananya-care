package org.motechproject.care.tools;


import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.io.IOUtils;
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/diagnostics/**")
public class AlertInformationService {

    private EnrollmentAlertService enrollmentAlertService;

    private AllEnrollments allEnrollments;

    private StringTemplate stringTemplate;

    @Autowired
    public AlertInformationService(EnrollmentAlertService enrollmentAlertService, AllEnrollments allEnrollments) throws IOException {
        this.enrollmentAlertService = enrollmentAlertService;
        this.allEnrollments = allEnrollments;
        InputStream resourceAsStream = getClass().getResourceAsStream("/alertResponse.st");
        stringTemplate = new StringTemplate(IOUtils.toString(resourceAsStream));
    }

    @RequestMapping(value="/alerts",method = RequestMethod.GET)
    public void captureAlertsFor(@RequestParam("externalId") String externalId, HttpServletResponse response) throws IOException {

        stringTemplate.reset();
        List<Enrollment> enrollments = allEnrollments.findByExternalId(externalId);
        List<EnrollmentAlert> enrollmentAlerts = new ArrayList<EnrollmentAlert>();
        for (Enrollment enrollment : enrollments) {
            MilestoneAlerts milestoneAlerts = enrollmentAlertService.getAlertTimings(enrollment);
            List<DateTime> dueWindowAlertTimings = milestoneAlerts.getDueWindowAlertTimings();
            if (dueWindowAlertTimings != null && !dueWindowAlertTimings.isEmpty())
                enrollmentAlerts.add(new EnrollmentAlert(enrollment, dueWindowAlertTimings.get(0)));
        }

        String result = "No Match";
        if (enrollmentAlerts.size() > 0) {
            stringTemplate.setAttribute("enrollmentAlerts", enrollmentAlerts);
            result = stringTemplate.toString();

        }

        response.getOutputStream().print(result);

    }

}
