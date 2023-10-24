package model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    private User user;

    @BeforeEach
    public void setUp() {
        user = new User("Pardis", "password", "Pardis@gmail.com", "2000-09-01", "123 Main St");
    }

    @ParameterizedTest
    @CsvSource({
            "22.5",
            "30.0",
            "14.76"
    })
    public void testAddCreditWithValidInput(float amount) throws InvalidCreditRange {
        user.addCredit(amount);
        assertEquals(amount, user.getCredit(), "Credit should be added");
    }
    @Test
    public void testAddCreditWithMultipleAddition() throws InvalidCreditRange {
        user.addCredit(10.0f);
        assertEquals(10.0f , user.getCredit(), "10 Credit should be added");
        user.addCredit(20.0f);
        assertEquals(30.0f, user.getCredit(), "20 extra Credit should be added");
    }

    @Test
    public void testAddCreditWithInvalidInput() {
        assertThrows(InvalidCreditRange.class, () -> user.addCredit(-10.0f), "InvalidCreditRange exception should be thrown");
    }

    @Test
    public void testWithdrawCreditWithSufficientCredit() throws InsufficientCredit, InvalidCreditRange {
        user.addCredit(100.0f);
        user.withdrawCredit(25.0f);
        assertEquals(75.0f, user.getCredit(), " 25 out of 100 Credit should be withdrawn");
    }
    @Test
    public void testWithdrawCreditWithInsufficientCredit() {
        assertThrows(InsufficientCredit.class, () -> user.withdrawCredit(50.0f), "InsufficientCredit exception should be thrown");
    }

    @Test
    public void testAddBuyItem() throws NotInStock {
        Commodity commodity = new Commodity();
        commodity.setId("1");
        commodity.setInStock(10);
        user.addBuyItem(commodity);
        assertEquals(1, user.getBuyList().get("1"), "Item should be added to buy list");
    }

    @Test
    public void testAddBuyItemWithMultipleAdditions() throws NotInStock {
        Commodity commodity = new Commodity();
        commodity.setId("1");
        commodity.setInStock(10);
        user.addBuyItem(commodity);
        assertEquals(1, user.getBuyList().get("1"), "Item should be added to buy list");
        user.addBuyItem(commodity);
        assertEquals(2, user.getBuyList().get("1"), "Quantity should increase when the same item is added again");
    }

    @Test
    public void testAddBuyItemWhenNotInStock() throws NotInStock {
        Commodity commodity = new Commodity();
        commodity.setId("1");
        commodity.setInStock(0);
        assertThrows(NotInStock.class, () -> user.addBuyItem(commodity), "NotInStock exception should be thrown");
    }

    @Test
    public void testRemoveItemFromBuyListWithItemNotInBuyListException() {
        Commodity commodity = new Commodity();
        commodity.setId("1");
        assertThrows(CommodityIsNotInBuyList.class, () -> user.removeItemFromBuyList(commodity), "CommodityIsNotInBuyList exception should be thrown");
    }

    @Test
    public void testRemoveItemFromBuyList() throws CommodityIsNotInBuyList, NotInStock {
        Commodity commodity = new Commodity();
        commodity.setId("1");
        commodity.setInStock(10);
        user.addBuyItem(commodity);
        user.removeItemFromBuyList(commodity);
        assertNull(user.getBuyList().get("1"), "Item should be removed from buy list");
    }
    @Test
    public void testRemoveItemFromBuyListThatIsAlreadyInBuyList() throws CommodityIsNotInBuyList, NotInStock {
        Commodity commodity = new Commodity();
        commodity.setId("1");
        commodity.setInStock(10);
        user.addBuyItem(commodity);
        user.addBuyItem(commodity);
        user.removeItemFromBuyList(commodity);
        assertEquals(1, user.getBuyList().get("1"));
    }

    @ParameterizedTest
    @CsvSource({
            "1, 2, 2",
            "2, 5, 5",
            "3, 3, 3"
    })
    public void testAddPurchasedItemItemDoesNotExist(String commodityId, int quantity, int expectedQuantity) {
        user.addPurchasedItem(commodityId, quantity);
        assertEquals(expectedQuantity, user.getPurchasedList().get(commodityId), "Purchased quantity is incorrect");
    }

    @Test
    public void testAddPurchasedItemItemAlreadyExists() {
        user.addPurchasedItem("1", 3);
        user.addPurchasedItem("1", 2);
        assertEquals(5, user.getPurchasedList().get("1"));
    }
}

