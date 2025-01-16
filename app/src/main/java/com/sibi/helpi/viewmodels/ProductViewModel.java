package com.sibi.helpi.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sibi.helpi.repositories.ImagesRepository;

import java.util.List;

public class ProductViewModel extends ViewModel {
    ImagesRepository imagesRepository = ImagesRepository.getInstance();

    public ProductViewModel() {
    }


}
