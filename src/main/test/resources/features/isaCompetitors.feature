Feature: Add competitors

  Scenario: Add 1 competitor in Decathlon
    Given the standing list for "Decathlon" is empty
    When the user adds competitor "Erik" for "Decathlon"
    Then the standing list for "Decathlon" has 1 competitor

  Scenario: Add the same competitor twice in Decathlon
    Given the standing list for "Decathlon" is empty
    When the user adds competitor "Erik" for "Decathlon"
    And the user adds competitor "Erik" for "Decathlon"
    Then the standing list for "Decathlon" has 1 competitor

  Scenario: Add 1 competitor in Heptathlon
    Given the standing list for "Heptathlon" is empty
    When the user adds competitor "Anna" for "Heptathlon"
    Then the standing list for "Heptathlon" has 1 competitor

  Scenario: Add 40 competitor in Decathlon
    Given the standing list for "Decathlon" is empty
    When the user adds 40 competitors for "Decathlon"
    Then the standing list for "Decathlon" has 40 competitor

  Scenario: Add 41 competitor in Heptathlon and get an error message
    Given the standing list for "Heptathlon" has 40 competitors
    When the user adds competitor "Anna" for "Hepthathlon"
    Then the standing list for "Heptathlon" has 40 competitor
    And an error message "??????" should be displayed