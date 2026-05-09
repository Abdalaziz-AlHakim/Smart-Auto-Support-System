package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import behavioral.strategy.*
import creational.factory.*
import structural.adapter.EmailMessage

// ══════════════════════════════════════════════════════════════════════════════
// ACTION PANEL (right sidebar)
// ══════════════════════════════════════════════════════════════════════════════

/**
 * ActionPanel — right sidebar with three sections:
 *   1. Submit  — create a new ticket (form dialog) or simulate an inbound email
 *   2. Escalate — choose a routing strategy and process the selected ticket
 *   3. Lifecycle — Resolve or Reopen the selected ticket
 *
 * All write operations are mediated by SupportFacade (submit / escalate / reopen)
 * or TicketSystemProxy (resolve) so role-based access rules are always enforced.
 */
@Composable
fun ActionPanel(state: AppState, modifier: Modifier) {
    var showSubmitDialog by remember { mutableStateOf(false) }
    var showEmailDialog  by remember { mutableStateOf(false) }

    Column(
        modifier              = modifier.background(Surface1).padding(16.dp),
        verticalArrangement   = Arrangement.spacedBy(10.dp)
    ) {
        Text("Actions", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        HorizontalDivider(color = Surface2)

        // ── Section 1: Submit ─────────────────────────────────────────────────
        SectionLabel(
            title = "Submit",
            guide = "Create a new support ticket manually or by converting an inbound email."
        )

        Tooltip(
            "Open a form to enter ticket details: title, description, type, and urgency.\n" +
            "Uses: Factory (creates typed subclass), Decorator (if URGENT), State, Observer."
        ) {
            ActionButton("New Ticket", Icons.Filled.Add, AccentPurple) {
                showSubmitDialog = true
            }
        }

        Tooltip(
            "Simulate receiving an inbound support email.\n" +
            "The Adapter pattern converts the email's subject keywords into a ticket type automatically."
        ) {
            ActionButton("Simulate Email", Icons.Filled.Email, AccentBlue) {
                showEmailDialog = true
            }
        }

        HorizontalDivider(color = Surface2)

        // ── Section 2: Escalate ───────────────────────────────────────────────
        SectionLabel(
            title = "Escalate Selected",
            guide = "Process the selected ticket through the handler chain using the chosen routing strategy."
        )

        var selectedStrategy by remember { mutableStateOf("Standard") }

        Tooltip(
            "Standard: L1 → L2 → Manager (normal flow)\n" +
            "Urgent: L2 → Manager (skips L1 for faster resolution)\n" +
            "Type-Based: routes by ticket category\n\n" +
            "Uses: Strategy pattern to build the chain; Chain of Responsibility to resolve."
        ) {
            StrategyDropdown(selectedStrategy) { selectedStrategy = it }
        }

        Tooltip(
            "Escalate the selected ticket using the routing strategy above.\n" +
            "Status is set to ESCALATED, then the chain attempts to resolve it.\n" +
            "Requires a ticket to be selected in the list."
        ) {
            ActionButton("Escalate", Icons.Filled.ArrowUpward, YellowWarn) {
                val ticket = state.selectedTicket
                if (ticket == null) { state.errorMessage = "Select a ticket first."; return@ActionButton }
                if (ticket.status == TicketStatus.RESOLVED) {
                    state.errorMessage = "Cannot escalate a RESOLVED ticket. Reopen it first."; return@ActionButton
                }
                if (ticket.status == TicketStatus.ESCALATED) {
                    state.errorMessage = "Ticket is already ESCALATED."; return@ActionButton
                }

                val strategy: RoutingStrategy = when (selectedStrategy) {
                    "Urgent"     -> UrgentRoutingStrategy()
                    "Type-Based" -> TypeBasedRoutingStrategy()
                    else         -> StandardRoutingStrategy()
                }
                val chainLog = mutableListOf<String>()
                try {
                    state.facade.escalateTicket(ticket, strategy, chainLog)
                    state.refreshTickets()
                    // Append chain decisions to the observable log panel
                    state.logLines.addAll(chainLog)
                } catch (e: Exception) {
                    state.errorMessage = e.message ?: "Escalation failed."
                }
            }
        }

        HorizontalDivider(color = Surface2)

        // ── Section 3: Lifecycle ──────────────────────────────────────────────
        SectionLabel(
            title = "Ticket Lifecycle",
            guide = "Manually change the status of the selected ticket. Role restrictions apply."
        )

        Tooltip(
            "Mark the selected ticket as RESOLVED.\n" +
            "Access is controlled by the Proxy pattern:\n" +
            "  • AGENT_L1 / L2 cannot resolve ESCALATED tickets.\n" +
            "  • MANAGER / ADMIN can resolve any ticket."
        ) {
            ActionButton("Resolve", Icons.Filled.CheckCircle, GreenOk) {
                val ticket = state.selectedTicket
                if (ticket == null) { state.errorMessage = "Select a ticket first."; return@ActionButton }
                try {
                    state.proxy.resolveTicket(ticket)
                    state.refreshTickets()
                } catch (e: SecurityException) {
                    state.errorMessage = "Access denied: ${e.message}"
                }
            }
        }

        Tooltip(
            "Reopen a RESOLVED ticket — transitions it back to OPEN.\n" +
            "Uses the State pattern: ResolvedState.reopen() → OpenState.\n" +
            "Only works on tickets with status RESOLVED."
        ) {
            ActionButton("Reopen", Icons.Filled.Refresh, AccentBlue) {
                val ticket = state.selectedTicket
                if (ticket == null) { state.errorMessage = "Select a ticket first."; return@ActionButton }
                if (ticket.status != TicketStatus.RESOLVED) {
                    state.errorMessage = "Only RESOLVED tickets can be reopened."; return@ActionButton
                }
                try {
                    state.facade.reopenTicket(ticket)
                    state.refreshTickets()
                } catch (e: Exception) {
                    state.errorMessage = e.message ?: "Reopen failed."
                }
            }
        }

        // ── Selected ticket detail card ────────────────────────────────────────
        state.selectedTicket?.let { t ->
            HorizontalDivider(color = Surface2)
            SectionLabel(title = "Selected Ticket", guide = "Details of the currently selected row.")
            SelectedTicketCard(t)
        }
    }

    // ── Dialogs (shown conditionally) ─────────────────────────────────────────
    if (showSubmitDialog) SubmitDialog(state)  { showSubmitDialog = false }
    if (showEmailDialog)  EmailDialog(state)   { showEmailDialog  = false }
}

// ══════════════════════════════════════════════════════════════════════════════
// SHARED HELPERS
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Section header with a title and a one-line guide below it.
 * The guide text explains what this section does in plain language.
 */
@Composable
fun SectionLabel(title: String, guide: String) {
    Column {
        Text(title.uppercase(), fontSize = 10.sp, color = TextSecondary,
            fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
        Text(guide, fontSize = 10.sp, color = TextSecondary.copy(alpha = 0.7f),
            lineHeight = 14.sp)
    }
}

/** Full-width coloured button with icon. Consistent shape and padding across all sections. */
@Composable
fun ActionButton(label: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Button(
        onClick         = onClick,
        modifier        = Modifier.fillMaxWidth(),
        colors          = ButtonDefaults.buttonColors(containerColor = color.copy(alpha = 0.20f)),
        shape           = RoundedCornerShape(8.dp),
        contentPadding  = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(label, color = color, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

/** Routing strategy picker — dropdown styled to match the dark theme. */
@Composable
fun StrategyDropdown(selected: String, onSelect: (String) -> Unit) {
    val options = listOf("Standard", "Urgent", "Type-Based")
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(
            onClick       = { expanded = true },
            modifier      = Modifier.fillMaxWidth(),
            colors        = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
            border        = androidx.compose.foundation.BorderStroke(1.dp, Surface2),
            shape         = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Icon(Icons.Filled.AccountTree, contentDescription = "Strategy icon",
                modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Strategy: $selected", fontSize = 12.sp, modifier = Modifier.weight(1f))
            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Expand")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false },
            modifier = Modifier.background(Surface2)) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text    = { Text(opt, color = TextPrimary, fontSize = 13.sp) },
                    onClick = { onSelect(opt); expanded = false }
                )
            }
        }
    }
}

/** Small info card showing the selected ticket's key fields. */
@Composable
fun SelectedTicketCard(ticket: Ticket) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Surface2),
        shape  = RoundedCornerShape(8.dp)
    ) {
        Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text("#${ticket.id} — ${ticket.title}", fontSize = 12.sp,
                color = TextPrimary, fontWeight = FontWeight.SemiBold, maxLines = 2)
            InfoRow("Type",     ticket.getTypeLabel())
            InfoRow("Status",   ticket.status.name)
            InfoRow("Priority", ticket.priority.name)
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row {
        Text("$label: ", fontSize = 11.sp, color = TextSecondary)
        Text(value,       fontSize = 11.sp, color = TextPrimary)
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// SUBMIT DIALOG
// ══════════════════════════════════════════════════════════════════════════════

/**
 * SubmitDialog — modal for manually creating a ticket.
 *
 * Uses:
 *  • Factory Method  — TicketFactory.createTicket() inside SupportFacade
 *  • Decorator       — wraps ticket with UrgentTicketDecorator if "Urgent" is checked
 *  • State           — TicketContext advances OPEN → IN_PROGRESS on submit
 *  • Observer        — CREATED event fires; LogPanel and StatsRow update automatically
 */
@Composable
fun SubmitDialog(state: AppState, onDismiss: () -> Unit) {
    var title        by remember { mutableStateOf("") }
    var description  by remember { mutableStateOf("") }
    var ticketType   by remember { mutableStateOf(TicketType.BUG) }
    var urgent       by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors   = CardDefaults.cardColors(containerColor = Surface1),
            shape    = RoundedCornerShape(16.dp),
            modifier = Modifier.width(440.dp)
        ) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {

                // Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Add, null, tint = AccentPurple, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Submit New Ticket", fontSize = 18.sp,
                        fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                HorizontalDivider(color = Surface2)

                // Title field
                Tooltip("Short summary of the issue. Required.") {
                    OutlinedTextField(
                        value         = title,
                        onValueChange = { title = it },
                        modifier      = Modifier.fillMaxWidth(),
                        label         = { Text("Title *") },
                        singleLine    = true,
                        colors        = dialogFieldColors()
                    )
                }

                // Description field
                Tooltip("Full details: steps to reproduce, affected version, error messages, etc.") {
                    OutlinedTextField(
                        value         = description,
                        onValueChange = { description = it },
                        modifier      = Modifier.fillMaxWidth().height(90.dp),
                        label         = { Text("Description") },
                        maxLines      = 4,
                        colors        = dialogFieldColors()
                    )
                }

                // Type dropdown
                Tooltip(
                    "Bug: software defect (L2 resolves)\n" +
                    "Complaint: service dissatisfaction (Manager resolves)\n" +
                    "Feature Request: new functionality (L2 resolves)\n" +
                    "Inquiry: general question (L1 resolves)"
                ) {
                    Box {
                        OutlinedButton(
                            onClick        = { typeExpanded = true },
                            modifier       = Modifier.fillMaxWidth(),
                            border         = androidx.compose.foundation.BorderStroke(1.dp, Surface2),
                            shape          = RoundedCornerShape(8.dp),
                            colors         = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
                        ) {
                            Text("Type: ${ticketType.name}", Modifier.weight(1f), fontSize = 13.sp)
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Expand")
                        }
                        DropdownMenu(typeExpanded, { typeExpanded = false },
                            modifier = Modifier.background(Surface2)) {
                            TicketType.values().forEach { t ->
                                DropdownMenuItem(
                                    text    = { Text(t.name, color = TextPrimary) },
                                    onClick = { ticketType = t; typeExpanded = false }
                                )
                            }
                        }
                    }
                }

                // Urgent checkbox
                Tooltip(
                    "Checking URGENT wraps the ticket with UrgentTicketDecorator.\n" +
                    "This overrides priority to URGENT and prefixes the display with 🔴.\n" +
                    "UrgentRoutingStrategy also skips L1 when escalating."
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()) {
                        Checkbox(
                            checked         = urgent,
                            onCheckedChange = { urgent = it },
                            colors          = CheckboxDefaults.colors(checkedColor = RedAlert)
                        )
                        Spacer(Modifier.width(4.dp))
                        Column {
                            Text("Mark as URGENT", fontSize = 13.sp,
                                color = if (urgent) RedAlert else TextSecondary)
                            Text("Applies UrgentTicketDecorator (Decorator pattern)",
                                fontSize = 10.sp, color = TextSecondary)
                        }
                    }
                }

                // Action buttons
                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment     = Alignment.CenterVertically) {
                    TextButton(onDismiss) { Text("Cancel", color = TextSecondary) }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                state.errorMessage = "Title is required."
                                return@Button
                            }
                            try {
                                state.facade.submitTicket(
                                    ticketType, title.trim(), description.trim(), urgent)
                                state.refreshTickets()
                                onDismiss()
                            } catch (e: Exception) {
                                state.errorMessage = e.message ?: "Submit failed."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Submit Ticket")
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// EMAIL DIALOG
// ══════════════════════════════════════════════════════════════════════════════

/**
 * EmailDialog — simulates receiving an inbound support email.
 *
 * Uses:
 *  • Adapter — EmailTicketAdapter converts EmailMessage → Ticket
 *    by scanning the subject for keywords (bug/complaint/feature/inquiry)
 */
@Composable
fun EmailDialog(state: AppState, onDismiss: () -> Unit) {
    var sender  by remember { mutableStateOf("client@corp.com") }
    var subject by remember { mutableStateOf("Bug: crash on login page") }
    var body    by remember { mutableStateOf("Reproducible on iOS 17.\nSteps:\n1. Open app\n2. Tap Login") }
    var urgent  by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors   = CardDefaults.cardColors(containerColor = Surface1),
            shape    = RoundedCornerShape(16.dp),
            modifier = Modifier.width(440.dp)
        ) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {

                // Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Email, null, tint = AccentBlue, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Simulate Inbound Email", fontSize = 18.sp,
                        fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                HorizontalDivider(color = Surface2)

                // Detection guide
                Card(colors = CardDefaults.cardColors(containerColor = Surface2),
                    shape  = RoundedCornerShape(8.dp)) {
                    Column(Modifier.padding(10.dp)) {
                        Text("🔍 Adapter Keyword Detection (EmailTicketAdapter)",
                            fontSize = 11.sp, color = AccentBlue, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text("bug / error / crash  → BugTicket\n" +
                             "complaint / unhappy  → ComplaintTicket\n" +
                             "feature / request    → FeatureRequestTicket\n" +
                             "(anything else)      → InquiryTicket",
                            fontSize = 10.sp, color = TextSecondary,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                    }
                }

                Tooltip("Email address of the customer sending this message.") {
                    OutlinedTextField(sender, { sender = it }, Modifier.fillMaxWidth(),
                        label  = { Text("From (Sender)") },
                        singleLine = true, colors = dialogFieldColors())
                }

                Tooltip("Subject line — keywords here determine the ticket type.") {
                    OutlinedTextField(subject, { subject = it }, Modifier.fillMaxWidth(),
                        label  = { Text("Subject *") },
                        singleLine = true, colors = dialogFieldColors())
                }

                Tooltip("Email body — becomes the ticket description.") {
                    OutlinedTextField(body, { body = it },
                        Modifier.fillMaxWidth().height(90.dp),
                        label  = { Text("Body") },
                        maxLines = 5, colors = dialogFieldColors())
                }

                Tooltip("Wraps the resulting ticket with UrgentTicketDecorator.") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(urgent, { urgent = it },
                            colors = CheckboxDefaults.colors(checkedColor = RedAlert))
                        Spacer(Modifier.width(4.dp))
                        Text("Mark as URGENT", fontSize = 13.sp,
                            color = if (urgent) RedAlert else TextSecondary)
                    }
                }

                // Action buttons
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onDismiss) { Text("Cancel", color = TextSecondary) }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (subject.isBlank()) {
                                state.errorMessage = "Subject is required."
                                return@Button
                            }
                            try {
                                state.facade.submitFromEmail(
                                    EmailMessage(sender.trim(), subject.trim(), body.trim()), urgent)
                                state.refreshTickets()
                                onDismiss()
                            } catch (e: Exception) {
                                state.errorMessage = e.message ?: "Email submit failed."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Send & Convert")
                    }
                }
            }
        }
    }
}
