package com.sibi.helpi.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.sibi.helpi.models.Postable;
import com.sibi.helpi.models.ProductPost;
import com.sibi.helpi.models.Report;
import com.sibi.helpi.repositories.ImagesRepository;
import com.sibi.helpi.repositories.PostRepository;
import com.sibi.helpi.repositories.ReportsRepository;
import com.sibi.helpi.utils.AppConstants;
import com.sibi.helpi.utils.AppConstants.ReportStatus;
import com.sibi.helpi.viewmodels.UserViewModel;

import java.util.List;

public class AdminDashBoardViewModel extends ViewModel {

    private final ReportsRepository reportsRepository;
    private final PostRepository postRepository;
    private final ImagesRepository imagesRepository;

    private final UserViewModel userViewModel;

    public AdminDashBoardViewModel() {
        reportsRepository = ReportsRepository.getInstance();
        postRepository = PostRepository.getInstance();
        imagesRepository = ImagesRepository.getInstance();
        userViewModel = new UserViewModel();  // TODO change to singleton
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
        String handlerId = userViewModel.getUserId();
        return reportsRepository.updateReport(reportId, handlerId, handlerNotes, newStatus);
    }

    public LiveData<Boolean> updatePostStatus(String postId, AppConstants.PostStatus newStatus) {
        return postRepository.updatePostStatus(postId, newStatus);
    }


    public LiveData<List<Postable>> getPosts(List<String> postIds) {
        return postRepository.getPosts(postIds);
    }





}
