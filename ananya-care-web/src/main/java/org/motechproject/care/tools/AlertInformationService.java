package org.motechproject.care.tools;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.io.IOUtils;
import org.motechproject.care.domain.CareCaseTask;
import org.motechproject.care.repository.AllCareCaseTasks;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.service.impl.EnrollmentAlertService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/diagnostics/**")
public class AlertInformationService {

    private EnrollmentAlertService enrollmentAlertService;
    private AllEnrollments allEnrollments;
    private StringTemplate stringTemplate;
    private AllCareCaseTasks allCareCaseTasks;
    private QuartzWrapper quartzWrapper;

    @Autowired
    public AlertInformationService(EnrollmentAlertService enrollmentAlertService, AllEnrollments allEnrollments, AllCareCaseTasks allCareCaseTasks, QuartzWrapper quartzWrapper) throws IOException {
        this.enrollmentAlertService = enrollmentAlertService;
        this.allEnrollments = allEnrollments;
        this.allCareCaseTasks = allCareCaseTasks;
        this.quartzWrapper = quartzWrapper;

        InputStream resourceAsStream = getClass().getResourceAsStream("/alertResponse.st");
        stringTemplate = new StringTemplate(IOUtils.toString(resourceAsStream));
    }

    @RequestMapping(value = "/alerts", method = RequestMethod.GET)
    public void captureAlertsFor(@RequestParam("externalId") String externalId, HttpServletResponse response) throws IOException, SchedulerException {

        stringTemplate.reset();
        List<Enrollment> enrollments = allEnrollments.findByExternalId(externalId);
        List<EnrollmentAlert> enrollmentAlerts = new ArrayList<EnrollmentAlert>();
        for (Enrollment enrollment : enrollments)
            enrollmentAlerts.add(getEnrollmentAlert(externalId, enrollment));

        String result = "No Match";
        if (enrollmentAlerts.size() > 0) {
            stringTemplate.setAttribute("enrollmentAlerts", enrollmentAlerts);
            result = stringTemplate.toString();

        }
        response.getOutputStream().print(result);
    }

    private EnrollmentAlert getEnrollmentAlert(String externalId, Enrollment enrollment) throws SchedulerException, IOException {
        String alertTimingsText;
        CareCaseTask careCaseTask = allCareCaseTasks.findByClientCaseIdAndMilestoneName(externalId, enrollment.getCurrentMilestoneName());
        if (careCaseTask != null)
            alertTimingsText = "An alert for this milestone has already been raised.";

        else
            alertTimingsText = getAlertDetails(externalId, enrollment);

        return new EnrollmentAlert(enrollment, alertTimingsText);
    }

    private String getAlertDetails(String externalId, Enrollment enrollment) throws SchedulerException, IOException {
        HashMap<String, String> alertDetails = quartzWrapper.checkQuartzQueueForAlertsForThisSchedule(externalId, enrollment.getScheduleName());
        if (alertDetails == null || alertDetails.isEmpty())
            return "No alert are scheduled in the quartz queue for this milestone.";
        return "Milestone Name - " + alertDetails.get("milestone") + " : " + alertDetails.get("time");
    }
}
