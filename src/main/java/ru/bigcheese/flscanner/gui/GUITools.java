package ru.bigcheese.flscanner.gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains helpful methods for GUI Swing.
 *
 * @author  BigCheese
 */
public final class GUITools {

    public static JPanel createVerticalBoxPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        return p;
    }

    public static JPanel createHorizontalBoxPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        return p;
    }

    public static void setLabelStyle(JLabel label, int style) {
        Font font = label.getFont();
        label.setFont(new Font(font.getName(), style, font.getSize()));
    }

    public static <T> List<T> getComboBoxItems(JComboBox<T> comboBox) {
        ComboBoxModel<T> model = comboBox.getModel();
        List<T> list = new ArrayList<>(model.getSize());
        for (int i = 0; i < model.getSize(); i++) {
            T item = model.getElementAt(i);
            list.add(item);
        }
        return list;
    }
}
