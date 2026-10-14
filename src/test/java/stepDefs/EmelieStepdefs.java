package stepDefs;

import com.example.decathlon.core.CompetitionService;

import com.example.decathlon.core.ScoringService;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmelieStepdefs {

    private ScoringService scoringService = new ScoringService();
    private CompetitionService competitionService = new CompetitionService(scoringService);
    private String activeCompetitorName;
    private int actualPoints;
    private Exception lastException;

    @Given("typ of competition {string} is chosed and a {string} is registered")
    public void aCompetitorIsRegistered(String competition, String name) {
        competitionService.addCompetitor(name, competition);
        this.activeCompetitorName = name;
    }
    @When("I enter result {string} for event {string}")
    public void iEnterResultForEvent(String result, String event) {
        //gör om textsträngen till en double
        double rawResult = Double.parseDouble(result);

        try {
            actualPoints = competitionService.score(activeCompetitorName, event, rawResult);
        } catch (Exception e) {
            this.lastException = e;
        }
        //System.out.println(actualPoints);
        //System.out.println(lastException);
       // System.out.println("beräknande poängen är: " + actualPoints);
    };

    @Then("the event should display a {string}")
    public void theEventShouldDisplayA(String points) {

        int expected = Integer.parseInt(points);

        //System.out.println("Expected point: "+expected);
        //System.out.println("Actual point: " + actualPoints);
        assertEquals(expected, actualPoints);
    }


    @Then("I get the message {string}")
    public void iGetTheMessage(String text) {
        String expected = text;

        String actual = lastException.getMessage();
        //System.out.println(actual);
        assertEquals(expected, actual);


    }
}
