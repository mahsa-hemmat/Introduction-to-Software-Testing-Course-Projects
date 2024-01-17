package model;

import exceptions.InsufficientCredit;
import exceptions.InvalidCreditRange;
import exceptions.CommodityIsNotInBuyList;
import exceptions.NotInStock;
import io.cucumber.java.en.*;
import io.cucumber.spring.CucumberContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@CucumberContextConfiguration
public class MySteps {
    private User user;
    private Commodity commodity;
    private Exception exception;

    @Given("user credit is {float}")
    public void aUserWithABalanceOfExists(float balance) {
        user = new User();
        user.setCredit(balance);
    }

    @Given("commodity exists with quantity 1 in buy list")
    public void aCommodityExistsQ1() throws NotInStock {
        user = new User();
        commodity = new Commodity();
        commodity.setId("1");
        commodity.setInStock(100);
        user.addBuyItem(commodity);
    }

    @Given("commodity exists with quantity 7 in buy list")
    public void aCommodityExistsQ7() throws NotInStock {
        user = new User();
        commodity = new Commodity();
        commodity.setId("1");
        commodity.setInStock(100);
        user.addBuyItem(commodity);
        user.addBuyItem(commodity);
        user.addBuyItem(commodity);
        user.addBuyItem(commodity);
        user.addBuyItem(commodity);
        user.addBuyItem(commodity);
        user.addBuyItem(commodity);
    }

    @Given("commodity does not exist in buy list")
    public void commodityDoesNotExist() {
        user = new User();
        commodity = new Commodity();
        commodity.setId("1");
        commodity.setInStock(100);
    }

    @When("quantity is <2 and user removes commodity")
    public void removeWithSmallQuantity() {
        try {
            user.removeItemFromBuyList(commodity);
        } catch (Exception e) {
            exception = e;
        }

    }

    @When("quantity is 7 and user removes commodity")
    public void removeWithBigQuantity() {
        try {
            user.removeItemFromBuyList(commodity);
        } catch (Exception e) {
            exception = e;
        }

    }

    @When("user adds amount {float} to the credit")
    public void theUserAddsCreditOfToTheirAccount(float amount) {
        try {
            user.addCredit(amount);
        } catch (InvalidCreditRange e) {
            exception = e;
        }
    }

    @When("user withdraws amount {float}")
    public void theUserWithdrawsCreditOfFromTheirAccount(float amount) {
        try {
            user.withdrawCredit(amount);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("user credit should be {float}")
    public void theUserCreditShouldBe(float newBalance) {
        assertEquals(newBalance, user.getCredit());
    }

    @Then("there will be no such commodity in buy list")
    public void theCommodityShouldntBeThere() {assertFalse(user.getBuyList().containsKey("1"));}

    @Then("quantity of that commodity in buy list will be {int}")
    public void theCommodityShouldBeLess(int newQuantity) {assertEquals(newQuantity, user.getBuyList().get("1"));}

    @Then("InvalidCreditRange exception should be thrown")
    public void anInvalidCreditRangeExceptionShouldBeThrown() {
        assertInstanceOf(InvalidCreditRange.class, exception);
    }

    @Then("InsufficientCredit exception should be thrown")
    public void anInsufficientCreditExceptionShouldBeThrown() {
        assertInstanceOf(InsufficientCredit.class, exception);
    }

    @Then("an error should be raised: CommodityIsNotInBuyList")
    public void thenErrorShouldBeRaisedCommodityIsNotInBuyList() { assertInstanceOf(CommodityIsNotInBuyList.class, exception); }

    @And("user credit should still be {float}")
    public void theUserSBalanceShouldRemain(float balance) {
        assertEquals(balance, user.getCredit());
    }
}