package client;



//region LIBRARIES USED
//Java Libraries:
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.*;
import java.awt.print.PrinterException;
import java.io.*;
import java.net.Socket;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
//Our Classes:
import data.*;
//endregion



/**
 * @author  Paul Johannes Aru
 * @version 0.9
 * @since   2021-03-15
 */
public class Application {
    //region APPLICATION VARIABLES
    private Socket socket;
    private ObjectOutputStream packetOutputStream;
    private ObjectInputStream packetInputStream;
    private Interface guiWindow;
    private List<Airport> launchData;
    private Airport openAirport;
    private List<Route> routeData;
    private boolean editMode = false;
    //endregion



    // Empty Application Constructor:
    private Application() {}



    //region CONNECT TO SERVER
    /**
     * Establishes a Connection to the Server.
     * <p>
     * Attempts to Connect to the Server. Currently
     * that is set to 127.0.0.1:2000 a.k.a. the local
     * system.
     *
     * @return Whether a Connection was Established or not.
     */
    private boolean connectServer() {
        disconnectServer();
        guiWindow.printLog(MessageType.STATUS,"Connecting to Server..");
        // Connection Succeeded:
        try {
            socket = new Socket("127.0.0.1", 2000);
            packetOutputStream = new ObjectOutputStream(socket.getOutputStream());
            packetInputStream = new ObjectInputStream(socket.getInputStream());
            guiWindow.printLog(MessageType.STATUS,"Server Connected");
            return true;
        // Connection Failed:
        } catch (IOException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
            guiWindow.printLog(MessageType.ERROR,ex.toString());
            return false;
        }
    }
    //endregion


