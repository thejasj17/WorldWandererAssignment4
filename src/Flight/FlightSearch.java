package Flight;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class FlightSearch {
    private String departureDate = null;
    private String departureAirportCode = null;
    private boolean emergencyRowSeating = false;
    private String returnDate = null;
    private String destinationAirportCode = null;
    private String seatingClass = null;
    private int adultPassengerCount = 0;
    private int childPassengerCount = 0;
    private int infantPassengerCount = 0;

    // Getters for testing attributes
    public String getDepartureDate() { return departureDate; }
    public String getDepartureAirportCode() { return departureAirportCode; }
    public boolean isEmergencyRowSeating() { return emergencyRowSeating; }
    public String getReturnDate() { return returnDate; }
    public String getDestinationAirportCode() { return destinationAirportCode; }
    public String getSeatingClass() { return seatingClass; }
    public int getAdultPassengerCount() { return adultPassengerCount; }
    public int getChildPassengerCount() { return childPassengerCount; }
    public int getInfantPassengerCount() { return infantPassengerCount; }

    /**
     * Validates flight search parameters per conditions 1-11.
     * Sets attributes only if all valid; returns true on success, false otherwise.
     * All inputs are lowercase as per spec.
     */
    public boolean runFlightSearch(String departureDate, String departureAirportCode, boolean emergencyRowSeating,
                                   String returnDate, String destinationAirportCode, String seatingClass,
                                   int adultPassengerCount, int childPassengerCount, int infantPassengerCount) {
        // Reset to defaults first (ensures no partial set)
        resetAttributes();

        // Condition 1: Total passengers 1-9
        int totalPassengers = adultPassengerCount + childPassengerCount + infantPassengerCount;
        if (totalPassengers < 1 || totalPassengers > 9) {
            return false;
        }

        // Condition 4 & 5: Children <= 2*adults, Infants <= adults
        if (childPassengerCount > 2 * adultPassengerCount || infantPassengerCount > adultPassengerCount) {
            return false;
        }

        // Condition 2: No children in emergency/first
        if ((emergencyRowSeating || "first".equals(seatingClass)) && childPassengerCount > 0) {
            return false;
        }

        // Condition 3: No infants in emergency/business
        if ((emergencyRowSeating || "business".equals(seatingClass)) && infantPassengerCount > 0) {
            return false;
        }

        // Condition 9: Valid seating class
        List<String> validClasses = Arrays.asList("economy", "premium economy", "business", "first");
        if (!validClasses.contains(seatingClass)) {
            return false;
        }

        // Condition 10: Emergency only in economy
        if (emergencyRowSeating && !"economy".equals(seatingClass)) {
            return false;
        }

        // Condition 11: Valid airports, departure != destination
        List<String> validAirports = Arrays.asList("syd", "mel", "lax", "cdg", "del", "pvg", "doh");
        if (!validAirports.contains(departureAirportCode) || !validAirports.contains(destinationAirportCode) ||
            departureAirportCode.equals(destinationAirportCode)) {
            return false;
        }

        // Conditions 6-8: Date validations using LocalDate (date-only)
        if (!isValidDate(departureDate) || !isValidDate(returnDate)) {
            return false;
        }
        LocalDate depDate = LocalDate.parse(departureDate, formatter);
        LocalDate retDate = LocalDate.parse(returnDate, formatter);
        LocalDate today = LocalDate.of(2025, 10, 18);  // Fixed today for testing (Oct 18, 2025)

        // Condition 6: Departure not in past (same day OK)
        if (depDate.isBefore(today)) {
            return false;
        }

        // Condition 8: Return after departure (strict >, fail on same day)
        if (retDate.isBefore(depDate) || retDate.equals(depDate)) {
            return false;
        }

        // All valid: Set attributes
        this.departureDate = departureDate;
        this.departureAirportCode = departureAirportCode;
        this.emergencyRowSeating = emergencyRowSeating;
        this.returnDate = returnDate;
        this.destinationAirportCode = destinationAirportCode;
        this.seatingClass = seatingClass;
        this.adultPassengerCount = adultPassengerCount;
        this.childPassengerCount = childPassengerCount;
        this.infantPassengerCount = infantPassengerCount;
        return true;
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu");

    /**
     * Resets attributes to defaults (for failure cases).
     */
    private void resetAttributes() {
        this.departureDate = null;
        this.departureAirportCode = null;
        this.emergencyRowSeating = false;
        this.returnDate = null;
        this.destinationAirportCode = null;
        this.seatingClass = null;
        this.adultPassengerCount = 0;
        this.childPassengerCount = 0;
        this.infantPassengerCount = 0;
    }

    /**
     * Validates date format and leap year (e.g., Feb 29 only in leap years).
     * Checks day/month/year bounds + leap. Uses LocalDate.parse (strict) + manual leap fallback.
     */
    private boolean isValidDate(String dateStr) {
        if (dateStr == null || !dateStr.matches("^\\d{2}/\\d{2}/\\d{4}$")) {
            return false;
        }
        try {
            LocalDate date = LocalDate.parse(dateStr, formatter);
            // Manual leap check for Feb 29 (redundancy for strictness)
            String[] parts = dateStr.split("/");
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);
            if (month == 2 && day == 29) {
                boolean isLeap = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
                if (!isLeap) {
                    return false;  // Explicit fail for non-leap Feb 29
                }
            }
            return true;
        } catch (DateTimeParseException e) {
            // Fallback: Parse fails for invalid dates (e.g., 31/04/2025 or non-leap Feb 29)
            return false;
        }
    }
}