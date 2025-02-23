package com.sibi.helpi.fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sibi.helpi.R;
import com.sibi.helpi.adapters.ImageSliderAdapter;
import com.sibi.helpi.models.Postable;
import com.sibi.helpi.models.ProductPost;
import com.sibi.helpi.models.Report;
import com.sibi.helpi.models.Resource;
import com.sibi.helpi.utils.AppConstants;
import com.sibi.helpi.utils.LocationUtil;
import com.sibi.helpi.viewmodels.ChatViewModel;
import com.sibi.helpi.viewmodels.SearchProductViewModel;
import com.sibi.helpi.utils.AppConstants.PostStatus;
import com.sibi.helpi.viewmodels.UserViewModel;

public class PostablePageFragment extends Fragment {

    private ViewPager2 productImages;
    private TextView productTitle;
    private TextView productDescription;
    private TextView postCategory;
    private TextView posSubtCategory;
    private TextView postLocation;
    private TextView postCondition;
    private TextView deliveryPersonName;

    private FloatingActionButton acceptButton;
    private FloatingActionButton rejectButton;
    private Postable postable;

    private SearchProductViewModel postableViewModel;
    private UserViewModel userViewModel;

    private Button reportButton;

    private FloatingActionButton emailFab;
    private ChatViewModel chatViewModel;
    private Dialog loadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postableViewModel = new SearchProductViewModel();
        userViewModel = UserViewModel.getInstance();

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_postable_page, container, false);

        initViews(view);

        if (getActivity() != null) {
            assert getArguments() != null;
            String sourcePage = getArguments().getString("sourcePage");
            if (sourcePage != null) {
                if (!sourcePage.equals("AdminDashBoardFragment")) {
                    // Hide the accept and reject buttons
                    view.findViewById(R.id.acceptFab).setVisibility(View.GONE);
                    view.findViewById(R.id.rejectFab).setVisibility(View.GONE);
                }
            }

        }

        if (getArguments() != null) {

            String sourcePage = getArguments().getString("sourcePage");
            if (sourcePage != null) {
                if (!sourcePage.equals("AdminDashBoardFragment")) {
                    // Hide the accept and reject buttons
                    acceptButton.setVisibility(View.GONE);
                    rejectButton.setVisibility(View.GONE);
                }
            }

            postable = (Postable) getArguments().getSerializable("postable");

            if (postable != null) {
                addDataToViews(postable);

            }
        }
        emailFab = view.findViewById(R.id.emailFab);
        setupButtons();

        return view;
    }

    private void addDataToViews(Postable postable) {
        productTitle.setText(postable.getTitle());
        productDescription.append(": " + postable.getDescription());
        postCategory.append(": " + postable.getCategory());
        posSubtCategory.append(": " + postable.getSubCategory());
        postLocation.append(": " + LocationUtil.getLocationNameFromLocation(requireContext(), postable.getLocation()));

        if (postable instanceof ProductPost) {
            postCondition.append(": " + ((ProductPost) postable).getCondition());
        } else {
            postCondition.setVisibility(View.GONE);
        }

        String[] imageUrls = postable.getImageUrls().toArray(new String[0]);
        ImageSliderAdapter adapter = new ImageSliderAdapter(getContext(), imageUrls);
        productImages.setAdapter(adapter);

        // fetch the user name of the postable
        userViewModel.getUserByIdLiveData(postable.getUserId())
                .observe(getViewLifecycleOwner(), user -> {
                    if (user != null) {
                        deliveryPersonName.append(": " + user.getFullName());
                    }
                });
    }

    private void initViews(View view) {
        productTitle = view.findViewById(R.id.postableTitle);
        productImages = view.findViewById(R.id.imageSlider);
        postCategory = view.findViewById(R.id.postCategory);
        posSubtCategory = view.findViewById(R.id.postSubCategory);
        postLocation = view.findViewById(R.id.postLocation);
        postCondition = view.findViewById(R.id.postCondition);
        productDescription = view.findViewById(R.id.postDescription);
        deliveryPersonName = view.findViewById(R.id.deliveryPersonName);
        acceptButton = view.findViewById(R.id.acceptFab);
        rejectButton = view.findViewById(R.id.rejectFab);
        reportButton = view.findViewById(R.id.reportButton);
    }

    private void setupButtons() {
        acceptButton.setOnClickListener(v -> {
            // Accept the postable
            acceptPostable(postable);
        });

        rejectButton.setOnClickListener(v -> {
            // Reject the postable
            rejectPostable(postable);
        });

        reportButton.setOnClickListener(v -> {
            // Show the report dialog
            showReportDialog();
        });

        emailFab.setOnClickListener(v -> {
            if (postable != null) {
                String otherUserId = postable.getUserId();
                // Ensure we're not trying to chat with ourselves
                if (!otherUserId.equals(userViewModel.getCurrentUserId())) {
                    showLoadingDialog(); // Add a loading indicator
                    navigateToChat(otherUserId);
                } else {
                    Toast.makeText(getContext(), R.string.cannot_chat_with_yourself, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void navigateToChat(String otherUserId) {
        String currentUserId = userViewModel.getCurrentUserId();

        userViewModel.getUserByIdLiveData(otherUserId)
                .observe(getViewLifecycleOwner(), user -> {
                    if (user != null) {
                        String partnerName = user.getFullName();

                        chatViewModel.getChatByParticipants(currentUserId, otherUserId)
                                .observe(getViewLifecycleOwner(), chat -> {
                                    if (chat != null && chat.getChatId() != null) {
                                        hideLoadingDialog();
                                        // Navigate to chat screen
                                        Bundle args = new Bundle();
                                        args.putString("chatId", chat.getChatId());
                                        try {
                                            Navigation.findNavController(requireView())
                                                    .navigate(R.id.action_postablePageFragment_to_chatMessagesFragment, args);
                                        } catch (Exception e) {
                                            Log.e("PostablePageFragment", "Navigation failed", e);
                                            Toast.makeText(getContext(), R.string.failed_to_open_chat, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        hideLoadingDialog();
                        Toast.makeText(getContext(), R.string.could_not_find_user_information, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showLoadingDialog() {
        if (getContext() != null) {
            loadingDialog = new Dialog(getContext());
            loadingDialog.setContentView(R.layout.dialog_loading);
            loadingDialog.setCancelable(false);
            loadingDialog.show();
        }
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    private void acceptPostable(Postable post) {
        if (post != null) {
            String postId = post.getId();
            LiveData<Boolean> succeeded = postableViewModel.updatePostStatus(postId, PostStatus.APPROVED);
            succeeded.observe(getViewLifecycleOwner(), isSucceeded -> {
                if (isSucceeded) {
                    Toast.makeText(getContext(), R.string.post_accepted, Toast.LENGTH_SHORT).show();
                    // if the post is accepted, navigate back to the admin dashboard
                    Navigation.findNavController(getView()).navigate(R.id.action_postablePageFragment_to_adminDashBoardFragment);
                } else {
                    Toast.makeText(getContext(), R.string.failed_to_accept_post_please_try_again_later, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void rejectPostable(Postable post) {
        if (post != null) {
            String postId = post.getId();
            LiveData<Boolean> succeeded = postableViewModel.updatePostStatus(postId, PostStatus.REJECTED);
            succeeded.observe(getViewLifecycleOwner(), isSucceeded -> {
                if (isSucceeded) {
                    Toast.makeText(getContext(), R.string.post_rejected, Toast.LENGTH_SHORT).show();
                    // if the post is rejected, navigate back to the admin dashboard
                    Navigation.findNavController(getView()).navigate(R.id.action_postablePageFragment_to_adminDashBoardFragment);
                } else {
                    Toast.makeText(getContext(), R.string.failed_to_reject_post_please_try_again_later, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showReportDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.report_dialog);

        Spinner reportReasonSpinner = dialog.findViewById(R.id.reportReasonSpinner);
        EditText reportDescriptionEditText = dialog.findViewById(R.id.reportDescriptionEditText);
        Button submitReportButton = dialog.findViewById(R.id.submitReportButton);

        submitReportButton.setOnClickListener(v -> {
            String selectedReason = reportReasonSpinner.getSelectedItem().toString();
            String description = reportDescriptionEditText.getText().toString();


            // Handle the report submission here
            submitReport(selectedReason, description);

            dialog.dismiss();
        });

        dialog.show();
    }

    private void submitReport(String reason, String description) {
        String reporterId = userViewModel.getCurrentUserId();
        AppConstants.reportReason reasonEnum = AppConstants.reportReason.getReason(reason);
        Report report = new Report(postable.getId(), reasonEnum, reporterId, description);
        MutableLiveData<Resource<String>> succeeded = postableViewModel.fileReport(report);

        succeeded.observe(getViewLifecycleOwner(), isSucceeded -> {
            if (isSucceeded != null) {
                Toast.makeText(getContext(), R.string.report_filed_successfully, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), R.string.failed_to_file_report_please_try_again_later, Toast.LENGTH_SHORT).show();
            }
        });
    }

}