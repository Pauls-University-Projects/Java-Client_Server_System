package server;



//region LIBRARIES USED
//Java Libraries:
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
//Our Classes:
import data.*;
//endregion



/**
 * @author  Paul Johannes Aru
 * @version 0.9
 * @since   2021-03-23
 */
public class SystemThread implements Runnable{
    //region THREAD VARIABLES
    private Socket socket;
    private static int connectionCount = 0;
    private int connectionNumber;
    private ObjectInputStream packetInputStream;
    private ObjectOutputStream packetOutputStream;
    //endregion



    //region THREAD CONSTRUCTORS
    /**
     * Creates a new Server System Thread.
     *
     * @return Server SystemThread.
     */
    public SystemThread() {} // For Testing
    /**
     * Creates a new Server System Thread.
     * <p>
     * Provided a Socket for Communication, the
     * constructor creates a new Thread for Handling
     * a Single Client.
     *
     * @param  socket Communication Socket.
     * @return Server SystemThread.
     */
    public SystemThread(Socket socket) throws IOException {
        this.socket = socket;

        packetOutputStream = new ObjectOutputStream(socket.getOutputStream());
        packetInputStream = new ObjectInputStream(socket.getInputStream());

        connectionCount++;
        connectionNumber = connectionCount;
        println("Connection " + connectionNumber + " Established.");
    }
    //endregion



    //region MESSAGE PASSING
    /**
     * Print Text in System Main Thread.
     *
     * @param message Text String.
     */
    private void println(String message) {
        java.lang.System.out.println("Server (thread " + connectionNumber + "): " + message);
    }
    //endregion


    //region AIRPORT LIST GENERATOR
    /**
     * Generates a New Airports List from Database.
     * <p>
     * Requests the System to Provide All the Information
     * on Airports, Assigns the Values to right Parameters
     * and Compiles it into an ArrayList.
     *
     * @return Airport ArrayList.
     */
    private ArrayList<Airport> generateFullAirportList() {
        ArrayList<ArrayList<String>> databaseRaw;
        ArrayList<Airport> airports = new ArrayList<>();
        databaseRaw = System.databasePull("SELECT id, name, city, country, code, icao, latitude, longitude, altitude, offset, dst, timezone FROM airports");
        for (int row=0;row< databaseRaw.size();row++) {
            Airport airport = new Airport(databaseRaw.get(row).get(0));
            airport.setName(databaseRaw.get(row).get(1));
            airport.setCity(databaseRaw.get(row).get(2));
            airport.setCountry(databaseRaw.get(row).get(3));
            airport.setIATA(databaseRaw.get(row).get(4));
            airport.setICAO(databaseRaw.get(row).get(5));
            airport.setLatitude(Float.parseFloat(databaseRaw.get(row).get(6)));
            airport.setLongitude(Float.parseFloat(databaseRaw.get(row).get(7)));
            airport.setAltitude(Float.parseFloat(databaseRaw.get(row).get(8)));
            airport.setUTCoffset(Float.parseFloat(databaseRaw.get(row).get(9)));
            airport.setDSTregion(databaseRaw.get(row).get(10).charAt(0));
            airport.setTimeZoneRegion(databaseRaw.get(row).get(11));
            airports.add(airport);
        }
        return airports;
    }
    //endregion


