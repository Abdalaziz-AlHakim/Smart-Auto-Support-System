package behavioral.observer;
// Updated by Fady
/**
 * Observer that prints simulated email/SMS alert messages to stdout.
 *
 * PATTERN: Observer (Concrete Observer)
 * In a real system this would send an email or push notification.
 * For this demo it prints to the console, demonstrating that the Observer
 * pattern completely decouples notification channels from the domain core —
 * TicketSystem never knows or cares that emails are being "sent".
 */
public class ConsoleNotificationListener implements TicketEventListener {

    /**
     * Print a simulated external notification whenever a ticket event occurs.
     * The format mimics what an automated email system would produce.
     */
    @Override
    public void onTicketEvent(TicketEvent event) {
        switch (event.getType()) {

            case CREATED:
                System.out.printf(
                    "📧 [EMAIL] New ticket #%d submitted: \"%s\"%n",
                    event.getTicket().getId(), event.getTicket().getTitle());
                break;

            case ESCALATED:
                System.out.printf(
                    "📱 [SMS]   Ticket #%d escalated — agent action required!%n",
                    event.getTicket().getId());
                break;

            case RESOLVED:
                System.out.printf(
                    "📧 [EMAIL] Ticket #%d resolved. Customer will be notified.%n",
                    event.getTicket().getId());
                break;
        }
    }
}
