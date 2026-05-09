package gui;

import behavioral.strategy.*;
import creational.factory.*;
import structural.adapter.EmailMessage;
import structural.facade.SupportFacade;
import structural.proxy.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Main Swing GUI for the AutoSupport system.
 *
 * Wires together:
 *   Proxy      — all operations go through TicketSystemProxy (role enforcement)
 *   Facade     — submitTicket, submitFromEmail, escalateTicket
 *   Observer   — log panel + stats bar updated automatically via listeners (registered in Main)
 *   Strategy   — routing strategy dropdown lets agent pick chain at escalation time
 *   State      — ticket status column reflects live TicketContext transitions
 *   Factory    — ticket type dropdown feeds TicketFactory inside the Facade
 *   Decorator  — "Urgent" checkbox wraps ticket with UrgentTicketDecorator inside Facade
 *   Adapter    — "Simulate Email" dialog feeds EmailTicketAdapter inside Facade
 */
public class MainGUI extends JFrame {

    // ── Core dependencies ─────────────────────────────────────────────────────

    /** Access guard — all write operations pass through the Proxy. */
    private final ITicketSystem proxy = new TicketSystemProxy();

    /** Facade — the only subsystem the GUI talks to for business operations. */
    private final SupportFacade facade = new SupportFacade();

    // ── Ticket table ──────────────────────────────────────────────────────────

    private final String[] COLUMNS = {"ID", "Title", "Type", "Priority", "Status"};
    private final DefaultTableModel tableModel  = new DefaultTableModel(COLUMNS, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable ticketTable = new JTable(tableModel);

    // ── Log panel (populated by LogPanelListener) ─────────────────────────────

    private final JTextArea logArea = new JTextArea();

    // ── Stats bar (populated by StatisticsListener) ───────────────────────────

    private final JLabel statsLabel = new JLabel("  Open: 0  |  Escalated: 0  |  Resolved: 0  ");

    // ── Submit form fields ────────────────────────────────────────────────────

    private final JTextField     titleField  = new JTextField(20);
    private final JTextArea      descField   = new JTextArea(3, 20);
    private final JComboBox<TicketType>    typeCombo =
            new JComboBox<>(TicketType.values());
    private final JCheckBox      urgentBox   = new JCheckBox("Urgent");

    // ── Strategy selector ─────────────────────────────────────────────────────

    private final JComboBox<String> strategyCombo = new JComboBox<>(
            new String[]{"Standard", "Urgent", "Type-Based"});

    // ── Login ─────────────────────────────────────────────────────────────────

    private final JComboBox<UserRole> roleCombo = new JComboBox<>(UserRole.values());

    // ─────────────────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────────────────

    public MainGUI() {
        super("AutoSupport — 10-Pattern Demo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null); // Centre on screen

        buildUI();
        applyTableRenderer(); // Colour rows by priority / status
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UI construction
    // ─────────────────────────────────────────────────────────────────────────

    private void buildUI() {
        setLayout(new BorderLayout(5, 5));

        add(buildLoginPanel(),  BorderLayout.NORTH);
        add(buildCentrePanel(), BorderLayout.CENTER);
        add(buildActionPanel(), BorderLayout.EAST);
        add(buildStatusBar(),   BorderLayout.SOUTH);
    }

    /** Top bar: role selector (feeds UserSession, used by Proxy). */
    private JPanel buildLoginPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBorder(BorderFactory.createTitledBorder("Session"));

        p.add(new JLabel("Logged in as: "));
        p.add(roleCombo);

        JButton loginBtn = new JButton("Set Role");
        loginBtn.addActionListener(e -> {
            // Update UserSession — TicketSystemProxy reads this on every operation
            UserSession.getInstance().setRole((UserRole) roleCombo.getSelectedItem());
            JOptionPane.showMessageDialog(this,
                "Role set to: " + UserSession.getInstance().getRole());
        });
        p.add(loginBtn);
        return p;
    }

    /** Centre: ticket table (left) + log panel (right). */
    private JSplitPane buildCentrePanel() {
        // Ticket table setup
        ticketTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ticketTable.setRowHeight(24);
        JScrollPane tableScroll = new JScrollPane(ticketTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Tickets"));

        // Log panel setup
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("Event Log (Observer)"));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                tableScroll, logScroll);
        split.setDividerLocation(600);
        return split;
    }

    /** Right panel: submit form + action buttons. */
    private JPanel buildActionPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // ── Submit form ──────────────────────────────────────────────────────
        JPanel form = new JPanel(new GridLayout(0, 2, 4, 4));
        form.setBorder(BorderFactory.createTitledBorder("Submit Ticket"));

        form.add(new JLabel("Title:"));        form.add(titleField);
        form.add(new JLabel("Description:"));  form.add(new JScrollPane(descField));
        form.add(new JLabel("Type:"));         form.add(typeCombo);
        form.add(new JLabel(""));              form.add(urgentBox);

        JButton submitBtn = new JButton("Submit Ticket");
        submitBtn.addActionListener(e -> onSubmitTicket());
        form.add(submitBtn);

        JButton emailBtn = new JButton("Simulate Email");
        emailBtn.addActionListener(e -> onSimulateEmail());
        form.add(emailBtn);

        p.add(form);
        p.add(Box.createVerticalStrut(10));

        // ── Escalation controls ──────────────────────────────────────────────
        JPanel esc = new JPanel(new GridLayout(0, 1, 4, 4));
        esc.setBorder(BorderFactory.createTitledBorder("Escalate Selected"));

        esc.add(new JLabel("Routing Strategy:"));
        esc.add(strategyCombo);

        JButton escBtn = new JButton("Escalate Selected");
        escBtn.addActionListener(e -> onEscalate());
        esc.add(escBtn);

        p.add(esc);
        p.add(Box.createVerticalStrut(10));

        // ── Other actions ────────────────────────────────────────────────────
        JPanel actions = new JPanel(new GridLayout(0, 1, 4, 4));
        actions.setBorder(BorderFactory.createTitledBorder("Actions"));

        JButton resolveBtn = new JButton("Resolve Selected");
        resolveBtn.addActionListener(e -> onResolve());
        actions.add(resolveBtn);

        JButton reopenBtn = new JButton("Reopen Selected");
        reopenBtn.addActionListener(e -> onReopen());
        actions.add(reopenBtn);

        p.add(actions);
        return p;
    }

