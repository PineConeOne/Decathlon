package com.example.decathlon.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import com.example.decathlon.common.CalcTrackAndField;

public class MainGUI {

    private static final int MAX_COMPETITORS = 40;

    private static class EventDef {
        final String id;
        final String menuLabel;
        final String colLabel;
        final boolean track;
        final double a;
        final double b;
        final double c;
        final double min;
        final double max;

        EventDef(String id, String menuLabel, String colLabel, boolean track,
                 double a, double b, double c, double min, double max) {
            this.id = id;
            this.menuLabel = menuLabel;
            this.colLabel = colLabel;
            this.track = track;
            this.a = a;
            this.b = b;
            this.c = c;
            this.min = min;
            this.max = max;
        }
    }

    private static final EventDef[] DECA_EVENTS = new EventDef[]{
            new EventDef("100m", "100m", "100m", true, 25.4347, 18.0, 1.81, 5, 17.8),
            new EventDef("110mHurdles", "110m Hurdles", "110m hurdles", true, 5.74352, 28.5, 1.92, 10, 28.5),
            new EventDef("400m", "400m", "400m", true, 1.53775, 82.0, 1.81, 20, 100),
            new EventDef("1500m", "1500m", "1500m", true, 0.03768, 480.0, 18.5, 2, 7),
            new EventDef("discusThrow", "Discus Throw", "Discus", false, 12.91, 4.0, 1.1, 0, 85),
            new EventDef("highJump", "High Jump", "High jump", false, 0.8465, 75.0, 1.42, 0, 100),
            new EventDef("javelinThrow", "Javelin Throw", "Javelin", false, 10.14, 7.0, 1.08, 0, 110),
            new EventDef("longJump", "Long Jump", "Long jump", false, 0.13454, 220.0, 1.4, 250, 1000),
            new EventDef("poleVault", "Pole Vault", "Pole vault", false, 0.2797, 100.0, 1.35, 2, 1000),
            new EventDef("shotPut", "Shot Put", "Shot put", false, 51.39, 1.5, 1.05, 0, 30)
    };

    private static final EventDef[] HEP_EVENTS = new EventDef[]{
            new EventDef("hep100mHurdles", "110m Hurdles", "110m hurdles", true, 9.23076, 26.7, 18.35, 5, 26.4),
            new EventDef("hep200m", "200m", "200m", true, 4.99087, 42.5, 1.81, 14, 42.08),
            new EventDef("hep800m", "800m", "800m", true, 0.11193, 254.0, 1.88, 70, 250.79),
            new EventDef("hepHighJump", "High Jump", "High jump", false, 1.84523, 75.0, 1.348, 75.7, 270),
            new EventDef("hepJavelinThrow", "Javelin Throw", "Javelin", false, 15.9803, 3.8, 1.04, 0, 100),
            new EventDef("hepLongJump", "Long Jump", "Long jump", false, 0.1888807, 210.0, 1.41, 0, 400),
            new EventDef("hepShotPut", "Shot Put", "Shot put", false, 56.0211, 1.5, 1.05, 5, 100)
    };

    private final CalcTrackAndField calc = new CalcTrackAndField();

    private final List<String> competitorNames = new ArrayList<>();
    private final Map<String, Map<String, Integer>> decaResults = new LinkedHashMap<>();
    private final Map<String, Map<String, Integer>> hepResults = new LinkedHashMap<>();

