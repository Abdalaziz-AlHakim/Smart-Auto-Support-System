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
     * Resolve a ticket — restricted based on ticket type and escalation level.
     *
     * Rules:
     *   • MANAGER can resolve any ticket.
     *   • AGENT_L1 can only resolve INQUIRY tickets.
     *   • AGENT_L2 can resolve INQUIRY, BUG, and FEATURE_REQUEST.
     *   • No L1/L2 agent can resolve an ESCALATED ticket.
     *
     * @param t The ticket to resolve.
     * @throws SecurityException if the current role lacks permission.
     */
    @Override
    public void resolveTicket(Ticket t) {
        UserRole role = UserSession.getInstance().getRole();
        
        // Manager can do anything, no further checks needed for them
        if (role != UserRole.MANAGER) {
            // 1. Escalation check
            if (t.getStatus() == TicketStatus.ESCALATED) {
                throw new SecurityException("Role " + role + " cannot resolve an ESCALATED ticket.");
            }

            // Priority check for URGENT tickets
            if (t.getPriority() == creational.factory.Priority.URGENT) {
                throw new SecurityException("Role " + role + " cannot resolve URGENT tickets.");
            }

            // 2. Ticket Type check
            creational.factory.TicketType type = t.getType();
            if (role == UserRole.AGENT_L1 && type != creational.factory.TicketType.INQUIRY) {
                throw new SecurityException("Role AGENT_L1 can only resolve INQUIRY tickets.");
            }
            if (role == UserRole.AGENT_L2 && type == creational.factory.TicketType.COMPLAINT) {
                throw new SecurityException("Role AGENT_L2 cannot resolve COMPLAINT tickets.");
            }
        }

        real.resolveTicket(t); // Approved — delegate to the real system
    }

    /**
     * Pick up a ticket to start working on it.
     * Restricts access based on the ticket type.
     */
    @Override
    public void startProgress(Ticket t) {
        UserRole role = UserSession.getInstance().getRole();
        
        if (role != UserRole.MANAGER) {
            creational.factory.TicketType type = t.getType();
            if (role == UserRole.AGENT_L1 && type != creational.factory.TicketType.INQUIRY) {
                throw new SecurityException("Role AGENT_L1 can only pick up INQUIRY tickets.");
            }
            if (role == UserRole.AGENT_L2 && type == creational.factory.TicketType.COMPLAINT) {
                throw new SecurityException("Role AGENT_L2 cannot pick up COMPLAINT tickets.");
            }
        }
        
        real.startProgress(t);
    }

    /**
     * Reopen a ticket.
     * Restricts access based on the ticket type.
     */
    @Override
    public void reopenTicket(Ticket t) {
        UserRole role = UserSession.getInstance().getRole();
        
        if (role != UserRole.MANAGER) {
            creational.factory.TicketType type = t.getType();
            if (role == UserRole.AGENT_L1 && type != creational.factory.TicketType.INQUIRY) {
                throw new SecurityException("Role AGENT_L1 can only reopen INQUIRY tickets.");
            }
            if (role == UserRole.AGENT_L2 && type == creational.factory.TicketType.COMPLAINT) {
                throw new SecurityException("Role AGENT_L2 cannot reopen COMPLAINT tickets.");
            }
        }
        
        real.reopenTicket(t);
    }
}
