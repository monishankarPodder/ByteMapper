package agent;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.bytebuddy.asm.Advice;

public class TrackerAdvice {
    private static final ConcurrentHashMap<String, String> methodToTest = new ConcurrentHashMap<>();
    private static final ThreadLocal<String> currentTest = new ThreadLocal<>();

    @Advice.OnMethodEnter
    public static void captureTest(@Advice.Origin("#t#.#m") String methodName) {
        if (methodName.contains("Test")) {
            currentTest.set(methodName);
        } else {
            String test = currentTest.get();
            if (test != null) {
                methodToTest.putIfAbsent(methodName, test);
            }
        }
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void onExit() {
        try (FileWriter fw = new FileWriter("app/target/method_test_mapping.json")) {
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
    }
}
