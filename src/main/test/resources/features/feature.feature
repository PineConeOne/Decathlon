Feature: Score Limits
  Scenario Outline: reject values that are below or above the allowed range
    Given add competitor "Anna" for "<competition>"
    When  "<eventId>" with value <raw>
    Then the result should be "<expected>"
    Examples:
      | competition | eventId | raw | expected |
