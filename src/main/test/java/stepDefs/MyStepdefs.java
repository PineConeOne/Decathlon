package stepDefs;

import com.example.decathlon.core.CompetitionService;
import com.example.decathlon.core.ScoringService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyStepdefs {
    private CompetitionService competitionService;
    private String name;
    private boolean accepted;

    @Given("add competitor {string} for {string}")
    public void aCompetitorFor(String name, String competition) {
        competitionService = new CompetitionService(new ScoringService());
        this.name = name;
        competitionService.addCompetitor(name, competition);
    }

    @When("{string} with value {double}")
    public void withValue(String eventId, double raw) {
        try {
            competitionService.score(name, eventId, raw);
            accepted = true;
        } catch (CompetitionService.ScoreOutOfRangeException ex) {
            accepted = false;
        }
    }

    @Then("the result should be {string}")
    public void theResultShouldBe(String expected) {
        assertEquals(expected.equals("accepted"), accepted);
    }
}
