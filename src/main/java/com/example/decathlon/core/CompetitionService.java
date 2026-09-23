package com.example.decathlon.core;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CompetitionService {
    private final ScoringService scoring;

    public CompetitionService(ScoringService scoring) {
        this.scoring = scoring;
    }

    public static class ScoreOutOfRangeException extends RuntimeException {
        public ScoreOutOfRangeException(String message) {
            super(message);
        }
    }

    public static class Competitor {
        public final String name;
        public final Map<String, Integer> points = new ConcurrentHashMap<>();

        public Competitor(String name) {
            this.name = name;
        }

        public int total(List<ScoringService.EventDef> events) {
            int sum = 0;
            for (ScoringService.EventDef e : events) {
                Integer p = points.get(e.id);
                if (p != null) {
                    sum += p;
                }
            }
            return sum;
        }
    }

    private final Map<String, Competitor> competitors = new LinkedHashMap<>();

    public synchronized void addCompetitor(String name) {
        if (!competitors.containsKey(name)) {
            competitors.put(name, new Competitor(name));
        }
    }

    public synchronized List<String> competitorNames() {
        return new ArrayList<>(competitors.keySet());
    }

    public synchronized int score(String name, String eventId, double raw) {
        ScoringService.EventDef def = scoring.get(eventId);
        if (def == null) {
            throw new IllegalArgumentException("Unknown event.");
        }
        if (raw < def.min) {
            throw new ScoreOutOfRangeException("Value too low for " + def.colLabel + ". Minimum accepted value is " + def.min + ".");
        }
        if (raw > def.max) {
            throw new ScoreOutOfRangeException("Value too high for " + def.colLabel + ". Maximum accepted value is " + def.max + ".");
        }
        Competitor c = competitors.computeIfAbsent(name, Competitor::new);
        int pts = scoring.score(eventId, raw);
        c.points.put(eventId, pts);
        return pts;
    }

    public synchronized void setScore(String name, String eventId, int points) {
        Competitor c = competitors.computeIfAbsent(name, Competitor::new);
        c.points.put(eventId, points);
    }

    private synchronized Map<String, Object> groupStandings(List<ScoringService.EventDef> events) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Competitor c : competitors.values()) {
            boolean hasAny = false;
            for (ScoringService.EventDef e : events) {
                if (c.points.containsKey(e.id)) {
                    hasAny = true;
                    break;
                }
            }
            if (!hasAny) {
                continue;
            }
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("name", c.name);
            Map<String, Integer> scores = new LinkedHashMap<>();
            for (ScoringService.EventDef e : events) {
                Integer p = c.points.get(e.id);
                if (p != null) {
                    scores.put(e.id, p);
                }
            }
            row.put("scores", scores);
            row.put("total", c.total(events));
            rows.add(row);
        }
        rows.sort((r1, r2) -> (Integer) r2.get("total") - (Integer) r1.get("total"));
        int position = 1;
        for (Map<String, Object> row : rows) {
            row.put("position", position++);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("rows", rows);
        return result;
    }

    public synchronized Map<String, Object> standings() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("decathlon", groupStandings(ScoringService.DECATHLON_EVENTS));
        result.put("heptathlon", groupStandings(ScoringService.HEPTATHLON_EVENTS));
        return result;
    }

    public synchronized String exportCsv() {
        StringBuilder sb = new StringBuilder();
        appendCsvSection(sb, "Decathlon", ScoringService.DECATHLON_EVENTS);
        sb.append("\n");
        appendCsvSection(sb, "Heptathlon", ScoringService.HEPTATHLON_EVENTS);
        return sb.toString();
    }

    private void appendCsvSection(StringBuilder sb, String title, List<ScoringService.EventDef> events) {
        sb.append(title).append("\n");
        List<String> header = new ArrayList<>();
        header.add("Name");
        for (ScoringService.EventDef e : events) {
            header.add(e.colLabel);
        }
        header.add("Total points");
        sb.append(String.join(",", header)).append("\n");
        for (Competitor c : competitors.values()) {
            boolean hasAny = false;
            for (ScoringService.EventDef e : events) {
                if (c.points.containsKey(e.id)) {
                    hasAny = true;
                    break;
                }
            }
            if (!hasAny) {
                continue;
            }
            List<String> row = new ArrayList<>();
            row.add(c.name);
            int total = 0;
            for (ScoringService.EventDef e : events) {
                Integer p = c.points.get(e.id);
                row.add(p == null ? "" : String.valueOf(p));
                if (p != null) {
                    total += p;
                }
            }
            row.add(String.valueOf(total));
            sb.append(String.join(",", row)).append("\n");
        }
    }

    public synchronized void importCsv(String csv) {
        String[] lines = csv.split("\r?\n");
        List<ScoringService.EventDef> currentEvents = null;
        String[] currentHeader = null;
        for (String line : lines) {
            if (line.isBlank()) {
                currentEvents = null;
                currentHeader = null;
                continue;
            }
            if (line.equals("Decathlon")) {
                currentEvents = ScoringService.DECATHLON_EVENTS;
                currentHeader = null;
                continue;
            }
            if (line.equals("Heptathlon")) {
                currentEvents = ScoringService.HEPTATHLON_EVENTS;
                currentHeader = null;
                continue;
            }
            if (currentEvents == null) {
                continue;
            }
            if (currentHeader == null) {
                currentHeader = line.split(",", -1);
                continue;
            }
            String[] cells = line.split(",", -1);
            if (cells.length < 1) {
                continue;
            }
            String name = cells[0].trim();
            if (name.isEmpty()) {
                continue;
            }
            addCompetitor(name);
            for (int i = 1; i < cells.length - 1 && i < currentHeader.length - 1; i++) {
                String value = cells[i].trim();
                if (value.isEmpty()) {
                    continue;
                }
                String label = currentHeader[i].trim();
                ScoringService.EventDef def = findByLabel(currentEvents, label);
                if (def == null) {
                    continue;
                }
                try {
                    int points = Integer.parseInt(value);
                    setScore(name, def.id, points);
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }

    private ScoringService.EventDef findByLabel(List<ScoringService.EventDef> events, String label) {
        for (ScoringService.EventDef e : events) {
            if (e.colLabel.equals(label)) {
                return e;
            }
        }
        return null;
    }
}