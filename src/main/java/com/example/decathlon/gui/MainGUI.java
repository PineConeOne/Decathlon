package com.example.decathlon.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.example.decathlon.core.CompetitionService;
import com.example.decathlon.core.ScoringService;

public class MainGUI {

    private static final int MAX_COMPETITORS = 40;

    private final ScoringService scoringService = new ScoringService();
    private final CompetitionService competitionService = new CompetitionService(scoringService);

    private JTextField addNameField;
    private JComboBox<String> addCompetitionBox;
    private JComboBox<String> competitorBox;
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
        frame.setSize(1000, 750);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(buildAddCompetitorPanel());
        top.add(buildEnterResultPanel());

        frame.add(top, BorderLayout.NORTH);
        frame.add(buildResultsTabs(), BorderLayout.CENTER);
        frame.add(buildImportExportPanel(), BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private JPanel buildAddCompetitorPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Add competitor"));

        addNameField = new JTextField();
        addCompetitionBox = new JComboBox<>(new String[]{"Decathlon", "Heptathlon"});
        JButton addButton = new JButton("Add Competitor");
        addButton.addActionListener(e -> onAddCompetitor());

        panel.add(new JLabel("Enter Competitor's Name:"));
        panel.add(addNameField);
        panel.add(addCompetitionBox);
        panel.add(addButton);

        return panel;
    }

    private JPanel buildEnterResultPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Enter result"));

        competitorBox = new JComboBox<>();
        disciplineBox = new JComboBox<>();
        resultField = new JTextField();
        JButton calculateButton = new JButton("Calculate Score");

        competitorBox.addActionListener(e -> updateDisciplineBox());
        calculateButton.addActionListener(e -> onCalculateScore());

        panel.add(new JLabel("Competitor Name:"));
        panel.add(competitorBox);
        panel.add(new JLabel("Select Discipline:"));
        panel.add(disciplineBox);
        panel.add(new JLabel("Enter Result:"));
        panel.add(resultField);
        panel.add(new JLabel(""));
        panel.add(calculateButton);

        updateDisciplineBox();

        return panel;
    }

    private JPanel buildImportExportPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Import / Export"));

        JButton exportButton = new JButton("Export CSV");
        exportButton.addActionListener(e -> onExportCsv());

        JButton importButton = new JButton("Import CSV");
        importButton.addActionListener(e -> onImportCsv());

        panel.add(exportButton);
        panel.add(importButton);

        return panel;
    }

    private JTabbedPane buildResultsTabs() {
        JTabbedPane tabs = new JTabbedPane();

        decaTableModel = createTableModel(ScoringService.DECATHLON_EVENTS);
        hepTableModel = createTableModel(ScoringService.HEPTATHLON_EVENTS);

        JTable decaTable = new JTable(decaTableModel);
        JTable hepTable = new JTable(hepTableModel);

        tabs.addTab("Decathlon", new JScrollPane(decaTable));
        tabs.addTab("Heptathlon", new JScrollPane(hepTable));

        return tabs;
    }

    private DefaultTableModel createTableModel(List<ScoringService.EventDef> events) {
        List<String> columns = new ArrayList<>();
        columns.add("Result position");
        columns.add("Name");
        for (ScoringService.EventDef ev : events) {
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
        String selectedName = (String) competitorBox.getSelectedItem();
        String competition = selectedName == null ? null : competitionService.competitionOf(selectedName);
        List<ScoringService.EventDef> events = "Heptathlon".equals(competition) ? ScoringService.HEPTATHLON_EVENTS : ScoringService.DECATHLON_EVENTS;
        for (ScoringService.EventDef ev : events) {
            disciplineBox.addItem(ev.menuLabel);
        }
    }

    private void onAddCompetitor() {
        String name = addNameField.getText();
        String competition = (String) addCompetitionBox.getSelectedItem();
        String trimmedName = name == null ? "" : name.trim();

        if (competitionService.competitionOf(trimmedName) == null && competitionService.competitorCount() >= MAX_COMPETITORS) {
            JOptionPane.showMessageDialog(null, "Maximum of 40 competitors reached.", "Limit Reached", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            competitionService.addCompetitor(name, competition);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        refreshCompetitorBox();
        addNameField.setText("");
    }

    private void refreshCompetitorBox() {
        String previouslySelected = (String) competitorBox.getSelectedItem();
        competitorBox.removeAllItems();
        List<String> names = new ArrayList<>();
        names.addAll(competitionService.competitorNamesForCompetition("Decathlon"));
        names.addAll(competitionService.competitorNamesForCompetition("Heptathlon"));
        for (String name : names) {
            competitorBox.addItem(name);
        }
        if (previouslySelected != null && names.contains(previouslySelected)) {
            competitorBox.setSelectedItem(previouslySelected);
        }
        updateDisciplineBox();
    }

    private void onCalculateScore() {
        String name = (String) competitorBox.getSelectedItem();
        if (name == null) {
            JOptionPane.showMessageDialog(null, "Please add and select a competitor first.", "No Competitor Selected", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String competition = competitionService.competitionOf(name);
        List<ScoringService.EventDef> events = "Heptathlon".equals(competition) ? ScoringService.HEPTATHLON_EVENTS : ScoringService.DECATHLON_EVENTS;
        String menuLabel = (String) disciplineBox.getSelectedItem();
        ScoringService.EventDef event = null;
        for (ScoringService.EventDef ev : events) {
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

        try {
            competitionService.score(name, event.id, raw);
        } catch (CompetitionService.CompetitorNotFoundException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Competitor Not Found", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (CompetitionService.ScoreOutOfRangeException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Value Out Of Range", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        resultField.setText("");
        refreshTables();
    }

    private void refreshTables() {
        refreshTable(decaTableModel, ScoringService.DECATHLON_EVENTS);
        refreshTable(hepTableModel, ScoringService.HEPTATHLON_EVENTS);
    }

    private void refreshTable(DefaultTableModel model, List<ScoringService.EventDef> events) {
        model.setRowCount(0);
        for (CompetitionService.StandingRow row : competitionService.standingsFor(events)) {
            Object[] rowData = new Object[3 + events.size()];
            rowData[0] = row.position;
            rowData[1] = row.name;
            for (int i = 0; i < events.size(); i++) {
                Integer p = row.scores.get(events.get(i).id);
                rowData[2 + i] = p == null ? "" : p;
            }
            rowData[2 + events.size()] = row.total;
            model.addRow(rowData);
        }
    }

    private void onExportCsv() {
        JFileChooser chooser = new JFileChooser();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        chooser.setSelectedFile(new File("results-" + timestamp + ".csv"));
        int result = chooser.showSaveDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        Path path = chooser.getSelectedFile().toPath();
        try {
            Files.writeString(path, competitionService.exportCsv(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Export failed: " + ex.getMessage(), "Export Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onImportCsv() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        Path path = chooser.getSelectedFile().toPath();
        String csv;
        try {
            csv = Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Import failed: " + ex.getMessage(), "Import Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        competitionService.importCsv(csv);
        refreshCompetitorBox();
        refreshTables();
    }
}