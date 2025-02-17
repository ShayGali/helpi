// File: app/src/test/java/com/sibi/helpi/models/MessageTest.java

package com.sibi.helpi.models;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;



public class MessageTest {

    @Test
    public void messageInitializationWithUser() {
        Message message = new Message("Hello, user!", true);
        assertEquals("Hello, user!", message.getText());
        assertTrue(message.isUser());
    }

    @Test
    public void messageInitializationWithoutUser() {
        Message message = new Message("Hello, system!", false);
        assertEquals("Hello, system!", message.getText());
        assertFalse(message.isUser());
    }

    @Test
    public void messageTextIsNull() {
        Message message = new Message(null, true);
        assertNull(message.getText());
        assertTrue(message.isUser());
    }

    @Test
    public void messageTextIsEmpty() {
        Message message = new Message("", false);
        assertEquals("", message.getText());
        assertFalse(message.isUser());
    }

    @Test
    public void messageWithMockedText() {
        Message message = mock(Message.class);
        when(message.getText()).thenReturn("Mocked text");
        when(message.isUser()).thenReturn(true);

        assertEquals("Mocked text", message.getText());
        assertTrue(message.isUser());
    }
}