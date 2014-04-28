-- Change the target job class in the job_details as the path in 0.12.6 release is changed
UPDATE qrtz_job_details SET job_class_name='org.motechproject.scheduler.impl.MotechScheduledJob' WHERE sched_name='TestScheduler';
-- Change the job data(serialVersionUID for the records)
update qrtz_job_details set job_data = overlay(job_data::bytea placing '\024j:\021\020\275\245X' from position('\000mF\223\2424\222c' in job_data)) where sched_name='TestScheduler' and job_name like '%milestone.alert%' ;

