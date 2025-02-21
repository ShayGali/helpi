package com.sibi.helpi.fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.sibi.helpi.R;
import com.sibi.helpi.adapters.PostableAdapter;
import com.sibi.helpi.adapters.ReportAdapter;
import com.sibi.helpi.models.Pair;
import com.sibi.helpi.models.Postable;
import com.sibi.helpi.models.Report;
import com.sibi.helpi.utils.AppConstants;
import com.sibi.helpi.viewmodels.AdminDashBoardViewModel;
import com.sibi.helpi.viewmodels.UserViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AdminDashBoardFragment extends Fragment  {

    private ReportAdapter reportAdapter;
    private PostableAdapter postSliderAdapter;
    private List<Report> reportList;
    private List<Postable> postList;
    private AdminDashBoardViewModel adminDashBoardViewModel;
    private UserViewModel userViewModel;

    private Button addAdminButton;

    public AdminDashBoardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dash_board, container, false);

        RecyclerView reportsRecyclerView = view.findViewById(R.id.reportsRecyclerView);
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        RecyclerView postsRecyclerView = view.findViewById(R.id.postsToApproveRecyclerView);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the adapters
        reportAdapter = new ReportAdapter( pair -> {
            Bundle reportBundle = new Bundle();
            reportBundle.putSerializable("report", pair.getFirst());
            reportBundle.putSerializable("postable", pair.getSecond());
            Navigation.findNavController(view).navigate(R.id.action_adminDashBoardFragment_to_reportResolveFragment, reportBundle);
        });

        postSliderAdapter = new PostableAdapter(postable -> {
            Bundle productBundle = new Bundle();
            productBundle.putString("sourcePage", "AdminDashBoardFragment");
            productBundle.putSerializable("postable", postable);
            Navigation.findNavController(view).navigate(R.id.action_adminDashBoardFragment_to_postablePageFragment, productBundle);
        });

        reportsRecyclerView.setAdapter(reportAdapter);
        postsRecyclerView.setAdapter(postSliderAdapter);
        addAdminButton = view.findViewById(R.id.addAdminButton);

       userViewModel.getUserState().observe(getViewLifecycleOwner(), state -> {
            if (state.getUser() != null && !state.getUser().isGlobalAdmin()) {
                addAdminButton.setVisibility(View.GONE);
            }
        });



        setupObservers();
        setupButtons();

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminDashBoardViewModel = new ViewModelProvider(requireActivity()).get(AdminDashBoardViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }



    public void setupObservers() {
        adminDashBoardViewModel.getReports().observe(getViewLifecycleOwner(), reports -> {
            if (reports != null) {
                reportList = new ArrayList<>(reports);
                List<String> postIds = getPostIds(reportList);
                adminDashBoardViewModel.getPosts(postIds).observe(getViewLifecycleOwner(), posts -> {
                    if (posts != null) {
                        List<Pair<Report, Postable>> reportPostPairs = new ArrayList<>();
                        for (Report report : reportList) {
                            for (Postable post : posts) {
                                if (report.getPostId().equals(post.getId())) {
                                    reportPostPairs.add(new Pair<>(report, post));
                                    break;
                                }
                            }
                        }
                        reportAdapter.setReportList(reportPostPairs);
                    }
                });


        }
        });




        adminDashBoardViewModel.getPosts().observe(getViewLifecycleOwner(), posts -> {
            if (posts != null) {
                postList = new ArrayList<>(posts);
                postSliderAdapter.setPostableList(postList);

                // Fetch images for each product
                for (Postable productPost : postList) { //TODO - change to postable
                    adminDashBoardViewModel.getProductImages(productPost.getId()).observe(getViewLifecycleOwner(), imageUrls -> {
                        productPost.setImageUrls(imageUrls);
                        postSliderAdapter.notifyDataSetChanged();
                    });
                }
            }
        });
    }




    private List<String> getPostIds(List<Report> reports) {
        List<String> postIds = new ArrayList<>();
        for (Report report : reports) {
            postIds.add(report.getPostId());
        }
        return postIds;
    }

    private void setupButtons() {
        addAdminButton.setOnClickListener(v -> openDialog());
    }


    private void openDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_add_admin);



        // Initialize views
        EditText emailEditText = dialog.findViewById(R.id.newAdminEmailEditText);
        RadioGroup radioGroup = dialog.findViewById(R.id.adminTypeRadioGroup);
        Button okAddAdminButton = dialog.findViewById(R.id.okButtonNewAdmin);

        // Disable the button initially
        okAddAdminButton.setEnabled(false);

        // Add text change listener to the email EditText
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateInput(emailEditText, radioGroup, okAddAdminButton);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Add checked change listener to the RadioGroup
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            validateInput(emailEditText, radioGroup, okAddAdminButton);
        });

        // Set click listener for the OK button
        okAddAdminButton.setOnClickListener(v -> {
            AppConstants.UserType adminType = radioGroup.getCheckedRadioButtonId() == R.id.globalAdminRadioButton
                    ? AppConstants.UserType.GLOBAL_ADMIN
                    : AppConstants.UserType.LOCAL_ADMIN;
            onOkAddAdminButton(emailEditText.getText().toString(), adminType);
            dialog.dismiss();
        });


        // Adjust the dialog size
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9); // 90% of screen width
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; // Wrap content for height
            window.setAttributes(layoutParams);
        }

        dialog.show();
    }

    // Helper method to validate input and enable/disable the button
    private void validateInput(EditText emailEditText, RadioGroup radioGroup, Button okButton) {
        String email = emailEditText.getText().toString().trim();
        boolean isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        boolean isRadioSelected = radioGroup.getCheckedRadioButtonId() != -1;

        okButton.setEnabled(isEmailValid && isRadioSelected);
    }

    private void onOkAddAdminButton(String email, AppConstants.UserType adminType) {
        adminDashBoardViewModel.addAdmin(email, adminType).observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                Toast.makeText(requireContext(), "Admin added successfully", Toast.LENGTH_SHORT).show();

            } else {
             Toast.makeText(requireContext(), "Failed to add admin, are you sure the email is correct?", Toast.LENGTH_LONG).show();
            }
        });
    }



}