    private JTextField addNameField;
    private JComboBox<String> competitorBox;
    private JComboBox<String> groupBox;
    private JComboBox<String> disciplineBox;
    private JTextField resultField;
    private DefaultTableModel decaTableModel;
    private DefaultTableModel hepTableModel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainGUI().createAndShowGUI());
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Track and Field Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(buildAddCompetitorPanel());
        top.add(buildEnterResultPanel());

        frame.add(top, BorderLayout.NORTH);
        frame.add(buildResultsTabs(), BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private JPanel buildAddCompetitorPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Add competitor"));

        addNameField = new JTextField();
        JButton addButton = new JButton("Add Competitor");
        addButton.addActionListener(e -> onAddCompetitor());

        panel.add(new JLabel("Enter Competitor's Name:"));
        panel.add(addNameField);
        panel.add(addButton);

        return panel;
    }

    private JPanel buildEnterResultPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Enter result"));

        competitorBox = new JComboBox<>();
        groupBox = new JComboBox<>(new String[]{"Decathlon", "Heptathlon"});
        disciplineBox = new JComboBox<>();
        resultField = new JTextField();
        JButton calculateButton = new JButton("Calculate Score");

        groupBox.addActionListener(e -> updateDisciplineBox());
        calculateButton.addActionListener(e -> onCalculateScore());

        panel.add(new JLabel("Competitor Name:"));
        panel.add(competitorBox);
        panel.add(new JLabel("Select Group:"));
        panel.add(groupBox);
        panel.add(new JLabel("Select Discipline:"));
        panel.add(disciplineBox);
        panel.add(new JLabel("Enter Result:"));
        panel.add(resultField);
        panel.add(new JLabel(""));
        panel.add(calculateButton);

        updateDisciplineBox();

        return panel;
    }

    private JTabbedPane buildResultsTabs() {
        JTabbedPane tabs = new JTabbedPane();

        decaTableModel = createTableModel(DECA_EVENTS);
        hepTableModel = createTableModel(HEP_EVENTS);

        JTable decaTable = new JTable(decaTableModel);
        JTable hepTable = new JTable(hepTableModel);

        tabs.addTab("Decathlon", new JScrollPane(decaTable));
        tabs.addTab("Heptathlon", new JScrollPane(hepTable));

        return tabs;
    }

    private DefaultTableModel createTableModel(EventDef[] events) {
        List<String> columns = new ArrayList<>();
        columns.add("Result position");
        columns.add("Name");
        for (EventDef ev : events) {
            columns.add(ev.colLabel);
        }
        columns.add("Total points");

        return new DefaultTableModel(columns.toArray(), 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void updateDisciplineBox() {
        disciplineBox.removeAllItems();
        EventDef[] events = isDecathlon() ? DECA_EVENTS : HEP_EVENTS;
        for (EventDef ev : events) {
            disciplineBox.addItem(ev.menuLabel);
        }
    }

    private boolean isDecathlon() {
        return "Decathlon".equals(groupBox.getSelectedItem());
    }

    private void onAddCompetitor() {
        String name = addNameField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter a competitor's name.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (competitorNames.size() >= MAX_COMPETITORS) {
            JOptionPane.showMessageDialog(null, "Maximum of 40 competitors reached.", "Limit Reached", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!competitorNames.contains(name)) {
            competitorNames.add(name);
            competitorBox.addItem(name);
        }

        addNameField.setText("");
    }

    private void onCalculateScore() {
        String name = (String) competitorBox.getSelectedItem();
        if (name == null) {
            JOptionPane.showMessageDialog(null, "Please add and select a competitor first.", "No Competitor Selected", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean decathlon = isDecathlon();
        EventDef[] events = decathlon ? DECA_EVENTS : HEP_EVENTS;
        String menuLabel = (String) disciplineBox.getSelectedItem();
        EventDef event = null;
        for (EventDef ev : events) {
            if (ev.menuLabel.equals(menuLabel)) {
                event = ev;
                break;
            }
        }
        if (event == null) {
            return;
        }

        double raw;
        try {
            raw = Double.parseDouble(resultField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Please enter a valid number for the result.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (raw < event.min) {
            JOptionPane.showMessageDialog(null, "Value too low for " + event.menuLabel + ". Minimum accepted value is " + event.min + ".", "Value Too Low", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (raw > event.max) {
            JOptionPane.showMessageDialog(null, "Value too high for " + event.menuLabel + ". Maximum accepted value is " + event.max + ".", "Value Too High", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int score = event.track
                ? calc.calculateTrack(event.a, event.b, event.c, raw)
                : calc.calculateField(event.a, event.b, event.c, raw);

        Map<String, Map<String, Integer>> store = decathlon ? decaResults : hepResults;
        store.computeIfAbsent(name, k -> new LinkedHashMap<>()).put(event.id, score);

        resultField.setText("");

        refreshTable(decathlon);
    }

    private void refreshTable(boolean decathlon) {
        EventDef[] events = decathlon ? DECA_EVENTS : HEP_EVENTS;
        Map<String, Map<String, Integer>> store = decathlon ? decaResults : hepResults;
        DefaultTableModel model = decathlon ? decaTableModel : hepTableModel;

        List<Object[]> rows = new ArrayList<>();
        for (Map.Entry<String, Map<String, Integer>> entry : store.entrySet()) {
            String name = entry.getKey();
            Map<String, Integer> scores = entry.getValue();

            Object[] row = new Object[3 + events.length];
            row[1] = name;

            int total = 0;
            for (int i = 0; i < events.length; i++) {
                Integer p = scores.get(events[i].id);
                row[2 + i] = p == null ? "" : p;
                if (p != null) {
                    total += p;
                }
            }
            row[2 + events.length] = total;
            rows.add(row);
        }

        rows.sort((r1, r2) -> (Integer) r2[2 + events.length] - (Integer) r1[2 + events.length]);

        model.setRowCount(0);
        int position = 1;
        for (Object[] row : rows) {
            row[0] = position++;
            model.addRow(row);
        }
    }
}