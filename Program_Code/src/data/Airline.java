package data;



// LIBRARIES USED
import java.io.Serializable;



/**
 * @author  Paul Johannes Aru
 * @version 0.9
 * @since   2021-04-01
 */
public class Airline implements Serializable {
    //region AIRLINE PARAMETERS
    private int id = 0;
    private String name = "";
    private String alias = "";
    private char[] iataCode = {' ',' '};
    private char[] icaoCode = {' ',' ',' '};
    private String callsign = "";
    private String country = "";
    private boolean active = false;
    //endregion



    //region AIRLINE CONSTRUCTORS
    /**
     * Creates a new Airline Object.
     * <p>
     * Provided an ID-Number, this constructor
     * returns a New Empty Airline.
     *
     * @param  idNumber ID Number in String format.
     * @return New Airline Object.
     */
    public Airline(String idNumber) {
        id = Integer.parseInt(idNumber);
    }
    /**
     * Creates a new Airline Object.
     * <p>
     * Provided an ID-Number, this constructor
     * returns a New Empty Airline.
     *
     * @param  idNumber ID Number in Integer format.
     * @return New Airline Object.
     */
    public Airline(int idNumber) {id = idNumber;}
    //endregion



    //region PARAMETER ACCESS METHODS
    /**
     * Modifies the Airline ID Number.
     *
     * @param idNumber ID Integer.
     */
    public void setID(int idNumber) {id = idNumber;}
    /**
     * Returns the Airline ID Number.
     *
     * @return ID Integer.
     */
    public int getID() {
        return id;
    }

    /**
     * Modifies the Airline Name.
     *
     * @param nameText Name String.
     */
    public void setName(String nameText) {if (nameText!=null) {name = nameText;}}
    /**
     * Returns the Airline Name.
     *
     * @return Name String.
     */
    public String getName() {return name;}

    /**
     * Modifies the Airline Alias.
     *
     * @param aliasText Alias String.
     */
    public void setAlias(String aliasText) {if (aliasText!=null) {alias = aliasText;}}
    /**
     * Returns the Airline Alias.
     *
     * @return Alias String.
     */
    public String getAlias() {return alias;}

    /**
     * Modifies the Airline IATA Code.
     *
     * @param code IATA Code String.
     */
    public void setIATA(String code) {
        if (code != null && code.length() == 2) {
            iataCode = code.toCharArray();
        }
    }
    /**
     * Returns the Airline IATA Code.
     *
     * @return IATA 2 Character Array.
     */
    public char[] getIATA() { return iataCode; }

    /**
     * Modifies the Airline ICAO Code.
     *
     * @param code ICAO Code String.
     */
    public void setICAO(String code) {
        if (code != null && code.length() == 3) {
            icaoCode = code.toCharArray();
        }
    }
    /**
     * Returns the Airline ICAO Code.
     *
     * @return ICAO 3 Character Array.
     */
    public char[] getICAO() { return icaoCode; }

    /**
     * Modifies the Airline Callsign.
     *
     * @param callsignText Callsign String.
     */
    public void setCallsign(String callsignText) {if (callsignText!=null) {callsign = callsignText;}}
    /**
     * Returns the Airline Callsign.
     *
     * @return Callsign String.
     */
    public String getCallsign() {return callsign;}

    /**
     * Modifies the Airline Home Country.
     *
     * @param countryText Country String.
     */
    public void setCountry(String countryText) {if (countryText!=null) {country = countryText;}}
    /**
     * Returns the Airline Home Country.
     *
     * @return Country String.
     */
    public String getCountry() {return country;}

    /**
     * Modifies the Airline Active State.
     *
     * @param value (True/False).
     */
    public void setActive(boolean value) {active = value;}
    /**
     * Returns the Airline Active State.
     *
     * @return (True/False).
     */
    public boolean isActive() {return active;}
    //endregion
}
