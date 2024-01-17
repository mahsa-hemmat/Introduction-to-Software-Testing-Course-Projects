package controllers.api;

import application.BalootApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.CommoditiesController;
import exceptions.NotExistentCommodity;
import exceptions.NotExistentUser;
import model.Comment;
import model.Commodity;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import service.Baloot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static defines.Errors.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CommoditiesController.class)
@ContextConfiguration(classes = BalootApplication.class)
public class CommoditiesControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommoditiesController commoditiesController;

    @MockBean
    private Baloot baloot;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        commoditiesController.setBaloot(baloot);  // dependency injection
    }

    @Test
    public void testGetAllCommodities() throws Exception {
        ArrayList<Commodity> sampleCommodities = getCommodities();

        when(baloot.getCommodities()).thenReturn(sampleCommodities);
        mockMvc.perform(get("/commodities"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"));
        verify(baloot, times(1)).getCommodities();
    }

    @Test
    public void testGetACommodity() throws Exception {
        String commodityId = "1";
        Commodity commodity = new Commodity();
        commodity.setId(commodityId);

        when(baloot.getCommodityById(commodityId)).thenReturn(commodity);
        mockMvc.perform(get("/commodities/{id}", commodityId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("1"));
        verify(baloot, times(1)).getCommodityById(commodityId);
    }

    @Test
    public void testGetCommodityWhenCommodityDoesNotExist() throws Exception {
        when(baloot.getCommodityById(anyString())).thenThrow(new NotExistentCommodity());
        mockMvc.perform(get("/commodities/{id}", "NonExistentCommodityId"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
        verify(baloot, times(1)).getCommodityById(anyString());
    }

    @Test
    public void testRateCommodity() throws Exception {
        String commodityId = "1";
        Commodity commodity = new Commodity();
        commodity.setId(commodityId);
        String username = "testUser";
        String rate = "5";
        Map<String, String> input = new HashMap<>(){{
            put("username", username);
            put("rate", rate);}};

        when(baloot.getCommodityById(commodityId)).thenReturn(commodity);
        mockMvc.perform(post("/commodities/{id}/rate", commodityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().string("rate added successfully!"));
        verify(baloot, times(1)).getCommodityById(commodityId);
    }

    @Test
    public void testRateCommodityWhenCommodityDoesNotExist() throws Exception {
        String username = "testUser";
        String rate = "5";
        Map<String, String> input = new HashMap<>(){{
            put("username", username);
            put("rate", rate);}};

        when(baloot.getCommodityById(anyString())).thenThrow(new NotExistentCommodity());
        mockMvc.perform(post("/commodities/{id}/rate", "NonExistentCommodityId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(NOT_EXISTENT_COMMODITY));
        verify(baloot, times(1)).getCommodityById(anyString());
    }

    @Test
    public void testRateCommodityWhenNumberFormatIsInvalid() throws Exception {
        String username = "testUser";
        String rate = "5.7";
        Map<String, String> input = new HashMap<>(){{
            put("username", username);
            put("rate", rate);}};
        mockMvc.perform(post("/commodities/{id}/rate", "commodityId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("For input string: \"5.7\""));
        verify(baloot, never()).getCommodityById(anyString());
    }

    @Test
    public void testRateCommodityWhenRateRangeIsInvalid() throws Exception {
        String commodityId = "1";
        Commodity commodity = new Commodity();
        commodity.setId(commodityId);
        String username = "testUser";
        String rate = "20";
        Map<String, String> input = new HashMap<>(){{
            put("username", username);
            put("rate", rate);}};

        when(baloot.getCommodityById(commodityId)).thenReturn(commodity);
        mockMvc.perform(post("/commodities/{id}/rate", commodityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(INVALID_RATE_RANGE));
        verify(baloot, times(1)).getCommodityById(commodityId);
    }

    @Test
    public void testAddCommentToCommoditySuccess() throws Exception {
        String commodityId = "1";
        Commodity commodity = new Commodity();
        commodity.setId(commodityId);
        String username = "testUser";
        String commentText = "This is a test comment";
        User user = new User(username, "password", "test@gmail.com", "2000-01-01", "Tehran");
        Map<String, String> input =  new HashMap<>() {{
            put("username", username);
            put("comment", commentText);}};

        when(baloot.getCommodityById(commodityId)).thenReturn(commodity);
        when(baloot.generateCommentId()).thenReturn(10);
        when(baloot.getUserById(username)).thenReturn(user);
        doNothing().when(baloot).addComment(any(Comment.class));

        mockMvc.perform(post("/commodities/{id}/comment", commodityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().string("comment added successfully!"));

        verify(baloot, times(1)).getCommodityById(commodityId);
        verify(baloot, times(1)).generateCommentId();
        verify(baloot, times(1)).getUserById(username);
        verify(baloot, times(1)).addComment(any(Comment.class));
    }

    @Test
    public void testAddCommentToCommodityWhenUserDoesNotExist() throws Exception {
        String commodityId = "1";
        Commodity commodity = new Commodity();
        commodity.setId(commodityId);
        String username = "testUser";
        String commentText = "This is a test comment";
        Map<String, String> input =  new HashMap<>() {{
            put("username", username);
            put("comment", commentText);}};

        when(baloot.getCommodityById(commodityId)).thenReturn(commodity);
        when(baloot.generateCommentId()).thenReturn(10);
        when(baloot.getUserById(username)).thenThrow(new NotExistentUser());

        mockMvc.perform(post("/commodities/{id}/comment", commodityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(NOT_EXISTENT_USER));

        verify(baloot, times(1)).getCommodityById(commodityId);
        verify(baloot, times(1)).generateCommentId();
        verify(baloot, times(1)).getUserById(username);
    }

    @Test
    public void testAddCommentToCommodityWhenCommodityDoesNotExist() throws Exception {
        String commodityId = "1";
        String username = "testUser";
        String commentText = "This is a test comment";
        Map<String, String> input = new HashMap<>(){{
            put("username", username);
            put("comment", commentText);
        }};

        when(baloot.getCommodityById(commodityId)).thenThrow(new NotExistentCommodity());
        mockMvc.perform(post("/commodities/{id}/comment", commodityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(NOT_EXISTENT_COMMODITY));
        verify(baloot, times(1)).getCommodityById(commodityId);
    }

    @Test
    public void testGetCommentsForACommodity() throws Exception {
        int commodityId = 1;
        Commodity commodity = new Commodity();
        commodity.setId(String.valueOf(commodityId));
        ArrayList<Comment> sampleComments = new ArrayList<>();
        sampleComments.add(new Comment(1, "test1@gmail.com", "testUser1", commodityId, "Sample Comment1 For Test"));

        when(baloot.getCommodityById(String.valueOf(commodityId))).thenReturn(commodity);
        when(baloot.getCommentsForCommodity(commodityId)).thenReturn(sampleComments);
        mockMvc.perform(get("/commodities/{id}/comment", commodityId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userEmail").value("test1@gmail.com"))
                .andExpect(jsonPath("$[0].username").value("testUser1"))
                .andExpect(jsonPath("$[0].commodityId").value(commodityId))
                .andExpect(jsonPath("$[0].text").value("Sample Comment1 For Test"));
        verify(baloot, times(1)).getCommodityById(String.valueOf(commodityId));
        verify(baloot, times(1)).getCommentsForCommodity(commodityId);
    }

    @Test
    public void testGetCommodityCommentWhenCommodityDoesNotExist() throws Exception {
        when(baloot.getCommodityById(anyString())).thenThrow(new NotExistentCommodity());
        mockMvc.perform(get("/commodities/{id}/comment", "NonExistentCommodityId"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
        verify(baloot, times(1)).getCommodityById(anyString());
    }

    @Test
    public void testSearchCommoditiesByName() throws Exception {
        String searchOption = "name";
        String searchValue = "SampleName";
        ArrayList<Commodity> sampleCommodities = getCommodities();
        Map<String, String> input = new HashMap<>() {{
            put("searchOption",searchOption);
            put("searchValue",searchValue);
        }};

        when(baloot.filterCommoditiesByName(searchValue)).thenReturn(sampleCommodities);
        mockMvc.perform(post("/commodities/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"));
        verify(baloot, times(1)).filterCommoditiesByName(searchValue);
    }

    @Test
    public void testSearchCommoditiesByCategory() throws Exception{
        String searchOption = "category";
        String searchValue = "SampleCategory";
        ArrayList<Commodity> sampleCommodities = getCommodities();
        Map<String, String> input = new HashMap<>() {{
            put("searchOption",searchOption);
            put("searchValue",searchValue);
        }};

        when(baloot.filterCommoditiesByCategory(searchValue)).thenReturn(sampleCommodities);
        mockMvc.perform(post("/commodities/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"));
        verify(baloot, times(1)).filterCommoditiesByCategory(searchValue);
    }

    @Test
    public void testSearchCommoditiesByProvider() throws Exception{
        String searchOption = "provider";
        String searchValue = "SampleProviderName";
        ArrayList<Commodity> sampleCommodities = getCommodities();
        Map<String, String> input = new HashMap<>() {{
            put("searchOption",searchOption);
            put("searchValue",searchValue);
        }};

        when(baloot.filterCommoditiesByProviderName(searchValue)).thenReturn(sampleCommodities);
        mockMvc.perform(post("/commodities/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"));
        verify(baloot, times(1)).filterCommoditiesByProviderName(searchValue);
    }

    @Test
    public void testGetSuggestedCommodities() throws Exception {
        String commodityId = "1";
        Commodity commodity = new Commodity();
        commodity.setId(commodityId);
        ArrayList<Commodity> sampleCommodities = getCommodities();

        when(baloot.getCommodityById(commodityId)).thenReturn(commodity);
        when(baloot.suggestSimilarCommodities(commodity)).thenReturn(sampleCommodities);
        mockMvc.perform(get("/commodities/{id}/suggested", commodityId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"));
        verify(baloot, times(1)).getCommodityById(commodityId);
        verify(baloot, times(1)).suggestSimilarCommodities(commodity);
    }

    @Test
    public void testGetSuggestedCommoditiesWhenCommodityDoesNotExist() throws Exception {
        when(baloot.getCommodityById(anyString())).thenThrow(new NotExistentCommodity());
        mockMvc.perform(get("/commodities/{id}/suggested", "NonExistentCommodityId"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("[]"));
        verify(baloot, times(1)).getCommodityById(anyString());
    }

    // helper methods
    private static ArrayList<Commodity> getCommodities() {
        ArrayList<Commodity> sampleCommodities = new ArrayList<>();
        Commodity commodity1 = new Commodity();
        commodity1.setId("1");
        Commodity commodity2 = new Commodity();
        commodity2.setId("2");
        sampleCommodities.add(commodity1);
        sampleCommodities.add(commodity2);
        return sampleCommodities;
    }
}
