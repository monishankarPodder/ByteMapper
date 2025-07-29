package com.example.coverage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MethodCallTracker {

    public static ThreadLocal<String> currentTest = new ThreadLocal<>();

    private static final Map<String, Set<String>> testToMethods = new HashMap<>();

    public static void logMethod(String methodName) {
        String testName = currentTest.get();
        if (testName != null) {
            testToMethods.computeIfAbsent(testName, k -> new HashSet<>()).add(methodName);
        }
    }

    public static void setCurrentTest(String testName) {
        currentTest.set(testName);
    }

    public static void writeToFile() {
        try (FileWriter writer = new FileWriter("method-test-map.json")) {
            writer.write("{\n");
            int count = 0;
            for (Map.Entry<String, Set<String>> entry : testToMethods.entrySet()) {
                writer.write("  \"" + entry.getKey() + "\": [");
                writer.write(String.join(", ", entry.getValue().stream().map(s -> "\"" + s + "\"").toList()));
                writer.write("]");
                count++;
                if (count < testToMethods.size()) writer.write(",");
                writer.write("\n");
            }
            writer.write("}\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
