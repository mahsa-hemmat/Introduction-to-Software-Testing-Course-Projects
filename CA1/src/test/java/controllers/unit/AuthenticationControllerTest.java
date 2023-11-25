package controllers.unit;
import controllers.AuthenticationController;
import exceptions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import service.Baloot;
import model.User;

import java.util.HashMap;
import java.util.Map;


import static defines.Errors.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {
    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private Baloot baloot;

    @Test
    public void testLoginSuccess() throws NotExistentUser, IncorrectPassword {
        String username = "testUser";
        String password = "testPassword";
        Map<String, String> input = getInput(true, username, password);
        doNothing().when(baloot).login(username, password);

        ResponseEntity<String> response = authenticationController.login(input);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("login successfully!", response.getBody());

        // Verify login method was called
        verify(baloot, times(1)).login(username, password);
    }

    @Test
    public void testLoginWhenUserDoesNotExist() throws NotExistentUser, IncorrectPassword {
        String username = "testUser";
        String password = "testPassword";
        Map<String, String> input = getInput(true, username, password);

        doThrow(new NotExistentUser()).when(baloot).login(username, password);
        ResponseEntity<String> response = authenticationController.login(input);

        assertEquals(NOT_EXISTENT_USER, response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(baloot, times(1)).login(username, password);
    }

    @Test
    public void testLoginWhenPasswordIsIncorrect() throws NotExistentUser, IncorrectPassword {
        String username = "testUser";
        String password = "testPassword";
        Map<String, String> input = getInput(true, username, password);

        doThrow(new IncorrectPassword()).when(baloot).login(username, password);
        ResponseEntity<String> response = authenticationController.login(input);

        assertEquals(INCORRECT_PASSWORD, response.getBody());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        verify(baloot, times(1)).login(username, password);
    }

    @Test
    void testSignupSuccess() throws UsernameAlreadyTaken {
        Map<String, String> input = getInput(false, "testUser", "testPassword");
        doNothing().when(baloot).addUser(any(User.class));

        ResponseEntity<String> response = authenticationController.signup(input);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("signup successfully!", response.getBody());

        // Verify that addUser method was called exactly once
        verify(baloot, times(1)).addUser(any(User.class));
    }

    @Test
    void testSignupWhenUsernameIsAlreadyTaken() throws UsernameAlreadyTaken {
        Map<String, String> input = getInput(false, "testUser", "testPassword");
        doThrow(new UsernameAlreadyTaken()).when(baloot).addUser(any(User.class));

        ResponseEntity<String> response = authenticationController.signup(input);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(USERNAME_ALREADY_TAKEN, response.getBody());

        verify(baloot, times(1)).addUser(any(User.class));
    }

    // helper methods
    private static Map<String, String> getInput(boolean login, String username, String password) {
        Map<String, String> input = new HashMap<>();
        if(login) {
            input.put("username", username);
            input.put("password", password);
        } else {
            input.put("username", username);
            input.put("password", password);
            input.put("email", "newuser@example.com");
            input.put("birthDate", "2000-01-01");
            input.put("address", "Tehran St");
        }
        return input;
    }

}
