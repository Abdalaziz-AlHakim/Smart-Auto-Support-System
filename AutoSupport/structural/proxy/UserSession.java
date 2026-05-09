package structural.proxy;

/**
 * Singleton that tracks the currently logged-in user and their role.
 *
 * PATTERN: Proxy (support class)
 * TicketSystemProxy queries this singleton on every method call to determine
 * whether the current user has permission to perform the requested operation.
 *
 * This is itself a Singleton because there is always exactly one "current user"
 * session in a single-user desktop application.
 */
public class UserSession {

    private static UserSession instance;

    /** The role selected at login. Defaults to the lowest privilege level. */
    private UserRole role = UserRole.AGENT_L1;

    private UserSession() {}

    /**
     * @return The single UserSession instance for this application run.
     */
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    /**
     * Set the active user's role, called from the login panel in MainGUI.
     *
     * @param role The role the user selected at login.
     */
    public void setRole(UserRole role) {
        this.role = role;
    }

    /**
     * @return The role of the currently logged-in user.
     *         TicketSystemProxy uses this to decide if an operation is allowed.
     */
    public UserRole getRole() {
        return role;
    }
}
