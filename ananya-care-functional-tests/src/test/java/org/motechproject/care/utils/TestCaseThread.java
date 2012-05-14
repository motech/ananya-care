package org.motechproject.care.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestCaseThread extends Thread {

    private Object test;

    private List<Method> testMethods = new ArrayList<Method>();
    private List<Method> beforeMethods = new ArrayList<Method>();
    private List<Method> afterMethods = new ArrayList<Method>();

    private List<TestResult> testResults = new ArrayList<TestResult>();

    public TestCaseThread(Object test) {
        this.test = test;
        init(test);
    }

    private void init(Object test) {
        for (Method m : test.getClass().getMethods()) {
            if (m.isAnnotationPresent(Test.class)) {
                if(!m.isAnnotationPresent(Ignore.class)) {
                    m.setAccessible(true);
                    testMethods.add(m);
                }
                continue;
            }

            if (m.isAnnotationPresent(Before.class)) {
                m.setAccessible(true);
                beforeMethods.add(m);
                continue;
            }

            if (m.isAnnotationPresent(After.class)) {
                m.setAccessible(true);
                afterMethods.add(m);
            }
        }
    }

    public void run() {
        for(Method testMethod: testMethods) {
            try {
                doRun(testMethod);
                testResults.add(new TestResult(test.getClass(), testMethod));
            } catch (Throwable ex) {
                testResults.add(new TestResult(test.getClass(), testMethod, ex));
            }
        }
    }

    private void doRun(Method testMethod) throws Throwable {
        for(Method beforeMethod: beforeMethods) {
            beforeMethod.invoke(test);
        }

        Throwable error = null;
        try {
            testMethod.invoke(test);
        } catch(Throwable ex) {
            error = ex;
        } finally {
            try {
                for(Method afterMethod: afterMethods) {
                    afterMethod.invoke(test);
                }
            } catch (Throwable ex) {
                if(error == null) {
                    error = ex;
                }
            }
        }

        if(error != null) {
            throw error;
        }

    }

    public List<TestResult> getTestResults() {
        return testResults;
    }
}



