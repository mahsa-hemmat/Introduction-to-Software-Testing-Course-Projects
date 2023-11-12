package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    private Order order;

    @BeforeEach
    public void init() {
        order = new Order();
        order.setId(1);
        order.setCustomer(2);
        order.setPrice(50);
        order.setQuantity(2);
    }

    @Test
    public void testEqualsWhenEqual() {
        Order otherOrder = new Order();
        otherOrder.setId(1);

        assertEquals(order, otherOrder);
    }

    @Test
    public void testEqualsWhenNotEqual() {
        Order otherOrder = new Order();
        otherOrder.setId(2);

        assertNotEquals(order, otherOrder);
    }

    @Test
    public void testEqualsWithNonOrderObject() {
        assertFalse(order.equals("Not an Order"));
    }
}
