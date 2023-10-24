package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class CommentTest {
    private Comment comment;

    @BeforeEach
    public void init() {
        comment = new Comment(1, "user@gmail.com", "user", 1, "This is a test comment");
    }

    @Test
    public void testAddUserVoteWithLike() {
        comment.addUserVote("Mahsa", "like");
        assertEquals(1, comment.getLike(), "Like count is incorrect");
        assertEquals(0, comment.getDislike(), "Dislike count is incorrect");
    }
    @Test
    public void testAddUserVoteWithDislike() {
        comment.addUserVote("Pardis", "dislike");
        assertEquals(0, comment.getLike(), "Like count is incorrect");
        assertEquals(1, comment.getDislike(), "Dislike count is incorrect");
    }

    @Test
    public void testAddUserVoteWithMultipleVotes() {
        comment.addUserVote("Mahsa", "like");
        comment.addUserVote("Pardis", "dislike");
        comment.addUserVote("Sara", "like");
        comment.addUserVote("Ali", "dislike");

        assertEquals(2, comment.getLike(), "Like count should be 2");
        assertEquals(2, comment.getDislike(), "Dislike count should be 2");
    }

    @Test
    public void testUserLikesAndThenDislikes() {
        comment.addUserVote("Mahsa", "like");

        assertEquals(1, comment.getLike(), "Like count is incorrect");
        assertEquals(0, comment.getDislike(), "Dislike count is incorrect");
        assertEquals("like", comment.getUserVote().get("Mahsa"), "User vote is incorrect");

        comment.addUserVote("Mahsa", "dislike");

        assertEquals(0, comment.getLike(), "Like count is incorrect");
        assertEquals(1, comment.getDislike(), "Dislike count is incorrect");
        assertEquals("dislike", comment.getUserVote().get("Mahsa"), "User vote is incorrect");
    }

    @Test
    public void testGetCurrentDateFormat() {
        String formattedDate = comment.getCurrentDate();
        SimpleDateFormat expectedDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String expectedDate = expectedDateFormat.format(new Date());
        assertEquals(expectedDate, formattedDate);
    }

}

