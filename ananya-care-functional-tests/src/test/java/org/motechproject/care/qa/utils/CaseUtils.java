package org.motechproject.care.qa.utils;

import org.motechproject.care.domain.Child;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.commcarehq.domain.AlertDocCase;
import org.motechproject.commcarehq.repository.AllAlertDocCases;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CaseUtils {

    @Autowired
    private AllChildren allChildren;

    @Autowired
    private AllMothers allMothers;

    @Autowired
    private AllAlertDocCases allAlertDocCases;


    public Child getChildFromDb(final String childCaseId) {
        RetryTask<Child> taskToFetchChild = new RetryTask<Child>() {
            @Override
            protected Child perform() {
                return allChildren.findByCaseId(childCaseId);
            }
        };

        return taskToFetchChild.execute(120, 1000);
    }

    public Mother getMotherFromDb(final String motherCaseId) {
        RetryTask<Mother> taskToFetchMother = new RetryTask<Mother>() {
            @Override
            protected Mother perform() {
                return allMothers.findByCaseId(motherCaseId);
            }
        };

        return taskToFetchMother.execute(120, 1000);
    }

    public AlertDocCase getAlertDocFromDb(final String clientCaseId, final String taskName) {
        RetryTask<AlertDocCase> taskToFetchAlertDocCase = new RetryTask<AlertDocCase>() {
            @Override
            protected AlertDocCase perform() {
                List<AlertDocCase> alertDocCases = allAlertDocCases.findAllByCaseId(clientCaseId);
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

    public HashMap<String, String> createAPregnantMotherCaseInCommCare() throws IOException {
        final String motherCaseId = UUID.randomUUID().toString();
        String motherInstanceId = UUID.randomUUID().toString();
        String motherName = "mother_test_gen" + Math.random();

        HashMap<String, String> motherAttributes = new HashMap<String, String>();
        motherAttributes.put("caseId", motherCaseId);
        motherAttributes.put("instanceId", motherInstanceId);
        motherAttributes.put("name", motherName);

        HttpUtils.postXmlWithAttributes(motherAttributes, "/pregnantmother_new.st");
        return motherAttributes;
    }


}
