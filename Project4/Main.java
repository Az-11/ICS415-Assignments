// Main.java
import java.awt.*;
import javax.swing.*;

public class Main {
    private static final String HTML_INSTRUCTIONS =
        "<html><body style='font-family:sans-serif; padding:10px;'>"
      + "<h2 style='color:#0097A7;'>Curves</h2>"
      + "<ul>"
      + "  <li><b>Project-4:</b> Visual Bézier curve editor</li>"
      + "  <li><b>Bonus:</b> Handle poly Bézier chains</li>"
      + "  <li><b>Submit:</b> End of Week 14</li>"
      + "  <li><b>Bonus Due:</b> End of Week 15</li>"
      + "</ul>"
      + "<p style='font-size:90%; color:#555;'>"
      + "Use the tabs above to switch modes. Drag control handles to reshape curves."
      + "</p>"
      + "</body></html>";

    public static void main(String[] args) {
        // 1) Nimbus L&F
        try {
            for (UIManager.LookAndFeelInfo info: UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // fallback silently
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Custom Bézier Curve Editor");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 700);
            frame.setLocationRelativeTo(null);

            // 2) TabbedPane instead of CardLayout
            JTabbedPane tabs = new JTabbedPane();

            // BASIC tab
            JSplitPane basicSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                createInstructionPane(),
                new BezierSketch()
            );
            basicSplit.setResizeWeight(0.25);
            tabs.addTab("Basic Bézier", basicSplit);

            // BONUS tab
            JSplitPane bonusSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                createInstructionPane(),
                new PolyBezierEditor()
            );
            bonusSplit.setResizeWeight(0.25);
            tabs.addTab("Poly Bézier (Bonus)", bonusSplit);

            frame.add(tabs);
            frame.setVisible(true);
        });
    }

    private static JEditorPane createInstructionPane() {
        JEditorPane pane = new JEditorPane("text/html", HTML_INSTRUCTIONS);
        pane.setEditable(false);
        pane.setBackground(new Color(245, 248, 250));
        pane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(200,200,200)));
        return pane;
    }
}
