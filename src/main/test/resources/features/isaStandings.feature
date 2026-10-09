Feature: Standings

  Scenario Outline: Display current standings for 1 competitor
    Given the competitor "<Name>" is registered for "<Competition>"
    When the user adds event result <Result> for "<Event>" for "<Name>"
    Then the "<Competition>" standings should show "<Name>" with <Points> points for "<Event>"
    Examples:
      | Name | Competition | Result | Event          | Points |
      | Erik | Decathlon   | 250    | highJump       | 1296   |
      | Erik | Decathlon   | 600    | poleVault      | 1231   |
      | Anna | Heptathlon  | 10     | hep100mHurdles | 1617   |
      | Anna | Heptathlon  | 30     | hepShotPut     | 1887   |

  Scenario Outline: Display current standings for 3 competitor
    Given the competitor "<Name>" is registered for "<Competition>"
    When the user adds event result <Result> for "<Event>" for "<Name>"
    Then the "<Competition>" standings should show "<Name>" with <Points> points for "<Event>"
    Examples:
      | Name  | Competition | Result | Event | Points |
      | Erik  | Decathlon   | 10.5   | 100m  | 975    |
      | Johan | Decathlon   | 11     | 100m  | 861    |
      | Olle  | Decathlon   | 11.5   | 100m  | 753    |

  Scenario Outline: Display updated standings for 1 competitor
    Given the competitor "<Name>" is registered for "<Competition>"
    When the user adds event result <Result> for "<Event>" for "<Name>"
    And the user updates event result to <UpdatedResult> for "<Event>" for "<Name>"
    Then the "<Competition>" standings should show "<Name>" with <Points> points for "<Event>"
    Examples:
      | Name | Competition | Result | UpdatedResult | Event       | Points |
      | Erik | Decathlon   | 10     | 15            | 100m        | 185    |
      | Erik | Decathlon   | 60     | 65            | discusThrow | 1187   |
      | Anna | Heptathlon  | 110    | 150           | hep800m     | 693    |
      | Anna | Heptathlon  | 700    | 750           | hepLongJump | 1344   |






  Scenario: Display standings for multiple competitors in Decathlon
    Given the following competitors are registered for "Decathlon":
      | Erik  |
      | Johan |
      | Olle  |
    When the user adds the following results for "100m":
      | Name  | Result |
      | Erik  | 11.5   |
      | Johan | 11.0   |
      | Olle  | 10.5   |
    Then the "Decathlon" standings should show:
      | Position | Name  | Event | Points | Total Points |
      | 3        | Erik  | 100m  | 753    | 753          |
      | 2        | Johan | 100m  | 861    | 861          |
      | 1        | Olle  | 100m  | 975    | 975          |