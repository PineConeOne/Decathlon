package com.example.decathlon.core;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompetitionServiceTest {
    ScoringService scoring = new ScoringService();
    CompetitionService competitionService = new CompetitionService(scoring);

   @Test
    void addOneCompetitorSuccessful(){
       //CompetitionService competitionService = new CompetitionService(scoring);
       competitionService.addCompetitor("Staffan Staffansson", "Decathlon");
       int expected = 1;
       int actual = competitionService.competitorCount();

       assertEquals(expected,actual);
   }

   @Test
    void addTenCompetitorsSuccessful(){
        int expectedNumberOfCompetitors = 10;

        for (int i = 1; i<= expectedNumberOfCompetitors; i++){
            competitionService.addCompetitor("Deltagare" + i, "Decathlon");
        }
        int actualNumberOfCpompetitors = competitionService.competitorCount();
       System.out.println("Number of competitors added :"+actualNumberOfCpompetitors);

        assertEquals(expectedNumberOfCompetitors, actualNumberOfCpompetitors);
   }
    @Test
    void add40CompetitorsSuccessful(){
        int expectedNumberOfCompetitors = 40;

        for (int i = 1; i<= expectedNumberOfCompetitors; i++){
            competitionService.addCompetitor("Deltagare" + i, "Decathlon");
        }
        int actualNumberOfCpompetitors = competitionService.competitorCount();
        System.out.println("Number of competitors added :"+actualNumberOfCpompetitors);

        assertEquals(expectedNumberOfCompetitors, actualNumberOfCpompetitors);
    }

    @Test
    void adding41competitorsNotAllowed(){
        int expectedNumberOfCompetitors = 40;
        for (int i = 1; i<= 40; i++){
            competitionService.addCompetitor("Deltagare" + i, "Decathlon");
        }

        competitionService.addCompetitor("Deltagare 41", "Decathlon");  // Går det att lägga till 41:a deltageren?

        String expectedMessage = "Maximum number of competitor is already saved";

        int actualNumberOfCompetitors = competitionService.competitorCount();

        assertEquals(expectedNumberOfCompetitors, actualNumberOfCompetitors,expectedMessage);
    }

}
