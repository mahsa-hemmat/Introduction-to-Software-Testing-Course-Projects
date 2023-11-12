package domain;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EngineTest {

    private Engine engine;

    @BeforeEach
    void init() {
        engine = new Engine();
    }

    /*
    @Test
    void testAverageOrderQuantityByCustomerWhenNoOrders() {
        int average = engine.getAverageOrderQuantityByCustomer(1);
        assertEquals(0, average);
    }

    @Test
    void testAverageOrderQuantityByCustomerWhenOrdersAreNotForWantedCustomer() {
        Order order = new Order();
        order.setCustomer(2);
        engine.orderHistory.add(order);
        assertThrows(java.lang.ArithmeticException.class, ()->engine.getAverageOrderQuantityByCustomer(1), "/ by zero");
    }

    @Test
    void testAverageOrderQuantityByCustomerWithOrders() {
        Order order1 = new Order();
        int quantity1 = 5;
        order1.setCustomer(1);
        order1.setQuantity(quantity1);
        Order order2 = new Order();
        int quantity2 = 3;
        order2.setCustomer(1);
        order2.setQuantity(quantity2);
        engine.orderHistory.add(order1);
        engine.orderHistory.add(order2);

        int average = engine.getAverageOrderQuantityByCustomer(1);
        int expectedAverage = (quantity1 + quantity2) / 2;
        assertEquals(expectedAverage, average);
    }

    @Test
    void testQuantityPatternByPriceWhenNoOrders() {
        int pattern = engine.getQuantityPatternByPrice(100);
        assertEquals(0, pattern);
    }

    @Test
    void testQuantityPatternByPriceWithDifferentPrices() {
        Order order1 = new Order();
        order1.setId(1);
        order1.setPrice(10);
        order1.setQuantity(5);
        Order order2 = new Order();
        order2.setId(2);
        order2.setPrice(50);
        order2.setQuantity(3);
        engine.orderHistory.add(order1);
        engine.orderHistory.add(order2);

        int pattern = engine.getQuantityPatternByPrice(100);
        assertEquals(0, pattern);
    }

    @Test
    void testQuantityPatternByPriceWithNoPattenDetected() {
        Order order1 = new Order();
        order1.setId(1);
        order1.setPrice(10);
        order1.setQuantity(5);
        Order order2 = new Order();
        order2.setId(2);
        order2.setPrice(100);
        order2.setQuantity(10);
        Order order3 = new Order();
        order3.setId(3);
        order3.setPrice(100);
        order3.setQuantity(9);
        engine.orderHistory.add(order1);
        engine.orderHistory.add(order2);
        engine.orderHistory.add(order3);

        int pattern = engine.getQuantityPatternByPrice(100);
        assertEquals(0, pattern);
    }

    @Test
    void testQuantityPatternByPriceWithPatternDetected() {
        Order order1 = new Order();
        order1.setId(1);
        order1.setPrice(10);
        order1.setQuantity(5);
        Order order2 = new Order();
        order2.setId(2);
        order2.setPrice(100);
        order2.setQuantity(10);
        Order order3 = new Order();
        order3.setId(3);
        order3.setPrice(100);
        order3.setQuantity(15);
        engine.orderHistory.add(order1);
        engine.orderHistory.add(order2);
        engine.orderHistory.add(order3);

        int pattern = engine.getQuantityPatternByPrice(100);
        assertEquals(5, pattern);
    }


    @Test
    void testCustomerFraudulentQuantityWhenQuantityNotGtAverageOrderQuantity () {
        Order order = new Order();
        order.setCustomer(1);
        order.setQuantity(15);

        engine.orderHistory.add(order);
        int fraudulentQuantity = engine.getCustomerFraudulentQuantity(order);

        assertEquals(0, fraudulentQuantity);
    }

    @Test
    void testCustomerFraudulentQuantityWhenQuantityGtAverageOrderQuantity () {
        Order order1 = new Order();
        order1.setCustomer(1);
        order1.setQuantity(15);
        Order order2 = new Order();
        order2.setCustomer(1);
        order2.setQuantity(20);

        engine.orderHistory.add(order1);
        int fraudulentQuantity = engine.getCustomerFraudulentQuantity(order2);

        assertEquals(5, fraudulentQuantity);
    }*/


    @Test
    void testAddOrderAndGetFraudulentQuantityWhenOrderExists() {
        Order order = new Order();
        order.setCustomer(1);
        order.setQuantity(15);

        engine.orderHistory.add(order);
        int fraudulentQuantity = engine.addOrderAndGetFraudulentQuantity(order);

        assertEquals(0, fraudulentQuantity);
        assertEquals(1, engine.orderHistory.size());
    }

    @Test
    void testAddOrderAndGetFraudulentQuantityCase1() {
        Order order = new Order();
        order.setCustomer(1);
        order.setQuantity(15);

        int fraudulentQuantity = engine.addOrderAndGetFraudulentQuantity(order);

        assertEquals(15, fraudulentQuantity);
        assertEquals(1, engine.orderHistory.size());
    }

    @Test
    void testAddOrderAndGetFraudulentQuantityCase2() {
        Order order1 = new Order();
        order1.setId(1);
        order1.setCustomer(2);
        order1.setPrice(100);
        order1.setQuantity(10);
        engine.orderHistory.add(order1);

        Order order = new Order();
        order.setId(2);
        order.setCustomer(1);
        order.setPrice(100);
        order.setQuantity(9);

        assertThrows(java.lang.ArithmeticException.class, ()->engine.addOrderAndGetFraudulentQuantity(order), "/ by zero");
    }

    @Test
    void testAddOrderAndGetFraudulentQuantityCase3() {
        Order order1 = new Order();
        order1.setId(1);
        order1.setCustomer(1);
        order1.setPrice(100);
        order1.setQuantity(10);
        engine.orderHistory.add(order1);

        Order order2 = new Order();
        order2.setId(2);
        order2.setCustomer(1);
        order2.setPrice(100);
        order2.setQuantity(5);

        int fraudulentQuantity = engine.addOrderAndGetFraudulentQuantity(order2);

        assertEquals(0, fraudulentQuantity);
        assertEquals(2, engine.orderHistory.size());
    }
    @Test
    void testAddOrderAndGetFraudulentQuantityCase4() {
        Order order1 = new Order();
        order1.setId(1);
        order1.setCustomer(1);
        order1.setPrice(10);
        order1.setQuantity(5);
        Order order2 = new Order();
        order2.setId(2);
        order1.setCustomer(1);
        order2.setPrice(50);
        order2.setQuantity(5);
        Order order3 = new Order();
        order3.setId(3);
        order1.setCustomer(1);
        order3.setPrice(100);
        order3.setQuantity(3);
        engine.orderHistory.add(order1);
        engine.orderHistory.add(order2);

        int fraudulentQuantity = engine.addOrderAndGetFraudulentQuantity(order3);

        assertEquals(0, fraudulentQuantity);
        assertEquals(3, engine.orderHistory.size());
    }

    @Test
    void testAddOrderAndGetFraudulentQuantityCase5() {
        Order order1 = new Order();
        order1.setId(1);
        order1.setCustomer(1);
        order1.setPrice(10);
        order1.setQuantity(5);
        Order order2 = new Order();
        order2.setId(2);
        order1.setCustomer(1);
        order2.setPrice(100);
        order2.setQuantity(5);
        Order order3 = new Order();
        order3.setId(3);
        order1.setCustomer(1);
        order3.setPrice(100);
        order3.setQuantity(3);
        engine.orderHistory.add(order1);
        engine.orderHistory.add(order2);

        int fraudulentQuantity = engine.addOrderAndGetFraudulentQuantity(order3);

        assertEquals(0, fraudulentQuantity);
        assertEquals(3, engine.orderHistory.size());
    }

    @Test
    void testAddOrderAndGetFraudulentQuantityCase6() {
        Order order1 = new Order();
        order1.setId(1);
        order1.setCustomer(1);
        order1.setPrice(10);
        order1.setQuantity(5);
        Order order2 = new Order();
        order2.setId(2);
        order1.setCustomer(1);
        order2.setPrice(100);
        order2.setQuantity(10);
        Order order3 = new Order();
        order3.setId(3);
        order1.setCustomer(1);
        order3.setPrice(100);
        order3.setQuantity(10);
        Order order = new Order();
        order.setId(4);
        order.setCustomer(1);
        order.setPrice(100);
        order.setQuantity(3);
        engine.orderHistory.add(order1);
        engine.orderHistory.add(order2);
        engine.orderHistory.add(order3);

        int fraudulentQuantity = engine.addOrderAndGetFraudulentQuantity(order);

        assertEquals(0, fraudulentQuantity);
        assertEquals(4, engine.orderHistory.size());
    }

    @Test
    void testAddOrderAndGetFraudulentQuantityCase7() {
        Order order1 = new Order();
        order1.setId(1);
        order1.setCustomer(1);
        order1.setPrice(10);
        order1.setQuantity(5);
        Order order2 = new Order();
        order2.setId(2);
        order1.setCustomer(1);
        order2.setPrice(100);
        order2.setQuantity(10);
        Order order3 = new Order();
        order3.setId(3);
        order1.setCustomer(1);
        order3.setPrice(100);
        order3.setQuantity(15);
        order3.setQuantity(15);
        Order order = new Order();
        order.setId(4);
        order.setCustomer(1);
        order.setPrice(100);
        order.setQuantity(3);
        engine.orderHistory.add(order1);
        engine.orderHistory.add(order2);
        engine.orderHistory.add(order3);

        int fraudulentQuantity = engine.addOrderAndGetFraudulentQuantity(order);

        assertEquals(5, fraudulentQuantity);
        assertEquals(4, engine.orderHistory.size());
    }
}
