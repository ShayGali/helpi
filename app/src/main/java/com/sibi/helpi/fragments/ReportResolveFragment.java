package com.sibi.helpi.fragments;

import static com.sibi.helpi.utils.LocaleHelper.getTranslatedCategory;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;
import com.sibi.helpi.R;
import com.sibi.helpi.adapters.ImageSliderAdapter;
import com.sibi.helpi.models.Postable;
import com.sibi.helpi.models.Report;
import com.sibi.helpi.utils.AppConstants;
import com.sibi.helpi.viewmodels.AdminDashBoardViewModel;

import java.util.List;
import java.util.Objects;

public class ReportResolveFragment extends Fragment {

    private TextView postTitle, postDescription, reportReason, reportDescription;
    private ViewPager2 imageSlider;
    private Button resolveReportButton;
    private Report report;

    private Postable postable;



    private AdminDashBoardViewModel adminDashBoardViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_resolve, container, false);
        adminDashBoardViewModel = new AdminDashBoardViewModel();

        postTitle = view.findViewById(R.id.PostTitleReport);
        postDescription = view.findViewById(R.id.PostDescReport);
        reportReason = view.findViewById(R.id.reportReason);
        reportDescription = view.findViewById(R.id.reportDescription);
        imageSlider = view.findViewById(R.id.imageSliderReport);
        resolveReportButton = view.findViewById(R.id.resolveReportButton);

        if (getArguments() != null) {
            report = (Report) getArguments().getSerializable("report");
            postable = (Postable) getArguments().getSerializable("postable");
            if (report != null) {
                updateUI(report);
            }
        }

        resolveReportButton.setOnClickListener(v -> openDialog());
        return view;
    }

    private void updateUI(Report report) {


        String reportReason = getTranslatedCategory(requireContext(), report.getReason().getReasonText(), "reportReason", AppConstants.ENGLISH_TO_LOCAL);
        reportReason = getString(R.string.report_reason) + " " + reportReason;
        String reportDescription = getString(R.string.report_description) + " " + report.getReporterNotes();
        String postTitle = postable.getTitle();
        String postDescription = getString(R.string.post_description) + " " + postable.getDescription();

        this.postTitle.setText(postTitle);
        this.postDescription.setText(postDescription);
        this.reportReason.setText(reportReason);
        this.reportDescription.setText(reportDescription);




        List<String> imageUrls = postable.getImageUrls();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            ImageSliderAdapter adapter = new ImageSliderAdapter(getContext(), imageUrls.toArray(new String[0]));
            imageSlider.setAdapter(adapter);
        }
    }

    private void openDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.resolve_report_dialog);

        EditText adminNotes = dialog.findViewById(R.id.etAdminNotes);
        Button deltePostButton = dialog.findViewById(R.id.btnDeletePost);
        Button rejectReportButton = dialog.findViewById(R.id.btnRejectReport);

        deltePostButton.setOnClickListener(v -> {
            String handlerNotes = adminNotes.getText().toString();
            onDeleteReport(handlerNotes);
            dialog.dismiss();
        });

        rejectReportButton.setOnClickListener(v -> {
            String handlerNotes = adminNotes.getText().toString();
            onRejectReport(handlerNotes);
            dialog.dismiss();
        });

        dialog.show();


    }


    public void onDeleteReport(String handlerNotes) { //
        adminDashBoardViewModel.updateReport(report.getReportId(), AppConstants.ReportStatus.RESOLVED, handlerNotes).observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                adminDashBoardViewModel.updatePostStatus(report.getPostId(), AppConstants.PostStatus.DELETED);
                // Notify the adapter that the data has changed
                Toast.makeText(getContext(), R.string.report_resolved_post_deleted, Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigate(R.id.action_reportResolveFragment_to_adminDashBoardFragment);
            } else {
                // Handle the failure case
                Toast.makeText(getContext(), R.string.failed_to_resolve_report, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void onRejectReport(String handlerNotes) {
        adminDashBoardViewModel.updateReport(report.getReportId(), AppConstants.ReportStatus.REJECTED, handlerNotes).observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                // Notify the adapter that the data has changed
                Toast.makeText(getContext(), R.string.report_rejected, Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigate(R.id.action_reportResolveFragment_to_adminDashBoardFragment);

            } else {
                // Handle the failure case
                Toast.makeText(getContext(), R.string.failed_to_reject_report, Toast.LENGTH_SHORT).show();
            }
        });


    }

}
