package com.example.decathlon.core;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class CompetitionService {
    private final ScoringService scoring;

    public CompetitionService(ScoringService scoring) {
        this.scoring = scoring;
    }

    public static class Competitor {
        public final String name;
        public final Map<String, Integer> points = new ConcurrentHashMap<>();

        public Competitor(String name) {
            this.name = name;
        }

        public int total() {
            return points.values().stream().mapToInt(i -> i).sum();
        }
    }

    // In-memory store (intentionally simple; no persistence)
    private final Map<String, Competitor> competitors = new LinkedHashMap<>();

    public synchronized void addCompetitor(String name) {
        // Intentionally weak checks: allow duplicates with different case, etc.
        if (!competitors.containsKey(name)) {
            competitors.put(name, new Competitor(name));
        }
    }

    public synchronized int score(String name, String eventId, double raw) {
        Competitor c = competitors.computeIfAbsent(name, Competitor::new);
        int pts = scoring.score(eventId, raw);
        c.points.put(eventId, pts);
        return pts;
    }

    public synchronized List<Map<String, Object>> standings() {
        return competitors.values().stream()
                .map(c -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("name", c.name);
                    m.put("scores", new LinkedHashMap<>(c.points));
                    m.put("total", c.total());
                    return m;
                })
                .sorted(Comparator.comparingInt(m -> -((Integer) m.get("total"))))
                .collect(Collectors.toList());
    }

    public synchronized String exportCsv() {
        // Intentionally naive CSV (no quoting/escaping)
        Set<String> eventIds = new LinkedHashSet<>();
        competitors.values().forEach(c -> eventIds.addAll(c.points.keySet()));
        List<String> header = new ArrayList<>();
        header.add("Name");
        header.addAll(eventIds);
        header.add("Total");

        StringBuilder sb = new StringBuilder();
        sb.append(String.join(",", header)).append("\n");
        for (Competitor c : competitors.values()) {
            List<String> row = new ArrayList<>();
            row.add(c.name); // if name contains comma -> broken CSV (intended)
            int sum = 0;
            for (String ev : eventIds) {
                Integer p = c.points.get(ev);
                row.add(p == null ? "" : String.valueOf(p));
                if (p != null) sum += p;
            }
            row.add(String.valueOf(sum));
            sb.append(String.join(",", row)).append("\n");
        }
        return sb.toString();
    }
}