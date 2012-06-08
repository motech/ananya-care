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
import java.util.Date;
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

    public AlertDetails checkQuartzQueueForNextAlertsForThisSchedule(String externalId, String scheduleName) throws SchedulerException, IOException {
        AlertDetails nextAlertDetails = new NullAlertDetails();
        for (String triggerName : scheduler.getTriggerNames("default")) {
            Trigger trigger = scheduler.getTrigger(triggerName, "default");
            JobDetail detail = scheduler.getJobDetail(trigger.getJobName(), "default");

            JobDataMap dataMap = detail.getJobDataMap();
            if (scheduleName.equals(dataMap.get(EventDataKeys.SCHEDULE_NAME)) && externalId.equals(dataMap.get(EventDataKeys.EXTERNAL_ID))) {
                EnrollmentRecord enrollment = trackingService.getEnrollment(externalId, scheduleName);
                if(enrollment == null) {
                    continue;
                }
                AlertDetails alertDetails = getAlertDetail(trigger, dataMap, new LocalDate(enrollment.getReferenceDateTime()));
                if(alertDetails.isBefore(nextAlertDetails)) {
                    nextAlertDetails = alertDetails;
                }
            }
        }
        return nextAlertDetails;
    }

    private AlertDetails getAlertDetail(Trigger trigger, JobDataMap dataMap, LocalDate startDate) {
        LocalDate endDate = startDate.plusYears(2).plusMonths(1);
        List times = TriggerUtils.computeFireTimesBetween(trigger, new BaseCalendar(), startDate.toDate(), endDate.toDate());
        if (times == null || times.size() == 0) {
            return new NullAlertDetails();
        }

        MilestoneAlert milestoneAlert = (MilestoneAlert) dataMap.get(EventDataKeys.MILESTONE_NAME);
        String milestoneName = milestoneAlert.getMilestoneName();
        return new AlertDetails(milestoneName, (String) dataMap.get(EventDataKeys.WINDOW_NAME), (Date) times.get(0));
    }
}
