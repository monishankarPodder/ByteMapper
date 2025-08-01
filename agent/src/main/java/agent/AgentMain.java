package agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class AgentMain {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("[Agent] Starting instrumentation...");

        AgentBuilder.Transformer transformer = new AgentBuilder.Transformer() {
            @Override
            public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder,
                                                    TypeDescription typeDescription,
                                                    ClassLoader classLoader,
                                                    JavaModule module,
                                                    ProtectionDomain domain) {
                return builder.visit(
                    Advice.to(TrackerAdvice.class)
                          .on(ElementMatchers.isMethod()
                                .and(ElementMatchers.not(ElementMatchers.nameStartsWith("java")))
                                .and(ElementMatchers.not(ElementMatchers.isConstructor())))
                );
            }
        };

        new AgentBuilder.Default()
            .type(ElementMatchers.nameStartsWith("com.example")) // your package prefix
            .transform(transformer)
            .installOn(inst);
    }
}
