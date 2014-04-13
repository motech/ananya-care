package org.motechproject.care.migration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

public class MigrateOldJobs {
	
	private final static Logger logger = Logger.getLogger(MigrateOldJobs.class);
	
	private MotechSchedulerService motechSchedulerService;
	
	private static final String JOB_GROUP_NAME = "default";
	
	private Scheduler scheduler;

	private static final int HOUR_INTERVAL = 1;                
	
	private static int intervalBetweenJobs = 3 ;
	
	public MigrateOldJobs(MotechSchedulerService motechSchedulerService){
		this.motechSchedulerService = motechSchedulerService;
		StdSchedulerFactory schedulerFactoryBean;
		try {
			schedulerFactoryBean = new StdSchedulerFactory("quartzForMigration.properties");
			scheduler = schedulerFactoryBean.getScheduler();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	public List<TriggerKey> getOldJobs() throws SchedulerException{
		return new ArrayList<TriggerKey>(scheduler.getTriggerKeys(GroupMatcher.triggerGroupContains(JOB_GROUP_NAME)));
	}
	
	public void setSchedulertoStandBy() throws SchedulerException{
		scheduler.standby();
	}
	
	public void rescheduleJobs(Trigger trigger, JobDetail jobDetail){
        MotechEvent motechEvent = getEventDataFromJob(jobDetail);
        Date startTime = getStartDateForTrigger(trigger);
        RunOnceSchedulableJob runOnceSchedulableJob = new RunOnceSchedulableJob(motechEvent,startTime);
        motechSchedulerService.safeScheduleRunOnceJob(runOnceSchedulableJob);
    }
	
	public MotechEvent getEventDataFromJob(JobDetail jobDetail){
		JobDataMap jobDataMap = jobDetail.getJobDataMap();
		String jobId = getJobIdFromKey(jobDetail.getKey());
        String eventType = jobDataMap.getString(MotechEvent.EVENT_TYPE_KEY_NAME);
        Map<String, Object> params = jobDataMap.getWrappedMap();
        params.remove(MotechEvent.EVENT_TYPE_KEY_NAME);
        params.put("JobID", jobId);
        return new MotechEvent(eventType, params);
	}
	
	public Date getStartDateForTrigger(Trigger trigger){
		Date startDate = trigger.getStartTime();
		if(startDate.before(new Date())){
			DateTime startTime = new DateTime().plusHours(HOUR_INTERVAL);
			startDate = startTime.plusSeconds(intervalBetweenJobs++).toDate();  //  Interval between two jobs is one second for misfired jobs
		}
		return startDate;
	}
	
	// JobKey Name will be in format : jobType-jobId
	public String getJobIdFromKey(JobKey jobKey){
		if(jobKey.getName().split("-").length > 1)
			return  jobKey.getName().split("-")[1];
		return null;
	}
	
	public JobDetail getJobDetail(Trigger trigger) throws SchedulerException{
		 return scheduler.getJobDetail(trigger.getJobKey());
	}
	
	public void runMigration() throws SchedulerException{
		logger.info("Starting Scheduler");
		setSchedulertoStandBy();							                 //   To stop the misfire of the old jobs.
		logger.info("Scheduler Standby");
		List<TriggerKey> triggers = getOldJobs();
		Trigger trigger = null ;
		for(TriggerKey triggerKey : triggers)
		{
			logger.info("trigger for Trigger Key :" + triggerKey);
			trigger = scheduler.getTrigger(triggerKey);
			rescheduleJobs(trigger, getJobDetail(trigger));
			scheduler.deleteJob(trigger.getJobKey());
		}
	}
}
