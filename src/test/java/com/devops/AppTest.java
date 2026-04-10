package com.devops;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    App app = new App();

    @Test
    void testFullAttendance() {
        assertEquals(100.0, app.calculateAttendance(30, 30));
    }

    @Test
    void testEligible() {
        assertEquals("ELIGIBLE", app.getStatus(75.0));
    }

    @Test
    void testShortAttendance() {
        assertEquals("SHORT - Apply for condonation", app.getStatus(65.0));
    }

    @Test
    void testDetained() {
        assertEquals("DETAINED - Not eligible for exam", app.getStatus(50.0));
    }

    @Test
    void testClassesNeeded() {
        // 18/30 = 60%, needs classes to reach 75%
        assertTrue(app.classesNeeded(18, 30) > 0);
    }

    @Test
    void testAlreadyEligible() {
        // 25/30 = 83.3%, needs 0 more classes
        assertEquals(0, app.classesNeeded(25, 30));
    }

    @Test
    void testZeroTotal() {
        assertEquals(0.0, app.calculateAttendance(0, 0));
    }
}