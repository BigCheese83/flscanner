package ru.bigcheese.flscanner.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;

public class UpdatesContentPanel extends JPanel implements ActionListener {

    private JPanel topPanel;                                    // contains the buttons displayed on the top
    private JPanel bottomPanel;                                 // contains the buttons displayed on the bottom
    private Map<String, BarInfo> bars = new LinkedHashMap<>();  // use to preserve the order of the bars
    private int visibleBar = 0;                                 // The currently visible bar (zero-based index)
    private JComponent visibleComponent;                        // A placeholder for the currently visible component
    private JLabel noBarsLabel = createNoBarsLabel();

    public UpdatesContentPanel() {
        setLayout(new BorderLayout());
        topPanel = new JPanel(new GridLayout(1, 1));
        bottomPanel = new JPanel(new GridLayout(1, 1));
        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);
        add(noBarsLabel, BorderLayout.CENTER);
    }

    public void addBar(String name, JComponent component, int count) {
        BarInfo barInfo = new BarInfo(name, component, count);
        barInfo.getButton().addActionListener(this);
        bars.put(name, barInfo);
        render();
    }

    public void addBar(String name, Icon icon, JComponent component, int count) {
        BarInfo barInfo = new BarInfo(name, icon, component, count);
        barInfo.getButton().addActionListener(this);
        bars.put(name, barInfo);
        render();
    }

    public void removeBar(String name) {
        bars.remove(name);
        render();
    }

    public int getVisibleBar() {
        return visibleBar;
    }

    public void setVisibleBar(int visibleBar) {
        if (visibleBar > 0 && visibleBar < bars.size() - 1) {
            this.visibleBar = visibleBar;
            render();
        }
    }

    /**
     * Causes the bar component to rebuild itself; this means that
     * it rebuilds the top and bottom panels of bars as well as making the
     * currently selected bar's panel visible
     */
    public void render() {

        if (bars.size() > 0) {
            remove(noBarsLabel);
        }

        // Compute how many bars we are going to have where
        int totalBars = bars.size();
        int topBars = visibleBar + 1;
        int bottomBars = totalBars - topBars;

        // Get an iterator to walk through out bars with
        ListIterator<String> iterator = new ArrayList<>(bars.keySet()).listIterator(bars.size());

        // Render the top bars: remove all components, reset the GridLayout to
        // hold to correct number of bars, add the bars, and "validate" it to
        // cause it to re-layout its components
        topPanel.removeAll();
        GridLayout topLayout = (GridLayout) topPanel.getLayout();
        topLayout.setRows(topBars);
        BarInfo barInfo = null;
        for (int i = 0; i < topBars; i++) {
            barInfo = bars.get(iterator.previous());
            topPanel.add(barInfo.getButton());
        }
        topPanel.validate();

        // Render the center component: remove the current component (if there
        // is one) and then put the visible component in the center of this panel
        if (barInfo != null) {
            if (visibleComponent != null) {
                remove(visibleComponent);
            }
            visibleComponent = barInfo.getComponent();
            add(visibleComponent, BorderLayout.CENTER);
        }

        // Render the bottom bars: remove all components, reset the GridLayout to
        // hold to correct number of bars, add the bars, and "validate" it to
        // cause it to re-layout its components
        bottomPanel.removeAll();
        GridLayout bottomLayout = (GridLayout) bottomPanel.getLayout();
        bottomLayout.setRows(bottomBars);
        for (int i = 0; i < bottomBars; i++) {
            barInfo = bars.get(iterator.previous());
            bottomPanel.add(barInfo.getButton());
        }
        bottomPanel.validate();

        // Validate all of our components: cause this container to re-layout its subcomponents
        this.validate();
    }

    /**
     * Invoked when one of our bars is selected
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        int currentBar = bars.size() - 1;
        for (Map.Entry<String, BarInfo> entry : bars.entrySet()) {
            if (entry.getValue().getButton() == e.getSource()) {
                // Found the selected button
                visibleBar = currentBar;
                render();
                return;
            }
            currentBar--;
        }
    }

    private JLabel createNoBarsLabel() {
        ImageIcon image = new ImageIcon(getClass().getResource("/images/sad-smiley.png"));
        JLabel label = new JLabel("No updates", image, JLabel.CENTER);
        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setVerticalTextPosition(JLabel.TOP);
        label.setFont(new Font(null, Font.ITALIC, 16));
        label.setForeground(Color.darkGray);
        return label;
    }

    /**
     * Internal class that maintains information about individual bars;
     * specifically it maintains the following information:
     *
     * name         The name of the bar
     * button       The associated JButton for the bar
     * component    The component maintained in the bar
     */
    private static class BarInfo {

        private final String name;
        private final JButton button;
        private final JComponent component;
        private final long timestamp;

        BarInfo(String name, JComponent component, int count) {
            this.name = name;
            this.component = component;
            this.button = new JButton(getButtonLabel(name, count));
            this.timestamp = System.currentTimeMillis();
        }

        BarInfo(String name, Icon icon, JComponent component, int count) {
            this.name = name;
            this.component = component;
            this.button = new JButton(getButtonLabel(name, count), icon);
            this.timestamp = System.currentTimeMillis();
        }

        String getName() {
            return name;
        }

        JButton getButton() {
            return button;
        }

        JComponent getComponent() {
            return component;
        }

        long getTimestamp() {
            return timestamp;
        }

        private String getButtonLabel(String name, int count) {
            return count > 0 ? name + " (" + count + ")" : name;
        }
    }
}
