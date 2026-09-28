package com.example.decathlon.core;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScoringServiceTest {
    private  ScoringService scoring = new ScoringService();

    // Decathlon
    @Test
    void shouldCalculatePointsFor100Meters() {
        int points = scoring.score("100m", 10.0);

        assertEquals(1096, points);
    }
    @Test
    void shouldCalculatePointsFor110Meters() {
        int points = scoring.score("110mHurdles", 10.0);

        assertEquals(1556, points);
    }
    @Test
    void shouldCalculatePointsFor400m() {
        int points = scoring.score("400m", 60.0);

        assertEquals(413, points);
    }

    @Test
    void shouldCalculatePointsFor1500m() {
        int points = scoring.score("1500m", 150);

        assertEquals(1719, points);
    }
    @Test
    void shouldCalculatePointsForDiscusThrow() {
        int points = scoring.score("discusThrow", 50);

        assertEquals(870, points);
    }
    @Test
    void shouldCalculatePointsForHighJump() {
        int points = scoring.score("highJump", 150);

        assertEquals(389, points);
    }
    @Test
    void shouldCalculatePointsForLongJump() {
        int points = scoring.score("longJump", 700);

        assertEquals(814, points);
    }

    @Test
    void shouldCalculatePointsForShotPut() {
        int points = scoring.score("shotPut", 15.0);

        assertEquals(790, points);
    }

    @Test
    void shouldCalculatePointsForPoleVault() {
        int points = scoring.score("poleVault", 400);

        assertEquals(617, points);
    }

    @Test
    void shouldCalculatePointsForJavelinThrow() {
        int points = scoring.score("javelinThrow", 60.0);

        assertEquals(738, points);
    }
    // Heptathlon
    @Test
    void shouldCalculatePointsForHep100mHurdles() {
        int points = scoring.score("hep100mHurdles", 14.0);

        assertEquals(978, points);
    }

    @Test
    void shouldCalculatePointsForHepHighJump() {
        int points = scoring.score("hepHighJump", 175);

        assertEquals(916, points);
    }

    @Test
    void shouldCalculatePointsForHepShotPut() {
        int points = scoring.score("hepShotPut", 14.0);

        assertEquals(794, points);
    }

    @Test
    void shouldCalculatePointsForHep200m() {
        int points = scoring.score("hep200m", 25.0);

        assertEquals(887, points);
    }

    @Test
    void shouldCalculatePointsForHepLongJump() {
        int points = scoring.score("hepLongJump", 600);

        assertEquals(850, points);
    }

    @Test
    void shouldCalculatePointsForHepJavelinThrow() {
        int points = scoring.score("hepJavelinThrow", 45.0);

        assertEquals(763, points);
    }

    @Test
    void shouldCalculatePointsForHep800m() {
        int points = scoring.score("hep800m", 135.0);

        assertEquals(893, points);
    }
}
