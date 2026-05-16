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

// ══════════════════════════════════════════════════════════════════════════════
// MAIN APPLICATION LAYOUT
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun App(state: AppState) {
    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize().background(BgDark)
        ) {
            Column(Modifier.fillMaxSize()) {
                TopBar(state)
                StatisticsRow(state)
                
                Row(Modifier.weight(1f)) {
                    TicketPanel(state, Modifier.weight(1f))
                    VerticalDivider(color = Surface2)
                    ActionPanel(state, Modifier.width(300.dp))
                }
                
                HorizontalDivider(color = Surface2)
                LogPanel(state, Modifier.height(200.dp))
            }

            // Global Error Notification (Snackbar)
            state.errorMessage?.let { msg ->
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                    containerColor = Color(0xFF2D161B),
                    contentColor   = Color(0xFFFFB4AB),
                    action = {
                        TextButton(onClick = { state.errorMessage = null }) {
                            Text("OK", color = Color(0xFFFFB4AB))
                        }
                    }
                ) { Text(msg) }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// COMPONENTS
// ══════════════════════════════════════════════════════════════════════════════

/** Top bar with App Logo and Role Switcher (Proxy pattern UI) */
@Composable
fun TopBar(state: AppState) {
    Row(
        modifier        = Modifier.fillMaxWidth().height(64.dp).background(Surface1).padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(32.dp).clip(CircleShape).background(AccentPurple), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.AutoGraph, null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("AutoSupport", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("Design Pattern Ticketing System", fontSize = 10.sp, color = TextSecondary)
            }
        }
        
        RoleDropdown(state)
    }
}

/** Dashboard cards showing derived statistics (Observer pattern sync) */
@Composable
fun StatisticsRow(state: AppState) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard("Open Tickets", state.openCount.toString(), Icons.Filled.Inbox, AccentPurple, Modifier.weight(1f))
        StatCard("Escalated",    state.escalatedCount.toString(), Icons.Filled.TrendingUp, YellowWarn, Modifier.weight(1f))
        StatCard("Resolved",     state.resolvedCount.toString(), Icons.Filled.CheckCircle, GreenOk, Modifier.weight(1f))
    }
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors   = CardDefaults.cardColors(containerColor = Surface1),
        shape    = RoundedCornerShape(12.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(color.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(label, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                Text(value, fontSize = 24.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/** Main table showing the ticket list with Search/Filter */
@Composable
fun TicketPanel(state: AppState, modifier: Modifier) {
    Column(modifier.padding(horizontal = 16.dp)) {
        
        // Search and Filter Bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { state.searchQuery = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search by ID or Title...", color = TextSecondary, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { state.searchQuery = "" }) {
                            Icon(Icons.Default.Clear, null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Surface1,
                    focusedContainerColor = Surface1,
                    unfocusedBorderColor = Surface2,
                    focusedBorderColor = AccentPurple,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )
            Spacer(Modifier.width(12.dp))
            Text("${state.filteredTickets.size} visible", fontSize = 11.sp, color = TextSecondary)
        }

        TableHeader()
        
        Box(Modifier.fillMaxSize()) {
            val listState = rememberLazyListState()
            LazyColumn(state = listState) {
                items(state.filteredTickets) { ticket ->
                    TicketRow(ticket, isSelected = state.selectedTicket?.id == ticket.id) {
                        state.selectedTicket = ticket
                    }
                }
            }
        }
    }
}

@Composable
fun TableHeader() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("ID",      Modifier.width(40.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
        Text("TICKET",  Modifier.weight(1f),   fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
        Text("TYPE",    Modifier.width(100.dp),fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
        Text("STATUS",  Modifier.width(100.dp),fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
        Text("PRIORITY",Modifier.width(90.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
    }
}

@Composable
fun TicketRow(ticket: Ticket, isSelected: Boolean, onClick: () -> Unit) {
    val bg = if (isSelected) Surface2 else Color.Transparent
    val border = if (isSelected) Modifier.border(1.dp, Surface2, RoundedCornerShape(8.dp)) else Modifier
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .then(border)
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("#${ticket.id}", Modifier.width(40.dp), fontSize = 12.sp, color = TextPrimary)
        Text(
            ticket.title, 
            Modifier.weight(1f).padding(end = 12.dp), 
            fontSize = 13.sp, color = TextPrimary, 
            maxLines = 1, overflow = TextOverflow.Ellipsis
        )
        TypeChip(ticket.getTypeLabel(), Modifier.width(100.dp))
        StatusChip(ticket.status, Modifier.width(100.dp))
        PriorityBadge(ticket.priority, Modifier.width(90.dp))
    }
}

@Composable
fun TypeChip(label: String, modifier: Modifier) {
    val clean = label.removePrefix("[URGENT] ")
    val color = when {
        label.contains("Bug") -> Color(0xFFC06C84)
        label.contains("Complaint") -> Color(0xFF6C5B7B)
        else -> AccentBlue.copy(alpha = 0.5f)
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        contentColor = color,
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
    ) {
        Text(clean, Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun StatusChip(status: TicketStatus, modifier: Modifier) {
    val color = when (status) {
        TicketStatus.OPEN        -> TextSecondary
        TicketStatus.IN_PROGRESS -> AccentBlue
        TicketStatus.ESCALATED   -> YellowWarn
        TicketStatus.RESOLVED    -> GreenOk
    }
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(6.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(8.dp))
        Text(status.name.replace("_", " "), fontSize = 11.sp, color = TextPrimary)
    }
}

@Composable
fun PriorityBadge(priority: Priority, modifier: Modifier) {
    val (text, color) = when (priority) {
        Priority.URGENT -> "🔴 URGENT" to RedAlert
        else            -> "NORMAL"    to TextSecondary
    }
    Text(text, modifier, fontSize = 10.sp, color = color, fontWeight = FontWeight.Bold)
}

/** Real-time system event log (Observer pattern output) */
@Composable
fun LogPanel(state: AppState, modifier: Modifier) {
    Column(modifier.background(Surface1).padding(16.dp)) {
        Text("System Events Log", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
        Spacer(Modifier.height(8.dp))
        LazyColumn(Modifier.fillMaxSize(), reverseLayout = true) {
            items(state.logLines.reversed()) { line ->
                Text(line, fontSize = 11.sp, color = TextPrimary, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, modifier = Modifier.padding(vertical = 1.dp))
            }
        }
    }
}

/** Dropdown to switch roles (enables Proxy pattern demo) */
@Composable
fun RoleDropdown(state: AppState) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Surface(
            modifier = Modifier.clickable { expanded = true },
            color = Surface2,
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(Modifier.padding(horizontal = 14.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(GreenOk))
                Spacer(Modifier.width(10.dp))
                Text("Acting As: ${state.currentRole.name.replace("_", " ")}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Icon(Icons.Default.ArrowDropDown, null, tint = TextSecondary)
            }
        }
        DropdownMenu(expanded, { expanded = false }, modifier = Modifier.background(Surface2)) {
            UserRole.values().forEach { role ->
                DropdownMenuItem(
                    text = { Text(role.name, color = TextPrimary, fontSize = 12.sp) },
                    onClick = { state.setRole(role); expanded = false }
                )
            }
        }
    }
}

// Tooltip is defined in Theme.kt

/** Shared field styling for all dialogs */
@Composable
fun dialogFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = AccentPurple,
    unfocusedBorderColor = Surface2,
    focusedTextColor     = TextPrimary,
    unfocusedTextColor   = TextPrimary,
    focusedLabelColor    = AccentPurple,
    unfocusedLabelColor  = TextSecondary
)
