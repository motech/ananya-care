package org.motechproject.care.repository;


import org.junit.Assert;
import org.junit.Test;
import org.motechproject.care.domain.CareCaseTask;
import org.motechproject.care.utils.CaseUtils;
import org.motechproject.care.utils.SpringIntegrationTest;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class AllCareCaseTasksTest extends SpringIntegrationTest {
    @Autowired
    private AllCareCaseTasks allCareCaseTasks;

    @Test
    public void shouldSaveTheTask(){
        String caseID = CaseUtils.getUniqueCaseId();
;
        CareCaseTask careCaseTask = new CareCaseTask( "TT 1", "ownerID", caseID,"motechUserID", DateUtil.now().toString(),"taskID", DateUtil.today().toString(), DateUtil.today().plusDays(4).toString(), "mother_case_type", "id", "mother_id");
        allCareCaseTasks.add(careCaseTask);
        Assert.assertNotNull(careCaseTask.getId());
    }
}
