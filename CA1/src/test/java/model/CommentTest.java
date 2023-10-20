package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CommentTest {
    private Comment comment;

    @BeforeEach
    public void setUp() {
        comment = new Comment(1, "mahsa@gmail.com", "mahsa", 1, "This is a test comment");
    }

    @Test
    public void testAddUserVote() {
        comment.addUserVote("Alice", "like");
        comment.addUserVote("Bob", "dislike");
        comment.addUserVote("Charlie", "like");

        assertEquals(2, comment.getLike(), "Like count is incorrect");
        assertEquals(1, comment.getDislike(), "Dislike count is incorrect");

        assertEquals("like", comment.getUserVote().get("Alice"), "Alice's vote is incorrect");
        assertEquals("dislike", comment.getUserVote().get("Bob"), "Bob's vote is incorrect");
        assertEquals("like", comment.getUserVote().get("Charlie"), "Charlie's vote is incorrect");
    }

    @Test
    public void testUserLikesAndThenDislikes() {
        comment.addUserVote("Alice", "like");

        assertEquals(1, comment.getLike(), "Like count is incorrect");
        assertEquals(0, comment.getDislike(), "Dislike count is incorrect");
        assertEquals("like", comment.getUserVote().get("Alice"), "Alice's vote is incorrect");

        comment.addUserVote("Alice", "dislike");

        assertEquals(0, comment.getLike(), "Like count is incorrect");
        assertEquals(1, comment.getDislike(), "Dislike count is incorrect");
        assertEquals("dislike", comment.getUserVote().get("Alice"), "Alice's vote is incorrect");
    }
}

