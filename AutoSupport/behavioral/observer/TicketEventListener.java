package behavioral.observer;
// Updated by Fady
/**
 * Observer interface for ticket lifecycle events.
 *
 * PATTERN: Observer
 * Any class that wants to react to ticket events (creation, escalation,
 * resolution) must implement this interface and register itself with
 * TicketSystem.subscribe(). The subject (TicketSystem) holds a list of
 * these listeners and calls onTicketEvent() on all of them whenever
 * a relevant change occurs — without knowing or caring what each listener does.
 */
public interface TicketEventListener {

    /**
     * Called by TicketSystem whenever a ticket lifecycle event occurs.
     *
     * @param event Carries the affected ticket and the type of event
     *              (CREATED, ESCALATED, RESOLVED).
     */
    void onTicketEvent(TicketEvent event);
}
