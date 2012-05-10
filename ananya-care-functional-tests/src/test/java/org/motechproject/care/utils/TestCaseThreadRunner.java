package org.motechproject.care.utils;


import org.junit.Assert;

import java.util.ArrayList;

public class TestCaseThreadRunner {

    private ArrayList<TestCaseThread> instances = new ArrayList<TestCaseThread>();

    protected void runTest(TestCaseThread testCaseThread) {
        instances.add(testCaseThread);
        testCaseThread.setDaemon(true);
        testCaseThread.start();

    }

    protected void verify() {
        for(TestCaseThread testCaseThread : instances) {
            if(testCaseThread.isAlive()) {
                try {
                    Thread.sleep(1000);
                } catch(InterruptedException ex) {
                    //Do nothing
                }
                this.verify();
                return;
            }
        }

        int failedTestsCounter = 0;
        for(TestCaseThread testCaseThread : instances) {
            if(testCaseThread.hasError()) {
                failedTestsCounter++;
                Throwable ex = testCaseThread.getError();
                System.err.println("Test failed. Test Class: " + testCaseThread.getClass().getCanonicalName());
                ex.printStackTrace();
            }
        }


        Assert.assertTrue(failedTestsCounter + " test(s) failed out of " + instances.size(), failedTestsCounter == 0);
    }
}
