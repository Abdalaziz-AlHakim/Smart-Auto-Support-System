# AutoSupport — Intelligent Customer Support Ticket System
## Full Implementation Prompt (Java + Swing GUI)

---

## 0. Project Summary

Build a desktop Java application called **AutoSupport** — a customer support ticket management system. It allows users to submit support tickets (manually or from a simulated email), mark them as urgent, escalate them through support levels, and resolve them. The app demonstrates 6 Gang-of-Four design patterns in a realistic, cohesive system.

**Language:** Java (JDK 11+)  
**GUI:** Java Swing  
**Build:** Plain Java project (no Maven/Gradle needed, unless preferred)  
**Entry point:** `Main.java` at the root package  

---

## 1. Complete File & Package Structure

```
AutoSupport/
│
├── Main.java
│
├── creational/
│   ├── singleton/
│   │   └── TicketSystem.java
│   └── factory/
│       ├── TicketFactory.java
│       ├── Ticket.java
│       ├── BugTicket.java
│       ├── ComplaintTicket.java
│       ├── FeatureRequestTicket.java
│       └── InquiryTicket.java
│
├── structural/
│   ├── facade/
│   │   └── SupportFacade.java
│   ├── adapter/
│   │   ├── EmailMessage.java
│   │   ├── TicketSource.java
│   │   └── EmailTicketAdapter.java
│   └── decorator/
│       ├── TicketDecorator.java
│       └── UrgentTicketDecorator.java
│
├── behavioral/
│   └── chain/
│       ├── SupportHandler.java
│       ├── Level1Handler.java
│       ├── Level2Handler.java
│       └── ManagerHandler.java
│
└── gui/
    └── MainGUI.java
```

---

## 2. Enums & Shared Constants

Define these enums inside their most relevant class or as standalone files:

```java
// TicketType.java (inside creational/factory/ or root)
public enum TicketType {
    BUG, COMPLAINT, FEATURE_REQUEST, INQUIRY
}

// TicketStatus.java
public enum TicketStatus {
    OPEN, IN_PROGRESS, ESCALATED_L1, ESCALATED_L2, ESCALATED_MANAGER, RESOLVED
}

// TicketPriority.java
public enum TicketPriority {
    NORMAL, URGENT
}
```

---

## 3. Pattern 1 — Singleton: `TicketSystem`

**Package:** `creational.singleton`

**Purpose:** One and only one global registry of all tickets in the application. Any class can call `TicketSystem.getInstance()` to access or modify the ticket list without passing it around.

**Implementation:**
```java
public class TicketSystem {
    private static TicketSystem instance;
    private List<Ticket> tickets = new ArrayList<>();
    private int idCounter = 1;

    private TicketSystem() {}

    public static TicketSystem getInstance() {
        if (instance == null) {
            instance = new TicketSystem();
        }
        return instance;
    }

    public void addTicket(Ticket t) { tickets.add(t); }
    public List<Ticket> getAllTickets() { return tickets; }
    public int generateId() { return idCounter++; }
    public void removeTicket(Ticket t) { tickets.remove(t); }
}
```

---

## 4. Pattern 2 — Factory Method: `TicketFactory`

**Package:** `creational.factory`

### `Ticket.java` (Abstract base class)
```java
public abstract class Ticket {
    protected int id;
    protected String title;
    protected String description;
    protected TicketType type;
    protected TicketStatus status;
    protected TicketPriority priority;

    public Ticket(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = TicketStatus.OPEN;
        this.priority = TicketPriority.NORMAL;
    }

    public abstract String getTypeLabel();

    // Getters and setters for all fields
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TicketType getType() { return type; }
    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }
    public TicketPriority getPriority() { return priority; }
    public void setPriority(TicketPriority priority) { this.priority = priority; }

    @Override
    public String toString() {
        return "[#" + id + "] " + getTypeLabel() + " - " + title + " | " + status + " | " + priority;
    }
}
```

### Concrete Ticket Subclasses
Each subclass calls `super(id, title, description)`, sets its own `this.type`, and implements `getTypeLabel()`:

