package Controllers;

import controllers.CommoditiesController;
import exceptions.*;
import model.Comment;
import model.Commodity;
import model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import service.Baloot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static defines.Errors.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommoditiesControllerTest {
    @InjectMocks
    private CommoditiesController commoditiesController;

    @Mock
    private Baloot baloot;

    @Test
    public void testGetCommoditiesSuccess(){
        ArrayList<Commodity> sampleCommodities = getCommodities("1", "2");

        when(baloot.getCommodities()).thenReturn(sampleCommodities);
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.getCommodities();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleCommodities, response.getBody());

        verify(baloot, times(1)).getCommodities();
    }

    @Test
    public void testGetCommoditySuccess() throws NotExistentCommodity {
        String commodityId = "1";
        Commodity commodity = new Commodity();
        commodity.setId(commodityId);

        when(baloot.getCommodityById(commodityId)).thenReturn(commodity);
        ResponseEntity<Commodity> response = commoditiesController.getCommodity(commodityId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(commodity, response.getBody());

        verify(baloot, times(1)).getCommodityById(commodityId);
    }

    @Test
    public void testGetCommodityWhenCommodityDiesNotExist() throws NotExistentCommodity {
        when(baloot.getCommodityById(anyString())).thenThrow(new NotExistentCommodity());
        ResponseEntity<Commodity> response = commoditiesController.getCommodity(anyString());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(baloot, times(1)).getCommodityById(anyString());
    }

    @Test
    public void testRateCommoditySuccess() throws NotExistentCommodity {
        String commodityId = "1";
        Commodity commodity = new Commodity();
        commodity.setId(commodityId);
        String username = "testUser";
        String rate = "5";
        Map<String, String> input = new HashMap<>();
        input.put("username", username);
        input.put("rate", rate);

        when(baloot.getCommodityById(commodityId)).thenReturn(commodity);
        ResponseEntity<String> response = commoditiesController.rateCommodity(commodityId, input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("rate added successfully!", response.getBody());

        verify(baloot, times(1)).getCommodityById(commodityId);
    }

    @Test
    public void testRateCommodityWhenCommodityDoesNotExist() throws NotExistentCommodity {
        String username = "testUser";
        String rate = "5";
        Map<String, String> input = new HashMap<>();
        input.put("username", username);
        input.put("rate", rate);

        when(baloot.getCommodityById(anyString())).thenThrow(new NotExistentCommodity());
        ResponseEntity<String> response = commoditiesController.rateCommodity(anyString(), input);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(NOT_EXISTENT_COMMODITY, response.getBody());

        verify(baloot, times(1)).getCommodityById(anyString());
    }

    @Test
    public void testRateCommodityWhenNumberFormatIsInvalid() {
        String username = "testUser";
        String rate = "5.7";
        Map<String, String> input = new HashMap<>();
        input.put("username", username);
        input.put("rate", rate);

        ResponseEntity<String> response = commoditiesController.rateCommodity("2", input);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("For input string: \"5.7\"", response.getBody());
    }

    @Test
    public void testRateCommodityWhenRateRangeIsInvalid() throws NotExistentCommodity {
        String commodityId = "1";
        Commodity commodity = new Commodity();
        commodity.setId(commodityId);
        String username = "testUser";
        String rate = "20";
        Map<String, String> input = new HashMap<>();
        input.put("username", username);
        input.put("rate", rate);

        when(baloot.getCommodityById(commodityId)).thenReturn(commodity);
        ResponseEntity<String> response = commoditiesController.rateCommodity(commodityId, input);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(INVALID_RATE_RANGE, response.getBody());

        verify(baloot, times(1)).getCommodityById(commodityId);
    }

    @Test
    public void testAddCommentToCommoditySuccess() throws NotExistentUser {
        String id = "1";
        String username = "testUser";
        String commentText = "This is a test comment";
        User user = new User(username, "password", "test@gmail.com", "2000-01-01", "Tehran");
        Map<String, String> input = new HashMap<>();
        input.put("username", username);
        input.put("comment", commentText);

        when(baloot.generateCommentId()).thenReturn(10);
        when(baloot.getUserById(username)).thenReturn(user);
        doNothing().when(baloot).addComment(any(Comment.class));
        ResponseEntity<String> response = commoditiesController.addCommodityComment(id, input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("comment added successfully!", response.getBody());

        verify(baloot, times(1)).generateCommentId();
        verify(baloot, times(1)).getUserById(username);
        verify(baloot, times(1)).addComment(any(Comment.class));
    }

    @Test
    public void testAddCommentToCommodityWhenUserDoesNotExist() throws NotExistentUser {
        String id = "1";
        String username = "testUser";
        String commentText = "This is a test comment";
        Map<String, String> input = new HashMap<>();
        input.put("username", username);
        input.put("comment", commentText);

        when(baloot.generateCommentId()).thenReturn(10);
        when(baloot.getUserById(username)).thenThrow(new NotExistentUser());
        ResponseEntity<String> response = commoditiesController.addCommodityComment(id, input);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(NOT_EXISTENT_USER, response.getBody());

        verify(baloot, times(1)).generateCommentId();
        verify(baloot, times(1)).getUserById(username);
    }

    @Test
    public void testGetCommodityComment(){
        int commodityId = 1;
        ArrayList<Comment> sampleComments = new ArrayList<>();
        sampleComments.add(new Comment(1, "test1@gmail.com", "testUser1", commodityId, "Sample Comment1 For Test"));
        sampleComments.add(new Comment(2, "test2@gmail.com", "testUser2", commodityId, "Sample Comment2 For Test"));

        when(baloot.getCommentsForCommodity(commodityId)).thenReturn(sampleComments);
        ResponseEntity<ArrayList<Comment>> response = commoditiesController.getCommodityComment(String.valueOf(commodityId));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleComments, response.getBody());

        verify(baloot, times(1)).getCommentsForCommodity(commodityId);
    }

    @Test
    public void testSearchCommoditiesByName() {
        String searchOption = "name";
        String searchValue = "SampleName";
        ArrayList<Commodity> sampleCommodities = getCommodities("1", "2");
        Map<String, String> input = new HashMap<>();
        input.put("searchOption", searchOption);
        input.put("searchValue", searchValue);

        when(baloot.filterCommoditiesByName(searchValue)).thenReturn(sampleCommodities);
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.searchCommodities(input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleCommodities, response.getBody());

        verify(baloot, times(1)).filterCommoditiesByName(searchValue);
    }

    @Test
    public void testSearchCommoditiesByCategory() {
        String searchOption = "category";
        String searchValue = "SampleCategory";
        ArrayList<Commodity> sampleCommodities = getCommodities("1", "2");
        Map<String, String> input = new HashMap<>();
        input.put("searchOption", searchOption);
        input.put("searchValue", searchValue);

        when(baloot.filterCommoditiesByCategory(searchValue)).thenReturn(sampleCommodities);
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.searchCommodities(input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleCommodities, response.getBody());

        verify(baloot, times(1)).filterCommoditiesByCategory(searchValue);
    }

    @Test
    public void testSearchCommoditiesByProvider() {
        String searchOption = "provider";
        String searchValue = "SampleProviderName";
        ArrayList<Commodity> sampleCommodities = getCommodities("1", "2");
        Map<String, String> input = new HashMap<>();
        input.put("searchOption", searchOption);
        input.put("searchValue", searchValue);

        when(baloot.filterCommoditiesByProviderName(searchValue)).thenReturn(sampleCommodities);
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.searchCommodities(input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleCommodities, response.getBody());

        verify(baloot, times(1)).filterCommoditiesByProviderName(searchValue);
    }

    @Test
    public void testGetSuggestedCommoditiesSuccess() throws NotExistentCommodity {
        String commodityId = "1";
        Commodity commodity1 = new Commodity();
        commodity1.setId(commodityId);
        ArrayList<Commodity> sampleCommodities = getCommodities("2", "3");

        when(baloot.getCommodityById(commodityId)).thenReturn(commodity1);
        when(baloot.suggestSimilarCommodities(commodity1)).thenReturn(sampleCommodities);
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.getSuggestedCommodities(commodityId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleCommodities, response.getBody());

        verify(baloot, times(1)).getCommodityById(commodityId);
        verify(baloot, times(1)).suggestSimilarCommodities(commodity1);
    }

    @Test
    public void testGetSuggestedCommoditiesWhenCommodityDoesNotExist() throws NotExistentCommodity {
        String commodityId = "1";

        when(baloot.getCommodityById(commodityId)).thenThrow(new NotExistentCommodity());
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.getSuggestedCommodities(commodityId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(new ArrayList<>(), response.getBody());

        verify(baloot, times(1)).getCommodityById(commodityId);
    }

    // helper methods
    private static ArrayList<Commodity> getCommodities(String commodityId1, String commodityId2) {
        ArrayList<Commodity> sampleCommodities = new ArrayList<>();
        Commodity commodity1 = new Commodity();
        commodity1.setId(commodityId1);
        Commodity commodity2 = new Commodity();
        commodity2.setId(commodityId2);
        sampleCommodities.add(commodity1);
        sampleCommodities.add(commodity2);
        return sampleCommodities;
    }
}

