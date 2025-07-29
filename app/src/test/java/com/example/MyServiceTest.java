package com.example;

import org.junit.Before;
import org.junit.Test;

public class MyServiceTest {

    @Before
    public void setUp() {
        AgentMain.currentTest.set(Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    @Test
    public void testAddUser() {
        new MyService().addUser();
    }

    @Test
    public void testRemoveUser() {
        new MyService().removeUser();
    }
}
