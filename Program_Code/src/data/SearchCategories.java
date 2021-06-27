package data;



/**
 * @author  Paul Johannes Aru
 * @version 0.9
 * @since   2021-04-14
 */
public enum SearchCategories {
    // CATEGORIES:
    NAME,
    IATA,
    ICAO;

    // USER FORMAT:
    public String toString() {
        switch (this) {
            case NAME:  return "Airport Name";
            case IATA:  return "IATA Code";
            case ICAO:  return "ICAO Code";
            default:    return "Non-Existent";
        }
    }
}
