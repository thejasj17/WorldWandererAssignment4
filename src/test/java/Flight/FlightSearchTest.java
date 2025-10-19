package Flight;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FlightSearchTest {
    private FlightSearch search;

    @BeforeEach
    public void setUp() {
        search = new FlightSearch();
    }

    // TC1: Total passengers
    @Test
    public void testTotalPassengersMin() {
        boolean result = search.runFlightSearch("19/10/2025", "syd", false, "20/10/2025", "mel", "economy", 1, 0, 0);
        assertTrue(result);
        assertEquals(1, search.getAdultPassengerCount());
    }

    @Test
    public void testTotalPassengersMaxFail() {
        boolean result = search.runFlightSearch("19/10/2025", "syd", false, "20/10/2025", "mel", "economy", 0, 0, 10);
        assertFalse(result);
        assertEquals(0, search.getInfantPassengerCount());
    }

    @Test
    public void testDepartureTodayValid() {
        boolean result = search.runFlightSearch("18/10/2025", "syd", false, "20/10/2025", "mel", "economy", 1, 0, 0);
        assertTrue(result);
        assertEquals("18/10/2025", search.getDepartureDate());
    }

    @Test
    public void testDeparturePastFail() {
        boolean result = search.runFlightSearch("17/10/2025", "syd", false, "20/10/2025", "mel", "economy", 1, 0, 0);
        assertFalse(result);
        assertNull(search.getDepartureDate());
    }

    // TC7: Date format/leap 
    @Test
    public void testLeapYearValid() {
        boolean result = search.runFlightSearch("29/02/2028", "syd", false, "01/03/2028", "mel", "economy", 1, 0, 0);
        assertTrue(result);
        assertEquals("29/02/2028", search.getDepartureDate());
    }

    @Test
    public void testNonLeapFeb29Fail() {
        boolean result = search.runFlightSearch("29/02/2025", "syd", false, "01/03/2026", "mel", "economy", 1, 0, 0);
        assertFalse(result);
        assertNull(search.getDepartureDate());  // Remains default
    }

    // TC8: Return > departure 
    @Test
    public void testReturnNextDayValid() {
        boolean result = search.runFlightSearch("19/10/2025", "syd", false, "20/10/2025", "mel", "economy", 1, 0, 0);
        assertTrue(result);
        assertEquals("20/10/2025", search.getReturnDate());
    }

    @Test
    public void testReturnSameDayFail() {
        boolean result = search.runFlightSearch("19/10/2025", "syd", false, "19/10/2025", "mel", "economy", 1, 0, 0);
        assertFalse(result);
        assertNull(search.getReturnDate());
    }

    // TC9: Seating class (unchanged)
    // ...

    // TC10: Emergency row (unchanged)
    // ...

    // TC11: Airports (unchanged)
    // ...

    // TC12: All valid - FIXED: Max children data to 1 adult + 2 children (boundary, total=3)
    @Test
    public void testAllValidMinPassengers() {
        boolean result = search.runFlightSearch("19/10/2025", "syd", false, "20/10/2025", "lax", "premium economy", 1, 0, 0);
        assertTrue(result);
        assertEquals("premium economy", search.getSeatingClass());
    }

    @Test
    public void testAllValidMaxChildren() {  // FIXED: 1 adult + 2 children (max ratio, total=3 <=9)
        boolean result = search.runFlightSearch("19/10/2025", "mel", false, "25/10/2025", "cdg", "business", 1, 2, 0);
        assertTrue(result);
        assertEquals(2, search.getChildPassengerCount());
    }

    @Test
    public void testAllValidMaxInfants() {  // FIXED: 1 adult + 1 infant (max ratio, total=2)
        boolean result = search.runFlightSearch("19/10/2025", "del", false, "22/10/2025", "pvg", "first", 1, 0, 1);
        assertTrue(result);
        assertEquals(1, search.getInfantPassengerCount());
    }

    @Test
    public void testAllValidWithEmergency() {  // FIXED: 1 adult + 0 child + 0 infant (no restrictions violated)
        boolean result = search.runFlightSearch("19/10/2025", "doh", true, "21/10/2025", "syd", "economy", 1, 0, 0);
        assertTrue(result);
        assertTrue(search.isEmergencyRowSeating());
    }
}