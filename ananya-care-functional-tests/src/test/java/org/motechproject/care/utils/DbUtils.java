package org.motechproject.care.utils;

import org.ektorp.BulkDeleteDocument;
import org.ektorp.CouchDbConnector;
import org.motechproject.care.domain.CareCaseTask;
import org.motechproject.care.domain.Child;
import org.motechproject.care.domain.Client;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllCareCaseTasks;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.commcarehq.domain.AlertDocCase;
import org.motechproject.commcarehq.repository.AllAlertDocCases;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DbUtils {

    @Autowired
    private AllChildren allChildren;

    @Autowired
    private AllMothers allMothers;

    @Autowired
    private AllAlertDocCases allAlertDocCases;

    @Autowired
    private AllCareCaseTasks allCareCaseTasks;

    @Qualifier("ananyaCareDbConnector")
    @Autowired
    private CouchDbConnector ananyaCareDbConnector;

    @Qualifier("ananyaCareDummyAppDbConnector")
    @Autowired
    private CouchDbConnector ananyaCareDummyAppDbConnector;

    @Autowired
    private ScheduleTrackingService trackingService;


    public Child getChildWithRetry(final String childCaseId) {
        RetryTask<Child> taskToFetchChild = new RetryTask<Child>() {
            @Override
            protected Child perform() {
                return allChildren.findByCaseId(childCaseId);
            }
        };

        return taskToFetchChild.execute(120, 1000);
    }

    public Mother getMotherWithRetry(final String motherCaseId) {
        RetryTask<Mother> taskToFetchMother = new RetryTask<Mother>() {
            @Override
            protected Mother perform() {
                return allMothers.findByCaseId(motherCaseId);
            }
        };

        return taskToFetchMother.execute(120, 1000);
    }

    public AlertDocCase getAlertDocCaseWithRetry(final String clientCaseId, final String taskName) {
        RetryTask<AlertDocCase> taskToFetchAlertDocCase = new RetryTask<AlertDocCase>() {
            @Override
            protected AlertDocCase perform() {
                List<AlertDocCase> alertDocCases = allAlertDocCases.findAllByClientCaseId(clientCaseId);
                for(AlertDocCase alertDocCase : alertDocCases) {
                    if(alertDocCase.getXmlDocument().contains("<task_id>"+taskName+"</task_id>")) {
                        return alertDocCase;
                    }
                }
                return null;
            }
        };
        return taskToFetchAlertDocCase.execute(300, 1000);
    }

    public AlertDocCase getAlertDocCaseWithRetry(final String caseId, final boolean closeCase) {
        RetryTask<AlertDocCase> taskToFetchAlertDocCase = new RetryTask<AlertDocCase>() {
            @Override
            protected AlertDocCase perform() {
                List<AlertDocCase> alertDocCases = allAlertDocCases.findAllByCaseId(caseId);
                for(AlertDocCase alertDocCase : alertDocCases) {
                    if(!closeCase && !alertDocCase.getXmlDocument().contains("<close")) {
                        return alertDocCase;
                    }
                    if(closeCase && alertDocCase.getXmlDocument().contains("<close")) {
                        return alertDocCase;
                    }
                }
                return null;
            }
        };
        return taskToFetchAlertDocCase.execute(300, 1000);
    }

    public EnrollmentRecord getEnrollment(String externalId, String scheduleName) {
        return trackingService.getEnrollment(externalId, scheduleName);
    }

    public CareCaseTask getCareCaseTask(String clientCaseId, String scheduleName) {
        return allCareCaseTasks.findByClientCaseIdAndMilestoneName(clientCaseId, scheduleName);
    }

    public void deleteClients(List<Client> toDelete) {
        ananyaCareDbConnector.executeBulk(toDelteDocumentList(toDelete));
    }

    public void deleteAlertDocCases(List<AlertDocCase> toDelete) {
        ananyaCareDummyAppDbConnector.executeBulk(toDelteDocumentList(toDelete));
    }

    private <T> List<BulkDeleteDocument> toDelteDocumentList(List<T> toDelete) {
        List<BulkDeleteDocument> delteDocumentsList = new ArrayList<BulkDeleteDocument>();
        for(T item: toDelete) {
            delteDocumentsList.add(BulkDeleteDocument.of(item));
        }
        return delteDocumentsList;
    }

    public void unenroll(List<Pair> toUnenroll) {
        for(Pair p: toUnenroll){
            String externalId = p.getFirst().toString();
            String scheduleName = p.getSecond().toString();
            ArrayList<String> scheduleNames = new ArrayList<String>();
            scheduleNames.add(scheduleName);
            trackingService.unenroll(externalId, scheduleNames);
        }
    }
}
