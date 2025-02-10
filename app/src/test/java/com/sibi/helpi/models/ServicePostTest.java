package com.sibi.helpi.models;

import com.sibi.helpi.utils.AppConstants.PostStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ServicePostTest {

    private ServicePost servicePost;

    @Mock
    private List<String> mockImageUrls;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        servicePost = new ServicePost("Title", "Description", "Category", "SubCategory", "Region", "User123", "New");
    }

    @Test
    public void testConstructor() {
        assertEquals("Title", servicePost.getTitle());
        assertEquals("Description", servicePost.getDescription());
        assertEquals("Category", servicePost.getCategory());
        assertEquals("SubCategory", servicePost.getSubCategory());
        assertEquals("Region", servicePost.getRegion());
        assertEquals("User123", servicePost.getUserId());
    }




    @Test
    public void testSetAndGetTitle() {
        servicePost.setTitle("New Title");
        assertEquals("New Title", servicePost.getTitle());
    }

    @Test
    public void testSetAndGetId() {
        servicePost.setId("12345");
        assertEquals("12345", servicePost.getId());
    }

    @Test
    public void testSetAndGetDescription() {
        servicePost.setDescription("New Description");
        assertEquals("New Description", servicePost.getDescription());
    }

    @Test
    public void testSetAndGetCategory() {
        servicePost.setCategory("New Category");
        assertEquals("New Category", servicePost.getCategory());
    }

    @Test
    public void testSetAndGetSubCategory() {
        servicePost.setSubCategory("New SubCategory");
        assertEquals("New SubCategory", servicePost.getSubCategory());
    }

    @Test
    public void testSetAndGetRegion() {
        servicePost.setRegion("New Region");
        assertEquals("New Region", servicePost.getRegion());
    }

    @Test
    public void testSetAndGetUserId() {
        servicePost.setUserId("NewUser123");
        assertEquals("NewUser123", servicePost.getUserId());
    }

    @Test
    public void testSetAndGetStatus() {
        servicePost.setStatus(PostStatus.APPROVED);
        assertEquals(PostStatus.APPROVED, servicePost.getStatus());
    }

    @Test
    public void testSetAndGetImageUrls() {
        servicePost.setImageUrls(mockImageUrls);
        assertEquals(mockImageUrls, servicePost.getImageUrls());
    }

    @Test
    public void testMockedImageUrlsInteraction() {
        servicePost.setImageUrls(mockImageUrls);
        when(mockImageUrls.size()).thenReturn(3);

        assertEquals(3, servicePost.getImageUrls().size());
        verify(mockImageUrls).size();
    }

    @Test
    public void testToString() {
        servicePost.setId("12345");
        servicePost.setStatus(PostStatus.UNDER_REVIEW);
        servicePost.setImageUrls(mockImageUrls);
        servicePost.setTitle("Service");
        when(mockImageUrls.size()).thenReturn(2);

        when(mockImageUrls.toString()).thenReturn("[img1.jpg, img2.jpg]");

        String expected = "Service{id='12345', title='Service', description='Description', category='Category', subCategory='SubCategory', region='Region', userId='User123', imageUrls=[img1.jpg, img2.jpg], postStatus=Under Review}";

        assertEquals(expected, servicePost.toString());
    }
}
