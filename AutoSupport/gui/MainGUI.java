package gui;

import creational.factory.TicketPriority;
import creational.factory.TicketStatus;
import creational.factory.TicketType;
import behavioral.chain.Level1Handler;
import behavioral.chain.Level2Handler;
import behavioral.chain.ManagerHandler;
import behavioral.chain.SupportHandler;
import creational.factory.Ticket;
import creational.singleton.TicketSystem;
import structural.adapter.EmailMessage;
import structural.facade.SupportFacade;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Main GUI window for the AutoSupport application.
 * <p>
 * Provides a Swing-based interface with a ticket table, control panel,
 * and real-time log panel. All user actions (submit, email, escalate,
 * resolve, clear) are handled through button listeners.
 * </p>
 */
public class MainGUI extends JFrame {

    /** Shared log list used by Facade and Chain handlers. */
    private List<String> log = new ArrayList<>();

    /** Facade for ticket submission operations. */
    private SupportFacade facade = new SupportFacade(log);

    /** Table model backing the ticket JTable. */
    private DefaultTableModel tableModel;

    /** The ticket table component. */
    private JTable ticketTable;

    /** Text area displaying real-time log messages. */
    private JTextArea logArea;

    /** Input field for ticket title. */
    private JTextField titleField;

    /** Input field for ticket description. */
    private JTextArea descriptionField;

    /** Dropdown for selecting ticket type. */
    private JComboBox<TicketType> typeCombo;

    /** Checkbox for marking a ticket as urgent. */
    private JCheckBox urgentCheck;

