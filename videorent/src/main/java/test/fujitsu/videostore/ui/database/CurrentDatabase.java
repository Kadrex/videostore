package test.fujitsu.videostore.ui.database;

import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import test.fujitsu.videostore.backend.database.Database;
import test.fujitsu.videostore.backend.database.DatabaseFactory;

public final class CurrentDatabase {

    private static final String CURRENT_DATABASE_ATTRIBUTE_KEY = CurrentDatabase.class.getCanonicalName();

    private CurrentDatabase() {
    }

    /**
     * Get's current database instance from session
     *
     * @return Database instance
     */
    public static Database get() {
        return (Database) getCurrentRequest().getWrappedSession().getAttribute(CURRENT_DATABASE_ATTRIBUTE_KEY);
    }

    /**
     * Creates database instance and set's it to current active session
     *
     * @param databasePath database file path
     */
    public static void set(String databasePath) {
        if (databasePath == null) {
            getCurrentRequest().getWrappedSession().removeAttribute(CURRENT_DATABASE_ATTRIBUTE_KEY);
        } else {
            getCurrentRequest().getWrappedSession().setAttribute(CURRENT_DATABASE_ATTRIBUTE_KEY, DatabaseFactory.from(databasePath));
        }
    }

    private static VaadinRequest getCurrentRequest() {
        VaadinRequest request = VaadinService.getCurrentRequest();
        if (request == null) {
            throw new IllegalStateException(
                    "No request bound to current thread.");
        }
        return request;
    }
}
