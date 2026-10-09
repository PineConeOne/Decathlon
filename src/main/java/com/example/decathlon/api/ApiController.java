package com.example.decathlon.api;

import com.example.decathlon.core.CompetitionService;
import com.example.decathlon.dto.ScoreReq;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final CompetitionService comp;

    public ApiController(CompetitionService comp) {
        this.comp = comp;
    }

    @PostMapping("/competitors")
    public ResponseEntity<?> add(@RequestBody Map<String, String> body) {
        String name = Optional.ofNullable(body.get("name")).orElse("");
        String competition = Optional.ofNullable(body.get("competition")).orElse("");
        try {
            comp.addCompetitor(name, competition);
            return ResponseEntity.status(201).build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/competitors")
    public ResponseEntity<?> competitors(@RequestParam String competition) {
        try {
            List<String> names = comp.competitorNamesSortedForCompetition(competition);
            return ResponseEntity.ok(names);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/score")
    public ResponseEntity<?> score(@RequestBody ScoreReq r) {
        try {
            int pts = comp.score(r.name(), r.event(), r.raw());
            return ResponseEntity.ok(Map.of("points", pts));
        } catch (CompetitionService.CompetitorNotFoundException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (CompetitionService.ScoreOutOfRangeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/score/invalid")
    public ResponseEntity<?> invalidResult(@RequestBody Map<String, String> body) {
        String name = Optional.ofNullable(body.get("name")).orElse("");
        String event = Optional.ofNullable(body.get("event")).orElse("");
        try {
            comp.invalidResult(name, event);
            return ResponseEntity.ok().build();
        } catch (CompetitionService.CompetitorNotFoundException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/standings")
    public Map<String, Object> standings() {
        return comp.standings();
    }

    @GetMapping(value = "/export.csv", produces = MediaType.TEXT_PLAIN_VALUE)
    public String export() {
        return comp.exportCsv();
    }

    @PostMapping(value = "/import", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> importCsv(@RequestBody String csv) {
        try {
            comp.importCsv(csv);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}