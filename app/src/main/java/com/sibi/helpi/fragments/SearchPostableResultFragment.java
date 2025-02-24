package com.sibi.helpi.fragments;

import static com.sibi.helpi.utils.AppConstants.ENGLISH_TO_LOCAL;
import static com.sibi.helpi.utils.LocaleHelper.getTranslatedCategory;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.GeoPoint;
import com.sibi.helpi.R;
import com.sibi.helpi.adapters.PostableAdapter;
import com.sibi.helpi.models.Postable;
import com.sibi.helpi.models.ProductPost;
import com.sibi.helpi.utils.AppConstants;
import com.sibi.helpi.viewmodels.SearchProductViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchPostableResultFragment extends Fragment {

    private RecyclerView productRecyclerView;
    private PostableAdapter postsSliderAdapter;
    private List<Postable> productPostList;
    private SearchProductViewModel searchProductViewModel;

    public SearchPostableResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchProductViewModel = new SearchProductViewModel();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_postable_result, container, false);

        productRecyclerView = view.findViewById(R.id.productRecycleView);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // get the search query from the bundle
        Bundle bundle = getArguments();
        assert bundle != null;
        String category = bundle.getString("category");
        String subcategory = bundle.getString("subcategory");
        String productStatus = bundle.getString("productStatus"); // the condition of the product
        double latitude = bundle.getDouble("latitude");
        double longitude = bundle.getDouble("longitude");
        String postType = bundle.getString("postType");

        AppConstants.PostType type = AppConstants.PostType.fromString(postType);

        // get the products from the repository
        LiveData<List<Postable>> postsLiveData = searchProductViewModel.getPosts(category, subcategory, new GeoPoint(latitude, longitude) , productStatus,type );




        postsSliderAdapter = new PostableAdapter(postable -> {
            Bundle postableBundle = new Bundle();
            postableBundle.putString("sourcePage", "SearchPostableResultFragment");
            postableBundle.putSerializable("postable", postable);
            Navigation.findNavController(view).navigate(R.id.action_searchPostableResultFragment_to_postablePageFragment, postableBundle);
        });

        productRecyclerView.setAdapter(postsSliderAdapter);

        // observe the products
        postsLiveData.observe(getViewLifecycleOwner(), products -> {
            productPostList = new ArrayList<>(products);
            // Translate the category, subcategory and productStatus to The locale language
            for (Postable post : productPostList) {
                post.setCategory(getTranslatedCategory(getContext(), post.getCategory(), "category", ENGLISH_TO_LOCAL));
                post.setSubCategory(getTranslatedCategory(getContext(), post.getSubCategory(), "subcategory",ENGLISH_TO_LOCAL));
                if (post instanceof ProductPost) {
                    ((ProductPost) post).setCondition(getTranslatedCategory(getContext(), ((ProductPost) post).getCondition(), "condition",ENGLISH_TO_LOCAL));
                }


            }

            postsSliderAdapter.setPostableList(productPostList);

            // Fetch images for each product
            for (Postable productPost : productPostList) {
                searchProductViewModel.getProductImages(productPost.getId()).observe(getViewLifecycleOwner(), imageUrls -> {
                    productPost.setImageUrls(imageUrls);
                    postsSliderAdapter.notifyDataSetChanged();
                });
            }
        });
        return view;
    }
}

