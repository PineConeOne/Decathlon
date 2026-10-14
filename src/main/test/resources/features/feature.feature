Feature: Score Limits
  Scenario Outline: accept results at the limits and reject results outside them
    Given add competitor "Anna" for "<competition>"
    When "<eventId>" with value <raw>
    Then the result should be "<expected>"

    Examples:
      | competition | eventId         | raw    | expected |
      | Decathlon   | 100m            | 4.9     | rejected |
      | Decathlon   | 100m            | 5      | accepted |
      | Decathlon   | 100m            | 20     | accepted |
      | Decathlon   | 100m            | 20.1   | rejected |
      | Decathlon   | 110mHurdles     | 9.9    | rejected |
      | Decathlon   | 110mHurdles     | 10     | accepted |
      | Decathlon   | 110mHurdles     | 30     | accepted |
      | Decathlon   | 110mHurdles     | 30.1   | rejected |
      | Decathlon   | 400m            | 19.9   | rejected |
      | Decathlon   | 400m            | 20     | accepted |
      | Decathlon   | 400m            | 100    | accepted |
      | Decathlon   | 400m            | 100.1  | rejected |
      | Decathlon   | 1500m           | 149.9  | rejected |
      | Decathlon   | 1500m           | 150    | accepted |
      | Decathlon   | 1500m           | 400    | accepted |
      | Decathlon   | 1500m           | 400.1  | rejected |
      | Decathlon   | discusThrow     | -0.1   | rejected |
      | Decathlon   | discusThrow     | 0      | accepted |
      | Decathlon   | discusThrow     | 85     | accepted |
      | Decathlon   | discusThrow     | 85.1   | rejected |
      | Decathlon   | highJump        | -0.1   | rejected |
      | Decathlon   | highJump        | 0      | accepted |
      | Decathlon   | highJump        | 300    | accepted |
      | Decathlon   | highJump        | 300.1  | rejected |
      | Decathlon   | javelinThrow    | -0.1   | rejected |
      | Decathlon   | javelinThrow    | 0      | accepted |
      | Decathlon   | javelinThrow    | 110    | accepted |
      | Decathlon   | javelinThrow    | 110.1  | rejected |
      | Decathlon   | longJump        | -0.1   | rejected |
      | Decathlon   | longJump        | 0      | accepted |
      | Decathlon   | longJump        | 1000   | accepted |
      | Decathlon   | longJump        | 1000.1 | rejected |
      | Decathlon   | poleVault       | -0.1   | rejected |
      | Decathlon   | poleVault       | 0      | accepted |
      | Decathlon   | poleVault       | 1000   | accepted |
      | Decathlon   | poleVault       | 1000.1 | rejected |
      | Decathlon   | shotPut         | -0.1   | rejected |
      | Decathlon   | shotPut         | 0      | accepted |
      | Decathlon   | shotPut         | 30     | accepted |
      | Decathlon   | shotPut         | 30.1   | rejected |
      | Heptathlon  | hep100mHurdles  | 9.9    | rejected |
      | Heptathlon  | hep100mHurdles  | 10     | accepted |
      | Heptathlon  | hep100mHurdles  | 30     | accepted |
      | Heptathlon  | hep100mHurdles  | 30.1   | rejected |
      | Heptathlon  | hepHighJump     | -0.1   | rejected |
      | Heptathlon  | hepHighJump     | 0      | accepted |
      | Heptathlon  | hepHighJump     | 300    | accepted |
      | Heptathlon  | hepHighJump     | 300.1  | rejected |
      | Heptathlon  | hepJavelinThrow | -0.1   | rejected |
      | Heptathlon  | hepJavelinThrow | 0      | accepted |
      | Heptathlon  | hepJavelinThrow | 110    | accepted |
      | Heptathlon  | hepJavelinThrow | 110.1  | rejected |
      | Heptathlon  | hepLongJump     | -0.1   | rejected |
      | Heptathlon  | hepLongJump     | 0      | accepted |
      | Heptathlon  | hepLongJump     | 1000   | accepted |
      | Heptathlon  | hepLongJump     | 1000.1 | rejected |
      | Heptathlon  | hepShotPut      | -0.1   | rejected |
      | Heptathlon  | hepShotPut      | 0      | accepted |
      | Heptathlon  | hepShotPut      | 30     | accepted |
      | Heptathlon  | hepShotPut      | 30.1   | rejected |
      | Heptathlon  | hep200m         | 19.9   | rejected |
      | Heptathlon  | hep200m         | 20     | accepted |
      | Heptathlon  | hep200m         | 100    | accepted |
      | Heptathlon  | hep200m         | 100.1  | rejected |
      | Heptathlon  | hep800m         | 69.9   | rejected |
      | Heptathlon  | hep800m         | 70     | accepted |
      | Heptathlon  | hep800m         | 250    | accepted |
      | Heptathlon  | hep800m         | 250.1  | rejected |