    //region DISCONNECT FROM SERVER
    /**
     * Disconnects from the Server.
     * <p>
     * Attempts to Disconnect from the Server.
     */
    public void disconnectServer() {
        if (socket != null) {
            guiWindow.printLog(MessageType.STATUS,"Disconnecting from the Server..");
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                socket = null;
            }
        }
    }
    //endregion


    //region TALK TO SERVER
    /**
     * Send (& Receive) a Packet to (& from) the Server.
     * <p>
     * Sends a Packet that contains an instruction (and
     * data) to the Server and receives a Packet (with
     * information) in return.
     *
     * @param container A Packet object to be sent.
     * @return A Packet object received in return.
     */
    public Packet talkToServer(Packet container) {
        Packet recievedContainer = null;
        // Send Data:
        guiWindow.printLog(MessageType.STATUS,"Sending to Server..");
        try {
            packetOutputStream.writeObject(container);
        } catch (IOException e) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, e);
        }
        // Receive Data:
        guiWindow.printLog(MessageType.STATUS,"Waiting for Server..");
        try {
            recievedContainer = (Packet) packetInputStream.readObject();
            guiWindow.printLog(MessageType.STATUS,"Received Response");
        } catch (IOException e) {
            guiWindow.printLog(MessageType.ERROR,"(IOException) " + e);
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, e);
        } catch (ClassNotFoundException e) {
            guiWindow.printLog(MessageType.ERROR,"(ClassNotFoundException) " + e);
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, e);
        }
        return recievedContainer;
    }
    //endregion


    //region FIND AIRPORT
    /**
     * Check if an Airport Exists in the Database.
     * <p>
     * This Method Looks for the Given Airport from
     * a List of Airports Pre-Fetched From the Server.
     *
     * @param category String Specifying the Search Category ("IATA Code"/"ICAO Code"/"Airport Name").
     * @param searchTerm Airport Name or other Identifying Piece of Text.
     * @return Whether a Match Was Found.
     */
    public boolean matchAirport(String category, String searchTerm) {
        if (category.equals(SearchCategories.IATA.toString()) && searchTerm.length()!=3) {
            guiWindow.printLog(MessageType.FAILURE,"IATA Code Not 3 Characters Long");
            return false;
        } else if (category.equals(SearchCategories.ICAO.toString()) && searchTerm.length()!=4) {
            guiWindow.printLog(MessageType.FAILURE,"ICAO Code Not 4 Characters Long");
            return false;
        }
        for (int airport=0;airport<launchData.size();airport++) {
            if ((category.equals(SearchCategories.NAME.toString()) && launchData.get(airport).getName().toLowerCase().equals(searchTerm.toLowerCase())) ||
            (category.equals(SearchCategories.IATA.toString()) && new String(launchData.get(airport).getIATA()).equals(searchTerm.toUpperCase())) ||
            (category.equals(SearchCategories.ICAO.toString()) && new String(launchData.get(airport).getICAO()).equals(searchTerm.toUpperCase()))) {
                guiWindow.printLog(MessageType.SUCCESS,"Found " + launchData.get(airport).getName() + " Airport");
                guiWindow.setAirportLabel("Welcome to " + launchData.get(airport).getName() + " Airport, [" +
                    new String(launchData.get(airport).getIATA()) + "], [" +
                    new String(launchData.get(airport).getICAO()) + "], Local Time: "+
                        (LocalDateTime.now().plusHours((long) launchData.get(airport).getUTCoffset()).format(DateTimeFormatter.ofPattern("HH:mm"))));
                openAirport = launchData.get(airport);
                return true;
            }
        }
        guiWindow.printLog(MessageType.FAILURE,"Finding Search Term");
        return false;
    }
    //endregion


    //region REQUEST ROUTES
    /**
     * Request Routes for the Open Airport from the Server.
     * <p>
     * This Method Request A List of Routes for the
     * Open Airport and Sends it onwards to the
     * table in the GUI Window.
     */
    public void requestRoutes() {
        Packet request = new Packet(Command.FETCH_ROUTES);
        request.sendNumber(openAirport.getID());
        Packet response = talkToServer(request);
        routeData = response.receiveRouteList();
        guiWindow.tableDisplayRoutes(routeData);
        if (response.receiveRouteList() != null) {
            guiWindow.printLog(MessageType.SUCCESS,"Received Routes for "+openAirport.getName());
        } else {
            guiWindow.printLog(MessageType.FAILURE, "Receiving Routes for "+openAirport.getName());
        }
    }
    //endregion


    //region ADD A ROUTE
    /**
     * Add a New Route.
     * <p>
     * This Method Request an Available Route
     * ID from the Server and Creates a new
     * Route Object.
     */
    public void addRoute() {
        Packet request = new Packet(Command.FETCH_AVAILABLE_ROUTE_ID);
        Packet response = talkToServer(request);
        int newID = response.receiveNumber();
        routeData.add(new Route(String.valueOf(newID)));
        int tableRow = guiWindow.tableAddRow();
        guiWindow.tableModifyValue(tableRow,0,String.valueOf(newID));
        guiWindow.printLog(MessageType.SUCCESS,"Created Route "+newID);
    }
    //endregion


    //region REMOVE A ROUTE
    /**
     * Delete a Route.
     * <p>
     * This Method Removes a Route Based on the
     * Row Number that it is Stored/Displayed on.
     * @param routeRow The Row Number that Contains the Route.
     */
    public void removeRoute(int routeRow) {
        guiWindow.tableRemoveRow(routeRow);
        guiWindow.printLog(MessageType.SUCCESS,"Removed " + routeData.get(routeRow).getAirline().getName()+"'s " +routeData.get(routeRow).getConnectingAirport().getName() + " Route");
        routeData.remove(routeRow);
    }
    //endregion


    //region SYNCHRONISE ROUTES
    /**
     * Send Routes to the Server for Saving.
     * <p>
     * This Method Requests the Server to
     * update it's database to match the
     * clients route info.
     */
    public void saveChanges() {
        Packet dispatch = new Packet(Command.DISPATCH_CHANGES);
        dispatch.sendRouteList(routeData);
        dispatch.sendNumber(openAirport.getID());
        Packet response = talkToServer(dispatch);
        if (response.receiveCondition()) {
            guiWindow.printLog(MessageType.SUCCESS,"Updated Routes in Server");
        } else {
            guiWindow.printLog(MessageType.FAILURE,"Updating Routes in Server");
        }
    }
    //endregion


    //region SEND ROUTES TO PRINTER
    /**
     * Send a Table to the Printer.
     * <p>
     * Given a JTable, this method sends
     * the table to a printer.
     *
     * @param guiTable The Table that Is Sent to the Printer.
     */
    public void printRoutes(JTable guiTable) {
        PrintRequestAttributeSet set = new HashPrintRequestAttributeSet();
        set.add(OrientationRequested.LANDSCAPE);
        MessageFormat header = new MessageFormat("ATC Routes for " + openAirport.getName() + " at "+ (LocalDateTime.now().plusHours((long) openAirport.getUTCoffset())));
        MessageFormat footer = new MessageFormat("IATA:["+new String(openAirport.getIATA())+"], ICAO:["+new String(openAirport.getICAO())+"]");
        try {
            guiTable.print(JTable.PrintMode.FIT_WIDTH, header, footer, false, set, false);
            guiWindow.printLog(MessageType.SUCCESS,"Printed Open Routes");
        } catch (PrinterException e) {
            e.printStackTrace();
            guiWindow.printLog(MessageType.ERROR,"(Printing) "+e);
        }

    }
    //endregion


    //region VARIABLE ACCESS METHODS
    /**
     * Returns Whether User is in Edit Mode.
     *
     * @return Whether Edit Mode is Active or Not.
     */
    public boolean getEditMode() {return editMode;}
    /**
     * Modifies the Edit Mode.
     *
     * @param mode (True/False).
     */
    public void setEditMode(boolean mode) {editMode = mode;}
    /**
     * Returns the Local List of Routes.
     *
     * @return A List of Route Objects.
     */
    public List<Route> getAppData() {return routeData;}
    /**
     * Modifies the Local List of Routes.
     *
     * @param tableData A List of Route Objects.
     */
    public void setAppData(List<Route> tableData) {routeData = tableData;}
    //endregion



    //region MAIN
    @SuppressWarnings("CStyleArrayDeclaration")
    public static void main(String args[]) {
        //Create a New Application and GUI for it:
        Application clientApplication = new Application();
        clientApplication.guiWindow = new Interface(clientApplication);
        //Connect to Server and Download Available Airports:
        while (!clientApplication.connectServer()) {
            clientApplication.guiWindow.printLog(MessageType.FAILURE,"Connection Failed, Retrying in 3...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            clientApplication.guiWindow.printLog(MessageType.FAILURE,"Connection Failed, Retrying in 2...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            clientApplication.guiWindow.printLog(MessageType.FAILURE,"Connection Failed, Retrying in 1...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        clientApplication.launchData = (clientApplication.talkToServer(new Packet(Command.FETCH_ALL_AIRPORTS))).receiveAirportList();
        //Display the Available Airports and Enable Search:
        clientApplication.guiWindow.tableDisplayAirports(clientApplication.launchData);
        clientApplication.guiWindow.printLog(MessageType.SUCCESS,"Connected, Ready to Select an Airport");
        clientApplication.guiWindow.enableSearchFeature(clientApplication);

    }
    //endregion
}
