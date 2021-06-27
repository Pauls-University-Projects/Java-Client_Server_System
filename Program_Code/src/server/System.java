package server;



//region LIBRARIES USED
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
//endregion



/**
 * @author  Paul Johannes Aru
 * @version 0.9
 * @since   2021-03-16
 */
public class System {
    //region SYSTEM PARAMETERS
    //Constants:
    private static final String DATABASE_NAME = "flight-data.sqlite";
    //Variables:
    private static List<Integer> reservedRouteIDs;
    //endregion



    //region SQL ACCESS
    /**
     * Establishes a Connection to SQLite Database.
     * <p>
     * Attempts to Establish a Connection to the SQLite
     * Database. Database File Name is Specified in the
     * Class Constant called DATABASE_NAME.
     *
     * @return SQLite Connection.
     */
    private static Connection accessDatabase() throws SQLException {
        Connection conn = DriverManager.getConnection(("jdbc:sqlite:database/"+DATABASE_NAME), "", "");
        java.lang.System.out.println("Accessing Database "+DATABASE_NAME+"...");
        return conn;
    }
    //endregion


    //region DATABASE PULL DATA
    /**
     * Request Information from the Database.
     * <p>
     * Given an SQL Command in String format, the
     * method returns an ArrayList of Rows, that in
     * and on itself is an ArrayList of String Cells,
     * corresponding to the Columns Requested.
     *
     * @param sqlCommand SQL Command String.
     * @return An ArrayList Matrix of String Components.
     */
    public synchronized static ArrayList<ArrayList<String>> databasePull(String sqlCommand) {
        ArrayList<ArrayList<String>> fetchedData = new ArrayList<ArrayList<String>>();
        long logTime = java.lang.System.currentTimeMillis();
        int logItems = 0;

        try (Connection conn = accessDatabase();
            PreparedStatement prep = conn.prepareStatement(sqlCommand)) {
            ResultSet dataLine = prep.executeQuery();
            int columns = dataLine.getMetaData().getColumnCount()+1;
            while (dataLine.next()) {
                ArrayList<String> row = new ArrayList<>();
                for (int column=1;column<columns;column++) {
                    row.add(dataLine.getString(column));
                }
                fetchedData.add(row);
                logItems++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(System.class.getName()).log(Level.SEVERE, null, ex);
        }

        java.lang.System.out.println("Done. Fetched "+logItems+" items in "+(java.lang.System.currentTimeMillis()-logTime)+"ms!");
        return fetchedData;
    }
    //endregion


    //region DATABASE PUSH DATA
    /**
     * Dispatch Information to the Database.
     * <p>
     * Given an SQL Command in String format, the
     * method can either add or modify Database
     * information.
     *
     * @param sqlCommand SQL Command String.
     */
    public synchronized static void databasePush(String sqlCommand) {
        try (Connection conn = accessDatabase(); // auto close the connection object after try
            PreparedStatement prep = conn.prepareStatement(sqlCommand)) {
            prep.execute();
        } catch (SQLException ex) {
            Logger.getLogger(System.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //endregion


    //region SERVER COMMUNICATIONS
    /**
     * Endless Loop that Handles Client-Thread Assignment.
     * <p>
     * Opens a new Socket for the Server. When a Client
     * Connects, a New Thread will be Created to handle
     * the client and the Server returns to Waiting for
     * New Clients.
     */
    @SuppressWarnings("InfiniteLoopStatement")
    private static void runServer() {
        java.lang.System.out.println("Server: Launched System.");
        reservedRouteIDs = new ArrayList<>();

        try (ServerSocket serverSocket = new ServerSocket(2000)) {
            //noinspection InfiniteLoopStatement
            while (true) {
                java.lang.System.out.println("Server: Ready for Clients.");
                try {
                    Socket socket = serverSocket.accept();
                    SystemThread clientThread = new SystemThread(socket);
                    Thread connectionThread = new Thread(clientThread);
                    connectionThread.start();
                } catch (IOException ex) {
                    java.lang.System.out.println("ERROR: Server Failed to Connect to a Client!!!");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(System.class.getName()).log(Level.SEVERE, null, ex);
            java.lang.System.out.println("Server: Halted System.");
        }
    }
    //endregion


    //region PARAMETER ACCESS METHODS
    /**
     * Returns Reserved Route IDs.
     *
     * @return ID Integer List.
     */
    public synchronized static List<Integer> returnReservedRouteIDs() {return reservedRouteIDs;}
    /**
     * Reserves a Route ID.
     *
     * @param id ID Integer.
     */
    public synchronized static void reserveRouteID(int id) {reservedRouteIDs.add(id);}
    //endregion



    // MAIN:
    public static void main(@SuppressWarnings("CStyleArrayDeclaration") String args[]) {
        runServer();
    }
}
