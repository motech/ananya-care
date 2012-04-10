package org.motechproject.care.schedule.service;

import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 * User: pchandra
 * Date: 4/5/12
 * Time: 10:56 AM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class CareScheduleTrackingService  {

    private ScheduleTrackingService trackingService;

    @Autowired
    public CareScheduleTrackingService(ScheduleTrackingService trackingService){
        this.trackingService = trackingService;
    }


    public void enroll(EnrollmentRequest enrollmentrequest) {
        trackingService.enroll(enrollmentrequest);
        EnrollmentRecord enrollment = trackingService.getEnrollment(enrollmentrequest.getExternalId(), enrollmentrequest.getScheduleName());


    }
}
