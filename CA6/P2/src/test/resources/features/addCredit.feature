Feature: User can add their balance using this function and the description is as follows:
  As a user, I want to add some amount of money to increase my credit so that I can make orders.

  Scenario: the user adds a positive amount
    Given user credit is 40.0
    When user adds amount 12.0 to the credit
    Then user credit should be 52.0

  Scenario: the user adds a negative amount
    Given user credit is 40.0
    When user adds amount -12.0 to the credit
    Then InvalidCreditRange exception should be thrown
    And user credit should still be 40.0