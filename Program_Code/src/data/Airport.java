package data;



// LIBRARIES USED
import java.io.Serializable;



/**
 * @author  Paul Johannes Aru
 * @version 0.9
 * @since   2021-03-15
 */
public class Airport implements Serializable {
    //region AIRPORT PARAMETERS
    private int id = 0;
    private String name = "";
    private String city = "";
    private String country = "";
    private char[] iataCode = {' ',' ',' '}; //International Air Transport Association
    private char[] icaoCode = {' ',' ',' ',' '}; //International Civil Aviation Organization (U.N.)
    private float latitude = 0; //(Negative = South, Positive = North) Interesting Article on Preferable Data-Types for Coordinates: https://sqlsouth.wordpress.com/2013/11/12/storing-latitude-and-longitude-coordinates/
    private float longitude = 0; //(Negative = West, Positive = East)
    private float altitude = 0; //From Sea Level
    private float utcTimeOffset = 0; //Hours from UTC
    private char dstRegion = 'U'; //Daylight Savings Time Region (E = Europe, A = North America, S = South America, O = Australia, Z = New Zealand, N = None, U = Unknown)
    private String timeZoneRegion = ""; //"tz" Olson Format
    //endregion



    //region AIRPORT CONSTRUCTORS
    /**
     * Creates a new Airport Object.
     * <p>
     * Provided an ID-Number, this constructor
     * returns a New Empty Airport.
     *
     * @param  idNumber ID Number in String format.
     * @return New Airport Object.
     */
    public Airport(String idNumber) {
        id = Integer.parseInt(idNumber);
    }
    /**
     * Creates a new Airport Object.
     *
     * @return New Airport Object.
     */
    public Airport() {}
    //endregion



    //region PARAMETER ACCESS METHODS
    /**
     * Modifies the Airport ID Number.
     *
     * @param idNumber ID Integer.
     */
    public void setID(int idNumber) {id = idNumber;}
    /**
     * Returns the Airport ID Number.
     *
     * @return ID Integer.
     */
    public int getID() {
        return id;
    }

    /**
     * Modifies the Airport Name.
     *
     * @param nameText Name String.
     */
    public void setName(String nameText) { if (nameText!=null) {name = nameText;}}
    /**
     * Returns the Airport Name.
     *
     * @return Name String.
     */
    public String getName() {
        return name;
    }

    /**
     * Modifies the Airport City.
     *
     * @param cityText City String.
     */
    public void setCity(String cityText) { if (cityText!=null) {city = cityText;} }
    /**
     * Returns the Airport City.
     *
     * @return City String.
     */
    public String getCity() {
        return city;
    }

    /**
     * Modifies the Airport Country.
     *
     * @param countryText Country String.
     */
    public void setCountry(String countryText) { if (countryText!=null) {country = countryText;} }
    /**
     * Returns the Airport Country.
     *
     * @return Country String.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Modifies the Airport IATA Code.
     *
     * @param code IATA String.
     */
    public void setIATA(String code) {
        if (code != null && code.length() == 3) {
            iataCode = code.toCharArray();
        }
    }
    /**
     * Returns the Airport IATA Code.
     *
     * @return IATA 3 Character Array.
     */
    public char[] getIATA() { return iataCode; }

    /**
     * Modifies the Airport ICAO Code.
     *
     * @param code ICAO String.
     */
    public void setICAO(String code) {
        if (code != null && code.length() == 4) {
            icaoCode = code.toCharArray();
        }
    }
    /**
     * Returns the Airport ICAO Code.
     *
     * @return ICAO 4 Character Array.
     */
    public char[] getICAO() { return icaoCode; }

    /**
     * Modifies the Airport Latitude Coordinates.
     *
     * @param latitudeNumber Latitude Float.
     */
    public void setLatitude(float latitudeNumber) {latitude = latitudeNumber;}
    /**
     * Returns the Airport Latitude Coordinates.
     *
     * @return Latitude Float.
     */
    public float getLatitude() {return latitude;}

    /**
     * Modifies the Airport Longitude Coordinates.
     *
     * @param longitudeNumber Longitude Float.
     */
    public void setLongitude(float longitudeNumber) {longitude = longitudeNumber;}
    /**
     * Returns the Airport Longitude Coordinates.
     *
     * @return Longitude Float.
     */
    public float getLongitude() {return longitude;}

    /**
     * Modifies the Airport Altitude Coordinates.
     *
     * @param altitudeNumber Altitude Float.
     */
    public void setAltitude(float altitudeNumber) {altitude = altitudeNumber;}
    /**
     * Returns the Airport Altitude Coordinates.
     *
     * @return Altitude Float.
     */
    public float getAltitude() {return altitude;}

    /**
     * Modifies the Airport Timezone Offset from UTC.
     *
     * @param timeOffset Hours Float.
     */
    public void setUTCoffset(float timeOffset) {utcTimeOffset = timeOffset;}
    /**
     * Returns the Airport Timezone Offset from UTC.
     *
     * @return Hours Float.
     */
    public float getUTCoffset() {return utcTimeOffset;}

    /**
     * Modifies the Airport Daylights Saving Time Region.
     * <p>
     * (E = Europe, A = North America, S = South America,
     * O = Australia, Z = New Zealand, N = None, U = Unknown)
     *
     * @param region ('E'/'A'/'S'/'O'/'Z'/'N'/'U') Character.
     */
    public void setDSTregion(char region) {dstRegion = region;}
    /**
     * Returns the Airport Daylights Saving Time Region.
     *<p>
     * (E = Europe, A = North America, S = South America,
     * O = Australia, Z = New Zealand, N = None, U = Unknown)
     *
     * @return ('E'/'A'/'S'/'O'/'Z'/'N'/'U') Character.
     */
    public float getDSTregion() {return dstRegion;}

    /**
     * Modifies the Airport Timezone Region.
     *
     * @param region Region String.
     */
    public void setTimeZoneRegion(String region) {if (timeZoneRegion!=null) {timeZoneRegion = region;}}
    /**
     * Returns the Airport Timezone Region.
     *
     * @return Region String.
     */
    public String getTimeZoneRegion() {return timeZoneRegion;}
    //endregion


    //region TEXTUAL REPRESENTATION METHODS
    /**
     * Returns a String that Identifies the Airport
     * <p>
     * Currently the String Representation contains
     * the ID, Name, IATA code and ICAO code.
     *
     * @return Info String.
     */
    public String toString() {
        return (getID() + ": " + getName() + ", IATA: " + new String(getIATA()) + ", ICAO: " + new String(getICAO()));
    }
    /**
     * Returns a String Array of Airport Parameters.
     * <p>
     * This could be used for displaying the Airport
     * on a Table.
     *
     * @return Info String Array.
     */
    public String[] toStringArray() {
        return new String[]{String.valueOf(id) , name, new String(iataCode) , new String(icaoCode)};
    }
    //endregion
}
