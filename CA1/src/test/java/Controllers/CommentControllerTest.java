package Controllers;

import controllers.CommentController;
import exceptions.NotExistentComment;
import model.Comment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import service.Baloot;

import java.util.HashMap;
import java.util.Map;

import static defines.Errors.NOT_EXISTENT_COMMENT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {
    @InjectMocks
    private CommentController commentController;

    @Mock
    private Baloot baloot;

    @Test
    public void testLikeCommentSuccess() throws NotExistentComment {
        int commentId = 1;
        String username = "testUser";
        Comment comment = new Comment(commentId, "test@gmail.com", "testUser1", 1, "Sample Comment For Test");
        Map<String, String> input = new HashMap<>();
        input.put("username", username);

        when(baloot.getCommentById(commentId)).thenReturn(comment);
        ResponseEntity<String> response = commentController.likeComment(String.valueOf(commentId), input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("The comment was successfully liked!", response.getBody());

        verify(baloot, times(1)).getCommentById(commentId);
    }

    @Test
    public void testLikeCommentWhenCommentDoesNotExist() throws NotExistentComment {
        int commentId = 1;
        String username = "testUser";
        Map<String, String> input = new HashMap<>();
        input.put("username", username);

        when(baloot.getCommentById(commentId)).thenThrow(new NotExistentComment());
        ResponseEntity<String> response = commentController.likeComment(String.valueOf(commentId), input);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(NOT_EXISTENT_COMMENT, response.getBody());

        verify(baloot, times(1)).getCommentById(commentId);
    }

    @Test
    public void testDislikeCommentSuccess() throws NotExistentComment {
        int commentId = 1;
        String username = "testUser";
        Comment comment = new Comment(commentId, "test@gmail.com", "testUser1", 1, "Sample Comment For Test");
        Map<String, String> input = new HashMap<>();
        input.put("username", username);

        when(baloot.getCommentById(commentId)).thenReturn(comment);
        ResponseEntity<String> response = commentController.dislikeComment(String.valueOf(commentId), input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("The comment was successfully disliked!", response.getBody());

        verify(baloot, times(1)).getCommentById(commentId);
    }

    @Test
    public void testDislikeCommentWhenCommentDoesNotExist() throws NotExistentComment {
        int commentId = 1;
        String username = "testUser";
        Map<String, String> input = new HashMap<>();
        input.put("username", username);

        when(baloot.getCommentById(commentId)).thenThrow(new NotExistentComment());
        ResponseEntity<String> response = commentController.likeComment(String.valueOf(commentId), input);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(NOT_EXISTENT_COMMENT, response.getBody());

        verify(baloot, times(1)).getCommentById(commentId);
    }

}
