package com.example.decathlon.core;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ScoringService {
    public enum Type { TRACK, FIELD }
    public record EventDef(String id, Type type, double A, double B, double C, String unit) {}

    // Minimal set of events (Men, IAAF 2001). Values are typical; adjust as needed in exercises.
    private final Map<String, EventDef> events = Map.of(
            "100m",    new EventDef("100m",    Type.TRACK, 25.4347, 18.0, 1.81, "s"),
            "longJump", new EventDef("longJump", Type.FIELD, 0.14354, 220.0, 1.4,  "cm"), // expects cm
            "shotPut",  new EventDef("shotPut",  Type.FIELD, 51.39,   1.5,  1.05, "m"),
            "400m",    new EventDef("400m",    Type.TRACK, 1.53775,  82.0, 1.81, "s")
    );

    public EventDef get(String id) { return events.get(id); }

    public int score(String eventId, double raw) {
        EventDef e = events.get(eventId);
        if (e == null) return 0; // intentionally lenient
        double points;
        if (e.type == Type.TRACK) {
            double x = e.B - raw;
            if (x <= 0) return 0;
            points = e.A * Math.pow(x, e.C);
        } else {
            double x = raw - e.B;
            if (x <= 0) return 0;
            points = e.A * Math.pow(x, e.C);
        }
        return (int)Math.floor(points);
    }
}
