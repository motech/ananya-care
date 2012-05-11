package org.motechproject.care.utils;


import org.junit.Assert;

import java.util.ArrayList;

public class TestCaseThreadRunner {

    private ArrayList<TestCaseThread> instances = new ArrayList<TestCaseThread>();

    protected void runTest(Object obj) {
        TestCaseThread testCaseThread = new TestCaseThread(obj);
        testCaseThread.setDaemon(true);
        instances.add(testCaseThread);
        testCaseThread.start();
    }


    protected void verify() {
        while(true) {
            boolean anyThreadAlive = false;
            for(TestCaseThread testCaseThread : instances) {
                if(testCaseThread.isAlive()) {
                    anyThreadAlive = true;
                    break;
                }
            }
            if(!anyThreadAlive) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch(InterruptedException ex) {
                //Do nothing
            }
        }


        int failedTests = 0;
        int totalTests = 0;

        for(TestCaseThread testCaseThread : instances) {
            for(TestResult testResult: testCaseThread.getTestResults()) {
                totalTests++;
                testResult.print();
                if(testResult.hasError()) {
                    failedTests++;
                }
            }
        }

        Assert.assertTrue(failedTests + " out of " + totalTests + " failed.", failedTests == 0);
        System.out.println(totalTests + " out of " + totalTests + " passed.");
    }
}
