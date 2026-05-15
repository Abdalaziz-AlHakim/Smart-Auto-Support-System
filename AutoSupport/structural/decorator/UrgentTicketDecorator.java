package structural.decorator;

import creational.factory.Priority;
import creational.factory.Ticket;

/**
 * Concrete decorator that elevates a ticket's priority to URGENT at runtime.
 *
 * PATTERN: Decorator
 * Wraps any Ticket (or another decorator) and overrides only the three methods
 * that change when a ticket is urgent. Everything else is delegated to the
 * wrapped ticket via TicketDecorator's default implementations.
 *
 * Why Decorator and not a boolean flag on Ticket?
 * A flag mixes urgency logic into the base class. Decorator keeps urgency
 * as an independent, composable wrapper — zero changes to Ticket subclasses.
 */
public class UrgentTicketDecorator extends TicketDecorator {

    /**
     * @param wrapped Any Ticket to mark as urgent.
     *                May itself already be decorated.
     */
    public UrgentTicketDecorator(Ticket wrapped) {
        super(wrapped);
    }

    /**
     * Always returns URGENT, overriding the wrapped ticket's priority.
     */
    @Override
    public Priority getPriority() {
        return Priority.URGENT;
    }

    /**
     * Prepends "[URGENT] " to the wrapped ticket's type label.
     * Example: "Bug" → "[URGENT] Bug"
     */
    @Override
    public String getTypeLabel() {
        return "[URGENT] " + wrapped.getTypeLabel();
    }

    /**
     * Prepends a red circle emoji so the GUI table row is instantly recognisable.
     * Example: "[#3] App crash (Bug) — OPEN" → "🔴 [#3] App crash ..."
     */
    @Override
    public String toString() {
        return "🔴 " + wrapped.toString();
    }
}