    /**
     * Constructs the MainGUI, initializes all components, and lays out the window.
     */
    public MainGUI() {
        setTitle("AutoSupport \u2014 Ticket Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        // --- Title Bar ---
        JLabel titleLabel = new JLabel("AutoSupport \u2014 Ticket Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(44, 62, 80));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // --- Left Panel (Controls) ---
        JPanel leftPanel = buildLeftPanel();
        add(leftPanel, BorderLayout.WEST);

        // --- Center: Table + Log split ---
        JSplitPane centerSplit = buildCenterPanel();
        add(centerSplit, BorderLayout.CENTER);
    }

    /**
     * Builds the left control panel with input fields and action buttons.
     *
     * @return the constructed left panel
     */
    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(250, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(236, 240, 241));

        // Title
        panel.add(new JLabel("Ticket Title:"));
        titleField = new JTextField();
        titleField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        panel.add(titleField);
        panel.add(Box.createVerticalStrut(8));

        // Description
        panel.add(new JLabel("Description:"));
        descriptionField = new JTextArea(4, 20);
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionField);
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        panel.add(descScroll);
        panel.add(Box.createVerticalStrut(8));

        // Type dropdown
        panel.add(new JLabel("Type:"));
        typeCombo = new JComboBox<>(TicketType.values());
        typeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        panel.add(typeCombo);
        panel.add(Box.createVerticalStrut(8));

        // Urgent checkbox
        urgentCheck = new JCheckBox("Mark as Urgent");
        urgentCheck.setBackground(new Color(236, 240, 241));
        panel.add(urgentCheck);
        panel.add(Box.createVerticalStrut(15));

        // Buttons
        panel.add(createButton("Submit Ticket", e -> onSubmitTicket()));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createButton("Simulate Email", e -> onSimulateEmail()));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createButton("Escalate Selected", e -> onEscalateSelected()));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createButton("Resolve Selected", e -> onResolveSelected()));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createButton("Clear Log", e -> onClearLog()));

        return panel;
    }

    /**
     * Creates a styled button with the given text and action listener.
     *
     * @param text     the button label
     * @param listener the action to perform on click
     * @return a configured JButton
     */
    private JButton createButton(String text, java.awt.event.ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        btn.setBackground(new Color(52, 152, 219));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.addActionListener(listener);
        return btn;
    }

    /**
     * Builds the center panel containing the ticket table and log area.
     *
     * @return a JSplitPane with table on top and log on bottom
     */
    private JSplitPane buildCenterPanel() {
        // Table
        String[] columns = { "ID", "Type", "Priority", "Status", "Title" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ticketTable = new JTable(tableModel);
        ticketTable.setDefaultRenderer(Object.class, new TicketCellRenderer());
        ticketTable.setRowHeight(24);
        ticketTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        JScrollPane tableScroll = new JScrollPane(ticketTable);

        // Log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setBackground(new Color(44, 62, 80));
        logArea.setForeground(new Color(46, 204, 113));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("System Log"));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, logScroll);
        split.setDividerLocation(350);
        split.setResizeWeight(0.65);
        return split;
    }

    // ======================== Action Handlers ========================

    /**
     * Handles the Submit Ticket button. Reads input fields, submits via Facade,
     * refreshes the table, and updates the log.
     */
    private void onSubmitTicket() {
        String title = titleField.getText().trim();
        String desc = descriptionField.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a ticket title.", "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        TicketType type = (TicketType) typeCombo.getSelectedItem();
        boolean urgent = urgentCheck.isSelected();

        facade.submitTicket(type, title, desc, urgent);
        refreshTable();
        refreshLog();

        // Clear inputs
        titleField.setText("");
        descriptionField.setText("");
        urgentCheck.setSelected(false);
    }

    /**
     * Handles the Simulate Email button. Opens a modal dialog for email input,
     * then submits via Facade adapter pipeline.
     */
    private void onSimulateEmail() {
        JDialog dialog = new JDialog(this, "Simulate Email", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField senderField = new JTextField(20);
        JTextField subjectField = new JTextField(20);
        JTextArea bodyArea = new JTextArea(4, 20);
        bodyArea.setLineWrap(true);
        bodyArea.setWrapStyleWord(true);
        JCheckBox urgentEmailCheck = new JCheckBox("Mark as Urgent");

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Sender:"), gbc);
        gbc.gridx = 1;
        dialog.add(senderField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Subject:"), gbc);
        gbc.gridx = 1;
        dialog.add(subjectField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("Body:"), gbc);
        gbc.gridx = 1;
        dialog.add(new JScrollPane(bodyArea), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        dialog.add(urgentEmailCheck, gbc);

        JButton sendBtn = new JButton("Send Email");
        sendBtn.addActionListener(e -> {
            String sender = senderField.getText().trim();
            String subject = subjectField.getText().trim();
            String body = bodyArea.getText().trim();
            if (sender.isEmpty() || subject.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Sender and Subject are required.", "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            EmailMessage email = new EmailMessage(sender, subject, body);
            facade.submitFromEmail(email, urgentEmailCheck.isSelected());
            refreshTable();
            refreshLog();
            dialog.dispose();
        });
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(sendBtn, gbc);

        dialog.setVisible(true);
    }

    /**
     * Handles the Escalate Selected button. Builds a fresh chain
     * (L1 → L2 → Manager) and passes the selected ticket through it.
     */
    private void onEscalateSelected() {
        int row = ticketTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to escalate.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<Ticket> tickets = TicketSystem.getInstance().getAllTickets();
        Ticket ticket = tickets.get(row);

        // Build a fresh chain each time
        SupportHandler l1 = new Level1Handler(log);
        SupportHandler l2 = new Level2Handler(log);
        SupportHandler manager = new ManagerHandler(log);
        l1.setNext(l2);
        l2.setNext(manager);

        log.add("--- Escalating Ticket #" + ticket.getId() + " ---");
        l1.handle(ticket);

        refreshTable();
        refreshLog();
    }

    /**
     * Handles the Resolve Selected button. Directly sets the selected
     * ticket's status to RESOLVED.
     */
    private void onResolveSelected() {
        int row = ticketTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to resolve.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<Ticket> tickets = TicketSystem.getInstance().getAllTickets();
        Ticket ticket = tickets.get(row);
        ticket.setStatus(TicketStatus.RESOLVED);
        log.add("> Ticket #" + ticket.getId() + " manually RESOLVED.");
        refreshTable();
        refreshLog();
    }

    /**
     * Handles the Clear Log button. Clears both the log list and the text area.
     */
    private void onClearLog() {
        log.clear();
        logArea.setText("");
    }

    // ======================== Refresh Helpers ========================

    /**
     * Refreshes the ticket table by clearing all rows and re-populating
     * from the TicketSystem singleton.
     */
    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Ticket t : TicketSystem.getInstance().getAllTickets()) {
            tableModel.addRow(new Object[] {
                    t.getId(),
                    t.getTypeLabel(),
                    t.getPriority(),
                    t.getStatus(),
                    t.getTitle()
            });
        }
    }

    /**
     * Refreshes the log text area with all messages from the shared log list.
     */
    private void refreshLog() {
        StringBuilder sb = new StringBuilder();
        for (String entry : log) {
            sb.append(entry).append("\n");
        }
        logArea.setText(sb.toString());
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    // ======================== Custom Renderer ========================

    /**
     * Custom table cell renderer that colors rows based on ticket state:
     * <ul>
     * <li>URGENT priority → light red background</li>
     * <li>RESOLVED status → light green background</li>
     * <li>All others → default white background</li>
     * </ul>
     */
    private class TicketCellRenderer extends DefaultTableCellRenderer {

        /** Light red color for urgent tickets. */
        private final Color URGENT_COLOR = new Color(255, 204, 204);

        /** Light green color for resolved tickets. */
        private final Color RESOLVED_COLOR = new Color(204, 255, 204);

        /**
         * Returns the renderer component with the appropriate background color.
         *
         * @param table      the JTable
         * @param value      the cell value
         * @param isSelected whether the cell is selected
         * @param hasFocus   whether the cell has focus
         * @param row        the row index
         * @param column     the column index
         * @return the configured renderer component
         */
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (!isSelected) {
                String priority = table.getModel().getValueAt(row, 2).toString();
                String status = table.getModel().getValueAt(row, 3).toString();

                if (status.equals("RESOLVED")) {
                    c.setBackground(RESOLVED_COLOR);
                } else if (priority.equals("URGENT")) {
                    c.setBackground(URGENT_COLOR);
                } else {
                    c.setBackground(Color.WHITE);
                }
            }
            return c;
        }
    }
}
