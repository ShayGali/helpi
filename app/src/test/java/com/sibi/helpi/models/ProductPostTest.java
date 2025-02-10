package com.sibi.helpi.models;

import org.junit.Test;
import static org.junit.Assert.*;

import com.sibi.helpi.utils.AppConstants;

import java.util.List;
import java.util.ArrayList;

public class ProductPostTest {

    @Test
    public void productPostInitialization() {
        ProductPost productPost = new ProductPost("Title", "Description", "Category", "SubCategory", "Region", "Condition", "UserId");
        assertEquals("Title", productPost.getTitle());
        assertEquals("Description", productPost.getDescription());
        assertEquals("Category", productPost.getCategory());
        assertEquals("SubCategory", productPost.getSubCategory());
        assertEquals("Region", productPost.getRegion());
        assertEquals("Condition", productPost.getCondition());
        assertEquals("UserId", productPost.getUserId());
        assertEquals(AppConstants.PostStatus.UNDER_REVIEW, productPost.getStatus());
    }

    @Test
    public void productPostDefaultConstructor() {
        ProductPost productPost = new ProductPost();
        assertNull(productPost.getTitle());
        assertNull(productPost.getDescription());
        assertNull(productPost.getCategory());
        assertNull(productPost.getSubCategory());
        assertNull(productPost.getRegion());
        assertNull(productPost.getCondition());
        assertNull(productPost.getUserId());
        assertNull(productPost.getStatus());
    }

    @Test
    public void productPostSettersAndGetters() {
        ProductPost productPost = new ProductPost();
        productPost.setTitle("New Title");
        productPost.setDescription("New Description");
        productPost.setCategory("New Category");
        productPost.setSubCategory("New SubCategory");
        productPost.setRegion("New Region");
        productPost.setCondition("New Condition");
        productPost.setUserId("New UserId");
        productPost.setStatus(AppConstants.PostStatus.APPROVED);

        assertEquals("New Title", productPost.getTitle());
        assertEquals("New Description", productPost.getDescription());
        assertEquals("New Category", productPost.getCategory());
        assertEquals("New SubCategory", productPost.getSubCategory());
        assertEquals("New Region", productPost.getRegion());
        assertEquals("New Condition", productPost.getCondition());
        assertEquals("New UserId", productPost.getUserId());
        assertEquals(AppConstants.PostStatus.APPROVED, productPost.getStatus());
    }

    @Test
    public void productPostImageUrls() {
        ProductPost productPost = new ProductPost();
        assertTrue(productPost.getImageUrls().isEmpty());

        List<String> imageUrls = new ArrayList<>();
        imageUrls.add("url1");
        imageUrls.add("url2");
        productPost.setImageUrls(imageUrls);

        assertEquals(2, productPost.getImageUrls().size());
        assertEquals("url1", productPost.getImageUrls().get(0));
        assertEquals("url2", productPost.getImageUrls().get(1));
    }

    @Test
    public void productPostToString() {
        ProductPost productPost = new ProductPost("Title", "Description", "Category", "SubCategory", "Region", "Condition", "UserId");
        productPost.setId("123");
        String expectedString = "Product{" +
                "id='123'" +
                ", description='Description'" +
                ", category='Category'" +
                ", subCategory='SubCategory'" +
                ", region='Region'" +
                ", condition='Condition'" +
                ", userId='UserId'" +
                ", postStatus=Under Review" +
                '}';
        assertEquals(expectedString, productPost.toString());
    }

    @Test
    public void productPostSetId() {
        ProductPost productPost = new ProductPost();
        productPost.setId("123");
        assertEquals("123", productPost.getId());
    }

    @Test
    public void productPostSetTitle() {
        ProductPost productPost = new ProductPost();
        productPost.setTitle("New Title");
        assertEquals("New Title", productPost.getTitle());
    }

    @Test
    public void productPostSetDescription() {
        ProductPost productPost = new ProductPost();
        productPost.setDescription("New Description");
        assertEquals("New Description", productPost.getDescription());
    }

    @Test
    public void productPostSetCategory() {
        ProductPost productPost = new ProductPost();
        productPost.setCategory("New Category");
        assertEquals("New Category", productPost.getCategory());
    }

    @Test
    public void productPostSetSubCategory() {
        ProductPost productPost = new ProductPost();
        productPost.setSubCategory("New SubCategory");
        assertEquals("New SubCategory", productPost.getSubCategory());
    }

    @Test
    public void productPostSetRegion() {
        ProductPost productPost = new ProductPost();
        productPost.setRegion("New Region");
        assertEquals("New Region", productPost.getRegion());
    }

    @Test
    public void productPostSetCondition() {
        ProductPost productPost = new ProductPost();
        productPost.setCondition("New Condition");
        assertEquals("New Condition", productPost.getCondition());
    }

    @Test
    public void productPostSetUserId() {
        ProductPost productPost = new ProductPost();
        productPost.setUserId("New UserId");
        assertEquals("New UserId", productPost.getUserId());
    }

    @Test
    public void productPostSetStatus() {
        ProductPost productPost = new ProductPost();
        productPost.setStatus(AppConstants.PostStatus.APPROVED);
        assertEquals(AppConstants.PostStatus.APPROVED, productPost.getStatus());
    }
}