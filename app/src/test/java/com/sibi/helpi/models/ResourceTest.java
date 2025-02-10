package com.sibi.helpi.models;

import org.junit.Test;


import static org.junit.Assert.*;


public class ResourceTest {

    @Test
    public void testSuccessResource() {
        String data = "Success Data";

        Resource<String> resource = Resource.success(data);

        assertEquals(Resource.Status.SUCCESS, resource.getStatus());
        assertEquals(data, resource.getData());
        assertNull(resource.getMessage());
    }

    @Test
    public void testErrorResource() {
        String data = "Error Data";
        String errorMessage = "An error occurred";

        Resource<String> resource = Resource.error(errorMessage, data);

        assertEquals(Resource.Status.ERROR, resource.getStatus());
        assertEquals(data, resource.getData());
        assertEquals(errorMessage, resource.getMessage());
    }

    @Test
    public void testLoadingResource() {
        String data = "Loading Data";

        Resource<String> resource = Resource.loading(data);

        assertEquals(Resource.Status.LOADING, resource.getStatus());
        assertEquals(data, resource.getData());
        assertNull(resource.getMessage());
    }

    @Test
    public void testConstructor() {
        String data = "Constructor Data";
        String message = "Test Message";
        Resource<String> resource = new Resource<>(Resource.Status.SUCCESS, data, message);

        assertEquals(Resource.Status.SUCCESS, resource.getStatus());
        assertEquals(data, resource.getData());
        assertEquals(message, resource.getMessage());
    }

    @Test
    public void testEnumValues() {
        Resource.Status[] statuses = Resource.Status.values();

        assertEquals(3, statuses.length);
        assertEquals(Resource.Status.SUCCESS, statuses[0]);
        assertEquals(Resource.Status.ERROR, statuses[1]);
        assertEquals(Resource.Status.LOADING, statuses[2]);
    }

    @Test
    public void testEnumValueOf() {
        assertEquals(Resource.Status.SUCCESS, Resource.Status.valueOf("SUCCESS"));
        assertEquals(Resource.Status.ERROR, Resource.Status.valueOf("ERROR"));
        assertEquals(Resource.Status.LOADING, Resource.Status.valueOf("LOADING"));
    }
}