- `BugTicket` → type = `TicketType.BUG`, label = `"Bug"`
- `ComplaintTicket` → type = `TicketType.COMPLAINT`, label = `"Complaint"`
- `FeatureRequestTicket` → type = `TicketType.FEATURE_REQUEST`, label = `"Feature Request"`
- `InquiryTicket` → type = `TicketType.INQUIRY`, label = `"Inquiry"`

### `TicketFactory.java`
```java
public class TicketFactory {
    public static Ticket createTicket(TicketType type, String title, String description) {
        int id = TicketSystem.getInstance().generateId();
        switch (type) {
            case BUG:             return new BugTicket(id, title, description);
            case COMPLAINT:       return new ComplaintTicket(id, title, description);
            case FEATURE_REQUEST: return new FeatureRequestTicket(id, title, description);
            case INQUIRY:         return new InquiryTicket(id, title, description);
            default: throw new IllegalArgumentException("Unknown ticket type");
        }
    }
}
```

---

## 5. Pattern 3 — Facade: `SupportFacade`

**Package:** `structural.facade`

**Purpose:** Hides the complexity of ticket creation, decoration, registration, and logging behind a single `submitTicket()` method.

```java
public class SupportFacade {
    private TicketSystem system = TicketSystem.getInstance();
    private List<String> log;

    public SupportFacade(List<String> sharedLog) {
        this.log = sharedLog;
    }

    /**
     * Full ticket submission pipeline in one call:
     * 1. Create ticket via Factory
     * 2. Optionally wrap with UrgentDecorator
     * 3. Register in TicketSystem
     * 4. Log the action
     */
    public Ticket submitTicket(TicketType type, String title, String description, boolean urgent) {
        Ticket ticket = TicketFactory.createTicket(type, title, description);
        log.add("> Ticket #" + ticket.getId() + " created as " + ticket.getTypeLabel());

        if (urgent) {
            ticket = new UrgentTicketDecorator(ticket);
            log.add("> Ticket #" + ticket.getId() + " decorated as URGENT");
        }

        ticket.setStatus(TicketStatus.IN_PROGRESS);
        system.addTicket(ticket);
        log.add("> Ticket #" + ticket.getId() + " registered in system. Status: IN_PROGRESS");
        return ticket;
    }

    /**
     * Submit a ticket sourced from a simulated email (uses Adapter internally)
     */
    public Ticket submitFromEmail(EmailMessage email, boolean urgent) {
        TicketSource adapter = new EmailTicketAdapter(email);
        Ticket ticket = adapter.toTicket();
        log.add("> Email from [" + email.getSender() + "] adapted into Ticket #" + ticket.getId());

        if (urgent) {
            ticket = new UrgentTicketDecorator(ticket);
            log.add("> Ticket #" + ticket.getId() + " decorated as URGENT");
        }

        ticket.setStatus(TicketStatus.IN_PROGRESS);
        system.addTicket(ticket);
        log.add("> Ticket #" + ticket.getId() + " registered from email. Status: IN_PROGRESS");
        return ticket;
    }
}
```

---

## 6. Pattern 4 — Adapter: `EmailTicketAdapter`

**Package:** `structural.adapter`

**Purpose:** An `EmailMessage` (external/legacy format with sender, subject, body) cannot be directly used as a `Ticket`. The adapter bridges the two incompatible interfaces.

### `EmailMessage.java` (the Adaptee — existing incompatible class)
```java
public class EmailMessage {
    private String sender;
    private String subject;
    private String body;

    public EmailMessage(String sender, String subject, String body) {
        this.sender = sender;
        this.subject = subject;
        this.body = body;
    }

    public String getSender() { return sender; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
}
```

### `TicketSource.java` (the Target interface)
```java
public interface TicketSource {
    Ticket toTicket();
}
```

### `EmailTicketAdapter.java` (the Adapter)
```java
public class EmailTicketAdapter implements TicketSource {
    private EmailMessage email;

    public EmailTicketAdapter(EmailMessage email) {
        this.email = email;
    }

    @Override
    public Ticket toTicket() {
        // Infer type from subject keywords, default to Inquiry
        TicketType type = TicketType.INQUIRY;
        String subject = email.getSubject().toLowerCase();
        if (subject.contains("bug") || subject.contains("error") || subject.contains("crash"))
            type = TicketType.BUG;
        else if (subject.contains("complaint") || subject.contains("unhappy"))
            type = TicketType.COMPLAINT;
        else if (subject.contains("feature") || subject.contains("request"))
            type = TicketType.FEATURE_REQUEST;

        int id = TicketSystem.getInstance().generateId();
        String title = email.getSubject();
        String description = "From: " + email.getSender() + "\n" + email.getBody();

        return TicketFactory.createTicket(type, title, description);
    }
}
```

