package com.example.decathlon.core;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScoringService {

    public static final class EventDef {
        public final String id;
        public final String menuLabel;
        public final String colLabel;
        public final boolean track;
        public final double a;
        public final double b;
        public final double c;
        public final double min;
        public final double max;

        public EventDef(String id, String menuLabel, String colLabel, boolean track,
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

    public static final List<EventDef> DECATHLON_EVENTS = List.of(
            new EventDef("100m", "100m (s)", "100m", true, 25.4347, 18.0, 1.81, 5, 20),
            new EventDef("110mHurdles", "110m Hurdles (s)", "110m hurdles", true, 5.74352, 28.5, 1.92, 10, 30),
            new EventDef("400m", "400m (s)", "400m", true, 1.53775, 82.0, 1.81, 20, 100),
            new EventDef("1500m", "1500m (s)", "1500m", true, 0.03768, 480.0, 1.85, 150, 400),
            new EventDef("discusThrow", "Discus Throw (m)", "Discus", false, 12.91, 4.0, 1.1, 0, 85),
            new EventDef("highJump", "High Jump (cm)", "High jump", false, 0.8465, 75.0, 1.42, 0, 300),
            new EventDef("javelinThrow", "Javelin Throw (m)", "Javelin", false, 10.14, 7.0, 1.08, 0, 110),
            new EventDef("longJump", "Long Jump (cm)", "Long jump", false, 0.14354, 220.0, 1.4, 0, 1000),
            new EventDef("poleVault", "Pole Vault (cm)", "Pole vault", false, 0.2797, 100.0, 1.35, 0, 1000),
            new EventDef("shotPut", "Shot Put (m)", "Shot put", false, 51.39, 1.5, 1.05, 0, 30)
    );

    public static final List<EventDef> HEPTATHLON_EVENTS = List.of(
            new EventDef("hep100mHurdles", "100m Hurdles (s)", "110m hurdles", true, 9.23076, 26.7, 1.835, 10, 30),
            new EventDef("hep200m", "200m (s)", "200m", true, 4.99087, 42.5, 1.81, 20, 100),
            new EventDef("hep800m", "800m (s)", "800m", true, 0.11193, 254.0, 1.88, 70, 250),
            new EventDef("hepHighJump", "High Jump (cm)", "High jump", false, 1.84523, 75.0, 1.348, 0, 300),
            new EventDef("hepJavelinThrow", "Javelin Throw (m)", "Javelin", false, 15.9803, 3.8, 1.04, 0, 110),
            new EventDef("hepLongJump", "Long Jump (cm)", "Long jump", false, 0.188807, 210.0, 1.41, 0, 1000),
            new EventDef("hepShotPut", "Shot Put (m)", "Shot put", false, 56.0211, 1.5, 1.05, 0, 30)
    );

    private final Map<String, EventDef> byId = new LinkedHashMap<>();

    public ScoringService() {
        for (EventDef e : DECATHLON_EVENTS) {
            byId.put(e.id, e);
        }
        for (EventDef e : HEPTATHLON_EVENTS) {
            byId.put(e.id, e);
        }
    }

    public EventDef get(String id) {
        return byId.get(id);
    }

    public int score(String eventId, double raw) {
        EventDef e = byId.get(eventId);
        if (e == null) {
            return 0;
        }
        double points;
        if (e.track) {
            double x = e.b - raw;
            if (x <= 0) {
                return 0;
            }
            points = e.a * Math.pow(x, e.c);
        } else {
            double x = raw - e.b;
            if (x <= 0) {
                return 0;
            }
            points = e.a * Math.pow(x, e.c);
        }
        return (int) Math.floor(points);
    }
}