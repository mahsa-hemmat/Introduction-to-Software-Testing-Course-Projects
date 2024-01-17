Feature: users can remove items from their list using this function
  As a user I want to remove items that I selected before in order to modify my order.
  Scenario: user removes a commodity that exists with quantity = 1 from buy list
    Given commodity exists with quantity 1 in buy list
    When quantity is <2 and user removes commodity
    Then there will be no such commodity in buy list

  Scenario: user removes a commodity that exists with quantity > 1 from buy list
    Given commodity exists with quantity 7 in buy list
    When quantity is 7 and user removes commodity
    Then quantity of that commodity in buy list will be 6

  Scenario: user removes a commodity that doesn't exist in buy list
    Given commodity does not exist in buy list
    When quantity is <2 and user removes commodity
    Then an error should be raised: CommodityIsNotInBuyList