package com.example.decathlon.core;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ScoringServiceNegativTest {

    private final ScoringService scoring = new ScoringService();




    @Test
    void shouldNotGiveSamePointsForDifferent100mTimes() {
        int a = scoring.score("100m", 10.0);
        int b = scoring.score("100m", 11.0);

        assertNotEquals(a, b);
    }


    @Test
    void shouldNotGiveSamePointsForDifferentShotPutDistances() {
        int a = scoring.score("shotPut", 14.0);
        int b = scoring.score("shotPut", 16.0);

        assertNotEquals(a, b);
    }






    @Test
    void shouldNotGiveSamePointsForDifferentHep200mTimes() {
        int a = scoring.score("hep200m", 23.5);
        int b = scoring.score("hep200m", 25.5);

        assertNotEquals(a, b);
    }






    @Test
    void shouldCalculateDifferentPointsForDecathlonAndHeptathlonHurdles() {
        int decathlonPoints = scoring.score("110mHurdles", 14.0);
        int heptathlonPoints = scoring.score("hep100mHurdles", 14.0);

        assertNotEquals(decathlonPoints, heptathlonPoints);
    }

}
