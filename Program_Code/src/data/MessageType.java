package data;

import java.awt.*;

/**
 * @author  Paul Johannes Aru
 * @version 0.9
 * @since   2021-04-14
 */
public enum MessageType {
    // TYPES:
    STATUS,
    SUCCESS,
    FAILURE,
    ERROR;


    //region MESSAGE WRAPPERS
    public String startString() {
        switch (this) {
            case STATUS:    return "Status: ";
            case SUCCESS:   return "SUCCESS: ";
            case FAILURE:   return "FAILURE: ";
            case ERROR:     return "ERROR: ";
            default:        return "";
        }
    }
    public String endString() {
        switch (this) {
            case STATUS:    return ".";
            case SUCCESS:   return "!";
            case FAILURE:   return "!";
            case ERROR:     return "!!!";
            default:        return "";
        }
    }
    //endregion


    //region FORMATS
    public Font font() {
        switch (this) {
            case STATUS:    return new Font("Courier", Font.PLAIN, 12);
            case SUCCESS:   return new Font("Courier", Font.PLAIN, 12);
            case FAILURE:   return new Font("Courier", Font.PLAIN, 12);
            case ERROR:     return new Font("Courier", Font.BOLD, 12);
            default:        return new Font("Courier", Font.PLAIN, 12);
        }
    }
    public Color colour() {
        switch (this) {
            case STATUS:    return Color.BLACK;
            case SUCCESS:   return new Color(0,153,51);
            case FAILURE:   return new Color(255,153,0);
            case ERROR:     return Color.RED;
            default:        return Color.BLACK;
        }
    }
    //endregion
}
