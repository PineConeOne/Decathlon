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

    public static class CompetitorNotFoundException extends RuntimeException {
        public CompetitorNotFoundException(String message) {
            super(message);
        }
    }

    public static class Competitor {
        public final String name;
        public final String competition;
        public final Map<String, Integer> points = new ConcurrentHashMap<>();

        public Competitor(String name, String competition) {
            this.name = name;
            this.competition = competition;
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

    public static final class StandingRow {
        public final int position;
        public final String name;
        public final Map<String, Integer> scores;
        public final int total;

        public StandingRow(int position, String name, Map<String, Integer> scores, int total) {
            this.position = position;
            this.name = name;
            this.scores = scores;
            this.total = total;
        }
    }

    private final Map<String, Competitor> competitors = new LinkedHashMap<>();

    public synchronized void addCompetitor(String name, String competition) {
        String trimmedName = name == null ? "" : name.trim();
        if (trimmedName.isEmpty()) {
            throw new IllegalArgumentException("Please enter a competitor's name.");
        }
        String normalizedCompetition = normalizeCompetition(competition);
        Competitor existing = competitors.get(trimmedName);
        if (existing != null) {
            if (!existing.competition.equals(normalizedCompetition)) {
                throw new IllegalArgumentException("Competitor \"" + trimmedName + "\" is already registered for " + existing.competition + ".");
            }
            return;
        }
        competitors.put(trimmedName, new Competitor(trimmedName, normalizedCompetition));
    }

    private String normalizeCompetition(String competition) {
        if (competition == null) {
            throw new IllegalArgumentException("Competition must be either \"Decathlon\" or \"Heptathlon\".");
        }
        String trimmed = competition.trim();
        if (trimmed.equalsIgnoreCase("Decathlon")) {
            return "Decathlon";
        }
        if (trimmed.equalsIgnoreCase("Heptathlon")) {
            return "Heptathlon";
        }
        throw new IllegalArgumentException("Competition must be either \"Decathlon\" or \"Heptathlon\".");
    }

    public synchronized int competitorCount() {
        return competitors.size();
    }

    public synchronized String competitionOf(String name) {
        Competitor c = competitors.get(name);
        return c == null ? null : c.competition;
    }

    public synchronized List<String> competitorNamesForCompetition(String competition) {
        List<String> names = new ArrayList<>();
        for (Competitor c : competitors.values()) {
            if (c.competition.equals(competition)) {
                names.add(c.name);
            }
        }
        return names;
    }

    public synchronized int score(String name, String eventId, double raw) {
        Competitor c = competitors.get(name);
        if (c == null) {
            throw new CompetitorNotFoundException("Competitor \"" + name + "\" is not registered. Add the competitor before submitting a result.");
        }
        ScoringService.EventDef def = scoring.get(eventId);
        if (def == null) {
            throw new IllegalArgumentException("Unknown event.");
        }
        String eventCompetition = ScoringService.HEPTATHLON_EVENTS.contains(def) ? "Heptathlon" : "Decathlon";
        if (!eventCompetition.equals(c.competition)) {
            throw new IllegalArgumentException("Competitor \"" + name + "\" is registered for " + c.competition + ", not " + eventCompetition + ".");
        }
        if (raw < def.min) {
            throw new ScoreOutOfRangeException("Value too low for " + def.colLabel + ". Minimum accepted value is " + def.min + ".");
        }
        if (raw > def.max) {
            throw new ScoreOutOfRangeException("Value too high for " + def.colLabel + ". Maximum accepted value is " + def.max + ".");
        }
        int pts = scoring.score(eventId, raw);
        c.points.put(eventId, pts);
        return pts;
    }

    public synchronized void setScore(String name, String eventId, int points) {
        Competitor c = competitors.get(name);
        if (c != null) {
            c.points.put(eventId, points);
        }
    }

    public synchronized List<StandingRow> standingsFor(List<ScoringService.EventDef> events) {
        List<StandingRow> rows = new ArrayList<>();
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
            Map<String, Integer> scores = new LinkedHashMap<>();
            for (ScoringService.EventDef e : events) {
                Integer p = c.points.get(e.id);
                if (p != null) {
                    scores.put(e.id, p);
                }
            }
            rows.add(new StandingRow(0, c.name, scores, c.total(events)));
        }
        rows.sort((r1, r2) -> r2.total - r1.total);
        List<StandingRow> positioned = new ArrayList<>();
        int position = 1;
        for (StandingRow r : rows) {
            positioned.add(new StandingRow(position++, r.name, r.scores, r.total));
        }
        return positioned;
    }

    private Map<String, Object> standingsMapFor(List<ScoringService.EventDef> events) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (StandingRow r : standingsFor(events)) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("position", r.position);
            row.put("name", r.name);
            row.put("scores", r.scores);
            row.put("total", r.total);
            rows.add(row);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("rows", rows);
        return result;
    }

    public synchronized Map<String, Object> standings() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("decathlon", standingsMapFor(ScoringService.DECATHLON_EVENTS));
        result.put("heptathlon", standingsMapFor(ScoringService.HEPTATHLON_EVENTS));
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
        for (StandingRow r : standingsFor(events)) {
            List<String> row = new ArrayList<>();
            row.add(r.name);
            for (ScoringService.EventDef e : events) {
                Integer p = r.scores.get(e.id);
                row.add(p == null ? "" : String.valueOf(p));
            }
            row.add(String.valueOf(r.total));
            sb.append(String.join(",", row)).append("\n");
        }
    }

    public synchronized void importCsv(String csv) {
        String[] lines = csv.split("\r?\n");
        List<ScoringService.EventDef> currentEvents = null;
        String currentCompetition = null;
        String[] currentHeader = null;
        for (String line : lines) {
            if (line.isBlank()) {
                currentEvents = null;
                currentCompetition = null;
                currentHeader = null;
                continue;
            }
            if (line.equals("Decathlon")) {
                currentEvents = ScoringService.DECATHLON_EVENTS;
                currentCompetition = "Decathlon";
                currentHeader = null;
                continue;
            }
            if (line.equals("Heptathlon")) {
                currentEvents = ScoringService.HEPTATHLON_EVENTS;
                currentCompetition = "Heptathlon";
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
            try {
                addCompetitor(name, currentCompetition);
            } catch (IllegalArgumentException ignored) {
            }
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