package org.motechproject.care.utils;

import java.lang.reflect.Method;

public class TestResult {
    private Class clazz;
    private Method method;
    private Throwable error;

    public TestResult(Class clazz, Method method, Throwable error) {
        this.clazz = clazz;
        this.method = method;
        this.error = error;
    }

    public TestResult(Class clazz, Method method) {
        this(clazz, method, null);
    }

    public boolean hasError() {
        return error != null;
    }

    public void printResult() {
        System.out.println("Test " + clazz.getCanonicalName() + "." + method.getName() + ":" + (hasError() ? " Failed" : " Passed"));
        if(hasError()) {
            System.err.println("Test Failed: " + clazz.getCanonicalName() + "." + method.getName());
            error.printStackTrace();
        }
    }
}
