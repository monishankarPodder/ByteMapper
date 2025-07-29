package agent;

import net.bytebuddy.asm.Advice;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TrackerAdvice {
    private static final ConcurrentHashMap<String, String> methodToTest = new ConcurrentHashMap<>();
    private static final ThreadLocal<String> currentTest = new ThreadLocal<>();

    static {
        // Shutdown hook to write method-test mapping at JVM exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            String filePath = System.getProperty("user.dir") + "/method_test_mapping.json";
            try (FileWriter fw = new FileWriter(filePath)) {
                fw.write("{\n");
                int i = 0;
                for (Map.Entry<String, String> entry : methodToTest.entrySet()) {
                    fw.write("  \"" + entry.getKey() + "\": \"" + entry.getValue() + "\"");
                    if (++i < methodToTest.size()) fw.write(",");
                    fw.write("\n");
                }
                fw.write("}\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin("#t#.#m") String methodName) {
        if (methodName.contains("Test")) {
            currentTest.set(methodName);
        } else {
            String test = currentTest.get();
            if (test != null) {
                methodToTest.putIfAbsent(methodName, test);
            }
        }
    }
}
