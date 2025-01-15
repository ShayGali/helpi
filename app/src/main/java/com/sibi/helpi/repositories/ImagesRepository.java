package com.sibi.helpi.repositories;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ImagesRepository {
    // singleton
    private static ImagesRepository instance;
    private static final Object lock = new Object();

    public static ImagesRepository getInstance() {
        synchronized (lock) {
            if (instance == null) {
                instance = new ImagesRepository();
            }
            return instance;
        }
    }

    private static final String COLLECTION_PATH = "images";
    private static final String PRODUCT_IMAGES_PATH = "product_images";

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference;


    StorageReference productsRef;

    private ImagesRepository() {
        storageReference = storage.getReference();
        productsRef = storageReference.child(COLLECTION_PATH).child(PRODUCT_IMAGES_PATH);
    }

    public void uploadProductImage(String productUUID, byte[] data) {
        UploadTask uploadTask = productsRef.putBytes(data);
        StorageReference productImagesRef = productsRef.child(productUUID).child(System.currentTimeMillis() + ".jpg");
        uploadTask.addOnFailureListener(exception -> {

            Log.e("ImagesRepository", "Failed to upload image: " + exception);
        }).addOnSuccessListener(taskSnapshot -> {
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
            // ...
            Log.d("ImagesRepository", "Image uploaded successfully");
        });
    }

    public void uploadProductImages(Context context, String productId, List<Uri> imageUris) {
        for (Uri imageUri : imageUris) {
            try {
                Bitmap compressedImage = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImage.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                byte[] data = baos.toByteArray();
                uploadProductImage(productId, data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
