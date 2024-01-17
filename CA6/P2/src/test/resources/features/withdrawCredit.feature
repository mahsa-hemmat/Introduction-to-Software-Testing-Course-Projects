Feature: user can decrease amount of their credit using this method
  As a user I want to withdraw credit so that I can make orders.

  Scenario: Withdrawing sufficient amount from credit
    Given user credit is 40.0
    When user withdraws amount 30.0
    Then user credit should be 10.0

  Scenario: Withdrawing insufficient amount from credit
    Given user credit is 40.0
    When user withdraws amount 50.0
    Then InsufficientCredit exception should be thrown
    And user credit should still be 40.0