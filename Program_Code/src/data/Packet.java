package data;



// LIBRARIES USED
import java.io.Serializable;
import java.util.List;



/**
 * @author  Paul Johannes Aru
 * @version 0.9
 * @since   2021-03-23
 */
public class Packet implements Serializable {
    //region PACKETABLE OBJECTS
    private Command command;

    private List<Airport> airportList;
    private List<Route> routesList;
    private int number;
    private String text;
    private boolean condition;
    //endregion



    //region PACKET CONSTRUCTORS
    /**
     * Creates a new Packet Object.
     *
     * @return New Packet Object.
     */
    public Packet() {}
    /**
     * Creates a new Packet Object.
     * <p>
     * Provided a Server Command, this
     * constructor returns a New Packet Object.
     *
     * @param  onlyCommand Server Command.
     * @return New Packet Object.
     */
    public Packet(Command onlyCommand) {
        command = onlyCommand;
    }
    //endregion



    //region PACKET OBJECT ACCESS METHODS
    public Command command() {return command;}

    /**
     * Attaches an Airport List to Packet.
     *
     * @param list Airport Object List.
     */
    public void sendAirportList(List<Airport> list) {
        airportList = list;
    }
    /**
     * Detaches an Airport List from Packet.
     *
     * @return Airport Object List.
     */
    public List<Airport> receiveAirportList() {
        return airportList;
    }

    /**
     * Attaches a Route List to Packet.
     *
     * @param list Route Object List.
     */
    public void sendRouteList(List<Route> list) {
        routesList = list;
    }
    /**
     * Detaches a Route List from Packet.
     *
     * @return Route Object List.
     */
    public List<Route> receiveRouteList() {
        return routesList;
    }

    /**
     * Attaches an Integer to Packet.
     *
     * @param value Number Integer.
     */
    public void sendNumber(int value) {number = value;}
    /**
     * Detaches an Integer from Packet.
     *
     * @return Number Integer.
     */
    public int receiveNumber() {return number;}

    /**
     * Attaches a String to Packet.
     *
     * @param string Text String.
     */
    public void sendText(String string) {text = string;}
    /**
     * Detaches a String from Packet.
     *
     * @return Text String.
     */
    public String receiveText() {return text;}

    /**
     * Attaches a Boolean to Packet.
     *
     * @param option Option Boolean.
     */
    public void sendCondition(boolean option) {condition = option;}
    /**
     * Detaches a Boolean from Packet.
     *
     * @return Option Boolean.
     */
    public boolean receiveCondition() {return condition;}
    //endregion
}