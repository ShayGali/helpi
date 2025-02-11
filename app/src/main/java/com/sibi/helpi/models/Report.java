package com.sibi.helpi.models;

import com.sibi.helpi.utils.AppConstants;
import com.sibi.helpi.utils.AppConstants.reportReason;
import com.sibi.helpi.utils.AppConstants.ReportStatus;

import java.util.Date;


public class Report {


    private final String postId;
    private final reportReason reason;
    private final String reporterId;
    private final String reporterNotes;
    private final Date dateReported; // date of the report
    private String handlerId;
    private String handlerNotes;
    private AppConstants.ReportStatus reportStatus;
    private String reportId;



    public Report() {
        this.postId = "";
        this.reason = reportReason.OTHER;
        this.reporterId = "";
        this.reportStatus = ReportStatus.PENDING;
        this.dateReported = new Date();      //Current date
        this.reporterNotes = "";
        this.reportId = "";
    }

    public Report(String postId, reportReason reason, String reporterId, String reporterNotes) {

        this.postId = postId;
        this.reason = reason;
        this.reporterId = reporterId;
        this.reportStatus = ReportStatus.PENDING;
        this.dateReported = new Date();      //Current date
        this.reporterNotes = reporterNotes;
        this.reportId = "";

    }
    public reportReason getReason() {
        return reason;
    }
    public String getPostId() {
        return postId;
    }
    public ReportStatus getReportStatus() {
        return reportStatus;
    }
    public void setReportStatus(ReportStatus status) {
        this.reportStatus = status;
    }
    public String getReporterId() {
        return reporterId;
    }
    public String getReporterNotes() {
        return reporterNotes;
    }
    public String getHandlerId() {
        return handlerId;
    }
    public void setHandlerId(String handlerId) {
        this.handlerId = handlerId;
    }
    public String getHandlerNotes() {
        return handlerNotes;
    }
    public void setHandlerNotes(String handlerNotes) {
        this.handlerNotes = handlerNotes;
    }
    public Date getDateReported() {
        return dateReported;
    }

    public String getReportId() {
        return reportId;
    }
    public void setReportId(String reportId) {
        this.reportId = reportId;
    }










}