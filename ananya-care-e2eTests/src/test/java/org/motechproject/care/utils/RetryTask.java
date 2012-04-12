package org.motechproject.care.utils;

public abstract class RetryTask<T> {

    public RetryTask() {
    }
    protected abstract T perform();

    public T execute(int numberOfTries, long wait) {
        for(int i=numberOfTries;i>0;i--){
            T result = perform();
            
            if(result != null) {
                return result;
            }
            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }


}