    //region ROUTES LIST GENERATOR
    /**
     * Generates a New Routes List from Database.
     * <p>
     * Given an Airport ID, Requests the System to
     * Provide All the Information on Routes that Depart
     * / Arrive from that Airports. In Addition, it also
     * gathers information for each routes airline and
     * connecting airport. At the end, it Assigns the
     * Values to right Objects & Parameters and Compiles
     * it into an ArrayList.
     *
     * @param airportID ID Integer.
     * @return Route ArrayList.
     */
    private List<Route> generateAirportRoutesList(int airportID) {
        ArrayList<Route> routes = new ArrayList<>();
        ArrayList<ArrayList<String>> databaseRaw;
        databaseRaw = System.databasePull("SELECT id, airline_id, source_id, dest_id, codeshare, stops, equipment FROM routes WHERE source_id=" + airportID + " OR dest_id=" + airportID);
        for (int row=0;row< databaseRaw.size();row++) {
            Airline airline = new Airline(databaseRaw.get(row).get(1));
            Airport airport;
            Route route = new Route(databaseRaw.get(row).get(0));
            if (databaseRaw.get(row).get(4) != null) {
                route.setCodeshare(databaseRaw.get(row).get(4).equals("Y"));
            }
            route.setStops(Integer.parseInt(databaseRaw.get(row).get(5)));
            route.setAircrafts(databaseRaw.get(row).get(6));

            //Gather Airline Information:
            ArrayList<ArrayList<String>> subDatabaseRaw = new ArrayList<>();
            subDatabaseRaw = System.databasePull("SELECT name, alias, iata, icao, callsign, country, active  FROM airlines WHERE id=" + databaseRaw.get(row).get(1));
            if (!subDatabaseRaw.equals(new ArrayList<>())) {
                airline.setName(subDatabaseRaw.get(0).get(0));
                airline.setAlias(subDatabaseRaw.get(0).get(1));
                airline.setIATA(subDatabaseRaw.get(0).get(2));
                airline.setICAO(subDatabaseRaw.get(0).get(3));
                airline.setCallsign(subDatabaseRaw.get(0).get(4));
                airline.setCountry(subDatabaseRaw.get(0).get(5));
                airline.setActive(subDatabaseRaw.get(0).get(6).equals("Y"));
            }

            //Gather Connecting Airport Information:
            if (airportID == Integer.parseInt(databaseRaw.get(row).get(2))) {
                route.setArriving(false);
                subDatabaseRaw = System.databasePull("SELECT name, city, country, code, icao, latitude, longitude, altitude, offset, dst, timezone FROM airports WHERE id=" + databaseRaw.get(row).get(3));
                airport = new Airport(databaseRaw.get(row).get(3));
            } else if (airportID == Integer.parseInt(databaseRaw.get(row).get(3))) {
                route.setArriving(true);
                subDatabaseRaw = System.databasePull("SELECT name, city, country, code, icao, latitude, longitude, altitude, offset, dst, timezone FROM airports WHERE id=" + databaseRaw.get(row).get(2));
                airport = new Airport(databaseRaw.get(row).get(2));
            } else {
                airport = new Airport(); //This Should Not Get Triggered
            }
            if (!subDatabaseRaw.equals(new ArrayList<>())) {
                airport.setName(subDatabaseRaw.get(0).get(0));
                airport.setCity(subDatabaseRaw.get(0).get(1));
                airport.setCountry(subDatabaseRaw.get(0).get(2));
                airport.setIATA(subDatabaseRaw.get(0).get(3));
                airport.setICAO(subDatabaseRaw.get(0).get(4));
                airport.setLatitude(Float.parseFloat(subDatabaseRaw.get(0).get(5)));
                airport.setLongitude(Float.parseFloat(subDatabaseRaw.get(0).get(6)));
                airport.setAltitude(Float.parseFloat(subDatabaseRaw.get(0).get(7)));
                airport.setUTCoffset(Float.parseFloat(subDatabaseRaw.get(0).get(8)));
                airport.setDSTregion(subDatabaseRaw.get(0).get(9).charAt(0));
                airport.setTimeZoneRegion(subDatabaseRaw.get(0).get(10));
            }

            route.setConnectingAirport(airport);
            route.setAirline(airline);
            routes.add(route);
        }
        return routes;
    }
    //endregion


