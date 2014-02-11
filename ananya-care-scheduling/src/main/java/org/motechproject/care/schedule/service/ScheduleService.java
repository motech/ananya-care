package org.motechproject.care.schedule.service;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.commons.date.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.commons.date.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class ScheduleService {
    public static final String DEFINITIONS_DIRECTORY_NAME = "/schedules";
    public static final String JSON_SUFFIX = ".json";
    protected ScheduleTrackingService trackingService;
    Logger logger = Logger.getLogger(ScheduleService.class);

    @Autowired
    public ScheduleService(ScheduleTrackingService trackingService) {
        this.trackingService = trackingService;
        try {
            registerAllSchedulesJsons();
        } catch (IOException e) {
            logger.error("Error occurred while parsing schedule jsons", e);
            throw new RuntimeException(e);
        }
    }

    private void registerAllSchedulesJsons() throws IOException {
        for (File file : getAllJsonFiles(DEFINITIONS_DIRECTORY_NAME)) {
            trackingService.add(IOUtils.toString(new FileReader(file)));
        }
    }

    private File[] getAllJsonFiles(String definitionsDirectoryName) {
        String schedulesDirectoryPath = getClass().getResource(definitionsDirectoryName).getPath();
        File[] files = new File(schedulesDirectoryPath).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String filename) {
                return filename.endsWith(JSON_SUFFIX);
            }
        });

        if(files == null) {
            return new File[0];
        }

        return files;
    }

    public void enroll(String caseId, DateTime referenceDate, String scheduleName) {
        if (isNotEnrolled(caseId, scheduleName)) {
            logger.info(String.format("Enrolling client for external id : %s , schedule : %s", caseId, scheduleName));
            trackingService.enroll(enrollmentRequestFor(caseId, referenceDate.toLocalDate(), scheduleName));
        }
    }

    public EnrollmentRecord unenroll(String caseId, String scheduleName) {
        EnrollmentRecord activeEnrollment = trackingService.getEnrollment(caseId, scheduleName);
        trackingService.unenroll(caseId, Arrays.asList(scheduleName));
        logger.info(String.format("Un-enrolled client for external id : %s , schedule : %s", caseId, scheduleName));

        if (activeEnrollment != null)
            return activeEnrollment;
        return getAnEnrollmentFor(caseId, scheduleName);

    }

    public void fulfillMilestone(String caseId, String milestoneName, DateTime vaccinationTakenDate, String scheduleName) {
        if (isCurrentMilestone(caseId, milestoneName, scheduleName))
            fulfillCurrentMilestone(caseId, vaccinationTakenDate, scheduleName);
    }

    private EnrollmentRecord getAnEnrollmentFor(String externalId, String scheduleName) {
        EnrollmentsQuery enrollmentsQuery = new EnrollmentsQuery()
                .havingExternalId(externalId)
                .havingSchedule(scheduleName);

        List<EnrollmentRecord> enrollmentRecords = trackingService.search(enrollmentsQuery);
        if (enrollmentRecords.size() >= 1)
            return enrollmentRecords.get(0);
        return null;
    }

    private boolean isNotEnrolled(String externalId, String scheduleName) {
        EnrollmentsQuery query = new EnrollmentsQuery()
                .havingExternalId(externalId)
                .havingSchedule(scheduleName);

        if (trackingService.search(query).size() >= 1)
            return false;
        return true;
    }

    private boolean isCurrentMilestone(String caseId, String milestoneName, String scheduleName) {
        EnrollmentRecord enrollment = trackingService.getEnrollment(caseId, scheduleName);
        if (enrollment == null) return false;
        return enrollment.getCurrentMilestoneName().equals(milestoneName);
    }

    private void fulfillCurrentMilestone(String caseId, DateTime fulfillmentDate, String scheduleName) {
        DateTime nowPlus2Minutes = DateTime.now().plusMinutes(2);
        DateTime fulfillmentDateTime = nowPlus2Minutes.withDate(fulfillmentDate.getYear(), fulfillmentDate.getMonthOfYear(), fulfillmentDate.getDayOfMonth());

        logger.info(String.format("Fulfilling current milestone for external id : %s , schedule : %s", caseId, scheduleName));
        trackingService.fulfillCurrentMilestone(caseId, scheduleName, fulfillmentDateTime.toLocalDate(), DateUtil.time(fulfillmentDateTime));
    }

    private EnrollmentRequest enrollmentRequestFor(String caseId, LocalDate referenceDate, String scheduleName) {
        DateTime now = DateTime.now();
        Time referenceTime = DateUtil.time(now.plusMinutes(2));
        LocalDate enrollmentDate = DateUtil.today();
        Time enrollmentTime = referenceTime;
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest();
        enrollmentRequest.setExternalId(caseId);
        enrollmentRequest.setScheduleName(scheduleName);
        enrollmentRequest.setReferenceDate(referenceDate);
        enrollmentRequest.setReferenceTime(referenceTime);
        enrollmentRequest.setEnrollmentDate(enrollmentDate);
        enrollmentRequest.setEnrollmentTime(enrollmentTime);

        return enrollmentRequest;
    }
}