---

## 7. Pattern 5 — Decorator: `UrgentTicketDecorator`

**Package:** `structural.decorator`

**Purpose:** Wraps any `Ticket` at runtime to add urgent priority behavior — without subclassing every ticket type separately.

### `TicketDecorator.java` (Abstract Decorator)
```java
public abstract class TicketDecorator extends Ticket {
    protected Ticket wrappedTicket;

    public TicketDecorator(Ticket ticket) {
        super(ticket.getId(), ticket.getTitle(), ticket.getDescription());
        this.wrappedTicket = ticket;
        this.type = ticket.getType();
        this.status = ticket.getStatus();
        this.priority = ticket.getPriority();
    }

    @Override
    public String getTypeLabel() {
        return wrappedTicket.getTypeLabel();
    }
}
```

### `UrgentTicketDecorator.java` (Concrete Decorator)
```java
public class UrgentTicketDecorator extends TicketDecorator {
    public UrgentTicketDecorator(Ticket ticket) {
        super(ticket);
        this.priority = TicketPriority.URGENT;
    }

    @Override
    public String getTypeLabel() {
        return "[URGENT] " + wrappedTicket.getTypeLabel();
    }

    @Override
    public String toString() {
        return "🔴 " + super.toString();
    }
}
```

---

## 8. Pattern 6 — Chain of Responsibility: Escalation Chain

**Package:** `behavioral.chain`

**Purpose:** A ticket is passed through a chain of handlers. Each handler either resolves it or passes it up. Models real support tiers.

**Resolution logic:**
- `Level1Handler` resolves tickets of type `INQUIRY` only
- `Level2Handler` resolves tickets of type `BUG` or `FEATURE_REQUEST`
- `ManagerHandler` resolves everything else (COMPLAINT, or anything unresolved)

### `SupportHandler.java` (Abstract Handler)
```java
public abstract class SupportHandler {
    protected SupportHandler nextHandler;
    protected List<String> log;

    public SupportHandler(List<String> log) {
        this.log = log;
    }

    public void setNext(SupportHandler next) {
        this.nextHandler = next;
    }

    public abstract void handle(Ticket ticket);

    protected void escalate(Ticket ticket) {
        if (nextHandler != null) {
            nextHandler.handle(ticket);
        } else {
            log.add("> No handler could resolve Ticket #" + ticket.getId());
            ticket.setStatus(TicketStatus.RESOLVED); // fallback
        }
    }
}
```

### `Level1Handler.java`
```java
public class Level1Handler extends SupportHandler {
    public Level1Handler(List<String> log) { super(log); }

    @Override
    public void handle(Ticket ticket) {
        ticket.setStatus(TicketStatus.ESCALATED_L1);
        log.add("> L1 Agent handling Ticket #" + ticket.getId() + " (" + ticket.getTypeLabel() + ")");
        if (ticket.getType() == TicketType.INQUIRY) {
            ticket.setStatus(TicketStatus.RESOLVED);
            log.add("> L1 Agent RESOLVED Ticket #" + ticket.getId());
        } else {
            log.add("> L1 cannot resolve. Escalating to L2...");
            ticket.setStatus(TicketStatus.ESCALATED_L2);
            escalate(ticket);
        }
    }
}
```

### `Level2Handler.java`
```java
public class Level2Handler extends SupportHandler {
    public Level2Handler(List<String> log) { super(log); }

    @Override
    public void handle(Ticket ticket) {
        log.add("> L2 Agent handling Ticket #" + ticket.getId());
        if (ticket.getType() == TicketType.BUG || ticket.getType() == TicketType.FEATURE_REQUEST) {
            ticket.setStatus(TicketStatus.RESOLVED);
            log.add("> L2 Agent RESOLVED Ticket #" + ticket.getId());
        } else {
            log.add("> L2 cannot resolve. Escalating to Manager...");
            ticket.setStatus(TicketStatus.ESCALATED_MANAGER);
            escalate(ticket);
        }
    }
}
```

