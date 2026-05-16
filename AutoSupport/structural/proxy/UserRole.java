package structural.proxy;

/**
 * Enum of user roles in the AutoSupport system.
 *
 * Used by TicketSystemProxy to enforce role-based access control.
 * Each role has a different set of allowed operations:
 *   AGENT_L1  – can submit, escalate, and resolve INQUIRY tickets.
 *   AGENT_L2  – can submit, escalate, and resolve BUG and FEATURE_REQUEST tickets.
 *   MANAGER   – can resolve any ticket including COMPLAINT and escalations.
 */
public enum UserRole {
    AGENT_L1,
    AGENT_L2,
    MANAGER
}
