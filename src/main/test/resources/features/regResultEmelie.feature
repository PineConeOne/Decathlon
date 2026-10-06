Feature: registrera resultat för alla grenar i decathlon & heptathlon

  Scenario Outline: godkända resultat ska ge poäng för decathlon & hepathlon
    Given typ of competition "<competition>" is chosed and a "<name>" is registered
    When I enter result "<result>" for event "<event>"
    Then the event should display a "<points>"

  Examples:
    | name  | competition | result | event           | points |
    | Simon | decathlon   | 12.13  | 100m            | 626    |
    | Simon | decathlon   | 555    | longJump        | 492    |
    | Simon | decathlon   | 20.22  | shotPut         | 1113   |
    | Simon | decathlon   | 221    | highJump        | 1002   |
    | Simon | decathlon   | 50.23  | 400m            | 804    |
    | Simon | decathlon   | 18.09  | 110mHurdles     | 516    |
    | Simon | decathlon   | 65.32  | discusThrow     | 1194   |
    | Simon | decathlon   | 555    | poleVault       | 1083   |
    | Simon | decathlon   | 45.45  | javelinThrow    | 522    |
    | Simon | decathlon   | 240    | 1500m           | 953    |
    | Anna  | heptathlon  | 16.43  | hep100mHurdles  | 662    |
    | Anna  | heptathlon  | 185    | hepHighJump     | 1041   |
    | Anna  | heptathlon  | 16.12  | hepShotPut      | 936    |
    | Anna  | heptathlon  | 27.51  | hep200m         | 670    |
    | Anna  | heptathlon  | 501    | hepLongJump     | 562    |
    | Anna  | heptathlon  | 32.99  | hepJavelinThrow | 533    |
    | Anna  | heptathlon  | 179    | hep800m         | 375    |


Scenario Outline: För höga värden, just utanför limits, ska ge felmeddelande
  Given typ of competition "<competition>" is chosed and a "<name>" is registered
  When I enter result "<result>" for event "<event>"
  Then I get the message "<text>"

  Examples:
    | name  | competition | result  | event           | text                                                             |
    | Simon | decathlon   | 20.01   | 100m            | Value too high for 100m. Maximum accepted value is 20.0.         |
    | Simon | decathlon   | 1001    | longJump        | Value too high for Long jump. Maximum accepted value is 1000.0.  |
    | Simon | decathlon   | 30.01   | shotPut         | Value too high for Shot put. Maximum accepted value is 30.0.     |
    | Simon | decathlon   | 301     | highJump        | Value too high for High jump. Maximum accepted value is 300.0.   |
    | Simon | decathlon   | 100.01  | 400m            | Value too high for 400m. Maximum accepted value is 100.0.        |
    | Simon | decathlon   | 30.01   | 110mHurdles     | Value too high for 110m hurdles. Maximum accepted value is 30.0. |
    | Simon | decathlon   | 85.01   | discusThrow     | Value too high for Discus. Maximum accepted value is 85.0.       |
    | Simon | decathlon   | 1000.01 | poleVault       | Value too high for Pole vault. Maximum accepted value is 1000.0. |
    | Simon | decathlon   | 110.01  | javelinThrow    | Value too high for Javelin. Maximum accepted value is 110.0.     |
    | Simon | decathlon   | 400.01  | 1500m           | Value too high for 1500m. Maximum accepted value is 400.0.       |
    | Anna  | heptathlon  | 30.01   | hep100mHurdles  | Value too high for 110m hurdles. Maximum accepted value is 30.0. |
    | Anna  | heptathlon  | 301     | hepHighJump     | Value too high for High jump. Maximum accepted value is 300.0.   |
    | Anna  | heptathlon  | 30.01   | hepShotPut      | Value too high for Shot put. Maximum accepted value is 30.0.     |
    | Anna  | heptathlon  | 100.01  | hep200m         | Value too high for 200m. Maximum accepted value is 100.0.        |
    | Anna  | heptathlon  | 1000.01 | hepLongJump     | Value too high for Long jump. Maximum accepted value is 1000.0.  |
    | Anna  | heptathlon  | 110.01  | hepJavelinThrow | Value too high for Javelin. Maximum accepted value is 110.0.     |
    | Anna  | heptathlon  | 250.01  | hep800m         | Value too high for 800m. Maximum accepted value is 250.0.        |


  Scenario Outline: För låga värden, just utanför limits, ska ge felmeddelande
    Given typ of competition "<competition>" is chosed and a "<name>" is registered
    When I enter result "<result>" for event "<event>"
    Then I get the message "<text>"

    Examples:
      | name  | competition | result | event           | text                                                            |
      | Simon | decathlon   | 4.99   | 100m            | Value too low for 100m. Minimum accepted value is 5.0.          |
      | Simon | decathlon   | -0.01  | longJump        | Value too low for Long jump. Minimum accepted value is 0.0.     |
      | Simon | decathlon   | -0.01  | shotPut         | Value too low for Shot put. Minimum accepted value is 0.0.      |
      | Simon | decathlon   | -0.01  | highJump        | Value too low for High jump. Minimum accepted value is 0.0.     |
      | Simon | decathlon   | 19.99  | 400m            | Value too low for 400m. Minimum accepted value is 20.0.         |
      | Simon | decathlon   | 9.99   | 110mHurdles     | Value too low for 110m hurdles. Minimum accepted value is 10.0. |
      | Simon | decathlon   | -0.01  | discusThrow     | Value too low for Discus. Minimum accepted value is 0.0.        |
      | Simon | decathlon   | -0.01  | poleVault       | Value too low for Pole vault. Minimum accepted value is 0.0.    |
      | Simon | decathlon   | -0.01  | javelinThrow    | Value too low for Javelin. Minimum accepted value is 0.0.       |
      | Simon | decathlon   | 149.99 | 1500m           | Value too low for 1500m. Minimum accepted value is 150.0.       |
      | Anna  | heptathlon  | 9.99   | hep100mHurdles  | Value too low for 110m hurdles. Minimum accepted value is 10.0. |
      | Anna  | heptathlon  | -0.01  | hepHighJump     | Value too low for High jump. Minimum accepted value is 0.0.     |
      | Anna  | heptathlon  | -0.01  | hepShotPut      | Value too low for Shot put. Minimum accepted value is 0.0.      |
      | Anna  | heptathlon  | 19.99  | hep200m         | Value too low for 200m. Minimum accepted value is 20.0.         |
      | Anna  | heptathlon  | -0.01  | hepLongJump     | Value too low for Long jump. Minimum accepted value is 0.0.     |
      | Anna  | heptathlon  | -0.01  | hepJavelinThrow | Value too low for Javelin. Minimum accepted value is 0.0.       |
      | Anna  | heptathlon  | 69.99  | hep800m         | Value too low for 800m. Minimum accepted value is 70.0.         |
