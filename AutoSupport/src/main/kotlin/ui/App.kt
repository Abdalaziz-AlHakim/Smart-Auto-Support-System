package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import creational.factory.*
import structural.proxy.UserRole

// ── Material3 dark colour scheme using shared Theme colours ──────────────────

private val AppColorScheme = darkColorScheme(
    primary      = AccentPurple,
    secondary    = AccentBlue,
    background   = BgDark,
    surface      = Surface1,
    onPrimary    = Color.White,
    onBackground = TextPrimary,
    onSurface    = TextPrimary,
)

// ══════════════════════════════════════════════════════════════════════════════
// ROOT
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Root composable.  Provides the MaterialTheme and assembles the 4-zone layout:
 *   TopBar | StatsRow | (TicketPanel + ActionPanel) | LogPanel
 */
@Composable
fun AutoSupportApp(state: AppState) {
    MaterialTheme(colorScheme = AppColorScheme) {
        Box(Modifier.fillMaxSize().background(BgDark)) {
            Column(Modifier.fillMaxSize()) {
                TopBar(state)
                StatsRow(state)
                Row(Modifier.weight(1f).fillMaxWidth()) {
                    TicketPanel(state, Modifier.weight(1f))
                    HorizontalDivider(
                        modifier  = Modifier.fillMaxHeight().width(1.dp),
                        color     = Surface2,
                        thickness = 1.dp
                    )
                    ActionPanel(state, Modifier.width(300.dp))
                }
                LogPanel(state, Modifier.height(165.dp).fillMaxWidth())
            }

            // ── Error snackbar (shown when state.errorMessage != null) ────────
            state.errorMessage?.let { msg ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                    Snackbar(
                        modifier      = Modifier.padding(16.dp),
                        containerColor = RedAlert,
                        action = {
                            TextButton(onClick = { state.errorMessage = null }) {
                                Text("Dismiss", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    ) { Text(msg, color = Color.White) }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// TOP BAR
// ══════════════════════════════════════════════════════════════════════════════

/**
 * TopBar — app title on the left, role selector on the right.
 * The role selector updates UserSession which TicketSystemProxy checks on every write.
 */
@Composable
fun TopBar(state: AppState) {
    Row(
        modifier            = Modifier.fillMaxWidth().background(Surface1)
                                      .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment   = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.SupportAgent, contentDescription = "App logo",
            tint = AccentPurple, modifier = Modifier.size(28.dp))
        Spacer(Modifier.width(10.dp))
        Text("AutoSupport", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text("  — Design Pattern Demo", fontSize = 13.sp, color = TextSecondary)

        Spacer(Modifier.weight(1f))

        // ── Role dropdown ────────────────────────────────────────────────────
        // Selecting a role here changes UserSession.role, which TicketSystemProxy
        // reads on every addTicket / resolveTicket call to enforce access rules.
        Tooltip("Select your role. Different roles have different permissions.\nMANAGER/ADMIN can resolve escalated tickets.") {
            RoleDropdown(state)
        }
    }
}

@Composable
fun RoleDropdown(state: AppState) {
    var expanded by remember { mutableStateOf(false) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Role:", color = TextSecondary, fontSize = 13.sp)
        Spacer(Modifier.width(8.dp))
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                colors  = ButtonDefaults.outlinedButtonColors(contentColor = AccentPurple),
                border  = androidx.compose.foundation.BorderStroke(1.dp, AccentPurple),
                shape   = RoundedCornerShape(8.dp)
            ) {
                Text(state.currentRole.name, fontSize = 13.sp)
                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Expand role list",
                    modifier = Modifier.size(18.dp))
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false },
                modifier = Modifier.background(Surface2)) {
                UserRole.values().forEach { role ->
                    DropdownMenuItem(
                        text    = { Text(role.name, color = TextPrimary) },
                        onClick = { state.setRole(role); expanded = false }
                    )
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// STATS ROW
// ══════════════════════════════════════════════════════════════════════════════

/**
 * StatsRow — four metric cards showing live ticket counts.
 * Counts are updated by AppState.onTicketEvent() which the Observer triggers.
 */
@Composable
fun StatsRow(state: AppState) {
    Row(
        modifier                = Modifier.fillMaxWidth().background(BgDark)
                                          .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement   = Arrangement.spacedBy(12.dp)
    ) {
        Tooltip("Tickets that have been submitted and are actively being worked on.") {
            StatCard("Open Tickets", state.openCount.toString(),
                AccentBlue, Icons.Filled.Inbox, Modifier.weight(1f))
        }
        Tooltip("Tickets passed beyond the initial handler and awaiting a senior decision.") {
            StatCard("Escalated", state.escalatedCount.toString(),
                YellowWarn, Icons.Filled.ArrowUpward, Modifier.weight(1f))
        }
        Tooltip("Tickets that have been fully resolved and closed.") {
            StatCard("Resolved", state.resolvedCount.toString(),
                GreenOk, Icons.Filled.CheckCircle, Modifier.weight(1f))
        }
        Tooltip("Total tickets ever submitted in this session.") {
            StatCard("Total Submitted", state.tickets.size.toString(),
                AccentPurple, Icons.Filled.ConfirmationNumber, Modifier.weight(1f))
        }
    }
}

@Composable
fun StatCard(label: String, value: String, color: Color,
             icon: ImageVector, modifier: Modifier) {
    Card(modifier   = modifier,
        shape       = RoundedCornerShape(12.dp),
        colors      = CardDefaults.cardColors(containerColor = Surface1)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(42.dp).clip(CircleShape).background(color.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
                Text(label, fontSize = 11.sp, color = TextSecondary)
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// TICKET PANEL
// ══════════════════════════════════════════════════════════════════════════════

/**
 * TicketPanel — scrollable list of all tickets.
 * Click a row to select it; the ActionPanel will use the selection.
 * Row background colour encodes priority/status at a glance.
 */
@Composable
fun TicketPanel(state: AppState, modifier: Modifier) {
    Column(modifier.background(BgDark).padding(horizontal = 16.dp)) {
        // Panel header
        Row(Modifier.fillMaxWidth().padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.TableRows, contentDescription = null,
                tint = AccentPurple, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Tickets", fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Spacer(Modifier.weight(1f))
            Text("${state.tickets.size} total", fontSize = 12.sp, color = TextSecondary)
        }

        TicketRowHeader()

        if (state.tickets.isEmpty()) {
            // Friendly empty state
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Inbox, null, tint = TextSecondary,
                        modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("No tickets yet — click New Ticket to get started.",
                        color = TextSecondary, fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize(), state = rememberLazyListState()) {
                items(state.tickets, key = { it.id }) { ticket ->
                    TicketRow(
                        ticket   = ticket,
                        selected = state.selectedTicket?.id == ticket.id,
                        onClick  = { state.selectedTicket = ticket }
                    )
                }
            }
        }
    }
}

@Composable
fun TicketRowHeader() {
    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)) {
        Text("ID",       Modifier.width(44.dp),  fontSize = 11.sp,
            color = TextSecondary, fontWeight = FontWeight.Bold)
        Text("Title",    Modifier.weight(1f),    fontSize = 11.sp,
            color = TextSecondary, fontWeight = FontWeight.Bold)
        Text("Type",     Modifier.width(120.dp), fontSize = 11.sp,
            color = TextSecondary, fontWeight = FontWeight.Bold)
        Text("Priority", Modifier.width(90.dp),  fontSize = 11.sp,
            color = TextSecondary, fontWeight = FontWeight.Bold)
        Text("Status",   Modifier.width(110.dp), fontSize = 11.sp,
            color = TextSecondary, fontWeight = FontWeight.Bold)
    }
    HorizontalDivider(color = Surface2)
}

@Composable
fun TicketRow(ticket: Ticket, selected: Boolean, onClick: () -> Unit) {
    val bgColor = when {
        selected                                -> AccentPurple.copy(alpha = 0.15f)
        ticket.priority == Priority.URGENT      -> RedAlert.copy(alpha = 0.08f)
        ticket.status   == TicketStatus.RESOLVED -> GreenOk.copy(alpha = 0.07f)
        ticket.status   == TicketStatus.ESCALATED-> YellowWarn.copy(alpha = 0.07f)
        else                                    -> Color.Transparent
    }
    Tooltip("Click to select this ticket, then use the Actions panel on the right.") {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .background(bgColor, RoundedCornerShape(6.dp))
                .then(
                    if (selected)
                        Modifier.border(1.dp, AccentPurple.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                    else Modifier
                )
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("#${ticket.id}", Modifier.width(44.dp), fontSize = 13.sp,
                color = TextSecondary, fontWeight = FontWeight.Medium)
            Text(ticket.title, Modifier.weight(1f), fontSize = 13.sp, color = TextPrimary,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            TypeChip(ticket.getTypeLabel(), Modifier.width(120.dp))
            PriorityBadge(ticket.priority, Modifier.width(90.dp))
            StatusBadge(ticket.status, Modifier.width(110.dp))
        }
    }
}

@Composable
fun TypeChip(label: String, modifier: Modifier) {
    val clean = label.removePrefix("[URGENT] ")
    val color = when (clean) {
        "Bug"             -> RedAlert
        "Complaint"       -> YellowWarn
        "Feature Request" -> AccentBlue
        else              -> TextSecondary
    }
    Text(clean, modifier, fontSize = 12.sp, color = color,
        maxLines = 1, overflow = TextOverflow.Ellipsis)
}

@Composable
fun PriorityBadge(p: Priority, modifier: Modifier) {
    val (color, text) = if (p == Priority.URGENT)
        RedAlert to "🔴 URGENT" else GreenOk to "NORMAL"
    Text(text, modifier, fontSize = 11.sp, color = color, fontWeight = FontWeight.SemiBold)
}

@Composable
fun StatusBadge(s: TicketStatus, modifier: Modifier) {
    val (color, text) = when (s) {
        TicketStatus.OPEN         -> AccentBlue   to "OPEN"
        TicketStatus.IN_PROGRESS  -> AccentPurple to "IN PROGRESS"
        TicketStatus.ESCALATED    -> YellowWarn   to "ESCALATED"
        TicketStatus.RESOLVED     -> GreenOk      to "RESOLVED"
    }
    Surface(modifier.clip(RoundedCornerShape(4.dp)), color = color.copy(alpha = 0.18f)) {
        Text(text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontSize = 10.sp, color = color, fontWeight = FontWeight.Bold)
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// LOG PANEL
// ══════════════════════════════════════════════════════════════════════════════

/**
 * LogPanel — real-time event log at the bottom of the window.
 * Automatically scrolls to the newest entry as events fire.
 * Powered by the Observer pattern: AppState.logLines grows every time
 * TicketSystem.notifyListeners() fires an event.
 */
@Composable
fun LogPanel(state: AppState, modifier: Modifier) {
    val listState = rememberLazyListState()

    // Auto-scroll to the last entry whenever a new log line is added
    LaunchedEffect(state.logLines.size) {
        if (state.logLines.isNotEmpty())
            listState.animateScrollToItem(state.logLines.size - 1)
    }

    Column(modifier.background(Surface1)) {
        // Header row
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Terminal, contentDescription = "Event log",
                tint = GreenOk, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Observer Event Log", fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Tooltip("Every ticket lifecycle event (CREATED, ESCALATED, RESOLVED)\nfires here via the Observer pattern (TicketSystem → Listeners).") {
                Icon(Icons.Filled.Info, contentDescription = "Log info",
                    tint = TextSecondary, modifier = Modifier.size(14.dp).padding(start = 4.dp))
            }
            Spacer(Modifier.weight(1f))
            Text("${state.logLines.size} events", fontSize = 11.sp, color = TextSecondary)
        }
        HorizontalDivider(color = Surface2)

        // Scrollable log entries
        LazyColumn(
            modifier  = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 4.dp),
            state     = listState
        ) {
            items(state.logLines) { line ->
                val color = when {
                    "✅" in line  -> GreenOk
                    "⬆"  in line  -> YellowWarn
                    "✔"  in line  -> AccentBlue
                    else          -> TextSecondary
                }
                Text(
                    text     = line,
                    fontSize = 11.sp,
                    color    = color,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    modifier = Modifier.padding(vertical = 1.dp)
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// DIALOGS — SubmitDialog and EmailDialog live in Actions.kt
// ══════════════════════════════════════════════════════════════════════════════

/** Shared text-field colour configuration — dark themed. */
@Composable
fun dialogFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = AccentPurple,
    unfocusedBorderColor = Surface2,
    focusedTextColor     = TextPrimary,
    unfocusedTextColor   = TextPrimary,
    cursorColor          = AccentPurple,
    focusedLabelColor    = AccentPurple,
    unfocusedLabelColor  = TextSecondary
)
