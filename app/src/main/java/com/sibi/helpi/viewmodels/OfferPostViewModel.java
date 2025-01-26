package com.sibi.helpi.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.sibi.helpi.models.Postable;
import com.sibi.helpi.repositories.PostRepository;
import com.sibi.helpi.models.Resource;

public class OfferPostViewModel extends ViewModel {
    private PostRepository postRepository;
    private MutableLiveData<Resource<String>> postLiveData = new MutableLiveData<>();

    public OfferPostViewModel() {
        postRepository = PostRepository.getInstance();
    }

    public LiveData<Resource<String>> getPostLiveData() {
        return postLiveData;
    }

    public void savePost(Postable post, byte[][] images) {
        postLiveData.setValue(Resource.loading(null));
        postRepository.savePost(post, images, postLiveData);
    }
}
