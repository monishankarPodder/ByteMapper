package agent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.bytebuddy.asm.Advice;

public class TrackerAdvice {
    private static final ConcurrentHashMap<String, String> methodToTest = new ConcurrentHashMap<>();
    private static final ThreadLocal<String> currentTest = new ThreadLocal<>();

    static {
        // JVM shutdown: write only once after all tests complete
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            String filePath = "app/target/method_test_mapping.json";
            try {
                File file = new File(filePath);
                file.getParentFile().mkdirs(); // ensure app/target exists

                try (FileWriter fw = new FileWriter(file)) {
                    fw.write("{\n");
                    int i = 0;
                    for (Map.Entry<String, String> entry : methodToTest.entrySet()) {
                        fw.write("  \"" + entry.getKey() + "\": \"" + entry.getValue() + "\"");
                        if (++i < methodToTest.size()) fw.write(",");
                        fw.write("\n");
                    }
                    fw.write("}\n");
                }
                System.out.println("✅ method_test_mapping.json created at: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("❌ Error writing method_test_mapping.json: " + e.getMessage());
            }
        }));
    }

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin("#t#.#m") String methodName) {
        String test = currentTest.get();
        if (test != null) {
            methodToTest.putIfAbsent(methodName, test);
        }
    }

    @Advice.OnMethodEnter
    public static void captureTest(@Advice.Origin("#t#.#m") String methodName) {
        if (methodName.contains("Test")) {
            currentTest.set(methodName);
        }
    }
}
