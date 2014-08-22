package org.motechproject.care.migration;


import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.motechproject.care.repository.AllCareCaseTasks;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.schedule.service.ScheduleService;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.service.impl.EnrollmentAlertService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/migrate")

public class DuplicateVaccinationAlertMigrationController {
    private AllCareCaseTasks allCareCaseTasks;
    private AllMothers allMothers;
    private AllChildren allChildren;
    private ScheduleService scheduleService;
    private EnrollmentAlertService enrollmentAlertService;
    private AllEnrollments allEnrollments;
    Logger logger = Logger.getLogger(DuplicateVaccinationAlertMigrationController.class);
    @Autowired
    private ForceCloseVaccinations forceCloseVaccinations;
    @Autowired
    private MotechSchedulerService motechSchedulerService;
    @Autowired
    private OpenTasksForClosedCases openTasksForClosedCases;
    
    @Autowired
    public DuplicateVaccinationAlertMigrationController(AllCareCaseTasks allCareCaseTasks, ScheduleService scheduleService, AllMothers allMothers, AllChildren allChildren, EnrollmentAlertService enrollmentAlertService, AllEnrollments allEnrollments) {
        this.allCareCaseTasks = allCareCaseTasks;
        this.scheduleService = scheduleService;
        this.allMothers = allMothers;
        this.allChildren = allChildren;
        this.enrollmentAlertService = enrollmentAlertService;
        this.allEnrollments = allEnrollments;
    }
    
    @RequestMapping(value = "/deleteDuplicateVaccinations/{fileName}", method = RequestMethod.GET)
    public void deleteDuplicateVaccinationAlerts(@PathVariable String fileName) {
        DuplicateVaccinationAlertMigration duplicateVaccinationAlertMigration = new DuplicateVaccinationAlertMigration(allCareCaseTasks, scheduleService, allMothers, allChildren, enrollmentAlertService, allEnrollments);
        logger.info("Starting to load Case Ids from CSV...");
        duplicateVaccinationAlertMigration.loadCaseIdsFromCSVAndDeleteDuplicateTasks(fileName);
    }
    
    @RequestMapping(value="/forceCloseCase/{fileName}" , method = RequestMethod.GET)
    public void forceCloseCaseTask(@PathVariable String fileName){
    	forceCloseVaccinations.forceCloseCases(fileName);
    }
    

    @RequestMapping(value="/oldJobs" , method = RequestMethod.GET)
    public void migrateOldJobs(HttpServletRequest request, HttpServletResponse response){
    	MigrateOldJobs migrateOldJobs = new MigrateOldJobs(motechSchedulerService);
    	try {
			migrateOldJobs.runMigration();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
    }
    
    @RequestMapping(value="/openTasks" , method = RequestMethod.GET)
    public void printCaseIds() throws IOException{
    	openTasksForClosedCases.writeTaskIdsToFile();
    }
    
    
}
