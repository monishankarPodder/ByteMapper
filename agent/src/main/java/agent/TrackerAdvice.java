package agent;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.Test;

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
    public static void captureTest(@Advice.Origin Class<?> clazz) {
        if (clazz.getAnnotation(Test.class) != null) {
            currentTest.set
