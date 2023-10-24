package model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CommodityTest {
    private Commodity commodity;
    @BeforeEach
    public void setUp() {
        commodity = new Commodity();
        commodity.setName("1");
        commodity.setName("iPhone");
        commodity.setInStock(100);
        commodity.setInitRate(9.8f);

    }

    @ParameterizedTest
    @CsvSource({
            "-10, 90",
            "-100, 0",
            "-50, 50"
    })
    public void testUpdateInStockWithValidInput(int amount, int expectedStock) throws NotInStock {
        commodity.updateInStock(amount);
        assertEquals(expectedStock, commodity.getInStock(), "Stock should be updated correctly");
    }

    @Test
    public void testUpdateInStockWithMultipleUpdates() throws NotInStock {
        commodity.updateInStock(-10);
        assertEquals(90, commodity.getInStock(), "Stock should decrease by 10");
        commodity.updateInStock(-15);
        assertEquals(75, commodity.getInStock(), "Stock should decrease by 15");
    }

    @ParameterizedTest
    @CsvSource({
            "-200",
            "-150"
    })
    public void testUpdateInStockWithException(int amount) {
        assertThrows(NotInStock.class, () -> commodity.updateInStock(amount), "NotInStock exception should be thrown");
    }

    @ParameterizedTest
    @CsvSource({
            "Mahsa, 9",
            "Pardis, 5",
    })
    public void testAddRateWithinValidRange(String name, int score) throws InvalidRateRange {
        commodity.addRate(name, score);
        assertEquals(score, commodity.getUserRate().get(name), "User score should be added");
    }

    @ParameterizedTest
    @CsvSource({
            "Mahsa, -1",
            "Pardis, 15",
    })
    public void testAddRateWithinInValidRange(String name, int score){
        assertThrows(InvalidRateRange.class, () -> commodity.addRate(name, score), "InvalidRateRange exception should be thrown");
    }

    @Test
    public void testAddRateForSameUser() throws InvalidRateRange {
        commodity.addRate("Mahsa", 2);
        assertEquals(2, commodity.getUserRate().get("Mahsa"), "User score should be added");
        commodity.addRate("Mahsa", 9);
        assertEquals(9, commodity.getUserRate().get("Mahsa"), "User score should be updated");
        assertEquals(1, commodity.getUserRate().size(), "User rates count should be 1");
    }

    @ParameterizedTest
    @CsvSource({
            "Mahsa, 4, Pardis, 9",
            "Pardis, 5, Mohammad, 1"
    })
    public void testCalcRating(String userName1, int score1 ,String userName2, int score2) throws InvalidRateRange {
        commodity.addRate(userName1, score1);
        commodity.addRate(userName2, score2);
        assertEquals(9.8f, commodity.getInitRate(), "Initial rating should remain the same");
        float expectedRating = (9.8f + (float)score1 + (float)score2) / 3;
        assertEquals(expectedRating, commodity.getRating(), 0.01f);
    }
    @Test
    public void testCalcRatingWithSameUser() throws InvalidRateRange {
        commodity.addRate("Mahsa", 5);
        assertEquals((9.8f + 5.0f)/2, commodity.getRating(), "Rating should be calculated as (4.0 + 9 + 5) / 3");
        commodity.addRate("Mahsa", 9);
        assertEquals((9.8f + 9.0f )/2, commodity.getRating(), "Rating should be calculated as (4.0 + 9 + 5) / 3");

    }
}

