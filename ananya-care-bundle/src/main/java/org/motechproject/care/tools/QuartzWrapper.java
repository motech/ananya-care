package org.motechproject.care.tools;

import org.joda.time.LocalDate;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.events.constants.EventDataKeys;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.TriggerUtils;
import org.quartz.impl.calendar.BaseCalendar;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.spi.OperableTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class QuartzWrapper {

    private Scheduler scheduler;
    private ScheduleTrackingService trackingService;

    @Autowired
    public QuartzWrapper(SchedulerFactoryBean schedulerFactoryBean, ScheduleTrackingService trackingService) {
        this.scheduler = schedulerFactoryBean.getScheduler();
        this.trackingService = trackingService;
    }

    public AlertDetails checkQuartzQueueForNextAlertsForThisSchedule(String externalId, String scheduleName) throws SchedulerException, IOException {
        AlertDetails nextAlertDetails = new NullAlertDetails();

        GroupMatcher<TriggerKey> groupMatcher = GroupMatcher.groupEquals("default");
        Set<TriggerKey> triggersInGroup = scheduler.getTriggerKeys(groupMatcher);

        for(TriggerKey triggerKey : triggersInGroup) {
            Trigger trigger = scheduler.getTrigger(triggerKey);
            JobDetail detail = scheduler.getJobDetail(JobKey.jobKey(triggerKey.getName(), triggerKey.getGroup()));

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
        if(!(trigger instanceof OperableTrigger)) {
            return new NullAlertDetails();
        }

        OperableTrigger operableTrigger = (OperableTrigger) trigger;
        LocalDate endDate = startDate.plusYears(2).plusMonths(1);

        List times = TriggerUtils.computeFireTimesBetween(operableTrigger, new BaseCalendar(), startDate.toDate(), endDate.toDate());
        if (times == null || times.size() == 0) {
            return new NullAlertDetails();
        }

        MilestoneAlert milestoneAlert = (MilestoneAlert) dataMap.get(EventDataKeys.MILESTONE_NAME);
        String milestoneName = milestoneAlert.getMilestoneName();
        return new AlertDetails(milestoneName, (String) dataMap.get(EventDataKeys.WINDOW_NAME), (Date) times.get(0));
    }
}