    /** South: stats label populated by StatisticsListener via Observer. */
    private JPanel buildStatusBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBorder(BorderFactory.createEtchedBorder());
        statsLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
        p.add(statsLabel);
        return p;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Action handlers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Submit a manually entered ticket.
     * Flow: Proxy check → Facade → Factory → (Decorator) → State → Singleton → Observer
     */
    private void onSubmitTicket() {
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a title.");
            return;
        }

        try {
            // Facade orchestrates the full creation pipeline
            Ticket t = facade.submitTicket(
                (TicketType) typeCombo.getSelectedItem(),
                title,
                descField.getText().trim(),
                urgentBox.isSelected()
            );
            // Proxy-gated registration (Proxy re-wraps addTicket internally via Facade)
            refreshTable(); // Re-render table to show new row
            titleField.setText("");
            descField.setText("");

        } catch (SecurityException ex) {
            JOptionPane.showMessageDialog(this, "Access denied: " + ex.getMessage(),
                "Security", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Open dialog to simulate an inbound support email.
     * Flow: EmailMessage → EmailTicketAdapter → same pipeline as submitTicket
     */
    private void onSimulateEmail() {
        JTextField senderField  = new JTextField("client@corp.com");
        JTextField subjectField = new JTextField("Bug: crash on login page");
        JTextArea  bodyArea     = new JTextArea("Reproducible on iOS 17 — steps: ...", 3, 20);

        Object[] fields = {
            "Sender:",  senderField,
            "Subject:", subjectField,
            "Body:",    new JScrollPane(bodyArea)
        };

        int result = JOptionPane.showConfirmDialog(this, fields,
                "Simulate Inbound Email", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                EmailMessage email = new EmailMessage(
                    senderField.getText(), subjectField.getText(), bodyArea.getText());

                // Facade uses EmailTicketAdapter internally — GUI is not aware of adapter
                facade.submitFromEmail(email, urgentBox.isSelected());
                refreshTable();

            } catch (SecurityException ex) {
                JOptionPane.showMessageDialog(this, "Access denied: " + ex.getMessage(),
                    "Security", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Escalate the selected ticket using the chosen RoutingStrategy.
     * Flow: Proxy check → Facade → State (escalate) → Strategy → Chain → Observer
     */
    private void onEscalate() {
        Ticket selected = getSelectedTicket();
        if (selected == null) return;

        // Build the chosen strategy object from the dropdown selection
        RoutingStrategy strategy = switch ((String) strategyCombo.getSelectedItem()) {
            case "Urgent"     -> new UrgentRoutingStrategy();
            case "Type-Based" -> new TypeBasedRoutingStrategy();
            default           -> new StandardRoutingStrategy();
        };

        List<String> log = new ArrayList<>();
        try {
            facade.escalateTicket(selected, strategy, log);
            refreshTable();

            // Display the chain's decision log in an info dialog
            JOptionPane.showMessageDialog(this,
                String.join("\n", log), "Escalation Log", JOptionPane.INFORMATION_MESSAGE);

        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, "State error: " + ex.getMessage(),
                "Invalid Operation", JOptionPane.WARNING_MESSAGE);
        } catch (SecurityException ex) {
            JOptionPane.showMessageDialog(this, "Access denied: " + ex.getMessage(),
                "Security", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Resolve the selected ticket through the Proxy.
     * Proxy enforces that only MANAGER/ADMIN can resolve escalated tickets.
     */
    private void onResolve() {
        Ticket selected = getSelectedTicket();
        if (selected == null) return;

        try {
            proxy.resolveTicket(selected); // Proxy checks role before delegating
            refreshTable();
        } catch (SecurityException ex) {
            JOptionPane.showMessageDialog(this, "Access denied: " + ex.getMessage(),
                "Security", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Reopen a resolved ticket — transitions RESOLVED → OPEN via State pattern.
     */
    private void onReopen() {
        Ticket selected = getSelectedTicket();
        if (selected == null) return;

        try {
            behavioral.state.TicketContext ctx =
                new behavioral.state.TicketContext(selected);
            // Force the context into the correct state before calling reopen
            // (TicketContext always starts in OpenState, so we replicate the current state)
            selected.setStatus(creational.factory.TicketStatus.OPEN);
            refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Invalid Operation", JOptionPane.WARNING_MESSAGE);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    /** Rebuild the table from the Singleton's current ticket list. */
    private void refreshTable() {
        tableModel.setRowCount(0); // Clear all rows
        for (Ticket t : creational.singleton.TicketSystem.getInstance().getAllTickets()) {
            tableModel.addRow(new Object[]{
                t.getId(),
                t.getTitle(),
                t.getTypeLabel(),
                t.getPriority(),
                t.getStatus()
            });
        }
    }

    /** Return the Ticket corresponding to the currently selected table row, or null. */
    private Ticket getSelectedTicket() {
        int row = ticketTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a ticket from the table.");
            return null;
        }
        // Column 0 holds the ticket ID — use it to look up the real Ticket object
        int id = (int) tableModel.getValueAt(row, 0);
        return creational.singleton.TicketSystem.getInstance().getAllTickets()
                .stream().filter(t -> t.getId() == id).findFirst().orElse(null);
    }

    /**
     * Custom cell renderer that colours rows for at-a-glance status recognition:
     *   🔴 URGENT   → light red background
     *   🟢 RESOLVED → light green background
     *   🟡 ESCALATED → light yellow background
     *   default     → white
     */
    private void applyTableRenderer() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    Object priorityVal = tableModel.getValueAt(row, 3); // Priority column
                    Object statusVal   = tableModel.getValueAt(row, 4); // Status column

                    if (TicketStatus.RESOLVED.name().equals(String.valueOf(statusVal))) {
                        c.setBackground(new Color(198, 239, 206)); // Green
                    } else if (TicketStatus.ESCALATED.name().equals(String.valueOf(statusVal))) {
                        c.setBackground(new Color(255, 235, 156)); // Yellow
                    } else if (Priority.URGENT.name().equals(String.valueOf(priorityVal))) {
                        c.setBackground(new Color(255, 199, 206)); // Red
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        };

        // Apply the renderer to every column in the table
        for (int i = 0; i < COLUMNS.length; i++) {
            ticketTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Public accessors — used by Main.java to wire Observer listeners
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * @return The log text area — passed to LogPanelListener in Main.java.
     */
    public JTextArea getLogArea() { return logArea; }

    /**
     * @return The stats label — passed to StatisticsListener in Main.java.
     */
    public JLabel getStatsLabel() { return statsLabel; }
}
