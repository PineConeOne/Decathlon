package stepDefs;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class MyStepdefs {
    @Given("add competitor {string} for {string}")
    public void aCompetitorFor(String arg0, String arg1, String arg2) {
    }

    @When("{string} with value {}")
    public void withValue(String arg0, String arg1, String arg2) {
    }

    @Then("the result should be {string}")
    public void theResultShouldBe(String arg0, String arg1) {
    }
}
