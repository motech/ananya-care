package org.motechproject.care.tools;

import org.joda.time.LocalDate;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.events.constants.EventDataKeys;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.quartz.*;
import org.quartz.impl.calendar.BaseCalendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Component
public class QuartzWrapper {

    private Scheduler scheduler;
    private ScheduleTrackingService trackingService;


    @Autowired
    public QuartzWrapper(SchedulerFactoryBean schedulerFactoryBean,ScheduleTrackingService trackingService) {
        this.scheduler = schedulerFactoryBean.getScheduler();
        this.trackingService = trackingService;

    }

    public HashMap<String, String> checkQuartzQueueForAlertsForThisSchedule(String externalId, String scheduleName) throws SchedulerException, IOException {
        HashMap<String, String> alertDetails = null;
        for (String triggerName : scheduler.getTriggerNames("default")) {
            Trigger trigger = scheduler.getTrigger(triggerName, "default");
            JobDetail detail = scheduler.getJobDetail(trigger.getJobName(), "default");

            JobDataMap dataMap = detail.getJobDataMap();
            if (scheduleName.equals(dataMap.get(EventDataKeys.SCHEDULE_NAME)) && externalId.equals(dataMap.get(EventDataKeys.EXTERNAL_ID))) {
                EnrollmentRecord enrollment = trackingService.getEnrollment(externalId, scheduleName);
                if (enrollment != null) {
                    alertDetails = getAlertTimes(trigger, detail, new LocalDate(enrollment.getReferenceDateTime()));
                }
            }
        }

        return alertDetails;

    }

    private HashMap<String, String> getAlertTimes(Trigger trigger, JobDetail detail, LocalDate startDate) {
        LocalDate endDate = startDate.plusYears(2);
        List times = TriggerUtils.computeFireTimesBetween(trigger, new BaseCalendar(), startDate.toDate(), endDate.toDate());

        MilestoneAlert milestoneAlert = (MilestoneAlert) detail.getJobDataMap().get(EventDataKeys.MILESTONE_NAME);
        String milestoneName = milestoneAlert.getMilestoneName();

        if (times != null && times.size() > 0) {
            HashMap<String, String> alertDetails = new HashMap<String, String>();
             alertDetails.put("milestone",milestoneName);
             alertDetails.put("time",times.get(0).toString());
            return alertDetails;
        }
        return null;
    }


}
