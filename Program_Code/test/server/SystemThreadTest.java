package server;

import data.Airport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class SystemThreadTest {
    final SystemThread testThread = new SystemThread();

    @Test
    void testAirportListGenerator() {
        List<Airport> testAirportList = testThread.generateFullAirportList();
        Airport testAirport = testAirportList.get((int) (Math.random()*testAirportList.size()));
        Assertions.assertNotEquals(0,testAirport.getID());
        Assertions.assertNotEquals("",testAirport.getName());
        Assertions.assertNotEquals("",testAirport.getCity());
        Assertions.assertNotEquals("",testAirport.getCountry());
        Assertions.assertNotEquals(new char[] {' ',' ',' '},testAirport.getIATA());
        Assertions.assertNotEquals(new char[] {' ',' ',' ',' '},testAirport.getICAO());
        Assertions.assertNotEquals(0,testAirport.getLatitude());
        Assertions.assertNotEquals(0,testAirport.getLongitude());
        Assertions.assertNotEquals(0,testAirport.getAltitude());
        Assertions.assertNotEquals(0,testAirport.getUTCoffset());
        Assertions.assertNotEquals(' ',testAirport.getDSTregion());
        Assertions.assertNotEquals("",testAirport.getTimeZoneRegion());
    }

    @Test
    void generateNewID() {
    }

    @Test
    void updateDatabase() {
    }
}