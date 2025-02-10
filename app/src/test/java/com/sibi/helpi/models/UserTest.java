package com.sibi.helpi.models;

import com.sibi.helpi.utils.AppConstants.UserType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserTest {

    private User user;

    @Mock
    private List<Postable> mockPosts;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User("test@example.com", "John", "Doe", "1234567890", "profile.jpg");
    }

    @Test
    public void testConstructor() {
        assertEquals("test@example.com", user.getEmail());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("1234567890", user.getPhoneNumber());
        assertEquals("profile.jpg", user.getProfileImgUri());
        assertEquals(UserType.DEFAULT_USER, user.getUserType());
    }

    @Test
    public void testSetAndGetEmail() {
        user.setEmail("new@example.com");
        assertEquals("new@example.com", user.getEmail());
    }

    @Test
    public void testSetAndGetFirstName() {
        user.setFirstName("Alice");
        assertEquals("Alice", user.getFirstName());
    }

    @Test
    public void testSetAndGetLastName() {
        user.setLastName("Smith");
        assertEquals("Smith", user.getLastName());
    }

    @Test
    public void testSetAndGetPhoneNumber() {
        user.setPhoneNumber("0987654321");
        assertEquals("0987654321", user.getPhoneNumber());
    }

    @Test
    public void testSetAndGetProfileImgUri() {
        user.setProfileImgUri("new_profile.jpg");
        assertEquals("new_profile.jpg", user.getProfileImgUri());
    }

    @Test
    public void testSetAndGetUserType() {
        user.setUserType(UserType.LOCAL_ADMIN);
        assertEquals(UserType.LOCAL_ADMIN, user.getUserType());
    }

    @Test
    public void testIsAdmin() {
        user.setUserType(UserType.DEFAULT_USER);
        assertFalse(user.isAdmin());

        user.setUserType(UserType.LOCAL_ADMIN);
        assertTrue(user.isAdmin());

        user.setUserType(UserType.GLOBAL_ADMIN);
        assertTrue(user.isAdmin());
    }

    @Test
    public void testGetUsername() {
        assertEquals("John Doe", user.getUsername());
    }

    @Test
    public void testSetAndGetPosts() {
        user.setPosts(mockPosts);
        assertEquals(mockPosts, user.getPosts());
    }

    @Test
    public void testMockedPostListInteraction() {
        user.setPosts(mockPosts);
        when(mockPosts.size()).thenReturn(5);

        assertEquals(5, user.getPosts().size());
        verify(mockPosts).size();
    }
}
