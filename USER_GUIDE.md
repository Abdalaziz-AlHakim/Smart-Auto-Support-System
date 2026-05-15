# AutoSupport — User Guide & Test Specification

Welcome to the **AutoSupport** system. This project is a professional demonstration of 10 classic software design patterns integrated into a modern, reactive customer support dashboard built with Compose Multiplatform.

---

## 📖 User Guide

### 1. Getting Started
- **Launch:** Run the app using `./gradlew run`.
- **Top Bar:** On the top right, you can switch your **Active Role** (Acting As). This is not a real login, but a way to simulate different users. This role changes your permissions (enforced by the **Proxy** pattern).
- **Dashboard:** The status cards at the top show live metrics. The main table lists all tickets, and the bottom panel shows real-time system events (**Observer** pattern).

### 2. Creating Tickets
- **Manual Submission:** Click **"New Ticket"**. You can choose a type (Bug, Complaint, etc.) and mark it as **URGENT**. 
    - *Urgent Meaning:* URGENT tickets are automatically locked to use the Urgent Routing Strategy when escalated, meaning they will skip L1 agents entirely for faster review.
- **Email Simulation:** Click **"Simulate Email"**. The system scans the subject for keywords to determine the ticket type.

### 3. Processing & Escalation
- **Selection:** Click any ticket in the table to select it.
- **Escalation:** Escalation means routing a ticket to the appropriate support level for review. Choose a **Routing Strategy** from the dropdown and click **"Escalate"**. The ticket's status changes to `ESCALATED` and stays in that state. The Chain of Responsibility logs who is assigned to review it, but does **not** resolve it immediately.
    - **Standard:** Normal flow (L1 → L2 → Manager).
    - **Urgent:** Auto-selected for urgent tickets. Skips L1, starts at L2.
    - **Type-Based:** Routes Bug/Feature to L2, Inquiries to L1, and Complaints directly to the Manager.
- **Resolution:** Resolution is a separate, manual action. Click **"Resolve"** to close an escalated or open ticket. 
    - *Proxy Rule:* Agents can only Pick Up, Reopen, and Resolve tickets within their tier (L1 handles Inquiries; L2 handles Bugs/Features). No agent can resolve an `ESCALATED` ticket—only the Manager can resolve those. This is enforced by the Proxy.

### 4. Lifecycle Management
- **State Rules:** You cannot escalate a `RESOLVED` ticket.
- **Reopening:** Select a `RESOLVED` ticket and click **"Reopen"** to put it back into the queue.

---

## 🧪 Test Specification & Verification

Use these test cases to verify the architectural integrity and logic of the system.

### Case 1: State Machine & Workflow Enforcement
*Goal: Verify that illegal transitions are blocked.*
1.  **Step:** Create a new ticket (Status: `OPEN`).
2.  **Step:** Immediately click **"Resolve"**.
3.  **Expected:** An error message "Cannot resolve an OPEN ticket" appears.
4.  **Step:** Click **"Escalate"**.
5.  **Expected:** System automatically moves it to `IN_PROGRESS` then `ESCALATED`.
6.  **Step:** Try to escalate the same ticket again.
7.  **Expected:** Error "Ticket is already ESCALATED" appears.

### Case 2: Proxy & Security (Role-Based Access)
*Goal: Verify that L1 agents cannot close senior-level escalations.*
1.  **Step:** Set Role to **AGENT_L1**.
2.  **Step:** Select an `ESCALATED` ticket.
3.  **Step:** Click **"Resolve"**.
4.  **Expected:** "Access Denied" error message (enforced by **Proxy**).
5.  **Step:** Switch Role to **MANAGER**.
6.  **Step:** Click **"Resolve"** again.
7.  **Expected:** Success. Ticket status moves to `RESOLVED`.


### Case 4: Statistics & Reopening (Observer Pattern)
*Goal: Verify counter accuracy after complex loops.*
1.  **Step:** Note the **Resolved** count.
2.  **Step:** Select a Resolved ticket and click **"Reopen"**.
3.  **Expected:** **Resolved** count decreases by 1; **Open** count increases by 1.
4.  **Step:** Resolve the same ticket again.
5.  **Expected:** **Resolved** count returns to previous value (no double-counting).

### Case 5: Email Adapter
*Goal: Verify keyword detection.*
1.  **Step:** Open "Simulate Email".
2.  **Step:** Set Subject to: *"My app is crashing, help!"*
3.  **Step:** Click "Send & Convert".
4.  **Expected:** A new ticket of type **BUG** is created (detected via "crash" keyword).

---

## 🛠 Design Pattern Inventory

| Pattern | Role in AutoSupport |
|---|---|
| **Singleton** | `TicketSystem`: The one true source of ticket data. |
| **Factory Method** | `TicketFactory`: Creates Bug, Complaint, etc. subclasses. |
| **Facade** | `SupportFacade`: Simplifies complex submission/escalation pipelines. |
| **Adapter** | `EmailTicketAdapter`: Converts `EmailMessage` → `Ticket`. |
| **Decorator** | `UrgentTicketDecorator`: Adds urgency metadata to any ticket. |
| **Proxy** | `TicketSystemProxy`: Enforces role-based security. |
| **State** | `TicketContext`: Manages legal status transitions. |
| **Observer** | `TicketEvent`: Updates UI and stats in real-time as events occur. |
| **Chain of Responsibility** | `SupportHandler`: Passes tickets through L1 → L2 → Manager. |