    //region ID GENERATOR
    /**
     * Generates a New Available ID for Database.
     * <p>
     * Given the Type of ID ("route"/"airline"/"airport"),
     * the Method Requests the last ID used for the
     * category and returns the next value in line. For
     * Routes, the method also checks reserved IDs, since
     * these might not be synchronised to the database yet.
     *
     * @param idType ("route"/"airline"/"airport").
     * @return ID Integer.
     */
    private int generateNewID(String idType) {
        int newID = 0;
        ArrayList<ArrayList<String>> databaseRaw;
        if (idType.toLowerCase().equals("route")) {
            databaseRaw = System.databasePull("SELECT id FROM routes ORDER BY id DESC LIMIT 1");
            int lastRouteID = Integer.parseInt(databaseRaw.get(0).get(0));
            newID = lastRouteID+1;
            while (System.returnReservedRouteIDs().contains(newID)) {
                newID++;
            }
            System.reserveRouteID(newID);
        } else if (idType.toLowerCase().equals("airline")) {
            databaseRaw = System.databasePull("SELECT id FROM airlines ORDER BY id DESC LIMIT 1");
            int lastAirlineID = Integer.parseInt(databaseRaw.get(0).get(0));
            newID = lastAirlineID+1;
        } else if (idType.toLowerCase().equals("airport")) {
            databaseRaw = System.databasePull("SELECT id FROM airports ORDER BY id DESC LIMIT 1");
            int lastAirlineID = Integer.parseInt(databaseRaw.get(0).get(0));
            newID = lastAirlineID+1;
        }
        return newID;
    }
    //endregion


