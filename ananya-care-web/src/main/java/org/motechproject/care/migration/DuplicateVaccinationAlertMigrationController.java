package org.motechproject.care.migration;


import org.apache.log4j.Logger;
import org.motechproject.care.repository.AllCareCaseTasks;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.schedule.service.ScheduleService;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.service.impl.EnrollmentAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
    public DuplicateVaccinationAlertMigrationController(AllCareCaseTasks allCareCaseTasks, ScheduleService scheduleService, AllMothers allMothers, AllChildren allChildren, EnrollmentAlertService enrollmentAlertService, AllEnrollments allEnrollments) {
        this.allCareCaseTasks = allCareCaseTasks;
        this.scheduleService = scheduleService;
        this.allMothers = allMothers;
        this.allChildren = allChildren;
        this.enrollmentAlertService = enrollmentAlertService;
        this.allEnrollments = allEnrollments;
    }

    @RequestMapping(value = "/deleteDuplicateVaccinations", method = RequestMethod.GET)
    public void deleteDuplicateVaccinationAlerts() {
        DuplicateVaccinationAlertMigration duplicateVaccinationAlertMigration = new DuplicateVaccinationAlertMigration(allCareCaseTasks, scheduleService, allMothers, allChildren, enrollmentAlertService, allEnrollments);
        logger.info("Starting to load Case Ids from CSV...");
        duplicateVaccinationAlertMigration.loadCaseIdsFromCSVAndDeleteDuplicateTasks();
    }
}
