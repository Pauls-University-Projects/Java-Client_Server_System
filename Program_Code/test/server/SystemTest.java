package server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class SystemTest {
    @Test
    void testDatabaseAccess() {
        ArrayList<ArrayList<String>> databaseRaw;
        System.databasePush("UPDATE airlines SET name='Nordica', iata='EE', icao='EST', callsign='REVAL' WHERE id=2218"); //Bringing the Dataset Up-to-Date ;)
        databaseRaw = System.databasePull("SELECT name, iata, icao, callsign from airlines WHERE id=2218");
        Assertions.assertEquals("Nordica",databaseRaw.get(0).get(0));
        Assertions.assertEquals("EE",databaseRaw.get(0).get(1));
        Assertions.assertEquals("EST",databaseRaw.get(0).get(2));
        Assertions.assertEquals("REVAL",databaseRaw.get(0).get(3));
        System.databasePush("UPDATE airports SET name='Lennart Meri Tallinn', city='Tallinn' WHERE id=415");
        databaseRaw = System.databasePull("SELECT name, city, country, code from airports WHERE id=415");
        Assertions.assertEquals("Lennart Meri Tallinn",databaseRaw.get(0).get(0));
        Assertions.assertEquals("Tallinn",databaseRaw.get(0).get(1));
        Assertions.assertEquals("Estonia",databaseRaw.get(0).get(2));
        Assertions.assertEquals("TLL",databaseRaw.get(0).get(3));
    }

}