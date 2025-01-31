package com.sibi.helpi.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.sibi.helpi.models.Postable;
import com.sibi.helpi.models.ProductPost;
import com.sibi.helpi.models.Report;
import com.sibi.helpi.repositories.ImagesRepository;
import com.sibi.helpi.repositories.PostRepository;
import com.sibi.helpi.repositories.ReportsRepository;

import java.util.List;

public class AdminDashBoardViewModel extends ViewModel {

    private final ReportsRepository reportsRepository;
    private final PostRepository postRepository;
    private final ImagesRepository imagesRepository;

    public AdminDashBoardViewModel() {
        reportsRepository = ReportsRepository.getInstance();
        postRepository = PostRepository.getInstance();
        imagesRepository = ImagesRepository.getInstance();
    }

    public LiveData<List<Report>> getReports() {
        return reportsRepository.getReports();
    }

//    public LiveData<List<Postable>> getPosts() {
//        return postRepository.getUnderReviewPosts();
//    }

    //for now we use product posts only

    public LiveData<List<ProductPost>> getPosts() {
        return postRepository.getUnderReviewPosts();
    }

    public LiveData<List<String>> getProductImages(String productId) {
        return imagesRepository.getProductImages(productId);
    }






}
