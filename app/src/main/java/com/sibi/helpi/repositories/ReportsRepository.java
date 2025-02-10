package com.sibi.helpi.repositories;

import static com.sibi.helpi.utils.AppConstants.COLLECTION_REPORTS;
import static com.sibi.helpi.utils.AppConstants.POST_UPLOAD_FAILED;



import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sibi.helpi.models.Report;
import com.sibi.helpi.models.Resource;
import com.sibi.helpi.utils.AppConstants;

import java.util.ArrayList;
import java.util.List;

public class ReportsRepository {
    public static ReportsRepository instance;
    private static final Object LOCK = new Object();

    public synchronized static ReportsRepository getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new ReportsRepository();
                }
            }
        }
        return instance;
    }

    private final CollectionReference reportsCollection;

    private ReportsRepository() {
        reportsCollection = FirebaseFirestore.getInstance().collection(COLLECTION_REPORTS);
    }

    public LiveData<List<Report>> getReports() {
        MutableLiveData<List<Report>> mutableLiveData = new MutableLiveData<>();
        reportsCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Report> reports = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Report report = document.toObject(Report.class);
                        if (report != null) {
                            if( report.getStatus()!=null && report.getStatus() == AppConstants.reportStatus.PENDING )
                                reports.add(report);
                        }
                    }
                    mutableLiveData.setValue(reports);
                })
                .addOnFailureListener(e -> {
                    Log.e("Repository", "Failed to fetch reports: " + e.getMessage());
                    mutableLiveData.setValue(null);
                });

        return mutableLiveData;
    }

    public void saveReport(Report report, MutableLiveData<Resource<String>> reportLiveData) {
        Log.d("Repository", "Starting report save");

        String reportId = reportsCollection.document().getId();
        Log.d("Repository", "Generated report ID: " + reportId);
        report.setReportId(reportId);

        saveReportData(report, reportLiveData);
    }

    private void saveReportData(Report report, MutableLiveData<Resource<String>> reportLiveData) {
        reportsCollection.document(report.getReportId())
                .set(report)
                .addOnSuccessListener(aVoid ->
                        reportLiveData.setValue(Resource.success(report.getReportId()))
                )
                .addOnFailureListener(e ->
                        reportLiveData.setValue(
                                Resource.error(POST_UPLOAD_FAILED + e.getMessage(), null)
                        )
                );
    }

    public LiveData<Boolean> updateReport(String reportId, String handlerId, AppConstants.reportStatus newStatus) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        reportsCollection.document(reportId)
                .update("reportStatus", newStatus, "handlerId", handlerId)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Repository", "Report updated successfully");
                    result.setValue(true);
                })
                .addOnFailureListener(e -> {
                    Log.e("Repository", "Failed to update report: " + e.getMessage());
                    result.setValue(false);
                });
        return result;
    }

}