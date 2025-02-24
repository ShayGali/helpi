package com.sibi.helpi.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.GeoPoint;
import com.sibi.helpi.models.Postable;
import com.sibi.helpi.models.Report;
import com.sibi.helpi.models.Resource;
import com.sibi.helpi.repositories.ImagesRepository;
import com.sibi.helpi.repositories.PostRepository;
import com.sibi.helpi.repositories.ReportsRepository;

import com.sibi.helpi.utils.AppConstants;

import java.util.List;



// I think this class should be changed to PostableViewModel, because it is used to get posts from the database and change their status.
public class SearchProductViewModel extends ViewModel {
    private final PostRepository postRepository;
    private final ImagesRepository imagesRepository;

    private final ReportsRepository reportsRepository;

    public SearchProductViewModel() {
        postRepository = PostRepository.getInstance();
        imagesRepository = ImagesRepository.getInstance();
        reportsRepository = ReportsRepository.getInstance();
    }

    public LiveData<List<Postable>> getPosts(String category, String subcategory, GeoPoint region, String productStatus, AppConstants.PostType postType) {
        return postRepository.getPosts(category, subcategory, region, productStatus, postType);
    }

    public LiveData<List<String>> getProductImages(String productId) {
        return imagesRepository.getProductImages(productId);
    }

    public LiveData<Boolean> updatePostStatus(String postId, AppConstants.PostStatus newStatus) {
        return postRepository.updatePostStatus(postId, newStatus);
    }

    public MutableLiveData<Resource<String>> fileReport(Report report) {
        MutableLiveData<Resource<String>> reportLiveData = new MutableLiveData<>();
        reportsRepository.saveReport(report, reportLiveData);
        return reportLiveData;
    }
    public LiveData<List<Postable>> getRecentPosts(int numOfPosts, AppConstants.PostType type) {
        MutableLiveData<List<Postable>> postsLiveData = new MutableLiveData<>();
        postRepository.getRecentPosts(numOfPosts, type).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                postsLiveData.setValue(task.getResult());
            } else {
                postsLiveData.setValue(null);
            }
        });
        return postsLiveData;
    }
}
