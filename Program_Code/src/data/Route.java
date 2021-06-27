package data;



// LIBRARIES USED
import java.io.Serializable;



/**
 * @author  Paul Johannes Aru
 * @version 0.9
 * @since   2021-03-27
 */
public class Route implements Serializable {
    //region ROUTE PARAMETERS
    private Airline carrier;
    private Airport connection;
    private int id = -1;
    private boolean arriving = false;
    private boolean codeshare = false;
    private int stops = 0; //0 Indicates Direct Flight
    private String aircrafts = "";
    //endregion



    //region ROUTE CONSTRUCTOR
    /**
     * Creates a new Route Object.
     * <p>
     * Provided an ID-Number, this constructor
     * returns a New Empty Route.
     *
     * @param  idNumber ID Number in String format.
     * @return New Route Object.
     */
    public Route(String idNumber) {
        carrier = new Airline(-1);
        connection = new Airport();
        id = Integer.parseInt(idNumber);
    }
    //endregion



    //region PARAMETER ACCESS METHODS
    /**
     * Modifies the Route Airline.
     *
     * @param object Carrier Airline.
     */
    public void setAirline(Airline object) {
        carrier = object;}
    /**
     * Returns the Route Airline.
     *
     * @return Carrier Airline.
     */
    public Airline getAirline() {return carrier;}

    /**
     * Modifies the Airport that Route Connects.
     *
     * @param object Connecting Airport.
     */
    public void setConnectingAirport(Airport object) {
        connection = object;}
    /**
     * Returns the Airport that Route Connects.
     *
     * @return Connecting Airport.
     */
    public Airport getConnectingAirport() {return connection;}

    /**
     * Modifies the Route ID Number.
     *
     * @param idNumber ID Integer.
     */
    public void setID(int idNumber) {id = idNumber;}
    /**
     * Returns the Route ID Number.
     *
     * @return ID Integer.
     */
    public int getID() {
        return id;
    }

    /**
     * Modifies Whether the Route is Departing/Arriving.
     *
     * @param option Condition Boolean.
     */
    public void setArriving(Boolean option) {arriving = option;}
    /**
     * Returns Whether the Route is Departing/Arriving.
     *
     * @return Condition Boolean.
     */
    public boolean getArriving() {return arriving;}
    /**
     * Returns Whether the Route is Departing/Arriving.
     *
     * @return ("Arriving"/"Departing").
     */
    public String getArrivingString() {
        if (arriving) { return "Arriving"; } else { return "Departing"; }
    }

    /**
     * Modifies Whether the Route is a Codeshare Agreement.
     *
     * @param option Condition Boolean.
     */
    public void setCodeshare(Boolean option) {codeshare = option;}
    /**
     * Returns Whether the Route is a Codeshare Agreement.
     *
     * @return Condition Boolean.
     */
    public boolean getCodeshare() {return codeshare;}

    /**
     * Modifies the Route Stops Number.
     * (Where 0 Indicates a Direct Flight)
     *
     * @param stopsNumber Stops Integer.
     */
    public void setStops(int stopsNumber) {stops = stopsNumber;}
    /**
     * Returns the Route Stops Number.
     * (Where 0 Indicates a Direct Flight)
     *
     * @return Stops Integer.
     */
    public int getStops() {
        return stops;
    }

    /**
     * Modifies the Route Aircraft(s).
     *
     * @param aircraftsText Aircraft(s) String.
     */
    public void setAircrafts(String aircraftsText) {if (aircraftsText!=null) {aircrafts = aircraftsText;}}
    /**
     * Returns the Route Aircraft(s).
     *
     * @return Aircraft(s) String.
     */
    public String getAircrafts() {return aircrafts;}
    //endregion


    //region TEXTUAL REPRESENTATION METHODS
    /**
     * Returns a String Array of Route Parameters.
     * <p>
     * This could be used for displaying the Route
     * on a Table.
     *
     * @return Info String Array.
     */
    public String[] toStringArray() {
        return new String[]{String.valueOf(id), aircrafts , carrier.getName(), carrier.getCallsign(), getArrivingString(), connection.getName(), new String(connection.getIATA()), new String(connection.getICAO()), connection.getCity(), connection.getCountry()};
    }
    //endregion


    //region COMPARISON METHOD
    public boolean equals(Route secondary) {
        if (id == secondary.id &&
            arriving == secondary.arriving &&
            codeshare == secondary.codeshare &&
            stops == secondary.stops &&
            aircrafts.toString().equals(secondary.aircrafts.toString())) {
            if (connection.getID() == secondary.connection.getID() &&
                    connection.getName().equals(secondary.connection.getName()) &&
                    connection.getCity().equals(secondary.connection.getCity()) &&
                    connection.getCountry().equals(secondary.connection.getCountry()) &&
                    connection.getIATA()[0]==secondary.connection.getIATA()[0] && // For some oddball reason this is the only way I could get the arrays to match
                    connection.getIATA()[1]==secondary.connection.getIATA()[1] &&
                    connection.getIATA()[2]==secondary.connection.getIATA()[2] &&
                    connection.getICAO()[0]==secondary.connection.getICAO()[0] &&
                    connection.getICAO()[1]==secondary.connection.getICAO()[1] &&
                    connection.getICAO()[2]==secondary.connection.getICAO()[2] &&
                    connection.getICAO()[3]==secondary.connection.getICAO()[3] &&
                    connection.getLatitude() == secondary.connection.getLatitude() &&
                    connection.getLongitude() == secondary.connection.getLongitude() &&
                    connection.getAltitude() == secondary.connection.getAltitude() &&
                    connection.getUTCoffset() == secondary.connection.getUTCoffset() &&
                    connection.getDSTregion() == secondary.connection.getDSTregion() &&
                    connection.getTimeZoneRegion().equals(secondary.connection.getTimeZoneRegion())) {
                if (carrier.getID() == secondary.carrier.getID() &&
                        carrier.getName().equals(secondary.carrier.getName()) &&
                        carrier.getAlias().equals(secondary.carrier.getAlias()) &&
                        carrier.getIATA()[0]==secondary.carrier.getIATA()[0] && // For some oddball reason this is the only way I could get the arrays to match
                        carrier.getIATA()[1]==secondary.carrier.getIATA()[1] &&
                        carrier.getICAO()[0]==secondary.carrier.getICAO()[0] &&
                        carrier.getICAO()[1]==secondary.carrier.getICAO()[1] &&
                        carrier.getICAO()[2]==secondary.carrier.getICAO()[2] &&
                        carrier.getCallsign().equals(secondary.carrier.getCallsign()) &&
                        carrier.getCountry().equals(secondary.carrier.getCountry()) &&
                        carrier.isActive() == secondary.carrier.isActive()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    //endregion
}
