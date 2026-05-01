import gui.MainGUI;
import javax.swing.SwingUtilities;

/**
 * Entry point for the AutoSupport application.
 * Launches the Swing GUI on the Event Dispatch Thread.
 */
public class Main {
    /**
     * Main method — starts the application by creating and displaying the MainGUI.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainGUI gui = new MainGUI();
            gui.setVisible(true);
        });
    }
}
