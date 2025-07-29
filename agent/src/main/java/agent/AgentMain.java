package agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.FileWriter;
import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AgentMain {

    public static final Map<String, Set<String>> methodToTests = new HashMap<>();
    public static final ThreadLocal<String> currentTest = new ThreadLocal<>();

    public static void premain(String agentArgs, Instrumentation inst) {
        new AgentBuilder.Default()
                .type(ElementMatchers.nameStartsWith("com.example"))
                .transform((builder, typeDescription, classLoader, module) ->
                        builder.visit(Advice.to(TrackerAdvice.class).on(ElementMatchers.isMethod()))
                ).installOn(inst);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try (FileWriter writer = new FileWriter("method_test_mapping.json")) {
                writer.write("{\n");
                int count = 0;
                for (Map.Entry<String, Set<String>> entry : methodToTests.entrySet()) {
                    writer.write("  \"" + entry.getKey() + "\": [\"" + String.join("\", \"", entry.getValue()) + "\"]");
                    count++;
                    if (count < methodToTests.size()) writer.write(",");
                    writer.write("\n");
                }
                writer.write("}\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    public static class TrackerAdvice {
        @Advice.OnMethodEnter
        static void enter(@Advice.Origin("#t.#m") String method) {
            String test = currentTest.get();
            if (test != null) {
                methodToTests.computeIfAbsent(method, k -> new HashSet<>()).add(test);
            }
        }
    }
}
