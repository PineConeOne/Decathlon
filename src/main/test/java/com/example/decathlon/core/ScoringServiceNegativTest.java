package com.example.decathlon.core;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ScoringServiceNegativTest {

    private final ScoringService scoring = new ScoringService();


    // DECATHLON (TIOKAMP)

    @Test
    void shouldNotReturnZeroForValid100mResult() {
        int points = scoring.score("100m", 10.5);

        assertNotEquals(0, points);
    }

    @Test
    void shouldNotGiveSamePointsForDifferent100mTimes() {
        int a = scoring.score("100m", 10.0);
        int b = scoring.score("100m", 11.0);

        assertNotEquals(a, b);
    }

    @Test
    void shouldNotMatchIncorrectExpectedPointsForLongJump() {
        int points = scoring.score("longJump", 700);

        assertNotEquals(813, points);
    }

    @Test
    void shouldNotGiveSamePointsForDifferentShotPutDistances() {
        int a = scoring.score("shotPut", 14.0);
        int b = scoring.score("shotPut", 16.0);

        assertNotEquals(a, b);
    }


    // HEPTATHLON (SJUKAMP)


    @Test
    void shouldNotReturnZeroForValidHep100mHurdles() {
        int points = scoring.score("hep100mHurdles", 14.0);

        assertNotEquals(0, points);
    }

    @Test
    void shouldNotGiveSamePointsForDifferentHep200mTimes() {
        int a = scoring.score("hep200m", 23.5);
        int b = scoring.score("hep200m", 25.5);

        assertNotEquals(a, b);
    }

    @Test
    void shouldNotMatchIncorrectPointsForHepHighJump() {
        int points = scoring.score("hepHighJump", 175);

        assertNotEquals(900, points);
    }

    @Test
    void shouldNotGiveSamePointsForDifferentHep800mTimes() {
        int a = scoring.score("hep800m", 130.0);
        int b = scoring.score("hep800m", 140.0);

        assertNotEquals(a, b);
    }


    @Test
    void shouldCalculateDifferentPointsForDecathlonAndHeptathlonHurdles() {
        int decathlonPoints = scoring.score("110mHurdles", 14.0);
        int heptathlonPoints = scoring.score("hep100mHurdles", 14.0);

        assertNotEquals(decathlonPoints, heptathlonPoints);
    }

}
