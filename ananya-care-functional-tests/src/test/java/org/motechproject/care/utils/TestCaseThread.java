package org.motechproject.care.utils;

public abstract class TestCaseThread extends Thread {

    private Throwable error;

    protected void before() {

    }

    protected void after() {

    }

    public Throwable getError() {
        return this.error;
    }

    public boolean hasError() {
        return this.error != null;
    }

    public void run() {
        try {
            doRun();
        } catch (Throwable ex) {
            this.error = ex;
        }
    }

    private void doRun() throws Throwable {
        before();
        Throwable error = null;
        try {
            test();
        } catch(Throwable ex) {
            error = ex;
        } finally {
            try {
                after();
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

    protected abstract void test();
}
