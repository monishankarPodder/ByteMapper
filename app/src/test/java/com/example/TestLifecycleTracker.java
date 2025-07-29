package com.example;

import org.junit.jupiter.api.AfterAll;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import static agent.TrackerAdvice.getMethodToTestMap;

public class TestLifecycleTracker {

    @AfterAll
    public static void dumpMethodMapping() {
        Map<String, String> methodToTest = getMethodToTestMap();

        try (FileWriter fw = new FileWriter("app/target/method_test_mapping.json")) {
            fw.write("{\n");
            int i = 0;
            for (Map.Entry<String, String> entry : methodToTest.entrySet()) {
                fw.write("  \"" + entry.getKey() + "\": \"" + entry.getValue() + "\"");
                if (++i < methodToTest.size()) fw.write(",");
                fw.write("\n");
            }
            fw.write("}\n");
            System.out.println("✅ method_test_mapping.json written");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
