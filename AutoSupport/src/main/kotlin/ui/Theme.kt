package ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Shared colour palette ─────────────────────────────────────────────────────
// All UI files import from here — no duplication of colour constants.

val BgDark        = Color(0xFF0F1117)   // Main window background
val Surface1      = Color(0xFF1A1D27)   // Cards, panels
val Surface2      = Color(0xFF252939)   // Dropdowns, dialogs, dividers
val AccentPurple  = Color(0xFF7C6AF7)   // Primary accent (buttons, selection)
val AccentBlue    = Color(0xFF4D9EF7)   // Secondary accent (email, info)
val GreenOk       = Color(0xFF3DD68C)   // Resolved / success
val YellowWarn    = Color(0xFFF5C842)   // Escalated / warning
val RedAlert      = Color(0xFFF74D6A)   // Urgent / error
val TextPrimary   = Color(0xFFEAEBF0)   // Main text
val TextSecondary = Color(0xFF8A8FA8)   // Labels, hints

// ── Shared tooltip composable ─────────────────────────────────────────────────
// Wrap any composable with Tooltip() to show a hover hint after 500 ms.
// Requires @OptIn(ExperimentalFoundationApi::class) at call sites.

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Tooltip(tip: String, content: @Composable () -> Unit) {
    TooltipArea(
        tooltip = {
            // Tooltip bubble styled to match the dark theme
            Surface(
                shape     = RoundedCornerShape(6.dp),
                color     = Surface2,
                modifier  = Modifier.shadow(6.dp, RoundedCornerShape(6.dp))
            ) {
                Text(
                    text     = tip,
                    color    = TextPrimary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        },
        delayMillis      = 500,                          // Delay before tooltip appears
        tooltipPlacement = TooltipPlacement.CursorPoint( // Position near the cursor
            alignment = Alignment.BottomEnd,
            offset    = DpOffset(0.dp, 16.dp)
        ),
        content = content
    )
}
