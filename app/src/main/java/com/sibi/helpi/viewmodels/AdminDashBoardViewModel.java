package com.sibi.helpi.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sibi.helpi.models.Postable;
import com.sibi.helpi.models.Report;
import com.sibi.helpi.repositories.ImagesRepository;
import com.sibi.helpi.repositories.PostRepository;
import com.sibi.helpi.repositories.ReportsRepository;
import com.sibi.helpi.repositories.UserRepository;
import com.sibi.helpi.stats.UserState;
import com.sibi.helpi.utils.AppConstants;
import com.sibi.helpi.utils.AppConstants.ReportStatus;

import java.util.List;

public class AdminDashBoardViewModel extends ViewModel {

    private final ReportsRepository reportsRepository;
    private final PostRepository postRepository;
    private final ImagesRepository imagesRepository;

    private final UserRepository userRepository;

    public AdminDashBoardViewModel() {
        reportsRepository = ReportsRepository.getInstance();
        postRepository = PostRepository.getInstance();
        imagesRepository = ImagesRepository.getInstance();
        userRepository = UserRepository.getInstance();
    }

    public LiveData<List<Report>> getReports() {
        return reportsRepository.getReports();
    }

//    public LiveData<List<Postable>> getPosts() {
//        return postRepository.getUnderReviewPosts();
//    }

    //for now we use product posts only

    public LiveData<List<Postable>> getPosts() {
        return postRepository.getUnderReviewPosts();
    }

    public LiveData<List<String>> getProductImages(String productId) { //TODO change to postImages
        return imagesRepository.getProductImages(productId);
    }

    public LiveData<Boolean> updateReport(String reportId, ReportStatus newStatus, String handlerNotes) {
        String handlerId = userRepository.getUUID();
        return reportsRepository.updateReport(reportId, handlerId, handlerNotes, newStatus);
    }

    public LiveData<Boolean> updatePostStatus(String postId, AppConstants.PostStatus newStatus) {
        return postRepository.updatePostStatus(postId, newStatus);
    }


    public LiveData<List<Postable>> getPosts(List<String> postIds) {
        return postRepository.getPosts(postIds);
    }

    public LiveData<Boolean> addAdmin(String email, AppConstants.UserType userType) {
        MutableLiveData<Boolean> successLiveData = new MutableLiveData<>();

        userRepository.addAdmin(email, userType)
                .addOnSuccessListener(isSuccessfullyAdded -> {
                    if (isSuccessfullyAdded) {
                        successLiveData.postValue(true);
                    } else {
                        successLiveData.postValue(false);
                    }
                })
                .addOnFailureListener(e -> {
                    successLiveData.postValue(false);
                });

        return successLiveData;
    }







}
