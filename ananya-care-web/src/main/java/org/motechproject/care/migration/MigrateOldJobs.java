package org.motechproject.care.migration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.motechproject.care.schedule.vaccinations.ExpirySchedule;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
/**
 * This is a part of fix to migrate the  quartz jobs created using the older version(1.8)
 * to new jobs using the updated version(2+).
 * We will declare a scheduler instance by name TestScheduler to read the left over jobs 
 * as the default scheduler name is set to the TestScheduler when we run the quartz migation 
 * script for the leftover jobs. Job class MotechScheduledJob path has been changed in the 0.12.6 
 * and thus the database job details needs to be changed.
 * 
 * This migration is triggered by the GET request to the Controller(MigrationController) used to trigger various fix 
 * on the production.
 * 
 * */
public class MigrateOldJobs {
	
    private final static Logger logger = Logger.getLogger(MigrateOldJobs.class);

    private MotechSchedulerService motechSchedulerService;

    private static final String JOB_GROUP_NAME = "default";

    private Scheduler scheduler;
    /* Time interval after which the old Misfire jobs start executing */
    private static final int HOUR_INTERVAL = 1;                
    /* Time interval Between each trigger is updated for each trigger */
    private static int intervalBetweenJobs = 0 ;
    
    private List<String> expiryCases = new ArrayList();
    
    private List<String> expiryMilestones = new ArrayList();
    
    public void populateExpiryCases(){
    	expiryCases.add(ExpirySchedule.ChildCare.getName());
    	expiryCases.add(ExpirySchedule.MotherCare.getName());
    }
    
    public void populateExpiryMilestones(){
    	expiryMilestones.add("OPV 0");
    	expiryMilestones.add("Bcg");
    	expiryMilestones.add("Hep 0");
    }
    

    public MigrateOldJobs(MotechSchedulerService motechSchedulerService ){
        this.motechSchedulerService = motechSchedulerService;
        populateExpiryCases();
        populateExpiryMilestones();
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

    public boolean rescheduleJobs(Trigger trigger, JobDetail jobDetail){
        MotechEvent motechEvent = getEventDataFromJob(jobDetail);
        boolean rescheduleJob = false;
        MilestoneEvent event = new MilestoneEvent(motechEvent);
        if(motechEvent.getSubject().equals(EventSubjects.MILESTONE_ALERT) ) {
        	String currentMilestone = event.getMilestoneAlert().getMilestoneName() ;
        	if(expiryCases.contains(currentMilestone) || (expiryMilestones.contains(currentMilestone) && event.getWindowName().equals(WindowName.late.name()) )){
        	rescheduleJob = true;
        	}// Reschedule only for child care and Mother Care as they send the close cases
    	}
        if(rescheduleJob){
        Date startTime = getStartDateForTrigger(trigger);
        if(startTime == null){
            startTime = trigger.getStartTime();
        }
        RunOnceSchedulableJob runOnceSchedulableJob = new RunOnceSchedulableJob(motechEvent,startTime);
        motechSchedulerService.safeScheduleRunOnceJob(runOnceSchedulableJob);
        }
        return rescheduleJob;
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


    /* Start time of the job remains same as the old jobs if the job is scheduled in sometime future.
    If the job is supposed to run in past, Reschedule the job to run after particular time(@HOUR_INTERVAL)*/

    public Date getStartDateForTrigger(Trigger trigger){
        Date startDate = trigger.getStartTime();
        if(startDate.before(new Date())){
	    DateTime startTime = new DateTime().plusHours(HOUR_INTERVAL);
	    startDate = startTime.plusSeconds(intervalBetweenJobs++*3).toDate();  //  Interval between two jobs is one second for misfired jobs
        return startDate;
        }
        return null;
    }

    /*JobKey Name will be in format : jobType-jobId */
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
	    if(rescheduleJobs(trigger, getJobDetail(trigger)))
	    	scheduler.deleteJob(trigger.getJobKey());
    	}
    }

    

}
