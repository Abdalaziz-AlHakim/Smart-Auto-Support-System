package creational.factory;

/**
 * Enum representing ticket urgency levels.
 *
 * NORMAL  – standard processing through the full L1→L2→Manager chain.
 * URGENT  – applied at runtime by UrgentTicketDecorator; skips L1 in UrgentRoutingStrategy.
 */
public enum Priority {
    NORMAL,
    URGENT
}
