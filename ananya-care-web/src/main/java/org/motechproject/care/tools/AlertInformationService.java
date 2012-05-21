package org.motechproject.care.tools;


import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDate;
import org.motechproject.delivery.schedule.util.Pair;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.events.constants.EventDataKeys;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.scheduletracking.api.service.impl.EnrollmentAlertService;
import org.quartz.*;
import org.quartz.impl.calendar.BaseCalendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/diagnostics/**")
public class AlertInformationService {

    private EnrollmentAlertService enrollmentAlertService;

    private AllEnrollments allEnrollments;

    private StringTemplate stringTemplate;

    private final Scheduler scheduler;
    private ScheduleTrackingService trackingService;
    private Map<Pair, List<Date>> alertTimes;




    @Autowired
    public AlertInformationService(EnrollmentAlertService enrollmentAlertService, AllEnrollments allEnrollments,SchedulerFactoryBean schedulerFactoryBean,ScheduleTrackingService trackingService) throws IOException {
        this.enrollmentAlertService = enrollmentAlertService;
        this.allEnrollments = allEnrollments;
        this.trackingService = trackingService;
        this.scheduler = schedulerFactoryBean.getScheduler();

        InputStream resourceAsStream = getClass().getResourceAsStream("/alertResponse.st");
        stringTemplate = new StringTemplate(IOUtils.toString(resourceAsStream));
    }

    @RequestMapping(value="/alerts",method = RequestMethod.GET)
    public void captureAlertsFor(@RequestParam("externalId") String externalId, HttpServletResponse response) throws IOException, SchedulerException {

        stringTemplate.reset();
        List<Enrollment> enrollments = allEnrollments.findByExternalId(externalId);
        List<EnrollmentAlert> enrollmentAlerts = new ArrayList<EnrollmentAlert>();
        for (Enrollment enrollment : enrollments) {
            String scheduleAlertTimings = checkQuartzQueueForAlertsForThisSchedule(externalId, enrollment.getScheduleName());
            if(scheduleAlertTimings!=null)
                enrollmentAlerts.add(new EnrollmentAlert(enrollment, scheduleAlertTimings));
            else
                enrollmentAlerts.add(new EnrollmentAlert(enrollment, "No alerts are scheduled in the quartz queue for this milestone. Your alert might have already been raised. Please check the database."));
        }

        String result = "No Match";
        if (enrollmentAlerts.size() > 0) {
            stringTemplate.setAttribute("enrollmentAlerts", enrollmentAlerts);
            result = stringTemplate.toString();

        }

        response.getOutputStream().print(result);

    }


    private String checkQuartzQueueForAlertsForThisSchedule(String externalId, String scheduleName) throws SchedulerException, IOException {
        String alertDateTime = null;
        for (String triggerName : scheduler.getTriggerNames("default")) {
            Trigger trigger = scheduler.getTrigger(triggerName, "default");
            JobDetail detail = scheduler.getJobDetail(trigger.getJobName(), "default");

            JobDataMap dataMap = detail.getJobDataMap();
            if (scheduleName.equals(dataMap.get(EventDataKeys.SCHEDULE_NAME)) && externalId.equals(dataMap.get(EventDataKeys.EXTERNAL_ID))) {
                EnrollmentRecord enrollment = trackingService.getEnrollment(externalId, scheduleName);
                if (enrollment != null) {
                    alertDateTime = getAlertTimes(trigger, detail, new LocalDate(enrollment.getReferenceDateTime()));
                }
            }
        }

        return alertDateTime;

    }

    private String getAlertTimes(Trigger trigger, JobDetail detail, LocalDate startDate) {
        LocalDate endDate = startDate.plusYears(2);
        List times = TriggerUtils.computeFireTimesBetween(trigger, new BaseCalendar(), startDate.toDate(), endDate.toDate());

        MilestoneAlert milestoneAlert = (MilestoneAlert) detail.getJobDataMap().get(EventDataKeys.MILESTONE_NAME);
        String milestoneName = milestoneAlert.getMilestoneName();

        if(times!=null && times.size()>0)
            return "Milestone Name"+milestoneName + ":" + times.get(0).toString();
        return null;
    }


}