    //region DATABASE SYNCHRONISATION
    /**
     * Synchronises Local Client Routes to Database.
     * <p>
     * Given the Client's Routes and Airport, the
     * method Adds, Removes and Edits Routes in the
     * Database to Match Up with the Client's.
     *
     * @param newRoutes Route List.
     * @param routesReferenceAirportID ID Integer.
     * @return Success/Failure.
     */
    private boolean updateDatabase(List<Route> newRoutes, int routesReferenceAirportID) {
        List<Route> localNewRoutes = newRoutes;
        List<Route> databaseRoutes = generateAirportRoutesList(routesReferenceAirportID);
        List<Integer> databaseIDs = new ArrayList<>();
        List<Integer> clientRouteIDs = new ArrayList<>();
        for (int route=0; route< databaseRoutes.size(); route++) {
            databaseIDs.add(databaseRoutes.get(route).getID());
        }
        for (int route=0; route< localNewRoutes.size(); route++) {
            clientRouteIDs.add(localNewRoutes.get(route).getID());
        }

        // Add New Routes:
        for (int route=0; route< localNewRoutes.size(); route++) {
            if (!databaseIDs.contains(localNewRoutes.get(route).getID())) {
                String insertSQL = "INSERT INTO routes (id, airline_id, source_id, dest_id, codeshare, stops, equipment) VALUES ("+localNewRoutes.get(route).getID()+", ";
                if (localNewRoutes.get(route).getAirline().getID() == 0) { //airline_id
                    insertSQL = insertSQL +(generateNewID("airline"))+", ";
                } else {
                    insertSQL = insertSQL +localNewRoutes.get(route).getAirline().getID()+", ";
                }
                if (localNewRoutes.get(route).getArriving()) { //source_id, dest_id
                    insertSQL = insertSQL +localNewRoutes.get(route).getConnectingAirport().getID()+", "+routesReferenceAirportID+", ";
                } else {
                    insertSQL = insertSQL +routesReferenceAirportID+", "+localNewRoutes.get(route).getConnectingAirport().getID()+", ";
                }
                if (localNewRoutes.get(route).getCodeshare()) { //codeshare
                    insertSQL = insertSQL+"'Y', ";
                } else {
                    insertSQL = insertSQL+"'N', ";
                }
                insertSQL = insertSQL+localNewRoutes.get(route).getStops()+", '"+localNewRoutes.get(route).getAircrafts()+"')"; //stops, equipment
                System.databasePush(insertSQL);
            }
        }

        databaseRoutes = generateAirportRoutesList(routesReferenceAirportID);

        // Remove Old Routes:
        if (databaseRoutes.size() > localNewRoutes.size()) {
            for (int route=databaseRoutes.size()-1; route>-1; route=route-1) {
                if (!clientRouteIDs.contains(databaseRoutes.get(route).getID())) {
                    String insertSQL = "DELETE FROM routes WHERE id=" +databaseRoutes.get(route).getID();
                    System.databasePush(insertSQL);
                }
            }
        }

        databaseRoutes = generateAirportRoutesList(routesReferenceAirportID);

        // Sort by ID:
        Collections.sort(localNewRoutes, new Comparator<Route>(){
            public int compare(Route obj1, Route obj2) {
                return Integer.valueOf(obj2.getID()).compareTo(Integer.valueOf(obj1.getID()));
            }
        });
        Collections.sort(databaseRoutes, new Comparator<Route>(){
            public int compare(Route obj1, Route obj2) {
                return Integer.valueOf(obj2.getID()).compareTo(Integer.valueOf(obj1.getID()));
            }
        });

        // Synchronise Updated Fields:
        for (int route=0; route<databaseRoutes.size(); route++) {
            if (!databaseRoutes.get(route).equals(localNewRoutes.get(route))) {
                //Update Airline Information:
                if (!databaseRoutes.get(route).getAirline().equals(localNewRoutes.get(route).getAirline())) {
                    Airline modified = localNewRoutes.get(route).getAirline();
                    modified.setID(generateNewID("airline"));
                    String insertSQL = "INSERT INTO airlines (id, name, alias, iata, icao, callsign, country, active)"+
                            " VALUES ("+modified.getID()+", '"+modified.getName()+"', '"+modified.getAlias()+"', '"+new String(modified.getIATA())+"', '"+new String(modified.getICAO())+"', '"+modified.getCallsign()+"', '"+modified.getCountry()+"', ";
                    if (modified.isActive()) {insertSQL=insertSQL+"'Y')";}
                    else {insertSQL=insertSQL+"'N')";}
                    System.databasePush(insertSQL);
                    insertSQL = "UPDATE routes SET airline_id="+modified.getID()+" WHERE id="+localNewRoutes.get(route).getID();
                    System.databasePush(insertSQL);
                    databaseRoutes.get(route).setAirline(modified);
                }
                //Update Connecting Airport Information:
                if (!databaseRoutes.get(route).getConnectingAirport().equals(localNewRoutes.get(route).getConnectingAirport())) {
                    Airport modified = localNewRoutes.get(route).getConnectingAirport();
                    modified.setID(generateNewID("airport"));
                    String insertSQL = "INSERT INTO airports (id, name, city, country, code, icao, latitude, longitude, altitude, offset, dst, timezone)"+
                            " VALUES ("+modified.getID()+", '"+modified.getName()+"', '"+modified.getCity()+"', '"+modified.getCountry()+"', '"+new String(modified.getIATA())+"', '"+new String(modified.getICAO())+"', "+modified.getLatitude()+", "+modified.getLongitude()+", "+modified.getAltitude()+", "+modified.getUTCoffset()+", '"+modified.getDSTregion()+"', '"+modified.getTimeZoneRegion()+"')";
                    System.databasePush(insertSQL);
                    if (localNewRoutes.get(route).getArriving()) {
                        insertSQL = "UPDATE routes SET source_id="+modified.getID()+", dest_id="+routesReferenceAirportID+" WHERE id="+localNewRoutes.get(route).getID();
                    } else {
                        insertSQL = "UPDATE routes SET source_id="+routesReferenceAirportID+", dest_id="+modified.getID()+" WHERE id="+localNewRoutes.get(route).getID();
                    }
                    System.databasePush(insertSQL);
                    databaseRoutes.get(route).setConnectingAirport(modified);
                }
                //Update Route Information:
                if (databaseRoutes.get(route).getCodeshare() != localNewRoutes.get(route).getCodeshare()) { //codeshare
                    String insertSQL="";
                    if (localNewRoutes.get(route).getCodeshare()) {
                        insertSQL = "UPDATE routes SET codeshare='Y' WHERE id="+localNewRoutes.get(route).getID();
                    } else if (!localNewRoutes.get(route).getCodeshare()) {
                        insertSQL = "UPDATE routes SET codeshare='N' WHERE id="+localNewRoutes.get(route).getID();
                    }
                    System.databasePush(insertSQL);
                }
                if (databaseRoutes.get(route).getStops() != localNewRoutes.get(route).getStops()) { //stops
                    String insertSQL="UPDATE routes SET stops="+localNewRoutes.get(route).getStops()+" WHERE id="+localNewRoutes.get(route).getID();
                    System.databasePush(insertSQL);
                }
                if (!databaseRoutes.get(route).getAircrafts().equals(localNewRoutes.get(route).getAircrafts())) { //equipment
                    String insertSQL="UPDATE routes SET equipment='"+localNewRoutes.get(route).getAircrafts()+"' WHERE id="+localNewRoutes.get(route).getID();
                    System.databasePush(insertSQL);
                }
            }
        }

        // Check Changes:
        databaseRoutes = generateAirportRoutesList(routesReferenceAirportID);

        // Sort by ID:
        Collections.sort(localNewRoutes, new Comparator<Route>(){
            public int compare(Route obj1, Route obj2) {
                //return Integer.valueOf(obj2.getID()).compareTo(Integer.valueOf(obj1.getID()));
                return Integer.valueOf(obj1.getID()).compareTo(Integer.valueOf(obj2.getID()));
            }
        });
        Collections.sort(databaseRoutes, new Comparator<Route>(){
            public int compare(Route obj1, Route obj2) {
                //return Integer.valueOf(obj2.getID()).compareTo(Integer.valueOf(obj1.getID()));
                return Integer.valueOf(obj1.getID()).compareTo(Integer.valueOf(obj2.getID()));
            }
        });

        boolean noSyncError = true;
        for (int route=0;route<localNewRoutes.size();route++) {
            if (!localNewRoutes.get(route).equals(databaseRoutes.get(route))) {
                Route test1=localNewRoutes.get(route);
                Route test2=databaseRoutes.get(route);
                noSyncError = false;
            }
        }
        return noSyncError;
    }
    //endregion



