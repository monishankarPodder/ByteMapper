package agent;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import net.bytebuddy.asm.Advice;

public class TrackerAdvice {
    private static final ConcurrentHashMap<String, String> methodToTest = new ConcurrentHashMap<>();
    private static final ThreadLocal<String> currentTest = new ThreadLocal<>();

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

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void onExit() {
        try (FileWriter fw = new FileWriter("method_test_mapping.json")) {
            fw.write("{\n");
            int i = 0;
            for (var entry : methodToTest.entrySet()) {
                fw.write("  \"" + entry.getKey() + "\": \"" + entry.getValue() + "\"");
                if (++i < methodToTest.size()) fw.write(",");
                fw.write("\n");
            }
            fw.write("}\n");
        } catch (IOException ignored) {}
    }
}
