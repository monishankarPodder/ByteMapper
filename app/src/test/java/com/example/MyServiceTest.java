package com.example;

import com.example.coverage.TestListener;
import org.junit.Rule;
import org.junit.Test;

public class MyServiceTest {

    @Rule
    public TestListener watcher = new TestListener();

    @Test
    public void testAddUser() {
        new MyService().addUser();
    }

    @Test
    public void testRemoveUser() {
        new MyService().removeUser();
    }
}
