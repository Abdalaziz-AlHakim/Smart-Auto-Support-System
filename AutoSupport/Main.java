import behavioral.observer.ConsoleNotificationListener;
import behavioral.observer.LogPanelListener;
import behavioral.observer.StatisticsListener;
import creational.singleton.TicketSystem;
import gui.MainGUI;

import javax.swing.*;

/**
 * Application entry point.
 *
 * Responsibilities:
 *   1. Launch the Swing GUI on the Event Dispatch Thread (EDT) — mandatory
 *      for all Swing applications to ensure thread safety.
 *   2. Register all Observer listeners with TicketSystem BEFORE the GUI
 *      is shown, so no events are missed from the very first ticket.
 *
 * Note: MainGUI is constructed first so the listener targets (logArea,
 * statsLabel) exist before the observers reference them. The GUI is made
 * visible only after all wiring is complete.
 */
public class Main {

    public static void main(String[] args) {

        // All Swing operations must be performed on the Event Dispatch Thread.
        // invokeLater() schedules this block to run on the EDT asynchronously.
        SwingUtilities.invokeLater(() -> {

            // 1 — Build the GUI (creates log panel, stats bar, table, etc.)
            MainGUI gui = new MainGUI();

            // 2 — Register Observer listeners with the Singleton BEFORE showing UI.
            //     Order determines notification order (log → console → stats).
            TicketSystem ts = TicketSystem.getInstance();

            ts.subscribe(new LogPanelListener(gui.getLogArea()));
                // Writes timestamped event messages to the GUI log panel

            ts.subscribe(new ConsoleNotificationListener());
                // Prints simulated email/SMS alerts to stdout (System.out)

            ts.subscribe(new StatisticsListener(gui.getStatsLabel()));
                // Keeps the open/escalated/resolved counters in the status bar current

            // 3 — All wiring complete; show the GUI window
            gui.setVisible(true);
        });
    }
}