### `ManagerHandler.java`
```java
public class ManagerHandler extends SupportHandler {
    public ManagerHandler(List<String> log) { super(log); }

    @Override
    public void handle(Ticket ticket) {
        log.add("> Manager handling Ticket #" + ticket.getId());
        ticket.setStatus(TicketStatus.RESOLVED);
        log.add("> Manager RESOLVED Ticket #" + ticket.getId());
    }
}
```

---

## 9. GUI: `MainGUI.java`

**Package:** `gui`

Build a single `JFrame` with the following layout using `BorderLayout`:

### Layout Specification

```
┌──────────────────────────────────────────────────────────────┐
│  TITLE: "AutoSupport — Ticket Management System"  [top bar]  │
├─────────────────────┬────────────────────────────────────────┤
│   LEFT PANEL        │   CENTER: JTable (ticket list)         │
│   (controls)        │   Columns:                             │
│                     │   ID | Type | Priority | Status        │
│ [Ticket Title: ___] │                                        │
│ [Description:  ___] │                                        │
│ [Type dropdown    ] │                                        │
│ [x] Mark as Urgent  ├────────────────────────────────────────┤
│                     │   BOTTOM: Log Panel (JTextArea)        │
│ [Submit Ticket]     │   Shows all system events in real-time │
│ [Simulate Email]    │                                        │
│ [Escalate Selected] │                                        │
│ [Resolve Selected]  │                                        │
│ [Clear Log]         │                                        │
└─────────────────────┴────────────────────────────────────────┘
```

### GUI Behavior Details

- **Submit Ticket button:** Reads title, description, type dropdown, urgent checkbox. Calls `SupportFacade.submitTicket(...)`. Refreshes the table. Appends to log.
- **Simulate Email button:** Opens a small `JDialog` with fields: Sender, Subject, Body. On confirm, creates an `EmailMessage` and calls `SupportFacade.submitFromEmail(...)`. Refreshes table and log.
- **Escalate Selected button:** Gets selected ticket from table. Builds the chain (`L1 → L2 → Manager`) and calls `chain.handle(ticket)`. Refreshes table and log.
- **Resolve Selected button:** Directly sets selected ticket status to `RESOLVED`. Logs the action. Refreshes table.
- **Clear Log button:** Clears the `JTextArea`.
- **Table rows** are colored: URGENT tickets show a light red background row. RESOLVED tickets show light green. Use a custom `TableCellRenderer` for this.
- The `List<String> log` is shared between `SupportFacade` and the chain handlers, so all messages appear in the same log panel.
- Call `SwingUtilities.invokeLater(...)` to launch the GUI from `Main.java`.

### Table Model

Use a `DefaultTableModel` with columns `["ID", "Type", "Priority", "Status", "Title"]`. Refresh it by calling `.setRowCount(0)` and re-populating from `TicketSystem.getInstance().getAllTickets()` after every action.

---

## 10. `Main.java`

```java
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainGUI gui = new MainGUI();
            gui.setVisible(true);
        });
    }
}
```

---

## 11. Pattern Justification (Why This Pattern, Not Another)

### Singleton — Why not a static class?
A static class cannot implement interfaces, cannot be passed as a dependency, and cannot be lazily initialized. Singleton gives us a real object that can be mocked, extended, and passed around. It also maps to a real business rule: there is exactly one support system running at any time.

### Factory Method — Why not Abstract Factory or Builder?
- **Abstract Factory** creates *families* of related objects (e.g., creating both a ticket AND its handler together). We only need one product hierarchy (Ticket types), so Abstract Factory is overkill.
- **Builder** is for objects with many optional, complex construction steps (e.g., building a HTTP request). A Ticket has simple, uniform construction — type, title, description. Factory Method is the right fit.

### Facade — Why not Mediator?
- **Mediator** manages many-to-many communication *between peer objects* (e.g., chat room participants). Our case is different: a client wants to do one thing (submit a ticket) without knowing the steps behind it. That is exactly Facade's job — simplifying a subsystem for outsiders, not coordinating peers.

