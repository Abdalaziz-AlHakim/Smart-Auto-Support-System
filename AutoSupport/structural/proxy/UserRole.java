package structural.proxy;

/**
 * Enum of user roles in the AutoSupport system.
 *
 * Used by TicketSystemProxy to enforce role-based access control.
 * Each role has a different set of allowed operations:
 *   AGENT_L1  – can submit and escalate L1-level tickets only
 *   AGENT_L2  – can submit, escalate and resolve L2-level tickets
 *   MANAGER   – can resolve any ticket including manager-level escalations
 *   ADMIN     – full access to all operations
 */
public enum UserRole {
    AGENT_L1,
    AGENT_L2,
    MANAGER,
    ADMIN
}
