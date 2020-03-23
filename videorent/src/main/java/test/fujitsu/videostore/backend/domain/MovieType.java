package test.fujitsu.videostore.backend.domain;

/**
 * Movie type
 * According that movie rent price should be calculated
 */
public enum MovieType {

    NEW(1, "New release"),
    REGULAR(2, "Regular rental"),
    OLD(3, "Old film");

    /**
     * Movie type representation in database
     */
    private final int databaseId;

    /**
     * Textural representation in database
     */
    private final String textualRepresentation;

    MovieType(int databaseId, String textualRepresentation) {
        this.databaseId = databaseId;
        this.textualRepresentation = textualRepresentation;
    }

    public String getTextualRepresentation() {
        return textualRepresentation;
    }

    public int getDatabaseId() {
        return databaseId;
    }
}