### Adapter — Why not Bridge?
- **Bridge** is a *proactive* pattern — you design it upfront to separate abstraction from implementation so both can vary independently in the future. 
- **Adapter** is a *reactive* pattern — you already have an incompatible class (`EmailMessage`) that you cannot change, and you need to plug it into an existing interface. That is our exact situation.

### Decorator — Why not subclassing (e.g., `UrgentBugTicket`, `UrgentComplaintTicket`)?
If we subclassed for urgency, we'd need a new subclass for *every combination* of type × priority — that's 8 subclasses minimum and explodes with more attributes. Decorator adds behavior at runtime to *any* ticket, composably, with one class. It also respects the Open/Closed Principle: we never touched the original ticket classes.

### Chain of Responsibility — Why not Strategy or State?
- **Strategy** selects *one* algorithm from a family to execute a task (e.g., different sorting strategies). It doesn't pass a request along — it just picks one handler. That's not escalation.
- **State** changes an object's *behavior based on its own internal state*. It's about the object itself changing, not about routing a request between external handlers.
- **Chain of Responsibility** is specifically designed for: "try this handler, if it can't handle it, pass it to the next." That is the definition of a support escalation chain.

---

## 12. Class Responsibility Table (for the Report)

| Class | Package | Pattern | Responsibility |
|---|---|---|---|
| `TicketSystem` | creational.singleton | Singleton | Global registry and ID generator for all tickets |
| `TicketFactory` | creational.factory | Factory Method | Creates the correct Ticket subclass based on type |
| `Ticket` | creational.factory | Factory Method | Abstract base class for all ticket types |
| `BugTicket` | creational.factory | Factory Method | Concrete product: Bug type ticket |
| `ComplaintTicket` | creational.factory | Factory Method | Concrete product: Complaint type ticket |
| `FeatureRequestTicket` | creational.factory | Factory Method | Concrete product: Feature Request ticket |
| `InquiryTicket` | creational.factory | Factory Method | Concrete product: Inquiry type ticket |
| `SupportFacade` | structural.facade | Facade | One-call interface for the full ticket submission pipeline |
| `EmailMessage` | structural.adapter | Adapter | The Adaptee — external email format incompatible with Ticket |
| `TicketSource` | structural.adapter | Adapter | The Target interface expected by the system |
| `EmailTicketAdapter` | structural.adapter | Adapter | Converts EmailMessage into a Ticket object |
| `TicketDecorator` | structural.decorator | Decorator | Abstract wrapper that extends Ticket behavior |
| `UrgentTicketDecorator` | structural.decorator | Decorator | Adds URGENT priority and visual marker to any ticket |
| `SupportHandler` | behavioral.chain | Chain of Responsibility | Abstract handler defining the chain contract |
| `Level1Handler` | behavioral.chain | Chain of Responsibility | Resolves Inquiry tickets; escalates others |
| `Level2Handler` | behavioral.chain | Chain of Responsibility | Resolves Bug/Feature tickets; escalates Complaints |
| `ManagerHandler` | behavioral.chain | Chain of Responsibility | Final handler; resolves all remaining tickets |
| `MainGUI` | gui | — | Swing UI: ticket table, controls, real-time log panel |
| `Main` | root | — | Entry point; launches GUI on the Swing event thread |

---

## 13. Key Coding Rules for the AI

1. All classes must include **Javadoc comments** on the class and every public method.
2. Use `TicketSystem.getInstance()` everywhere — never `new TicketSystem()`.
3. The `log` (`List<String>`) must be **one shared instance** passed by reference into `SupportFacade` and all chain handlers, so all messages appear in the same GUI log panel.
4. The chain must be **rebuilt** each time the Escalate button is clicked (do not reuse a stale chain from a previous escalation).
5. `UrgentTicketDecorator` must override `toString()` to return a string prefixed with `"🔴 "` so the GUI table row can be visually distinct.
6. The GUI table **must use a custom `TableCellRenderer`** to color rows: red-tinted for URGENT, green-tinted for RESOLVED.
7. All GUI actions must call a `refreshTable()` helper method after modifying data.
8. The email simulation dialog must be a `JDialog` (modal), not a new `JFrame`.
9. Enum files (`TicketType`, `TicketStatus`, `TicketPriority`) should each be in their own file at the root package or in `creational/factory/`.
10. Do not use external libraries. Pure Java + Swing only.
