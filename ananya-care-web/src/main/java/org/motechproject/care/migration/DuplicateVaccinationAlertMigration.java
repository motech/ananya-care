package org.motechproject.care.migration;


import org.apache.log4j.Logger;
import org.motechproject.care.domain.Child;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllCareCaseTasks;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.schedule.service.ScheduleService;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.service.impl.EnrollmentAlertService;
import org.motechproject.util.StringUtil;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class DuplicateVaccinationAlertMigration {

    Logger logger = Logger.getLogger(DuplicateVaccinationAlertMigration.class);
    private AllCareCaseTasks allCareCaseTasks;
    private AllMothers allMothers;
    private AllChildren allChildren;
    private ScheduleService scheduleService;
    private EnrollmentAlertService enrollmentAlertService;
    private AllEnrollments allEnrollments;



    public DuplicateVaccinationAlertMigration(AllCareCaseTasks allCareCaseTasks, ScheduleService scheduleService, AllMothers allMothers, AllChildren allChildren, EnrollmentAlertService enrollmentAlertService, AllEnrollments allEnrollments) {
        this.allCareCaseTasks = allCareCaseTasks;
        this.scheduleService = scheduleService;
        this.allMothers = allMothers;
        this.allChildren = allChildren;
        this.enrollmentAlertService = enrollmentAlertService;
        this.allEnrollments = allEnrollments;
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationCareMigrationContext.xml");
//        DuplicateVaccinationAlertMigration duplicateVaccinationAlertMigration = (DuplicateVaccinationAlertMigration) context.getBean("duplicateVaccinationAlertMigration");
//        duplicateVaccinationAlertMigration.loadCaseIdsFromCSVAndDeleteDuplicateTasks();
    }

    /**
     * Client id is the CASE ID of either the MOTHER CASE OR THE CHILD CASE for whom the
     * vaccination alerts have been raised multiple times due to the platform issue.
     * The platform scheduled the Repeatable schedule job to repeat indefinitely.
     *
     * @param clientCaseID
     */
    public void deleteAndUnEnrollDuplicateVaccinationAlerts(String clientCaseID) {

        if (!StringUtil.isNullOrEmpty(clientCaseID)) {

            DuplicateVaccinationAlertService duplicateVaccinationAlertService = new DuplicateVaccinationAlertService(allMothers, allCareCaseTasks, allChildren, allEnrollments, enrollmentAlertService);
            Mother mother = allMothers.findByCaseId(clientCaseID);
            if (mother != null) {
                duplicateVaccinationAlertService.deleteCareTasksForGivenMotherCase(clientCaseID);
            } else {
                Child child = allChildren.findByCaseId(clientCaseID);
                if (child != null) {
                    duplicateVaccinationAlertService.deleteCareTasksForGivenChildCase(clientCaseID);
                }
            }
        }
    }

    public void loadCaseIdsFromCSVAndDeleteDuplicateTasks(String fileName) {

    	List<String> caseIds = MigrationUtil.readFile(fileName);
        for (String caseId : caseIds) {
            logger.info("deleting details for the case Id : " + caseId);
            deleteAndUnEnrollDuplicateVaccinationAlerts(caseId);

        }
    }
}