    //region THREAD MAIN LOOP
    @Override
    public void run() {
        try {
            // Read and process names until an exception is thrown.
            println("Waiting For Client...");
            Packet message;
            while ((message = (Packet) packetInputStream.readObject()) != null) {
                println("Received " + message + " From Client.");
                Packet response = new Packet();

                // SERVER TASKS:

                if (message.command() == Command.FETCH_ALL_AIRPORTS) {
                    response.sendAirportList(generateFullAirportList());

                } else if (message.command() == Command.FETCH_ROUTES) {
                    response.sendRouteList(generateAirportRoutesList(message.receiveNumber()));

                } else if (message.command() == Command.FETCH_AVAILABLE_ROUTE_ID) {
                    response.sendNumber(generateNewID("route"));

                } else if (message.command() == Command.DISPATCH_CHANGES) {
                    response.sendCondition(updateDatabase(message.receiveRouteList(),message.receiveNumber()));
                }

                println("Sent " + response + " To Client.");
                packetOutputStream.writeObject(response);
            }
        } catch (IOException e) {
            Logger.getLogger(SystemThread.class.getName()).log(Level.SEVERE, null, e);
            println("Halted Thread.");
        } catch (ClassNotFoundException e) {
            Logger.getLogger(SystemThread.class.getName()).log(Level.SEVERE, null, e);
            java.lang.System.out.println("ERROR: Serialized Object Class Can Not be Found!!!");
        } finally {
            try {
                println("Lost Connection to Client.");
                socket.close();
            } catch (IOException e) {
                Logger.getLogger(SystemThread.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }
    //endregion
}
