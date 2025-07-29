package com.example.coverage;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class TestListener extends TestWatcher {
    @Override
    protected void starting(Description description) {
        MethodCallTracker.setCurrentTest(description.getMethodName());
    }

    @Override
    protected void finished(Description description) {
        MethodCallTracker.setCurrentTest(null);
        MethodCallTracker.writeToFile();
    }
}
