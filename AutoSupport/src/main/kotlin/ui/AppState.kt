package ui

import androidx.compose.runtime.*
import behavioral.observer.TicketEvent
import behavioral.observer.TicketEventType
import creational.factory.Ticket
import creational.factory.TicketStatus
import creational.factory.TicketType
import creational.factory.Priority
import creational.singleton.TicketSystem
import structural.facade.SupportFacade
import structural.proxy.ITicketSystem
import structural.proxy.TicketSystemProxy
import structural.proxy.UserRole
import structural.proxy.UserSession

/**
 * Central Compose-reactive state holder for the entire AutoSupport UI.
 *
 * Holds all mutableState variables that drive recomposition.
 * Observer listener in main.kt calls onTicketEvent() which updates these states,
 * triggering Compose to rerender affected composables automatically.
 *
 * STATS DESIGN: open/escalated/resolved counts are DERIVED from the live ticket
 * list on every event rather than maintained as manual running counters. This
 * eliminates drift caused by reopen, double-resolve, repeated escalation, etc.
 */
class AppState {

    // ── Domain dependencies ───────────────────────────────────────────────────
    val facade : SupportFacade  = SupportFacade()
    val proxy  : ITicketSystem  = TicketSystemProxy()

    // ── Reactive state ────────────────────────────────────────────────────────

    /** Live ticket list — rebuilt from Singleton after every event. */
    var tickets by mutableStateOf(listOf<Ticket>())
        private set

    /** Search query for filtering the ticket list. */
    var searchQuery by mutableStateOf("")

    /** Filtered ticket list based on searchQuery (ID or Title). */
    val filteredTickets: List<Ticket>
        get() {
            val base = if (searchQuery.isEmpty()) tickets 
                       else tickets.filter { 
                           it.id.toString().contains(searchQuery) || 
                           it.title.contains(searchQuery, ignoreCase = true) 
                       }
            return base.filter { ticket ->
                val type = ticket.type
                val isUrgent = ticket.priority == Priority.URGENT
                when (currentRole) {
                    UserRole.MANAGER -> isUrgent || type == TicketType.COMPLAINT
                    UserRole.AGENT_L2 -> !isUrgent && (type == TicketType.BUG || type == TicketType.FEATURE_REQUEST)
                    UserRole.AGENT_L1 -> !isUrgent && type == TicketType.INQUIRY
                }
            }
        }

    /** Currently selected ticket in the table. */
    var selectedTicket by mutableStateOf<Ticket?>(null)

    /** Timestamped log lines fed by Observer events. */
    val logLines = mutableStateListOf<String>()

    /**
     * Stats counters — derived from the live ticket list, never incremented manually.
     * Each is a computed property so they are always in sync with reality.
     */
    var openCount      by mutableStateOf(0)
    var escalatedCount by mutableStateOf(0)
    var resolvedCount  by mutableStateOf(0)

    /** Active user role (updated by login panel). */
    var currentRole by mutableStateOf(UserRole.AGENT_L1)

    // ── Observer callback (called from main.kt subscriber) ────────────────────

    fun onTicketEvent(event: TicketEvent) {
        // Refresh ticket list snapshot from the Singleton
        tickets = TicketSystem.getInstance().getAllTickets().toList()

        // Derive counts from actual ticket statuses — immune to counter drift
        recomputeStats()

        // Append timestamped log line (icon depends on event type)
        val (icon, label) = when (event.type) {
            TicketEventType.CREATED   -> "✅" to "CREATED"
            TicketEventType.ESCALATED -> "⬆" to "ESCALATED"
            TicketEventType.RESOLVED  -> "✔" to "RESOLVED"
            TicketEventType.REOPENED  -> "♻" to "REOPENED"
            TicketEventType.PICKED_UP -> "🧤" to "PICKED UP"
        }
        val time = java.time.LocalTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))
        logLines.add("[$time] $icon  Ticket #${event.ticket.id} \"${event.ticket.title}\" — $label")

        // Console notification (simulates email/SMS observer side-effects)
        when (event.type) {
            TicketEventType.CREATED   ->
                println("📧 [EMAIL] New ticket #${event.ticket.id}: \"${event.ticket.title}\"")
            TicketEventType.ESCALATED ->
                println("📱 [SMS]   Ticket #${event.ticket.id} escalated!")
            TicketEventType.RESOLVED  ->
                println("📧 [EMAIL] Ticket #${event.ticket.id} resolved.")
            TicketEventType.REOPENED  ->
                println("📧 [EMAIL] Ticket #${event.ticket.id} reopened.")
            TicketEventType.PICKED_UP ->
                println("🧤 [HAND]  Ticket #${event.ticket.id} picked up by agent.")
        }

        // If the selected ticket was the one updated, refresh it to show new status
        if (selectedTicket?.id == event.ticket.id) {
            selectedTicket = event.ticket
        }
    }

    /**
     * Recompute open/escalated/resolved by scanning the live ticket list.
     * Called after every Observer event so stats are always consistent with reality.
     */
    private fun recomputeStats() {
        var open = 0; var escalated = 0; var resolved = 0
        for (t in tickets) {
            when (t.status) {
                TicketStatus.OPEN,
                TicketStatus.IN_PROGRESS -> open++
                TicketStatus.ESCALATED   -> escalated++
                TicketStatus.RESOLVED    -> resolved++
            }
            
            // Urgent tickets also count towards the Escalated total, 
            // even if their status is still OPEN or IN_PROGRESS.
            if (t.priority == Priority.URGENT && t.status != TicketStatus.ESCALATED) {
                escalated++
            }
        }
        openCount      = open
        escalatedCount = escalated
        resolvedCount  = resolved
    }

    /** Refresh ticket list and recompute stats (called after actions that don't fire events). */
    fun refreshTickets() {
        tickets = TicketSystem.getInstance().getAllTickets().toList()
        recomputeStats()
    }

    /** Change the active role — updates UserSession for Proxy enforcement. */
    fun setRole(role: UserRole) {
        currentRole = role
        UserSession.getInstance().setRole(role)
    }

    /** Error message shown in a snackbar/dialog (null = no error). */
    var errorMessage by mutableStateOf<String?>(null)
}
