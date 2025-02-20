package com.sibi.helpi.models;
import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.assertEquals;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sibi.helpi.repositories.ImagesRepository;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PostProductTest {
    @Test
    public void testUploadProductImage() {
        ImagesRepository imagesRepository = mock(ImagesRepository.class);
        byte[] data = new byte[10];
        String productUUID = "productUUID";
        Task<Uri> task = mock(Task.class);

        StorageReference storageReference = mock(StorageReference.class);
        StorageReference postsRef = mock(StorageReference.class);
        StorageReference productImagesRef = mock(StorageReference.class);

        UploadTask uploadTask = mock(UploadTask.class);
        Uri uri = mock(Uri.class);

        when(imagesRepository.uploadProfileImage(productUUID, data)).thenReturn(task);
        when(postsRef.child(productUUID)).thenReturn(productImagesRef);
        when(productImagesRef.child(System.currentTimeMillis() + ".jpg")).thenReturn(productImagesRef);
        when(productImagesRef.putBytes(data)).thenReturn(uploadTask);
        when(task.getResult()).thenReturn(uri);
    }
}
