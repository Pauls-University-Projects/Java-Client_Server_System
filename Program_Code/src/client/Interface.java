package client;



//region LIBRARIES USED
//Java Libraries:
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
//Our Classes:
import data.*;
//endregion



/**
 * @author  Paul Johannes Aru
 * @version 0.9
 * @since   2021-03-15
 */
public class Interface{
    //region GUI ELEMENT VARIABLES
    private JComboBox<String> airportParameter;
    private JTextField airportField;
    private JButton searchButton;
    private JTable table;
    private JPanel mainPanel;
    private JLabel logLabel;
    private JButton editButton;
    private JButton addButton;
    private JButton removeButton;
    private JLabel airportLabel;
    private JButton saveButton;
    private JButton printButton;
    private final UniversalTableModel guiTable;
    //endregion



    //region INTERFACE CONSTRUCTOR
    /**
     * Creates a new GUI JFrame for given ATC Application.
     * <p>
     * Provided an Air Traffic Control Client Application,
     * Creates a new Graphical User Interface, based on
     * JFrame.
     *
     * @param  referenceApplication Client Application.
     * @return New GUI Window.
     */
    public Interface(Application referenceApplication) {
        final JFrame clientWindow = new JFrame("Air Traffic Information");
        // Initialise the Search Parameter Selector:
        final DefaultComboBoxModel<String> parameters = new DefaultComboBoxModel<>();
        parameters.addElement(SearchCategories.NAME.toString());
        parameters.addElement(SearchCategories.IATA.toString());
        parameters.addElement(SearchCategories.ICAO.toString());
        airportParameter.setModel(parameters);
        // Initialise the Table:
        guiTable = new UniversalTableModel();
        table.setModel(guiTable);
        // Clear Search Box on Click:
        airportField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (!airportField.getForeground().equals(Color.BLACK)) {
                    super.focusGained(e);
                    airportField.setText("");
                    airportField.setForeground(Color.BLACK);
                }
            }
        });
        // Attempt to Safely Disconnect from Server when Window is Closed:
        clientWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                referenceApplication.disconnectServer();
                System.exit(0);
            }
        });
        // Display and Re-Size Contents:
        clientWindow.add(mainPanel);
        clientWindow.setVisible(true);
        clientWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        clientWindow.pack();
    }
    //endregion



    //region TABLE FILLING METHODS
    /**
     * Fills the table with a list of Airports.
     * <p>
     * Takes a list of Airports and converts it to
     * the expected structure before filling out the
     * Table in our Interface.
     *
     * @param  data List of Airport objects.
     */
    public void tableDisplayAirports(List<Airport> data) {
        List<String[]> expectedFormat = new ArrayList<>();
        String[] titles = {"ID","Name","IATA","ICAO"};
        for (int item=0; item<data.size();item++) {
            expectedFormat.add(data.get(item).toStringArray());
        }
        guiTable.fillTable(expectedFormat, titles);
        guiTable.refreshTable();
    }
    /**
     * Fills the table with a list of Routes.
     * <p>
     * Takes a list of Routes and converts it to
     * the expected structure before filling out the
     * Table in our Interface.
     *
     * @param  data List of Route objects.
     */
    public void tableDisplayRoutes(List<Route> data) {
        List<String[]> expectedFormat = new ArrayList<>();
        String[] titles = {"Route ID","Expected Aircraft(s)","Airline","Callsign","Direction","From/To Airport","Airport IATA","Airport ICAO","Connecting City","Connecting Country"};
        for (int item=0; item<data.size();item++) {
            expectedFormat.add(data.get(item).toStringArray());
        }
        guiTable.fillTable(expectedFormat, titles);
        guiTable.refreshTable();
    }
    //endregion


    //region TABLE ROUTES READING METHOD
    /**
     * Reads the Table Contents into a Routes List.
     * <p>
     * Takes the Raw Contents of our Table and
     * converts it into a List of Route objects
     * before returning. Requires a Reference
     * list of Routes to match up with, since the
     * table does not display all of the parameters.
     *
     * @param  appData Reference List of Route objects.
     * @return List of Route objects.
     */
    public List<Route> tableReadRoutes(List<Route> appData) {
        ArrayList<String[]> tableFormat =  guiTable.getTableContents();
        for (int item=0; item<tableFormat.size(); item++) {
            if (appData.get(item).getID() == Integer.parseInt(tableFormat.get(item)[0])) {
                appData.get(item).setAircrafts(tableFormat.get(item)[1]);
                appData.get(item).getAirline().setName(tableFormat.get(item)[2]);
                appData.get(item).getAirline().setCallsign(tableFormat.get(item)[3]);
                if (tableFormat.get(item)[4] != null) {
                    if (tableFormat.get(item)[4].toLowerCase().equals("arriving")) {
                        appData.get(item).setArriving(true);
                    } else if (tableFormat.get(item)[4].toLowerCase().equals("departing")) {
                        appData.get(item).setArriving(false);
                    }
                }
                appData.get(item).getConnectingAirport().setName(tableFormat.get(item)[5]);
                if (tableFormat.get(item)[6] != null) {
                    appData.get(item).getConnectingAirport().setIATA(tableFormat.get(item)[6].toUpperCase());}
                if (tableFormat.get(item)[7] != null) {
                    appData.get(item).getConnectingAirport().setICAO(tableFormat.get(item)[7].toUpperCase());}
                appData.get(item).getConnectingAirport().setCity(tableFormat.get(item)[8]);
                appData.get(item).getConnectingAirport().setCountry(tableFormat.get(item)[9]);
            } else {
                printLog(MessageType.FAILURE,"Table Data Mismatched Application Data"); //This might happen if the user moves rows around, might get around it by sorting beforehand
            }
        }
        return appData;
    }
    //endregion


    //region TABLE MODIFYING METHODS
    /**
     * Removes Given Row from Table.
     * <p>
     * Given a row number, our table is modified
     * to remove the row.
     *
     * @param  row Row Number (Not ID).
     */
    public void tableRemoveRow(int row) {
        if (guiTable.removeRow(row)) {
            guiTable.refreshTable();
            printLog(MessageType.STATUS,"Removed Row "+row+" from Table");
        } else {
            printLog(MessageType.FAILURE,"Could not Remove Row "+row+" from Table");
        }
    }
    /**
     * Adds a New Row to our Table.
     * <p>
     * Creates and Adds a New Row to the Table.
     *
     * @return The Row Number for the New Row.
     */
    public int tableAddRow() {
        int rowPosition = guiTable.addRow();
        printLog(MessageType.STATUS,"Created New Item at Row "+rowPosition+" in Table");
        guiTable.refreshTable();
        return rowPosition;
    }
    /**
     * Modifies a Cell in our Table
     * <p>
     * Given a Column and Row Value, along with the new
     * value, this method Modifies the Table to display
     * the given value at that position instead.
     *
     * @param row Table Row Number (Not ID).
     * @param column Table Column Number.
     * @param value The New String Value.
     */
    public void tableModifyValue(int row, int column, String value) {
        if (guiTable.modifyValue(row,column,value)) {
            guiTable.refreshTable();
            printLog(MessageType.STATUS,"Set [Row: "+row+", Column: "+column+"] to "+value+" in Table");
        } else {
            printLog(MessageType.FAILURE,"Could not Modify [Row: "+row+", Column: "+column+"] in Table");
        }
    }
    //endregion


    //region LABEL UPDATING METHODS
    /**
     * Displays the Given Status Message.
     * <p>
     * Given a String, it is Displayed on the
     * log label in our Window for the User to
     * see.
     *
     * @param  text String Message.
     */
    public void printLog(MessageType type, String text) {
        logLabel.setForeground(type.colour());
        logLabel.setFont(type.font());
        logLabel.setText(type.startString()+text+type.endString());
    }


    /**
     * Displays the Given Airport.
     * <p>
     * Given a String, it is Displayed on the
     * Airport label in our Window for the User
     * to see.
     *
     * @param  text String Message.
     */
    public void setAirportLabel(String text) {airportLabel.setText(text);}
    //endregion


    //region ENABLE SEARCH METHOD
    /**
     * Enables the Search Tools in our GUI.
     * <p>
     * This method is designed to prevent the user
     * from accessing search tools before we have
     * established a connection to the server.
     *
     * @param  referenceApplication Client Application.
     */
    public void enableSearchFeature(Application referenceApplication) {
        airportParameter.setEnabled(true);
        airportField.setEnabled(true);
        searchButton.setEnabled(true);
        //Search Triggered By Button:
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean airportFound = referenceApplication.matchAirport(airportParameter.getSelectedItem().toString(),airportField.getText());
                //Searched Airport Exists, Fetching Routes:
                if (airportFound) {
                    referenceApplication.requestRoutes();
                    enableRouteFeatures(referenceApplication);
                }
            }
        });
        //Search Triggered By Enter Key:
        airportField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER) {
                    boolean airportFound = referenceApplication.matchAirport(airportParameter.getSelectedItem().toString(),airportField.getText());
                    //Searched Airport Exists, Fetching Routes:
                    if (airportFound) {
                        referenceApplication.requestRoutes();
                        enableRouteFeatures(referenceApplication);
                    }
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }
    //endregion


    //region ENABLE ROUTE-TOOLS METHOD
    /**
     * Enables the Route Tools in our GUI.
     * <p>
     * This method is designed to prevent the user
     * from accessing route tools before we have
     * displayed the requested routes.
     *
     * @param  referenceApplication Client Application.
     */
    public void enableRouteFeatures(Application referenceApplication) {
        editButton.setEnabled(true);
        printButton.setEnabled(true);
        //Edit Mode Triggered:
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!referenceApplication.getEditMode()) {
                    referenceApplication.setEditMode(true);
                    editButton.setText("CANCEL");
                    addButton.setEnabled(true);
                    removeButton.setEnabled(true);
                    saveButton.setEnabled(true); saveButton.setForeground(Color.RED);
                    guiTable.setEditMode(true);
                    printLog(MessageType.STATUS,"Editing Mode Enabled");
                    enableEditMode(referenceApplication);
                } else {
                    referenceApplication.setEditMode(false);
                    editButton.setText("EDIT");
                    addButton.setEnabled(false);
                    removeButton.setEnabled(false);
                    saveButton.setEnabled(false); saveButton.setForeground(Color.BLACK);
                    guiTable.setEditMode(false);
                    printLog(MessageType.STATUS,"Editing Mode Disabled");
                }

            }
        });
        //Print Triggered:
        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                referenceApplication.printRoutes(table);
            }
        });
    }
    //endregion


    //region ENABLE EDITING METHOD
    /**
     * Enables Route Editing in our GUI.
     * <p>
     * This method is designed to prevent the user
     * from accessing route editing tools before
     * they have confirmed they wish to edit.
     *
     * @param  referenceApplication Client Application.
     */
    public void enableEditMode(Application referenceApplication) {
        //Add Route Triggered:
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                referenceApplication.addRoute();
                editButton.setText("CANCEL");
            }
        });
        //Remove Route Triggered:
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int routeRow = table.getSelectedRow();
                if (routeRow != -1) {
                    referenceApplication.removeRoute(routeRow);
                    editButton.setText("CANCEL");
                } else {
                    printLog(MessageType.FAILURE,"Please Select a Route Before Clicking Remove");
                }
            }
        });
        //Server Synchronising Triggered:
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                referenceApplication.setAppData(tableReadRoutes(referenceApplication.getAppData()));
                referenceApplication.saveChanges();
                editButton.setText("CLOSE");
            }
        });
    }
    //endregion
}
