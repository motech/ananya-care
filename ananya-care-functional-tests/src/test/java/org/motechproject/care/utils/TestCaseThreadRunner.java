package org.motechproject.care.utils;


import org.junit.Assert;

import java.util.ArrayList;

public class TestCaseThreadRunner {

    private ArrayList<TestCaseThread> testInstances = new ArrayList<TestCaseThread>();

    protected void addTest(Object obj) {
        TestCaseThread testCaseThread = new TestCaseThread(obj);
        testCaseThread.setDaemon(true);
        testInstances.add(testCaseThread);
    }

    protected void run() {
        startTests();
        verify();
    }

    private void startTests() {
        for(Thread thread: testInstances) {
            thread.start();
        }
    }

    private void verify() {
        while(true) {
            boolean anyThreadAlive = false;
            for(TestCaseThread testCaseThread : testInstances) {
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
        printResult();
    }

    private void printResult() {
        int failedTests = 0;
        int totalTests = 0;

        for(TestCaseThread testCaseThread : testInstances) {
            for(TestResult testResult: testCaseThread.getTestResults()) {
                totalTests++;
                testResult.printResult();
                if(testResult.hasError()) {
                    failedTests++;
                }
            }
        }

        Assert.assertTrue(failedTests + " out of " + totalTests + " failed.", failedTests == 0);
        System.out.println(totalTests + " out of " + totalTests + " passed.");
    }
}
