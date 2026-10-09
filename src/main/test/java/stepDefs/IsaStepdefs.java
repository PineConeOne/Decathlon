//package stepDefs;
//
//import com.example.decathlon.core.CompetitionService;
//import com.example.decathlon.core.ScoringService;
//import java.util.List;
//import java.util.Map;
//import io.cucumber.java.en.And;
//import io.cucumber.java.en.Given;
//import io.cucumber.java.en.Then;
//import io.cucumber.java.en.When;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class IsaStepdefs {
//
//    private ScoringService scoringService = new ScoringService();
//    private CompetitionService competitionService = new CompetitionService(scoringService);
//
//    // isaCompetitors.feature
//    @Given("the standing list for {string} is empty")
//    public void theStandingListForIsEmpty(String competition) {
//        assertEquals(0,
//                competitionService.competitorNamesForCompetition(competition).size());
//    }
//
//    // isaCompetitors.feature
//    @When("the user adds competitor {string} for {string}")
//    public void theUserAddsCompetitorFor(String name, String competition) {
//        competitionService.addCompetitor(name, competition);
//    }
//
//    // isaCompetitors.feature
//    @Then("the standing list for {string} has {int} competitor")
//    public void theStandingListForDecathlonHasCompetitor(String competition, int standingsCount) {
//        assertEquals(standingsCount, competitionService.competitorNamesForCompetition(competition).size());
//    }
//
//    // isaCompetitors.feature
//    @When("the user adds {int} competitors for {string}")
//    public void theUserAddsCompetitorsFor(int count, String competition) {
//        for (int i = 1; i <= count; i++) {
//            competitionService.addCompetitor("Tävlande" + i, competition);
//        }
//    }
//
//    // isaCompetitors.feature
//    @Given("the standing list for {string} has {int} competitors")
//    public void theStandingListForHasCompetitors(String competition, int count) {
//        for (int i = 1; i <= count; i++) {
//            competitionService.addCompetitor("Tävlande" + i, competition);
//        }
//    }
//
//    // isaCompetitors.feature
//    @And("an error message {string} should be displayed")
//    public void anErrorMessageShouldBeDisplayed(String arg0) {
//    }
//
//    // ------------------------------------------------------------------------------------
//    // ################################## STANDINGS #######################################
//    // ------------------------------------------------------------------------------------
//
//    // isaStandings.feature
//    @Given("the competitor {string} is registered for {string}")
//    public void theCompetitorIsRegisteredFor(String name, String competition) {
//        competitionService.addCompetitor(name, competition);
//    }
//
//    // isaStandings.feature
//    @When("the user adds event result {double} for {string} for {string}")
//    public void theUserAddsEventResultForFor(double result, String event, String name) {
//        competitionService.score(name, event, result);
//    }
//
//    // isaStandings.feature - Denna kod tog jag hjälp av AI då den var lite mer komplicerad
//    @Then("the {string} standings should show {string} with {int} points for {string}")
//    public void theStandingsShouldShowWithPointsFor(String competition, String name, int points, String event) {
//
//        // Deklarerar en lista som håller rader av typen StandingRow
//        List<CompetitionService.StandingRow> standings;
//
//        // Kontrollerar om tävlingen som testas är Decathlon eller Heptathlon.
//        if (competition.equals("Decathlon")) {
//            standings = competitionService.standingsFor(ScoringService.DECATHLON_EVENTS);
//        } else {
//            standings = competitionService.standingsFor(ScoringService.HEPTATHLON_EVENTS);
//        }
//
//        for (int i = 0; i < standings.size(); i++) {
//            CompetitionService.StandingRow row = standings.get(i);
//            if (row.name.equals(name)) {
//                assertEquals(points, row.scores.get(event));
//            }
//        }
//    }
//
//    // isaStandings.feature
//    @And("the user updates event result to {double} for {string} for {string}")
//    public void theUserUpdateEventResultToForFor(double result, String event, String name) {
//        competitionService.score(name, event, result);
//    }
//
//
//
//
//
//    @Given("the following competitors are registered for {string}:")
//    public void theFollowingCompetitorsAreRegisteredFor(String competition, List<String> names) {
//        for (int i = 0; i < names.size(); i++) {
//            String name = names.get(i);
//            competitionService.addCompetitor(name, competition);
//        }
//    }
//
//    @When("the user adds the following results for {string}:")
//    public void theUserAddsTheFollowingResultsFor(String event, List<Map<String, String>> results) {
//        for (int i = 0; i < results.size(); i++) {
//            Map<String, String> row = results.get(i);
//            String name = row.get("Name");
//            double result = Double.parseDouble(row.get("Result"));
//            competitionService.score(name, event, result);
//        }
//    }
//
//    @Then("the {string} standings should show:")
//    public void theStandingsShouldShow(String competition, List<Map<String, String>> expectedStandings) {
//
//        // Skapar en variabel för att spara den faktiska resultatlistan som systemet räknar fram.
//        List<CompetitionService.StandingRow> actualStandings;
//
//        // Kontrollerar om tävlingen som testas är Decathlon eller Heptathlon.
//        if ("Decathlon".equals(competition)) {
//            actualStandings = competitionService.standingsFor(ScoringService.DECATHLON_EVENTS);
//        } else {
//            actualStandings = competitionService.standingsFor(ScoringService.HEPTATHLON_EVENTS);
//        }
//
//        // Startar en loop som går igenom varje rad i förväntade Standings, en efter en.
//        for (int i = 0; i < expectedStandings.size(); i++) {
//
//            // Hämtar ut den aktuella raden med alla dess kolumner från testtabellen.
//            Map<String, String> expected = expectedStandings.get(i);
//
//            // Läser ut deltagarens namn från kolumnen "Name".
//            String expectedName = expected.get("Name");
//            // Läser ut vilken gren det gäller från kolumnen "Event".
//            String expectedEvent = expected.get("Event");
//            // Hämtar grenpoängen som text och omvandlar den till ett heltal.
//            int expectedPoints = Integer.parseInt(expected.get("Points"));
//            // Hämtar den förväntade totalsumman som text och omvandlar den till ett heltal.
//            int expectedTotalPoints = Integer.parseInt(expected.get("Total Points"));
//            // Hämtar den förväntade placeringen (t.ex. 1, 2 eller 3) och omvandlar den till ett heltal.
//            int expectedPosition = Integer.parseInt(expected.get("Position"));
//
//            // 1. Verifiera position (placering 1 = index 0, placering 2 = index 1, osv.)
//            int actualIndex = expectedPosition - 1;
//
//            // Hämtar ut deltagaren som faktiskt hamnade på den platsen i resultatlistan.
//            CompetitionService.StandingRow actualRowAtPosition = actualStandings.get(actualIndex);
//
//            // 1. Verifiera namn
//            assertEquals(expectedName, actualRowAtPosition.name);
//
//            // 2. Verifiera grenpoäng
//            assertEquals(expectedPoints, actualRowAtPosition.scores.get(expectedEvent));
//
//            // 3. Verifiera totalpoäng
//            assertEquals(expectedTotalPoints, actualRowAtPosition.total);
//
//        }
//    }
//}
//
//
