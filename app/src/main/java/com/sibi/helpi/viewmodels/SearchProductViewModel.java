package com.sibi.helpi.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.sibi.helpi.models.Postable;
import com.sibi.helpi.models.ProductPost;
import com.sibi.helpi.repositories.ImagesRepository;
import com.sibi.helpi.repositories.PostRepository;
import com.sibi.helpi.utils.AppConstants;

import java.util.List;



// I think this class should be changed to PostableViewModel, because it is used to get posts from the database and change their status.
public class SearchProductViewModel extends ViewModel {
    private final PostRepository postRepository;
    private final ImagesRepository imagesRepository;

    public SearchProductViewModel() {
        postRepository = PostRepository.getInstance();
        imagesRepository = ImagesRepository.getInstance();
    }

    public LiveData<List<Postable>> getPosts(String category, String subcategory, String region, String productStatus) {
        return postRepository.getPosts(category, subcategory, region, productStatus, AppConstants.PostType.ANY);
    }

    public LiveData<List<String>> getProductImages(String productId) {
        return imagesRepository.getProductImages(productId);
    }

    public LiveData<Boolean> updatePostStatus(String postId, AppConstants.PostStatus newStatus) {
        return postRepository.updatePostStatus(postId, newStatus);
    }
}
