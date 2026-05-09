package structural.proxy;

import creational.factory.Ticket;
import creational.factory.TicketStatus;
import creational.singleton.TicketSystem;

import java.util.List;

/**
 * Security proxy that guards the real TicketSystem behind role-based checks.
 *
 * PATTERN: Proxy (Protection Proxy variant)
 * Both this class and TicketSystem implement ITicketSystem. The GUI and Facade
 * always hold an ITicketSystem reference — they never know whether they are
 * talking to the real system or the proxy. This lets the proxy intercept every
 * call, enforce access rules, and only then delegate to the real object.
 *
 * Why Proxy and not if-checks in the GUI?
 * GUI checks are scattered and easily bypassed. This proxy centralises ALL
 * access enforcement regardless of which component initiates the call.
 */
public class TicketSystemProxy implements ITicketSystem {

    /** The real subject — all approved calls are forwarded here. */
    private final TicketSystem real = TicketSystem.getInstance();

    /**
     * Add a ticket — allowed for all roles except unauthenticated users.
     * In this demo every role in UserRole enum may submit tickets.
     *
     * @param t The ticket to register.
     * @throws SecurityException if the session has no valid role set.
     */
    @Override
    public void addTicket(Ticket t) {
        // All named roles are permitted to submit tickets
        UserRole role = UserSession.getInstance().getRole();
        if (role == null) {
            throw new SecurityException("No active session — please log in first.");
        }
        real.addTicket(t); // Delegate to the real TicketSystem
    }

    /**
     * Retrieve all tickets — read-only operation permitted for all roles.
     */
    @Override
    public List<Ticket> getAllTickets() {
        return real.getAllTickets(); // No restriction on reading
    }

    /**
     * Resolve a ticket — restricted based on ticket escalation level.
     *
     * Rules:
     *   • AGENT_L1 cannot resolve tickets that have been escalated to Manager level.
     *   • AGENT_L2 cannot resolve tickets at Manager level either.
     *   • MANAGER and ADMIN may resolve any ticket.
     *
     * @param t The ticket to resolve.
     * @throws SecurityException if the current role lacks permission.
     */
    @Override
    public void resolveTicket(Ticket t) {
        UserRole role = UserSession.getInstance().getRole();

        // Manager-escalated tickets require MANAGER or ADMIN to close
        if (t.getStatus() == TicketStatus.ESCALATED
                && (role == UserRole.AGENT_L1 || role == UserRole.AGENT_L2)) {
            throw new SecurityException(
                "Role " + role + " cannot resolve a manager-level escalation.");
        }
        real.resolveTicket(t); // Approved — delegate to the real system
    }
}
