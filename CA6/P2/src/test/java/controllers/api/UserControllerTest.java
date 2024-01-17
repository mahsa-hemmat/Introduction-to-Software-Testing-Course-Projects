package controllers.api;


import application.BalootApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.UserController;
import exceptions.InvalidCreditRange;
import exceptions.NotExistentUser;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static defines.Errors.INVALID_CREDIT_RANGE;
import static defines.Errors.NOT_EXISTENT_USER;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import service.Baloot;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@ContextConfiguration(classes = BalootApplication.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserController userController;

    @MockBean
    private Baloot baloot;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userController.setBaloot(baloot);  // dependency injection
    }

    @Test
    void testGetUser() throws Exception {
        User mockUser = new User();
        mockUser.setUsername("Mahsa");
        when(baloot.getUserById("Mahsa")).thenReturn(mockUser);

        mockMvc.perform(get("/users/Mahsa"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("Mahsa"));

        verify(baloot, times(1)).getUserById(anyString());
    }

    @Test
    void testGetUserWhenUserDoesNotExist() throws Exception {
        when(baloot.getUserById(anyString())).thenThrow(new NotExistentUser());
        mockMvc.perform(get("/users/NonExistentUser"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
        verify(baloot, times(1)).getUserById(anyString());
    }

    @Test
    void testAddCredit() throws Exception {
        String userId = "Mahsa";
        User mockUser = mock(User.class);
        mockUser.setUsername(userId);
        when(baloot.getUserById(userId)).thenReturn(mockUser);

        Map<String, String> input = new HashMap<>();
        input.put("credit", "50");

        mockMvc.perform(post("/users/{id}/credit", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().string("credit added successfully!"));

        verify(baloot, times(1)).getUserById(userId);
        verify(mockUser, times(1)).addCredit(50.0f);
    }

    @Test
    void testAddCreditWhenCreditIsInInvalidRange() throws Exception {
        String userId = "Mahsa";
        User mockUser = mock(User.class);
        mockUser.setUsername(userId);
        Map<String, String> input = new HashMap<>();
        input.put("credit", "-10");

        when(baloot.getUserById(userId)).thenReturn(mockUser);
        doThrow(new InvalidCreditRange()).when(mockUser).addCredit(-10.0f);

        mockMvc.perform(post("/users/{id}/credit", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(INVALID_CREDIT_RANGE));

        verify(baloot, times(1)).getUserById(userId);
        verify(mockUser, times(1)).addCredit(-10.0f);
    }

    @Test
    void testAddCreditWhenUserDoesNotExist() throws Exception {
        Map<String, String> input = new HashMap<>();
        input.put("credit", "-50.0");

        when(baloot.getUserById(anyString())).thenThrow(new NotExistentUser());

        mockMvc.perform(post("/users/id/credit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(NOT_EXISTENT_USER));

        verify(baloot, times(1)).getUserById(anyString());
    }

    @Test
    void testAddCreditWhenCreditIsInInvalidFormat() throws Exception {
        String userId = "username";
        Map<String, String> input = new HashMap<>();
        input.put("credit", "invalidNumber");

        mockMvc.perform(post("/users/{id}/credit", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Please enter a valid number for the credit amount."));

        verify(baloot, never()).getUserById(anyString());
    }
}
