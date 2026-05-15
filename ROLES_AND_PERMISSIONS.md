# AutoSupport Roles & Permissions

This document outlines the various "Acting As" roles available in the AutoSupport system and the specific permissions associated with each role. In the codebase, role-based security is strictly enforced through the **Proxy** design pattern (`TicketSystemProxy`).

## 1. Available Roles

1. **`AGENT_L1`** (Level 1 Support Agent)
   - Handles general inquiries and front-line support tasks.
2. **`AGENT_L2`** (Level 2 Support Agent)
   - Handles technical bugs and feature requests.
3. **`MANAGER`** (Support Manager)
   - Handles complaints, high-level disputes, and serves as the final authority on escalations. Has unrestricted access across the system.

*(Note: The previously redundant `ADMIN` role has been removed to streamline the hierarchy).*

---

## 2. Permissions Matrix

| Action / Operation | AGENT_L1 | AGENT_L2 | MANAGER |
| :--- | :--- | :--- | :--- |
| **View Tickets & Logs** | ✅ Yes | ✅ Yes | ✅ Yes |
| **Submit / Simulate Email** | ✅ Yes | ✅ Yes | ✅ Yes |
| **Escalate Ticket** | ✅ Yes | ✅ Yes | ✅ Yes |
| **Pick Up `INQUIRY`** | ✅ Yes | ✅ Yes | ✅ Yes |
| **Pick Up `BUG` / `FEATURE`** | ❌ No | ✅ Yes | ✅ Yes |
| **Pick Up `COMPLAINT`** | ❌ No | ❌ No | ✅ Yes |
| **Resolve `INQUIRY`** (Not Escalated) | ✅ Yes | ✅ Yes | ✅ Yes |
| **Resolve `BUG` / `FEATURE`** (Not Escalated) | ❌ No | ✅ Yes | ✅ Yes |
| **Resolve `COMPLAINT`** (Not Escalated) | ❌ No | ❌ No | ✅ Yes |
| **Resolve `ESCALATED` Ticket** (Any Type) | ❌ No | ❌ No | ✅ Yes |
| **Reopen Ticket** | *Same rules as Resolve* | *Same rules as Resolve* | ✅ Yes |

---

## 3. Detailed Action Breakdown

### Unrestricted Actions (All Roles)
Because this is a collaborative support environment, all authenticated roles have broad permissions to view, create, and escalate work:
- **View & Submit:** Everyone can see the ticket board, create new tickets, or convert incoming emails into tickets.
- **Escalate:** Any agent can escalate a ticket if they cannot handle it. This relies on the **Routing Strategy** to pass it to the correct tier.

### Restricted Actions: Pick Up, Reopen & Resolve
The **Proxy** pattern strictly guards actions that imply ownership or final resolution (`startProgress`, `resolveTicket`, `reopenTicket`).
- **L1 Agents** are strictly prohibited from picking up, resolving, or reopening anything other than `INQUIRY` tickets.
- **L2 Agents** are restricted from interacting with `COMPLAINT` tickets.
- **Escalation Lock:** Once a ticket of *any* type is marked as `ESCALATED`, it requires managerial oversight. **L1 and L2 Agents** are strictly prohibited from resolving an `ESCALATED` ticket, even if it matches their allowed ticket type. 

*(Implementation Detail: `TicketSystemProxy` intercepts these actions. If the ticket type or status does not match the current session's role, a `SecurityException` is thrown before the real `TicketSystem` is ever touched).*
